package tools.vitruv.applications.pcmjava.reconstructionintegration.tests.pcmintegrationtest;

import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.CompositionFactory;
import org.palladiosimulator.pcm.core.composition.ProvidedDelegationConnector;
import org.palladiosimulator.pcm.core.composition.RequiredDelegationConnector;
import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.CompositeComponent;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RequiredRole;
import tools.vitruv.framework.change.description.CompositeContainerChange;
import tools.vitruv.framework.change.description.ConcreteChange;
import tools.vitruv.framework.change.description.VitruviusChange;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;
import tools.vitruv.framework.change.echange.root.InsertRootEObject;

@SuppressWarnings("all")
public class PcmModelBuilder {
  private List<VitruviusChange> changes;
  
  private EList<CompositeDataType> compositeDataTypes = new BasicEList<CompositeDataType>();
  
  private EList<CollectionDataType> collectionDataTypes = new BasicEList<CollectionDataType>();
  
  private EList<ComposedProvidingRequiringEntity> composedEntities = new BasicEList<ComposedProvidingRequiringEntity>();
  
  private Repository repo;
  
  public PcmModelBuilder(final List<VitruviusChange> changes) {
    this.changes = changes;
    this.repo = RepositoryFactory.eINSTANCE.createRepository();
    VitruviusChange _get = changes.get(0);
    final ConcreteChange repoChange = ((ConcreteChange) _get);
    EChange _get_1 = repoChange.getEChanges().get(0);
    final InsertRootEObject<EObject> rootChange = ((InsertRootEObject<EObject>) _get_1);
    EObject _newValue = rootChange.getNewValue();
    final Repository oldRepo = ((Repository) _newValue);
    this.repo.setEntityName(oldRepo.getEntityName());
    this.repo.setId(oldRepo.getId());
    this.repo.setRepositoryDescription(oldRepo.getRepositoryDescription());
    changes.remove(0);
  }
  
  public Repository createPCMModel() {
    final Consumer<VitruviusChange> _function = (VitruviusChange it) -> {
      this.createModelElement(it);
    };
    this.changes.forEach(_function);
    return this.repo;
  }
  
  public boolean createModelElement(final VitruviusChange change) {
    boolean _switchResult = false;
    boolean _matched = false;
    if (change instanceof CompositeContainerChange) {
      _matched=true;
      final Function1<VitruviusChange, Boolean> _function = (VitruviusChange it) -> {
        return Boolean.valueOf(this.createModelElement(it));
      };
      _switchResult = IterableExtensions.<VitruviusChange>forall(((CompositeContainerChange)change).getChanges(), _function);
    }
    if (!_matched) {
      if (change instanceof ConcreteChange) {
        _matched=true;
        _switchResult = this.createModelElementFromChange(((ConcreteChange)change));
      }
    }
    if (!_matched) {
      return false;
    }
    return _switchResult;
  }
  
  public String findDataTypeId(final CollectionDataType type) {
    final Function1<CollectionDataType, Boolean> _function = (CollectionDataType el) -> {
      return Boolean.valueOf(el.getId().equals(type.getId()));
    };
    final CollectionDataType data = IterableExtensions.<CollectionDataType>findFirst(this.collectionDataTypes, _function);
    return data.getId();
  }
  
  public String findDataTypeId(final CompositeDataType type) {
    final Function1<CompositeDataType, Boolean> _function = (CompositeDataType el) -> {
      return Boolean.valueOf(el.getId().equals(type.getId()));
    };
    final CompositeDataType data = IterableExtensions.<CompositeDataType>findFirst(this.compositeDataTypes, _function);
    return data.getId();
  }
  
