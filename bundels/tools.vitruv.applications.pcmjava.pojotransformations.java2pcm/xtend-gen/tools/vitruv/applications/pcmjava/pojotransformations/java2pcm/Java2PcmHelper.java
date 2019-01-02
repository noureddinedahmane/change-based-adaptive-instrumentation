package tools.vitruv.applications.pcmjava.pojotransformations.java2pcm;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.applications.pcmjava.util.java2pcm.TypeReferenceCorrespondenceHelper;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.framework.userinteraction.UserInteractor;

/**
 * Helper class for java2pcm reactions and routines.
 * Some methods are copied from other util classes because only this implementation is using them.
 * These methods are marked with a comment and can be removed from their util class in the future.
 */
@SuppressWarnings("all")
public class Java2PcmHelper {
  public static Set<OperationInterface> getCorrespondingOperationInterface(final EObject eObject, final CorrespondenceModel correspondenceModel) {
    return CorrespondenceModelUtil.<OperationInterface, Correspondence>getCorrespondingEObjectsByType(correspondenceModel, eObject, OperationInterface.class);
  }
  
  /**
   * Signatures are considered equal if methods have the same name, the same parameter types and the same return type
   * We do not consider modifiers (e.g. public or private here)
   */
  public static boolean sameSignature(final Method method1, final Method method2) {
    boolean _equals = Objects.equal(method1, method2);
    if (_equals) {
      return true;
    }
    boolean _equals_1 = method1.getName().equals(method2.getName());
    boolean _not = (!_equals_1);
    if (_not) {
      return false;
    }
    boolean _hasSameTargetReference = Java2PcmHelper.hasSameTargetReference(method1.getTypeReference(), method2.getTypeReference());
    boolean _not_1 = (!_hasSameTargetReference);
    if (_not_1) {
      return false;
    }
    int _size = method1.getParameters().size();
    int _size_1 = method2.getParameters().size();
    boolean _notEquals = (_size != _size_1);
    if (_notEquals) {
      return false;
    }
    int i = 0;
    EList<Parameter> _parameters = method1.getParameters();
    for (final Parameter param1 : _parameters) {
      {
        boolean _hasSameTargetReference_1 = Java2PcmHelper.hasSameTargetReference(param1.getTypeReference(), method2.getParameters().get(i).getTypeReference());
        boolean _not_2 = (!_hasSameTargetReference_1);
        if (_not_2) {
          return false;
        }
        i++;
      }
    }
    return true;
  }
  
  private static boolean hasSameTargetReference(final TypeReference reference1, final TypeReference reference2) {
    if ((Objects.equal(reference1, reference2) || reference1.equals(reference2))) {
      return true;
    }
    final Classifier target1 = Java2PcmHelper.getTargetClassifierFromTypeReference(reference1);
    final Classifier target2 = Java2PcmHelper.getTargetClassifierFromTypeReference(reference2);
    return (Objects.equal(target1, target2) || target1.equals(target2));
  }
  
  public static DataType getPCMDataTypeForTypeReference(final TypeReference typeReference, final CorrespondenceModel correspondenceModel, final UserInteractor userInteractor, final Repository repository, final Method newMethod) {
    return TypeReferenceCorrespondenceHelper.getCorrespondingPCMDataTypeForTypeReference(typeReference, correspondenceModel, userInteractor, repository, newMethod.getArrayDimension());
  }
  
  public static String getRootPackageName(final String packageName) {
    final int index = packageName.indexOf(".");
    if ((index < 0)) {
      return packageName;
    }
    return packageName.substring(0, packageName.indexOf("."));
  }
  
  public static String getLastPackageName(final String packageName) {
    int _indexOf = packageName.indexOf(".");
    int _plus = (_indexOf + 1);
    return packageName.substring(_plus);
  }
  
  public static ArrayList<Interface> findImplementingInterfacesFromTypeRefs(final EList<TypeReference> typeReferences) {
    final ArrayList<Interface> implementingInterfaces = new ArrayList<Interface>();
    for (final TypeReference typeRef : typeReferences) {
      {
        final Classifier classifier = Java2PcmHelper.getTargetClassifierFromImplementsReferenceAndNormalizeURI(typeRef);
        if ((classifier instanceof Interface)) {
          implementingInterfaces.add(((Interface)classifier));
        }
      }
    }
    return implementingInterfaces;
  }
  
