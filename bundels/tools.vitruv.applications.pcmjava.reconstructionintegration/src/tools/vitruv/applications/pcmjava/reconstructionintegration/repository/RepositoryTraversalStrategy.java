package tools.vitruv.applications.pcmjava.reconstructionintegration.repository;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompleteComponentType;
import org.palladiosimulator.pcm.repository.CompositeComponent;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.ProvidesComponentType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RequiredRole;

import tools.vitruv.framework.change.description.VitruviusChange;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.applications.pcmjava.reconstructionintegration.util.PcmChangeBuildHelper;
import tools.vitruv.extensions.constructionsimulation.traversal.EMFTraversalStrategy;
import tools.vitruv.extensions.constructionsimulation.traversal.ITraversalStrategy;
import tools.vitruv.framework.change.description.CompositeContainerChange;
import tools.vitruv.framework.change.description.VitruviusChangeFactory;
import static tools.vitruv.applications.pcmjava.reconstructionintegration.util.PcmChangeBuildHelper.*;

/**
 * The Class RepositoryTraversalStrategy.
 *
 * @author Sven Leonhardt, Benjamin Hettwer
 *
 *         Concrete implementation of a traversal strategy for PCM models
 */

public class RepositoryTraversalStrategy extends EMFTraversalStrategy implements ITraversalStrategy<Repository> {

    private final EList<VitruviusChange> changeList = new UniqueEList<VitruviusChange>();
    private VURI vuri;

    /*
     * (non-Javadoc)
     *
     * @see
     * tools.vitruv.integration.traversal.ITraversalStrategy#traverse(org.eclipse.emf
     * .ecore.EObject, org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.EList)
     */
    @Override
    public EList<VitruviusChange> traverse(final Repository entity, final URI uri, final EList<VitruviusChange> existingChanges)
            throws UnsupportedOperationException {

        if (existingChanges != null) {
            throw new UnsupportedOperationException("Repository Traversal cannot be placed on existing changes");
        }

        final Repository repository = entity;
        this.vuri = VURI.getInstance(uri);

        final EList<DataType> dataTypes = repository.getDataTypes__Repository();
        final EList<RepositoryComponent> components = repository.getComponents__Repository();
        final EList<Interface> interfaces = repository.getInterfaces__Repository();

        // Traverse elements in their logicals order
        this.traverseRepository(repository);
        this.traversePrimitiveDataTypes(dataTypes);
        this.traverseCompositeDataTypes(dataTypes);
        this.traverseCollectionDataTypes(dataTypes);
        this.traverseComponents(components);
        this.traverseInterfaces(interfaces);
        this.traverseProvidesComponents(components); // NOT SUPPORTED BY TRANSFORMATION
        this.traverseCompleteComponents(components); // NOT SUPPORTED BY TRANSFORMATION
        this.traverseRoles(components);
        this.traverseSignaturesAndParameters(interfaces);
        this.traverseInnerDeclarations(dataTypes);

        return this.changeList;

    }

    /**
     * Traverse interfaces
     *
     * @param interfaces
     *            the interfaces
     */
    private void traverseInterfaces(final EList<Interface> interfaces) {
        if (interfaces.size() > 0) {
            final EList<VitruviusChange> interfaceChanges = this.traverseInterfaceHierarchy(interfaces, this.vuri);
            for (final VitruviusChange change : interfaceChanges) {
                this.addChange(change, this.changeList);
            }
        }
    }

    /**
     * Traverse inner declarations.
     *
     * @param dataTypes
     *            the data types
     */
    private void traverseInnerDeclarations(final EList<DataType> dataTypes) {
        for (final DataType dataType : dataTypes) {
            if (dataType instanceof CompositeDataType) {

                final EList<InnerDeclaration> innerTypes = ((CompositeDataType) dataType)
                        .getInnerDeclaration_CompositeDataType();

                if (!innerTypes.isEmpty()) {
                    for (final InnerDeclaration innerDeclaration : innerTypes) {
                        this.addChange(encapsulateChanges(
                                PcmChangeBuildHelper.createChangeFromInnerDeclaration(innerDeclaration), this.vuri),
                                this.changeList);
                    }
                }
            }
        }
    }

