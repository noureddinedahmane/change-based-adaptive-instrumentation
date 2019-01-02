package tools.vitruv.applications.pcmjava.reconstructionintegration.composite;

import static tools.vitruv.applications.pcmjava.reconstructionintegration.util.PcmChangeBuildHelper.*;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.Connector;
import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Role;
import org.palladiosimulator.pcm.system.System;

import tools.vitruv.framework.change.description.VitruviusChange;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;
import tools.vitruv.applications.pcmjava.reconstructionintegration.util.PcmChangeBuildHelper;
import tools.vitruv.extensions.constructionsimulation.traversal.EMFTraversalStrategy;
import tools.vitruv.framework.change.description.CompositeContainerChange;
import tools.vitruv.framework.change.description.VitruviusChangeFactory;

/**
 * This base class provides common methods used for traversing composed PCM entities that derive
 * from ComposedProvidingRequiringEntity
 *
 * It respects the ProvidedRolesMustBeBound-constraint and makes the same assumptions for
 * RequiredRoles.
 *
 * This demands a post-ordered depth-first traversal strategy.
 *
 * @author Sven Leonhardt
 *
 */
public abstract class ComposedEntitiesTraversalStrategy extends EMFTraversalStrategy {

    protected EList<VitruviusChange> changeList;
    private final EList<ComposedProvidingRequiringEntity> traversedEntities = new BasicEList<ComposedProvidingRequiringEntity>();
    protected VURI vuri;

    /**
     * Recursive method implementing a post-order depth-first search.
     *
     * @param entity
     *            : the node to be traversed
     */
    protected void traverseComposedEntity(final ComposedProvidingRequiringEntity entity) {

        // found a confluent node, which has already been traversed? => nothing to do
        if (this.traversedEntities.contains(entity)) {
            return;
        }

        // Child nodes available?
        for (final AssemblyContext context : entity.getAssemblyContexts__ComposedStructure()) {

            // recursion
            final RepositoryComponent nextEntity = context.getEncapsulatedComponent__AssemblyContext();
            // BasicComponents are already traversed
            if (!(nextEntity instanceof BasicComponent) && !(entity instanceof System)) {
                this.traverseComposedEntity((ComposedProvidingRequiringEntity) nextEntity);
            }
        }

        // traverse context
        this.traverseAssemblyContexts(entity);

        // traverse roles and delegations
        this.traverseRolesAndDelegations(entity);

        // traverse assembly connectors
        this.traverseAssemblyConnectors(entity);

        // remember the already traversed entities
        this.traversedEntities.add(entity);

    }

    /**
     * traverses the assembly contexts.
     *
     * @param entity
     *            containing the contexts
     */
    private void traverseAssemblyContexts(final ComposedProvidingRequiringEntity entity) {

        for (final AssemblyContext context : entity.getAssemblyContexts__ComposedStructure()) {
            final List<EChange> assemblyContextChanges = PcmChangeBuildHelper.createChangeFromAssemblyContext(context);
            this.addChange(encapsulateChanges(assemblyContextChanges, this.vuri), this.changeList);
        }

    }

    /**
     * traverses roles and delegations with respect to the AllRolesMustBeBound constraint for
     * composed entities.
     *
     * @param entity
     *            containing the roles and delegations
     */
    private void traverseRolesAndDelegations(final ComposedProvidingRequiringEntity entity) {

        // roleDelegationChanges should look like this: ((Role)(Delegation)+)*
        final EList<EChange> roleDelegationChanges = PcmChangeBuildHelper.createChangesFromRolesAndDelegations(entity);

        // last element a role?
        if (roleDelegationChanges.size() > 0) {
            final InsertEReference<?,?> lastChange = (InsertEReference<?,?>) roleDelegationChanges
                    .get(roleDelegationChanges.size() - 1);
            if (lastChange.getNewValue() instanceof Role) {
                throw new IllegalArgumentException("The role " + ((Role) lastChange.getNewValue()).getEntityName()
                        + " does not have a delegation connector");
            }
        }

        CompositeContainerChange compChange = null;

        for (int i = 0; i < roleDelegationChanges.size(); i++) {

            // set the role and the first delegation connector as composite change
            @SuppressWarnings("unchecked")
            final InsertEReference<EObject, ?> change = (InsertEReference<EObject, ?>) roleDelegationChanges.get(i);
            if (change.getNewValue() instanceof Role) {
                compChange = VitruviusChangeFactory.getInstance().createCompositeContainerChange();
                compChange.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(roleDelegationChanges.get(i), this.vuri));
                compChange.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(roleDelegationChanges.get(i + 1), this.vuri));
                this.addChange(compChange, this.changeList);
                i++;
            } else {
                this.addChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(roleDelegationChanges.get(i), this.vuri), this.changeList);
            }

        }

    }

    /**
     * traverses assembly connectors.
     *
     * @param entity
     *            containing the connectors
     */
    private void traverseAssemblyConnectors(final ComposedProvidingRequiringEntity entity) {

        for (final Connector connector : entity.getConnectors__ComposedStructure()) {

            if (connector instanceof AssemblyConnector) {
                final List<EChange> assemblyConnectorChanges = PcmChangeBuildHelper.createChangeFromConnector(connector);
                this.addChange(PcmChangeBuildHelper.encapsulateChanges(assemblyConnectorChanges, this.vuri), this.changeList);
            }

        }

    }

}