  public boolean createModelElementFromChange(final ConcreteChange change) {
    boolean _xblockexpression = false;
    {
      EChange _get = change.getEChanges().get(0);
      final InsertEReference<EObject, EObject> innerChange = ((InsertEReference<EObject, EObject>) _get);
      final EObject newValue = innerChange.getNewValue();
      boolean _switchResult = false;
      boolean _matched = false;
      if (newValue instanceof CompositeDataType) {
        _matched=true;
        _switchResult = this.createCompositeDataType(((CompositeDataType)newValue));
      }
      if (!_matched) {
        if (newValue instanceof CollectionDataType) {
          _matched=true;
          _switchResult = this.createCollectionDataType(((CollectionDataType)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof InnerDeclaration) {
          _matched=true;
          _switchResult = this.createInnerDeclaration(((InnerDeclaration)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof BasicComponent) {
          _matched=true;
          _switchResult = this.createBasicComponent(((BasicComponent)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof CompositeComponent) {
          _matched=true;
          _switchResult = this.createCompositeComponent(((CompositeComponent)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof Interface) {
          _matched=true;
          _switchResult = this.createInterface(((Interface)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof OperationSignature) {
          _matched=true;
          _switchResult = this.createSignature(((OperationSignature)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof Parameter) {
          _matched=true;
          _switchResult = this.createParameter(((Parameter)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof OperationRequiredRole) {
          _matched=true;
          _switchResult = this.createRequiredRole(((OperationRequiredRole)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof OperationProvidedRole) {
          _matched=true;
          _switchResult = this.createProvidedRole(((OperationProvidedRole)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof AssemblyContext) {
          _matched=true;
          _switchResult = this.createAssemblyContext(((AssemblyContext)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof ProvidedDelegationConnector) {
          _matched=true;
          _switchResult = this.createProvidedDelegationConnector(((ProvidedDelegationConnector)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof RequiredDelegationConnector) {
          _matched=true;
          _switchResult = this.createRequiredDelegationConnector(((RequiredDelegationConnector)newValue));
        }
      }
      if (!_matched) {
        if (newValue instanceof AssemblyConnector) {
          _matched=true;
          _switchResult = this.createAssemblyConnector(((AssemblyConnector)newValue));
        }
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
  
  public boolean createAssemblyConnector(final AssemblyConnector newValue) {
    boolean _xblockexpression = false;
    {
      final AssemblyConnector connector = CompositionFactory.eINSTANCE.createAssemblyConnector();
      this.setValues(connector, newValue);
      final Function1<ComposedProvidingRequiringEntity, Boolean> _function = (ComposedProvidingRequiringEntity el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getParentStructure__Connector().getId()));
      };
      final ComposedProvidingRequiringEntity comp = IterableExtensions.<ComposedProvidingRequiringEntity>findFirst(this.composedEntities, _function);
      final Function1<AssemblyContext, Boolean> _function_1 = (AssemblyContext el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getProvidingAssemblyContext_AssemblyConnector().getId()));
      };
      final AssemblyContext provContext = IterableExtensions.<AssemblyContext>findFirst(comp.getAssemblyContexts__ComposedStructure(), _function_1);
      connector.setProvidingAssemblyContext_AssemblyConnector(provContext);
      final Function1<AssemblyContext, Boolean> _function_2 = (AssemblyContext el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getRequiringAssemblyContext_AssemblyConnector().getId()));
      };
      final AssemblyContext reqContext = IterableExtensions.<AssemblyContext>findFirst(comp.getAssemblyContexts__ComposedStructure(), _function_2);
      connector.setRequiringAssemblyContext_AssemblyConnector(reqContext);
      final Function1<ProvidedRole, Boolean> _function_3 = (ProvidedRole el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getProvidedRole_AssemblyConnector().getId()));
      };
      ProvidedRole _findFirst = IterableExtensions.<ProvidedRole>findFirst(provContext.getEncapsulatedComponent__AssemblyContext().getProvidedRoles_InterfaceProvidingEntity(), _function_3);
      final OperationProvidedRole provRole = ((OperationProvidedRole) _findFirst);
      connector.setProvidedRole_AssemblyConnector(provRole);
      final Function1<RequiredRole, Boolean> _function_4 = (RequiredRole el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getRequiredRole_AssemblyConnector().getId()));
      };
      RequiredRole _findFirst_1 = IterableExtensions.<RequiredRole>findFirst(reqContext.getEncapsulatedComponent__AssemblyContext().getRequiredRoles_InterfaceRequiringEntity(), _function_4);
      final OperationRequiredRole reqRole = ((OperationRequiredRole) _findFirst_1);
      connector.setRequiredRole_AssemblyConnector(reqRole);
      _xblockexpression = comp.getConnectors__ComposedStructure().add(connector);
    }
    return _xblockexpression;
  }
  
  public boolean createProvidedDelegationConnector(final ProvidedDelegationConnector newValue) {
    boolean _xblockexpression = false;
    {
      final ProvidedDelegationConnector connector = CompositionFactory.eINSTANCE.createProvidedDelegationConnector();
      this.setValues(connector, newValue);
      final Function1<ComposedProvidingRequiringEntity, Boolean> _function = (ComposedProvidingRequiringEntity el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getParentStructure__Connector().getId()));
      };
      final ComposedProvidingRequiringEntity comp = IterableExtensions.<ComposedProvidingRequiringEntity>findFirst(this.composedEntities, _function);
      final Function1<AssemblyContext, Boolean> _function_1 = (AssemblyContext el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getAssemblyContext_ProvidedDelegationConnector().getId()));
      };
      final AssemblyContext context = IterableExtensions.<AssemblyContext>findFirst(comp.getAssemblyContexts__ComposedStructure(), _function_1);
      connector.setAssemblyContext_ProvidedDelegationConnector(context);
      final Function1<RepositoryComponent, Boolean> _function_2 = (RepositoryComponent el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getInnerProvidedRole_ProvidedDelegationConnector().getProvidingEntity_ProvidedRole().getId()));
      };
      final RepositoryComponent innerComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_2);
      final Function1<ProvidedRole, Boolean> _function_3 = (ProvidedRole el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getInnerProvidedRole_ProvidedDelegationConnector().getId()));
      };
      ProvidedRole _findFirst = IterableExtensions.<ProvidedRole>findFirst(innerComp.getProvidedRoles_InterfaceProvidingEntity(), _function_3);
      final OperationProvidedRole innerProvRole = ((OperationProvidedRole) _findFirst);
      connector.setInnerProvidedRole_ProvidedDelegationConnector(innerProvRole);
      final Function1<RepositoryComponent, Boolean> _function_4 = (RepositoryComponent el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getOuterProvidedRole_ProvidedDelegationConnector().getProvidingEntity_ProvidedRole().getId()));
      };
      final RepositoryComponent outerComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_4);
      final Function1<ProvidedRole, Boolean> _function_5 = (ProvidedRole el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getOuterProvidedRole_ProvidedDelegationConnector().getId()));
      };
      ProvidedRole _findFirst_1 = IterableExtensions.<ProvidedRole>findFirst(outerComp.getProvidedRoles_InterfaceProvidingEntity(), _function_5);
      final OperationProvidedRole outerProvRole = ((OperationProvidedRole) _findFirst_1);
      connector.setOuterProvidedRole_ProvidedDelegationConnector(outerProvRole);
      _xblockexpression = comp.getConnectors__ComposedStructure().add(connector);
    }
    return _xblockexpression;
  }
  
  public boolean createRequiredDelegationConnector(final RequiredDelegationConnector newValue) {
    boolean _xblockexpression = false;
    {
      final RequiredDelegationConnector connector = CompositionFactory.eINSTANCE.createRequiredDelegationConnector();
      this.setValues(connector, newValue);
      final Function1<ComposedProvidingRequiringEntity, Boolean> _function = (ComposedProvidingRequiringEntity el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getParentStructure__Connector().getId()));
      };
      final ComposedProvidingRequiringEntity comp = IterableExtensions.<ComposedProvidingRequiringEntity>findFirst(this.composedEntities, _function);
      final Function1<AssemblyContext, Boolean> _function_1 = (AssemblyContext el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getAssemblyContext_RequiredDelegationConnector().getId()));
      };
      final AssemblyContext context = IterableExtensions.<AssemblyContext>findFirst(comp.getAssemblyContexts__ComposedStructure(), _function_1);
      connector.setAssemblyContext_RequiredDelegationConnector(context);
      final Function1<RepositoryComponent, Boolean> _function_2 = (RepositoryComponent el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getInnerRequiredRole_RequiredDelegationConnector().getRequiringEntity_RequiredRole().getId()));
      };
      final RepositoryComponent innerComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_2);
      final Function1<RequiredRole, Boolean> _function_3 = (RequiredRole el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getInnerRequiredRole_RequiredDelegationConnector().getId()));
      };
      RequiredRole _findFirst = IterableExtensions.<RequiredRole>findFirst(innerComp.getRequiredRoles_InterfaceRequiringEntity(), _function_3);
      final OperationRequiredRole innerReqRole = ((OperationRequiredRole) _findFirst);
      connector.setInnerRequiredRole_RequiredDelegationConnector(innerReqRole);
      final Function1<RepositoryComponent, Boolean> _function_4 = (RepositoryComponent el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getOuterRequiredRole_RequiredDelegationConnector().getRequiringEntity_RequiredRole().getId()));
      };
      final RepositoryComponent outerComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_4);
      final Function1<RequiredRole, Boolean> _function_5 = (RequiredRole el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getOuterRequiredRole_RequiredDelegationConnector().getId()));
      };
      RequiredRole _findFirst_1 = IterableExtensions.<RequiredRole>findFirst(outerComp.getRequiredRoles_InterfaceRequiringEntity(), _function_5);
      final OperationRequiredRole outerReqRole = ((OperationRequiredRole) _findFirst_1);
      connector.setOuterRequiredRole_RequiredDelegationConnector(outerReqRole);
      _xblockexpression = comp.getConnectors__ComposedStructure().add(connector);
    }
    return _xblockexpression;
  }
  
  public boolean createAssemblyContext(final AssemblyContext newValue) {
    boolean _xblockexpression = false;
    {
      final AssemblyContext context = CompositionFactory.eINSTANCE.createAssemblyContext();
      this.setValues(context, newValue);
      final Function1<RepositoryComponent, Boolean> _function = (RepositoryComponent el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getEncapsulatedComponent__AssemblyContext().getId()));
      };
      final RepositoryComponent encapsComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function);
      context.setEncapsulatedComponent__AssemblyContext(encapsComp);
      final Function1<RepositoryComponent, Boolean> _function_1 = (RepositoryComponent el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getParentStructure__AssemblyContext().getId()));
      };
      RepositoryComponent _findFirst = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_1);
      final ComposedProvidingRequiringEntity compEntity = ((ComposedProvidingRequiringEntity) _findFirst);
      _xblockexpression = compEntity.getAssemblyContexts__ComposedStructure().add(context);
    }
    return _xblockexpression;
  }
  
  public boolean createInnerDeclaration(final InnerDeclaration newValue) {
    boolean _xblockexpression = false;
    {
      final InnerDeclaration dec = RepositoryFactory.eINSTANCE.createInnerDeclaration();
      dec.setEntityName(newValue.getEntityName());
      DataType _datatype_InnerDeclaration = newValue.getDatatype_InnerDeclaration();
      if ((_datatype_InnerDeclaration instanceof CollectionDataType)) {
        DataType _datatype_InnerDeclaration_1 = newValue.getDatatype_InnerDeclaration();
        final String dataTypeId = this.findDataTypeId(((CollectionDataType) _datatype_InnerDeclaration_1));
        final Function1<CollectionDataType, Boolean> _function = (CollectionDataType el) -> {
          return Boolean.valueOf(el.getId().equals(dataTypeId));
        };
        final CollectionDataType data = IterableExtensions.<CollectionDataType>findFirst(this.collectionDataTypes, _function);
        dec.setDatatype_InnerDeclaration(data);
      } else {
        DataType _datatype_InnerDeclaration_2 = newValue.getDatatype_InnerDeclaration();
        if ((_datatype_InnerDeclaration_2 instanceof CompositeDataType)) {
          DataType _datatype_InnerDeclaration_3 = newValue.getDatatype_InnerDeclaration();
          final String dataTypeId_1 = this.findDataTypeId(((CompositeDataType) _datatype_InnerDeclaration_3));
          final Function1<CompositeDataType, Boolean> _function_1 = (CompositeDataType el) -> {
            return Boolean.valueOf(el.getId().equals(dataTypeId_1));
          };
          final CompositeDataType data_1 = IterableExtensions.<CompositeDataType>findFirst(this.compositeDataTypes, _function_1);
          dec.setDatatype_InnerDeclaration(data_1);
        } else {
          dec.setDatatype_InnerDeclaration(newValue.getDatatype_InnerDeclaration());
        }
      }
      final Function1<CompositeDataType, Boolean> _function_2 = (CompositeDataType el) -> {
        return Boolean.valueOf(el.getId().equals(newValue.getCompositeDataType_InnerDeclaration().getId()));
      };
      final CompositeDataType parent = IterableExtensions.<CompositeDataType>findFirst(this.compositeDataTypes, _function_2);
      _xblockexpression = parent.getInnerDeclaration_CompositeDataType().add(dec);
    }
    return _xblockexpression;
  }
  
  public boolean createCollectionDataType(final CollectionDataType newValue) {
    boolean _xblockexpression = false;
    {
      final CollectionDataType data = RepositoryFactory.eINSTANCE.createCollectionDataType();
      this.setValues(data, newValue);
      DataType _innerType_CollectionDataType = newValue.getInnerType_CollectionDataType();
      if ((_innerType_CollectionDataType instanceof CollectionDataType)) {
        DataType _innerType_CollectionDataType_1 = newValue.getInnerType_CollectionDataType();
        final String dataTypeId = this.findDataTypeId(((CollectionDataType) _innerType_CollectionDataType_1));
        final Function1<CollectionDataType, Boolean> _function = (CollectionDataType el) -> {
          return Boolean.valueOf(el.getId().equals(dataTypeId));
        };
        final CollectionDataType data1 = IterableExtensions.<CollectionDataType>findFirst(this.collectionDataTypes, _function);
        data.setInnerType_CollectionDataType(data1);
      } else {
        DataType _innerType_CollectionDataType_2 = newValue.getInnerType_CollectionDataType();
        if ((_innerType_CollectionDataType_2 instanceof CompositeDataType)) {
          DataType _innerType_CollectionDataType_3 = newValue.getInnerType_CollectionDataType();
          final String dataTypeId_1 = this.findDataTypeId(((CompositeDataType) _innerType_CollectionDataType_3));
          final Function1<CompositeDataType, Boolean> _function_1 = (CompositeDataType el) -> {
            return Boolean.valueOf(el.getId().equals(dataTypeId_1));
          };
          final CompositeDataType data1_1 = IterableExtensions.<CompositeDataType>findFirst(this.compositeDataTypes, _function_1);
          data.setInnerType_CollectionDataType(data1_1);
        } else {
          data.setInnerType_CollectionDataType(newValue.getInnerType_CollectionDataType());
        }
      }
      this.repo.getDataTypes__Repository().add(data);
      _xblockexpression = this.collectionDataTypes.add(data);
    }
    return _xblockexpression;
  }
  
  public boolean createCompositeDataType(final CompositeDataType newValue) {
    boolean _xblockexpression = false;
    {
      final CompositeDataType data = RepositoryFactory.eINSTANCE.createCompositeDataType();
      data.setId(newValue.getId());
      data.setEntityName(newValue.getEntityName());
      this.repo.getDataTypes__Repository().add(data);
      _xblockexpression = this.compositeDataTypes.add(data);
    }
    return _xblockexpression;
  }
  
  public boolean createProvidedRole(final OperationProvidedRole newValue) {
    boolean _xblockexpression = false;
    {
      final OperationProvidedRole role = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
      this.setValues(role, newValue);
      final Function1<Interface, Boolean> _function = (Interface iface) -> {
        return Boolean.valueOf(iface.getId().equals(newValue.getProvidedInterface__OperationProvidedRole().getId()));
      };
      Interface _findFirst = IterableExtensions.<Interface>findFirst(this.repo.getInterfaces__Repository(), _function);
      final OperationInterface opIf = ((OperationInterface) _findFirst);
      role.setProvidedInterface__OperationProvidedRole(opIf);
      final Function1<RepositoryComponent, Boolean> _function_1 = (RepositoryComponent comp) -> {
        return Boolean.valueOf(comp.getId().equals(newValue.getProvidingEntity_ProvidedRole().getId()));
      };
      final RepositoryComponent reqComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_1);
      _xblockexpression = reqComp.getProvidedRoles_InterfaceProvidingEntity().add(role);
    }
    return _xblockexpression;
  }
  
  public boolean createRequiredRole(final OperationRequiredRole newValue) {
    boolean _xblockexpression = false;
    {
      final OperationRequiredRole role = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
      this.setValues(role, newValue);
      final Function1<Interface, Boolean> _function = (Interface iface) -> {
        return Boolean.valueOf(iface.getId().equals(newValue.getRequiredInterface__OperationRequiredRole().getId()));
      };
      Interface _findFirst = IterableExtensions.<Interface>findFirst(this.repo.getInterfaces__Repository(), _function);
      final OperationInterface opIf = ((OperationInterface) _findFirst);
      role.setRequiredInterface__OperationRequiredRole(opIf);
      final Function1<RepositoryComponent, Boolean> _function_1 = (RepositoryComponent comp) -> {
        return Boolean.valueOf(comp.getId().equals(newValue.getRequiringEntity_RequiredRole().getId()));
      };
      final RepositoryComponent reqComp = IterableExtensions.<RepositoryComponent>findFirst(this.repo.getComponents__Repository(), _function_1);
      _xblockexpression = reqComp.getRequiredRoles_InterfaceRequiringEntity().add(role);
    }
    return _xblockexpression;
  }
  
  public boolean createParameter(final Parameter newValue) {
    boolean _xblockexpression = false;
    {
      final Parameter par = RepositoryFactory.eINSTANCE.createParameter();
      par.setParameterName(newValue.getParameterName());
      DataType _dataType__Parameter = newValue.getDataType__Parameter();
      if ((_dataType__Parameter instanceof CollectionDataType)) {
        DataType _dataType__Parameter_1 = newValue.getDataType__Parameter();
        final String dataTypeId = this.findDataTypeId(((CollectionDataType) _dataType__Parameter_1));
        final Function1<CollectionDataType, Boolean> _function = (CollectionDataType el) -> {
          return Boolean.valueOf(el.getId().equals(dataTypeId));
        };
        final CollectionDataType data1 = IterableExtensions.<CollectionDataType>findFirst(this.collectionDataTypes, _function);
        par.setDataType__Parameter(data1);
      } else {
        DataType _dataType__Parameter_2 = newValue.getDataType__Parameter();
        if ((_dataType__Parameter_2 instanceof CompositeDataType)) {
          DataType _dataType__Parameter_3 = newValue.getDataType__Parameter();
          final String dataTypeId_1 = this.findDataTypeId(((CompositeDataType) _dataType__Parameter_3));
          final Function1<CompositeDataType, Boolean> _function_1 = (CompositeDataType el) -> {
            return Boolean.valueOf(el.getId().equals(dataTypeId_1));
          };
          final CompositeDataType data1_1 = IterableExtensions.<CompositeDataType>findFirst(this.compositeDataTypes, _function_1);
          par.setDataType__Parameter(data1_1);
        } else {
          par.setDataType__Parameter(newValue.getDataType__Parameter());
        }
      }
      par.setModifier__Parameter(newValue.getModifier__Parameter());
      final OperationSignature oldSig = newValue.getOperationSignature__Parameter();
      final OperationInterface oldIf = oldSig.getInterface__OperationSignature();
      final Function1<Interface, Boolean> _function_2 = (Interface iface2) -> {
        return Boolean.valueOf(iface2.getId().equals(oldIf.getId()));
      };
      Interface _findFirst = IterableExtensions.<Interface>findFirst(this.repo.getInterfaces__Repository(), _function_2);
      final OperationInterface opIf = ((OperationInterface) _findFirst);
      final Function1<OperationSignature, Boolean> _function_3 = (OperationSignature sig2) -> {
        return Boolean.valueOf(sig2.getId().equals(oldSig.getId()));
      };
      OperationSignature _findFirst_1 = IterableExtensions.<OperationSignature>findFirst(opIf.getSignatures__OperationInterface(), _function_3);
      final OperationSignature opSig = ((OperationSignature) _findFirst_1);
      _xblockexpression = opSig.getParameters__OperationSignature().add(par);
    }
    return _xblockexpression;
  }
  
  public boolean createSignature(final OperationSignature newValue) {
    boolean _xblockexpression = false;
    {
      final OperationSignature sig = RepositoryFactory.eINSTANCE.createOperationSignature();
      this.setValues(sig, newValue);
      DataType _returnType__OperationSignature = newValue.getReturnType__OperationSignature();
      if ((_returnType__OperationSignature instanceof CollectionDataType)) {
        DataType _returnType__OperationSignature_1 = newValue.getReturnType__OperationSignature();
        final String dataTypeId = this.findDataTypeId(((CollectionDataType) _returnType__OperationSignature_1));
        final Function1<CollectionDataType, Boolean> _function = (CollectionDataType el) -> {
          return Boolean.valueOf(el.getId().equals(dataTypeId));
        };
        final CollectionDataType data1 = IterableExtensions.<CollectionDataType>findFirst(this.collectionDataTypes, _function);
        sig.setReturnType__OperationSignature(data1);
      } else {
        DataType _returnType__OperationSignature_2 = newValue.getReturnType__OperationSignature();
        if ((_returnType__OperationSignature_2 instanceof CompositeDataType)) {
          DataType _returnType__OperationSignature_3 = newValue.getReturnType__OperationSignature();
          final String dataTypeId_1 = this.findDataTypeId(((CompositeDataType) _returnType__OperationSignature_3));
          final Function1<CompositeDataType, Boolean> _function_1 = (CompositeDataType el) -> {
            return Boolean.valueOf(el.getId().equals(dataTypeId_1));
          };
          final CompositeDataType data1_1 = IterableExtensions.<CompositeDataType>findFirst(this.compositeDataTypes, _function_1);
          sig.setReturnType__OperationSignature(data1_1);
        } else {
          sig.setReturnType__OperationSignature(newValue.getReturnType__OperationSignature());
        }
      }
      final Function1<Interface, Boolean> _function_2 = (Interface iface) -> {
        return Boolean.valueOf(iface.getId().equals(newValue.getInterface__OperationSignature().getId()));
      };
      Interface _findFirst = IterableExtensions.<Interface>findFirst(this.repo.getInterfaces__Repository(), _function_2);
      final OperationInterface opIf = ((OperationInterface) _findFirst);
      _xblockexpression = opIf.getSignatures__OperationInterface().add(sig);
    }
    return _xblockexpression;
  }
  
  public boolean createInterface(final Interface newValue) {
    boolean _xblockexpression = false;
    {
      final OperationInterface interf = RepositoryFactory.eINSTANCE.createOperationInterface();
      this.setValues(interf, newValue);
      int _length = ((Object[])Conversions.unwrapArray(newValue.getParentInterfaces__Interface(), Object.class)).length;
      ExclusiveRange _doubleDotLessThan = new ExclusiveRange(0, _length, true);
      for (final Integer i : _doubleDotLessThan) {
        {
          final String id = newValue.getParentInterfaces__Interface().get((i).intValue()).getId();
          final Function1<Interface, Boolean> _function = (Interface el) -> {
            return Boolean.valueOf(el.getId().equals(id));
          };
          interf.getParentInterfaces__Interface().add(IterableExtensions.<Interface>findFirst(this.repo.getInterfaces__Repository(), _function));
        }
      }
      _xblockexpression = this.repo.getInterfaces__Repository().add(interf);
    }
    return _xblockexpression;
  }
  
  public boolean createBasicComponent(final BasicComponent newValue) {
    boolean _xblockexpression = false;
    {
      final BasicComponent comp = RepositoryFactory.eINSTANCE.createBasicComponent();
      this.setValues(comp, newValue);
      _xblockexpression = this.repo.getComponents__Repository().add(comp);
    }
    return _xblockexpression;
  }
  
  public boolean createCompositeComponent(final CompositeComponent newValue) {
    boolean _xblockexpression = false;
    {
      final CompositeComponent comp = RepositoryFactory.eINSTANCE.createCompositeComponent();
      this.setValues(comp, newValue);
      this.repo.getComponents__Repository().add(comp);
      _xblockexpression = this.composedEntities.add(comp);
    }
    return _xblockexpression;
  }
  
  public void setValues(final Entity createdElement, final Entity newValue) {
    createdElement.setEntityName(newValue.getEntityName());
    createdElement.setId(newValue.getId());
  }
}