    /**
     * Traverse signatures and parameters.
     *
     * @param interfaces
     *            the interfaces
     */
    private void traverseSignaturesAndParameters(final EList<Interface> interfaces) {
        for (final Interface inter : interfaces) {
            if (inter instanceof OperationInterface) {
                for (final OperationSignature signature : ((OperationInterface) inter)
                        .getSignatures__OperationInterface()) {

                    // cache parameters
                    /*
                     * EList<Parameter> parameters = new BasicEList<Parameter>(); for(Parameter p :
                     * signature.getParameters__OperationSignature()) { parameters.add(p); }
                     */

                    // clear parameters: Transformation only supports adding of empty signatures
                    // signature.getParameters__OperationSignature().clear();
                    this.addChange(
                    		encapsulateChanges(PcmChangeBuildHelper.createChangeFromSignature(signature), this.vuri),
                            this.changeList);
                    /*
                     * for (int i = 0; i < parameters.size(); i++) { addChange( new EMFModelChange(
                     * PCMChangeBuildHelper .createChangeFromParameter(parameters.get(i), signature,
                     * i), vuri), changeList); }
                     */
                    for (final Parameter parameter : signature.getParameters__OperationSignature()) {
                        this.addChange(encapsulateChanges(PcmChangeBuildHelper.createChangeFromParameter(parameter),
                                this.vuri), this.changeList);
                    }
                }
            }
        }
    }

    /**
     * Traverse roles.
     *
     * @param components
     *            the components
     */
    private void traverseRoles(final EList<RepositoryComponent> components) {
        for (final RepositoryComponent component : components) {
            if (component instanceof BasicComponent) {
                for (final ProvidedRole role : component.getProvidedRoles_InterfaceProvidingEntity()) {
                    this.addChange(encapsulateChanges(PcmChangeBuildHelper.createChangeFromRole(role), this.vuri),
                            this.changeList);
                }
                for (final RequiredRole role : component.getRequiredRoles_InterfaceRequiringEntity()) {
                    this.addChange(encapsulateChanges(PcmChangeBuildHelper.createChangeFromRole(role), this.vuri),
                            this.changeList);
                }
            }
        }
    }

