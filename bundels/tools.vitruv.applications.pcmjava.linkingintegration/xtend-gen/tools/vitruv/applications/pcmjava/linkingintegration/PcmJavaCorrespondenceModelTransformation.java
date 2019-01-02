package tools.vitruv.applications.pcmjava.linkingintegration;

import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.members.Constructor;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.parameters.Parametrizable;
import org.emftext.language.java.types.Type;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;
import org.somox.sourcecodedecorator.ComponentImplementingClassesLink;
import org.somox.sourcecodedecorator.DataTypeSourceCodeLink;
import org.somox.sourcecodedecorator.InnerDatatypeSourceCodeLink;
import org.somox.sourcecodedecorator.InterfaceSourceCodeLink;
import org.somox.sourcecodedecorator.MethodLevelSourceCodeLink;
import org.somox.sourcecodedecorator.impl.SourceCodeDecoratorRepositoryImpl;
import tools.vitruv.applications.pcmjava.linkingintegration.CorrespondenceTypeDeciding;
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaIntegrationExtending;
import tools.vitruv.applications.pcmjava.linkingintegration.ResourceLoadingHelper;
import tools.vitruv.extensions.integration.correspondence.integration.IntegrationCorrespondence;
import tools.vitruv.extensions.integration.correspondence.util.IntegrationCorrespondenceHelper;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.framework.correspondence.GenericCorrespondenceModel;
import tools.vitruv.framework.util.VitruviusConstants;
import tools.vitruv.framework.util.bridges.CollectionBridge;
import tools.vitruv.framework.util.bridges.EMFBridge;
import tools.vitruv.framework.util.bridges.EclipseBridge;
import tools.vitruv.framework.vsum.InternalVirtualModel;

/**
 * Class that creates correspondences between PCM and JaMopp model elements.
 * 
 * @author originally by Benjamin Hettwer, changed for thesis by Frederik Petersen
 */
@SuppressWarnings("all")
public class PcmJavaCorrespondenceModelTransformation {
  private HashSet<String> existingEntries = new HashSet<String>();
  
  protected Logger logger = Logger.getRootLogger();
  
  private String scdmPath;
  
  private String pcmPath;
  
  private List<IPath> jamoppPaths;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private Resource scdm;
  
  private Resource pcm;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private List<Resource> jaMoppResources;
  
  private Repository pcmRepo;
  
  @Accessors(AccessorType.PUBLIC_GETTER)
  private CorrespondenceModel cInstance;
  
  private InternalVirtualModel vmodel;
  
  private Set<org.emftext.language.java.containers.Package> packages;
  
  private org.emftext.language.java.containers.Package rootPackage;
  
  private IPath projectBase;
  
  public PcmJavaCorrespondenceModelTransformation(final String scdmPath, final String pcmPath, final List<IPath> jamoppPaths, final InternalVirtualModel vsum, final IPath projectBase) {
    this.cInstance = vsum.getCorrespondenceModel();
    this.scdmPath = scdmPath;
    this.pcmPath = pcmPath;
    this.jamoppPaths = jamoppPaths;
    HashSet<org.emftext.language.java.containers.Package> _hashSet = new HashSet<org.emftext.language.java.containers.Package>();
    this.packages = _hashSet;
    this.projectBase = projectBase;
    this.vmodel = vsum;
    this.logger.setLevel(Level.ALL);
  }
  
  public void createCorrespondences() {
    this.prepareTransformation();
    this.createPCMtoJaMoppCorrespondences();
  }
  