  public static Classifier getTargetClassifierFromImplementsReferenceAndNormalizeURI(final TypeReference reference) {
    Classifier interfaceClassifier = Java2PcmHelper.getTargetClassifierFromTypeReference(reference);
    if ((null == interfaceClassifier)) {
      return null;
    }
    boolean _eIsProxy = interfaceClassifier.eIsProxy();
    if (_eIsProxy) {
      final ResourceSet resSet = reference.eResource().getResourceSet();
      EObject _resolve = EcoreUtil.resolve(interfaceClassifier, resSet);
      interfaceClassifier = ((Classifier) _resolve);
    }
    Java2PcmHelper.normalizeURI(interfaceClassifier);
    return interfaceClassifier;
  }
  
  protected static Classifier _getTargetClassifierFromTypeReference(final TypeReference reference) {
    return null;
  }
  
  protected static Classifier _getTargetClassifierFromTypeReference(final NamespaceClassifierReference reference) {
    boolean _isNullOrEmpty = IterableExtensions.isNullOrEmpty(reference.getClassifierReferences());
    if (_isNullOrEmpty) {
      return null;
    }
    return Java2PcmHelper.getTargetClassifierFromTypeReference(reference.getClassifierReferences().get(0));
  }
  
  protected static Classifier _getTargetClassifierFromTypeReference(final ClassifierReference reference) {
    return reference.getTarget();
  }
  
  protected static Classifier _getTargetClassifierFromTypeReference(final PrimitiveType reference) {
    return null;
  }
  
  private static boolean normalizeURI(final EObject eObject) {
    if (((null == eObject.eResource()) || (null == eObject.eResource().getResourceSet()))) {
      return false;
    }
    final Resource resource = eObject.eResource();
    final ResourceSet resourceSet = resource.getResourceSet();
    final URI uri = resource.getURI();
    final URIConverter uriConverter = resourceSet.getURIConverter();
    final URI normalizedURI = uriConverter.normalize(uri);
    resource.setURI(normalizedURI);
    return true;
  }
  
  public static org.emftext.language.java.containers.Package getContainingPackageFromCorrespondenceModel(final Classifier classifier, final CorrespondenceModel correspondenceModel) {
    String namespace = classifier.getContainingCompilationUnit().getNamespacesAsString();
    if ((namespace.endsWith("$") || namespace.endsWith("."))) {
      int _length = namespace.length();
      int _minus = (_length - 1);
      namespace = namespace.substring(0, _minus);
    }
    final String finalNamespace = namespace;
    Set<org.emftext.language.java.containers.Package> packagesWithCorrespondences = CorrespondenceModelUtil.<org.emftext.language.java.containers.Package, Correspondence>getAllEObjectsOfTypeInCorrespondences(correspondenceModel, org.emftext.language.java.containers.Package.class);
    final Function1<org.emftext.language.java.containers.Package, Boolean> _function = (org.emftext.language.java.containers.Package pack) -> {
      String _namespacesAsString = pack.getNamespacesAsString();
      String _name = pack.getName();
      String _plus = (_namespacesAsString + _name);
      return Boolean.valueOf(finalNamespace.equals(_plus));
    };
    final Iterable<org.emftext.language.java.containers.Package> packagesWithNamespace = IterableExtensions.<org.emftext.language.java.containers.Package>filter(packagesWithCorrespondences, _function);
    if ((((null != packagesWithNamespace) && (0 < IterableExtensions.size(packagesWithNamespace))) && 
      (null != packagesWithNamespace.iterator().next()))) {
      return packagesWithNamespace.iterator().next();
    }
    return null;
  }
  
  public static Classifier getTargetClassifierFromTypeReference(final TypeReference reference) {
    if (reference instanceof ClassifierReference) {
      return _getTargetClassifierFromTypeReference((ClassifierReference)reference);
    } else if (reference instanceof NamespaceClassifierReference) {
      return _getTargetClassifierFromTypeReference((NamespaceClassifierReference)reference);
    } else if (reference instanceof PrimitiveType) {
      return _getTargetClassifierFromTypeReference((PrimitiveType)reference);
    } else if (reference != null) {
      return _getTargetClassifierFromTypeReference(reference);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(reference).toString());
    }
  }
}