    /**
     * Traverse complete components.
     *
     * @param components
     *            the components
     */
    private void traverseCompleteComponents(final EList<RepositoryComponent> components) {
        for (final RepositoryComponent component : components) {
            if (component instanceof CompleteComponentType) {

                final CompositeContainerChange compositeChange = VitruviusChangeFactory.getInstance().createCompositeContainerChange();

                final EList<EChange> completeCompositeChanges = PcmChangeBuildHelper
                        .createChangeFromCompleteComponent((CompleteComponentType) component);
                for (final EChange change : completeCompositeChanges) {
                    compositeChange.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(change, this.vuri));
                }

                this.addChange(compositeChange, this.changeList);
            }
        }
    }

    /**
     * Traverse provides components.
     *
     * @param components
     *            the components
     */
    private void traverseProvidesComponents(final EList<RepositoryComponent> components) {
        for (final RepositoryComponent component : components) {
            if (component instanceof ProvidesComponentType) {

            	final CompositeContainerChange compositeChange = VitruviusChangeFactory.getInstance().createCompositeContainerChange();

                final EList<EChange> providedComponentChanges = PcmChangeBuildHelper
                        .createChangeFromProvidesComponent((ProvidesComponentType) component);
                for (final EChange change : providedComponentChanges) {
                    compositeChange.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(change, this.vuri));
                }

                this.addChange(compositeChange, this.changeList);
            }
        }
    }

    /**
     * Traverse components.
     *
     * @param components
     *            the components
     */
    private void traverseComponents(final EList<RepositoryComponent> components) {
        for (final RepositoryComponent component : components) {

            List<EChange> componentChange = null;

            // create change from component
            if (component instanceof BasicComponent) {
                componentChange = PcmChangeBuildHelper.createChangeFromBasicComponent((BasicComponent) component);
            } else if (component instanceof CompositeComponent) {
                componentChange = PcmChangeBuildHelper
                        .createChangeFromCompositeComponent((CompositeComponent) component);
            } else {
                return;
            }

            for (EChange change : componentChange) {
            	this.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(change, this.vuri), this.changeList);	
            }
        }
    }

    /**
     * Traverse collection data types.
     *
     * @param dataTypes
     *            the data types
     */
    private void traverseCollectionDataTypes(final EList<DataType> dataTypes) {
        for (final DataType dataType : dataTypes) {
            if (dataType instanceof CollectionDataType) {
                final List<EChange> dataTypeChanges = PcmChangeBuildHelper.createChangeFromDataType(dataType);
                this.addChange(encapsulateChanges(dataTypeChanges, this.vuri), this.changeList);
            }
        }
    }

    /**
     * Traverse composite data types.
     *
     * @param dataTypes
     *            the data types
     */
    private void traverseCompositeDataTypes(final EList<DataType> dataTypes) {
        for (final DataType dataType : dataTypes) {
            if (dataType instanceof CompositeDataType) {

                // check for parent dataTypes => First traverse whom without one
                final EList<CompositeDataType> parents = ((CompositeDataType) dataType)
                        .getParentType_CompositeDataType();
                if (parents.isEmpty()) {
                    final List<EChange> dataTypeChanges = PcmChangeBuildHelper.createChangeFromDataType(dataType);
                    this.addChange(encapsulateChanges(dataTypeChanges, this.vuri), this.changeList);
                } else {
                    // TODO: traverse parents
                }
            }
        }
    }

    /**
     * Traverse primitive data types.
     *
     * @param dataTypes
     *            the data types
     */
    private void traversePrimitiveDataTypes(final EList<DataType> dataTypes) {
        for (final DataType dataType : dataTypes) {
            if (dataType instanceof PrimitiveDataType) {
                this.addChange(encapsulateChanges(PcmChangeBuildHelper.createChangeFromDataType(dataType), this.vuri),
                        this.changeList);
            }
        }
    }

    /**
     * Traverse repository.
     *
     * @param repository
     *            the repository
     */
    private void traverseRepository(final Repository repository) {
        final EChange repositoryChange = PcmChangeBuildHelper.createChangeFromRepository(repository);
        this.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(repositoryChange, this.vuri), this.changeList);
    }

    /**
     * Helper method for traversing interfaces with respect to their parent interfaces. We make the
     * assumption that there are no cycles in the depth of inheritance
     *
     * @param interfaces
     *            : List of all interfaces inside the repository
     * @param vuri
     *            the vuri
     * @return List of changes
     */
    private EList<VitruviusChange> traverseInterfaceHierarchy(final EList<Interface> interfaces, final VURI vuri) {

        final EList<Interface> traversedInterfaces = new BasicEList<Interface>();
        final EList<VitruviusChange> changes = new BasicEList<VitruviusChange>();

        // traverse interfaces. complexity is O(n�) but should be neglectable
        while (traversedInterfaces.size() < interfaces.size()) {
            for (final Interface inter : interfaces) {
                if (inter.getParentInterfaces__Interface().contains(inter)) {
                    throw new IllegalStateException("Interface cannot interherit from itself");
                }
                if (traversedInterfaces.containsAll(inter.getParentInterfaces__Interface())
                        && !traversedInterfaces.contains(inter)) {
                    List<EChange> interfaceChanges = PcmChangeBuildHelper.createChangeFromInterface(inter);
                    changes.add(encapsulateChanges(interfaceChanges, vuri));
                    traversedInterfaces.add(inter);
                }
            }
        }

        return changes;
    }

    
}