  /**
   * Loads PCM, SDCDM and JaMoPP resources.
   */
  private void prepareTransformation() {
    try {
      this.scdm = ResourceLoadingHelper.loadSCDMResource(this.scdmPath);
      this.pcm = ResourceLoadingHelper.loadPCMRepositoryResource(this.pcmPath);
      EObject _get = this.pcm.getContents().get(0);
      this.pcmRepo = ((Repository) _get);
      final Function1<IPath, File> _function = (IPath path) -> {
        return path.toFile();
      };
      this.jaMoppResources = ResourceLoadingHelper.loadJaMoPPResourceSet(ListExtensions.<IPath, File>map(this.jamoppPaths, _function));
      final Consumer<Resource> _function_1 = (Resource it) -> {
        Iterables.<org.emftext.language.java.containers.Package>addAll(this.packages, Iterables.<org.emftext.language.java.containers.Package>filter(it.getContents(), org.emftext.language.java.containers.Package.class));
      };
      this.jaMoppResources.forEach(_function_1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Creates the following correspondence hierarchy from the mappings
   * given by the SoMoX SourceCodeDecorator model. Additionally information
   * of the jaMoPP ResourceSet is used as well.
   * 
   * 
   * 1. PCMRepo <-> JaMopp Root-Package Correspondence
   * 		2. RepositoryComponent <-> Package correspondences
   * 		3. RepositoryComponent <-> CompilationUnit Correspondences
   * 		4. RepositoryComponent <-> jaMopp Class
   * 		5. PCM Interface <-> CompilationUnit Correspondences
   * 		6. PCM Interface <-> jaMopp Type (Class or Interface) Correspondences
   * 			7. OperationSignature <-> Method Correspondences
   * 				8. PCM Parameter <-> Ordinary Parameter Correspondences
   *  	9. PCM DataType <-> CompUnit correspondence
   * 		10. PCM DataType <-> JaMopp Type correspondence
   * 			11.PCM InnerDeclaration <-> JaMopp Field correspondence
   */
  private void createPCMtoJaMoppCorrespondences() {
    EObject _get = this.scdm.getContents().get(0);
    SourceCodeDecoratorRepositoryImpl scdmRepo = ((SourceCodeDecoratorRepositoryImpl) _get);
    this.createRepoPackageCorrespondence();
    final Consumer<ComponentImplementingClassesLink> _function = (ComponentImplementingClassesLink it) -> {
      this.createComponentClassCorrespondence(it);
    };
    scdmRepo.getComponentImplementingClassesLink().forEach(_function);
    final Consumer<InterfaceSourceCodeLink> _function_1 = (InterfaceSourceCodeLink it) -> {
      this.createInterfaceCorrespondence(it);
    };
    scdmRepo.getInterfaceSourceCodeLink().forEach(_function_1);
    final Consumer<MethodLevelSourceCodeLink> _function_2 = (MethodLevelSourceCodeLink it) -> {
      this.createMethodCorrespondence(it);
    };
    scdmRepo.getMethodLevelSourceCodeLink().forEach(_function_2);
    final Consumer<DataTypeSourceCodeLink> _function_3 = (DataTypeSourceCodeLink it) -> {
      this.createDataTypeCorrespondence(it);
    };
    scdmRepo.getDataTypeSourceCodeLink().forEach(_function_3);
    this.findAndExecuteAfterTransformationExtensions();
    this.vmodel.save();
  }
  
  private void findAndExecuteAfterTransformationExtensions() {
    final Consumer<PcmJavaIntegrationExtending> _function = (PcmJavaIntegrationExtending it) -> {
      it.afterBasicTransformations(this);
    };
    EclipseBridge.<PcmJavaIntegrationExtending>getRegisteredExtensions(PcmJavaIntegrationExtending.ID, 
      VitruviusConstants.getExtensionPropertyName(), PcmJavaIntegrationExtending.class).forEach(_function);
  }
  
  private Correspondence createRepoPackageCorrespondence() {
    return this.addCorrespondence(this.pcmRepo, this.getRootPackage());
  }
  
  private void createComponentClassCorrespondence(final ComponentImplementingClassesLink componentClassLink) {
    RepositoryComponent pcmComponent = componentClassLink.getComponent();
    if ((pcmComponent instanceof BasicComponent)) {
      EList<ConcreteClassifier> _implementingClasses = componentClassLink.getImplementingClasses();
      for (final ConcreteClassifier implementingClass : _implementingClasses) {
        {
          final ConcreteClassifier desreolvedClassInSCDM = this.<ConcreteClassifier>deresolveIfNesessary(implementingClass);
          ConcreteClassifier jamoppClass = this.<ConcreteClassifier>resolveJaMoppProxy(desreolvedClassInSCDM);
          final org.emftext.language.java.containers.Package package_ = this.getPackageForCommentable(jamoppClass);
          final Repository deresolvedPcmRepo = this.<Repository>deresolveIfNesessary(this.pcmRepo);
          final org.emftext.language.java.containers.Package deresolvedRootPackage = this.<org.emftext.language.java.containers.Package>deresolveIfNesessary(this.getRootPackage());
          Correspondence parentRepoPackageCorr = IterableUtil.<Set<Correspondence>, Correspondence>claimOne(CorrespondenceModelUtil.<Correspondence>getCorrespondencesBetweenEObjects(this.cInstance, CollectionBridge.<EObject>toSet(deresolvedPcmRepo), 
            CollectionBridge.<EObject>toSet(deresolvedRootPackage)));
          this.addCorrespondence(pcmComponent, package_, parentRepoPackageCorr);
          this.addCorrespondence(pcmComponent, jamoppClass.getContainingCompilationUnit(), parentRepoPackageCorr);
          this.addCorrespondence(pcmComponent, jamoppClass, parentRepoPackageCorr);
        }
      }
    }
  }
  
  private Correspondence createInterfaceCorrespondence(final InterfaceSourceCodeLink interfaceLink) {
    Correspondence _xblockexpression = null;
    {
      Interface pcmInterface = interfaceLink.getInterface();
      ConcreteClassifier _resolveJaMoppProxy = this.<ConcreteClassifier>resolveJaMoppProxy(interfaceLink.getGastClass());
      Type jamoppType = ((Type) _resolveJaMoppProxy);
      final Repository deresolvedPcmRepo = this.<Repository>deresolveIfNesessary(this.pcmRepo);
      final org.emftext.language.java.containers.Package deresolvedRootPackage = this.<org.emftext.language.java.containers.Package>deresolveIfNesessary(this.getRootPackage());
      Correspondence parentCorrespondence = IterableUtil.<Set<Correspondence>, Correspondence>claimOne(CorrespondenceModelUtil.<Correspondence>getCorrespondencesBetweenEObjects(this.cInstance, CollectionBridge.<EObject>toSet(deresolvedPcmRepo), 
        CollectionBridge.<EObject>toSet(deresolvedRootPackage)));
      this.addCorrespondence(pcmInterface, jamoppType.getContainingCompilationUnit(), parentCorrespondence);
      _xblockexpression = this.addCorrespondence(pcmInterface, jamoppType, parentCorrespondence);
    }
    return _xblockexpression;
  }
  
  private void createMethodCorrespondence(final MethodLevelSourceCodeLink methodLink) {
    final Member jamoppFunction = this.<Member>resolveJaMoppProxy(methodLink.getFunction());
    Commentable jamoppCommentable = null;
    Parametrizable jamoppParametrizable = null;
    if ((jamoppFunction instanceof Method)) {
      jamoppCommentable = jamoppFunction;
      jamoppParametrizable = ((Parametrizable)jamoppFunction);
    } else {
      if ((jamoppFunction instanceof Constructor)) {
        jamoppCommentable = jamoppFunction;
        jamoppParametrizable = ((Parametrizable)jamoppFunction);
      } else {
        throw new RuntimeException("Unexpected type in method level source code link.");
      }
    }
    Signature _operation = methodLink.getOperation();
    OperationSignature pcmMethod = ((OperationSignature) _operation);
    ConcreteClassifier jamoppInterface = jamoppCommentable.getContainingConcreteClassifier();
    OperationInterface pcmInterface = pcmMethod.getInterface__OperationSignature();
    final OperationInterface deresolvedPcmInterface = this.<OperationInterface>deresolveIfNesessary(pcmInterface);
    final ConcreteClassifier deresolvedJamoppInterface = this.<ConcreteClassifier>deresolveIfNesessary(jamoppInterface);
    Set<Correspondence> interfaceCorrespondence = CorrespondenceModelUtil.<Correspondence>getCorrespondencesBetweenEObjects(this.cInstance, CollectionBridge.<EObject>toSet(deresolvedPcmInterface), 
      CollectionBridge.<EObject>toSet(deresolvedJamoppInterface));
    boolean _isNullOrEmpty = IterableExtensions.isNullOrEmpty(interfaceCorrespondence);
    if (_isNullOrEmpty) {
      return;
    }
    final Set<Correspondence> _converted_interfaceCorrespondence = (Set<Correspondence>)interfaceCorrespondence;
    Correspondence methodCorrespondence = this.addCorrespondence(pcmMethod, jamoppFunction, ((Correspondence[])Conversions.unwrapArray(_converted_interfaceCorrespondence, Correspondence.class))[0]);
    EList<Parameter> _parameters__OperationSignature = pcmMethod.getParameters__OperationSignature();
    for (final Parameter pcmParam : _parameters__OperationSignature) {
      {
        final Function1<org.emftext.language.java.parameters.Parameter, Boolean> _function = (org.emftext.language.java.parameters.Parameter jp) -> {
          return Boolean.valueOf(jp.getName().equals(pcmParam.getParameterName()));
        };
        org.emftext.language.java.parameters.Parameter jamoppParam = IterableExtensions.<org.emftext.language.java.parameters.Parameter>findFirst(jamoppParametrizable.getParameters(), _function);
        if ((jamoppParam != null)) {
          this.addCorrespondence(pcmParam, ((OrdinaryParameter) jamoppParam), methodCorrespondence);
        }
      }
    }
  }
  
  private void createDataTypeCorrespondence(final DataTypeSourceCodeLink dataTypeLink) {
    DataType pcmDataType = dataTypeLink.getPcmDataType();
    Type _resolveJaMoppProxy = this.<Type>resolveJaMoppProxy(dataTypeLink.getJaMoPPType());
    Type jamoppType = ((Type) _resolveJaMoppProxy);
    final Repository deresolvedPcmRepo = this.<Repository>deresolveIfNesessary(this.pcmRepo);
    final org.emftext.language.java.containers.Package deresolvedRootPackage = this.<org.emftext.language.java.containers.Package>deresolveIfNesessary(this.getRootPackage());
    Correspondence parentCorrespondence = IterableUtil.<Set<Correspondence>, Correspondence>claimOne(CorrespondenceModelUtil.<Correspondence>getCorrespondencesBetweenEObjects(this.cInstance, CollectionBridge.<EObject>toSet(deresolvedPcmRepo), 
      CollectionBridge.<EObject>toSet(deresolvedRootPackage)));
    this.addCorrespondence(pcmDataType, jamoppType.getContainingCompilationUnit(), parentCorrespondence);
    Correspondence dataTypeCorrespondence = this.addCorrespondence(pcmDataType, jamoppType, parentCorrespondence);
    EList<InnerDatatypeSourceCodeLink> _innerDatatypeSourceCodeLink = dataTypeLink.getInnerDatatypeSourceCodeLink();
    boolean _tripleNotEquals = (_innerDatatypeSourceCodeLink != null);
    if (_tripleNotEquals) {
      EList<InnerDatatypeSourceCodeLink> _innerDatatypeSourceCodeLink_1 = dataTypeLink.getInnerDatatypeSourceCodeLink();
      for (final InnerDatatypeSourceCodeLink innerDataTypeLink : _innerDatatypeSourceCodeLink_1) {
        {
          InnerDeclaration innerDeclaration = innerDataTypeLink.getInnerDeclaration();
          Field _resolveJaMoppProxy_1 = this.<Field>resolveJaMoppProxy(innerDataTypeLink.getField());
          Field jamoppField = ((Field) _resolveJaMoppProxy_1);
          this.addCorrespondence(innerDeclaration, jamoppField, dataTypeCorrespondence);
        }
      }
    }
  }
  
  /**
   * Returns the {@link Package} for the given {@link Commentable} or null if none was found.
   */
  private org.emftext.language.java.containers.Package getPackageForCommentable(final Commentable commentable) {
    String namespace = commentable.getContainingCompilationUnit().getNamespacesAsString();
    if ((namespace.endsWith("$") || namespace.endsWith("."))) {
      int _length = namespace.length();
      int _minus = (_length - 1);
      namespace = namespace.substring(0, _minus);
    }
    final String finalNamespace = namespace;
    final Function1<org.emftext.language.java.containers.Package, Boolean> _function = (org.emftext.language.java.containers.Package pack) -> {
      String _namespacesAsString = pack.getNamespacesAsString();
      String _name = pack.getName();
      String _plus = (_namespacesAsString + _name);
      return Boolean.valueOf(finalNamespace.equals(_plus));
    };
    return IterableExtensions.<org.emftext.language.java.containers.Package>findFirst(this.packages, _function);
  }
  
  /**
   * Returns the resolved EObject for the given jaMopp proxy.
   */
  public <T extends EObject> T resolveJaMoppProxy(final T proxy) {
    if (((proxy == null) || (!proxy.eIsProxy()))) {
      return proxy;
    }
    EObject _resolve = EcoreUtil.resolve(proxy, this.jaMoppResources.get(0).getResourceSet());
    return ((T) _resolve);
  }
  
  /**
   * Returns top-level package of the loaded jamopp resource set.
   */
  private org.emftext.language.java.containers.Package getRootPackage() {
    if ((this.rootPackage != null)) {
      return this.rootPackage;
    }
    this.rootPackage = ((org.emftext.language.java.containers.Package[])Conversions.unwrapArray(this.packages, org.emftext.language.java.containers.Package.class))[0];
    for (final org.emftext.language.java.containers.Package package_ : this.packages) {
      {
        String _namespacesAsString = package_.getNamespacesAsString();
        String _name = package_.getName();
        String fullyQualifiedName = (_namespacesAsString + _name);
        int _length = fullyQualifiedName.length();
        int _length_1 = this.rootPackage.getName().length();
        int _length_2 = this.rootPackage.getNamespacesAsString().length();
        int _plus = (_length_1 + _length_2);
        boolean _lessThan = (_length < _plus);
        if (_lessThan) {
          this.rootPackage = package_;
        }
      }
    }
    return this.rootPackage;
  }
  
  protected Correspondence addCorrespondence(final EObject pcmObject, final EObject jamoppObject) {
    return this.addCorrespondence(pcmObject, jamoppObject, null);
  }
  
  /**
   * Creates an {@link EObjectCorrespondence} between the given EObjects
   * and adds it to the {@link CorrespondenceModel}
   */
  public Correspondence addCorrespondence(final EObject objectA, final EObject objectB, final Correspondence parent) {
    if (((objectA == null) || (objectB == null))) {
      throw new IllegalArgumentException("Corresponding elements must not be null!");
    }
    EObject deresolvedA = this.<EObject>deresolveIfNesessary(objectA);
    EObject deresolvedB = this.<EObject>deresolveIfNesessary(objectB);
    String _string = this.cInstance.calculateTuidFromEObject(deresolvedA).toString();
    String _string_1 = this.cInstance.calculateTuidFromEObject(deresolvedB).toString();
    String identifier = (_string + _string_1);
    Correspondence correspondence = null;
    boolean _contains = this.existingEntries.contains(identifier);
    boolean _not = (!_contains);
    if (_not) {
      final boolean useIntegrationCorrespondence = this.decideIntegrationCorrespondenceUsage(objectA, objectB);
      if (useIntegrationCorrespondence) {
        final GenericCorrespondenceModel<IntegrationCorrespondence> integrationCorrespondenceView = IntegrationCorrespondenceHelper.getEditableView(this.cInstance);
        correspondence = integrationCorrespondenceView.createAndAddCorrespondence(CollectionBridge.<EObject>toList(deresolvedA), CollectionBridge.<EObject>toList(deresolvedB));
      } else {
        correspondence = CorrespondenceModelUtil.createAndAddCorrespondence(this.cInstance, deresolvedA, deresolvedB);
      }
      this.existingEntries.add(identifier);
      this.logger.info(((("Created Correspondence for element: " + objectA) + " and Element: ") + objectB));
      return correspondence;
    }
    return null;
  }
  
  public boolean decideIntegrationCorrespondenceUsage(final EObject objectA, final EObject objectB) {
    final List<CorrespondenceTypeDeciding> correspondenceTypeDeciders = EclipseBridge.<CorrespondenceTypeDeciding>getRegisteredExtensions(CorrespondenceTypeDeciding.ID, 
      VitruviusConstants.getExtensionPropertyName(), CorrespondenceTypeDeciding.class);
    final CorrespondenceTypeDeciding correspondenceTypeDecider = IterableUtil.<List<CorrespondenceTypeDeciding>, CorrespondenceTypeDeciding>claimNotMany(correspondenceTypeDeciders);
    if ((null == correspondenceTypeDecider)) {
      return true;
    }
    return correspondenceTypeDecider.useIntegratedCorrespondence(objectA, objectB, this.cInstance, this.jaMoppResources);
  }
  
  /**
   * Converts the absolute resource URI of given EObject to platform URI
   * or does nothing if it already has one.
   */
  public <T extends EObject> T deresolveIfNesessary(final T object) {
    Resource _eResource = object.eResource();
    boolean _tripleEquals = (null == _eResource);
    if (_tripleEquals) {
      return object;
    }
    URI uri = object.eResource().getURI();
    boolean _isPlatform = uri.isPlatform();
    boolean _not = (!_isPlatform);
    if (_not) {
      String _string = this.projectBase.toString();
      String _plus = (_string + Character.valueOf(IPath.SEPARATOR));
      URI base = URI.createFileURI(_plus);
      URI relativeUri = uri.deresolve(base);
      Resource _eResource_1 = object.eResource();
      _eResource_1.setURI(EMFBridge.createPlatformResourceURI(relativeUri.toString()));
    }
    return object;
  }
  
  @Pure
  public Resource getScdm() {
    return this.scdm;
  }
  
  @Pure
  public List<Resource> getJaMoppResources() {
    return this.jaMoppResources;
  }
  
  @Pure
  public CorrespondenceModel getCInstance() {
    return this.cInstance;
  }
}
