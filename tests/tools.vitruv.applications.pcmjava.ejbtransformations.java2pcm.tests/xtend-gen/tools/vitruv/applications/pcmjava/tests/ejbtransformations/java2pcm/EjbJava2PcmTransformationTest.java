package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm;

import java.util.Collections;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import tools.vitruv.applications.pcmjava.ejbtransformations.java2pcm.change2commandtransforming.EjbJava2PcmChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;

/**
 * class that contains special methods for EJB testing
 */
@SuppressWarnings("all")
public abstract class EjbJava2PcmTransformationTest extends Java2PcmTransformationTest {
  public final static String STATELESS_ANNOTATION_NAME = "Stateless";
  
  public final static String REMOTE_ANNOTATION_NAME = "Remote";
  
  public final static String EJB_FIELD_ANNOTATION_NAME = "EJB";
  
  protected final static String TEST_CLASS_NAME = "TestEJBClass";
  
  protected final static String TEST_INTERFACE_NAME = "TestEJBInterface";
  
  protected final static String TEST_FIELD_NAME = "testEJBfield";
  
  @Override
  protected Iterable<ChangePropagationSpecification> createChangePropagationSpecifications() {
    EjbJava2PcmChangePropagationSpecification _ejbJava2PcmChangePropagationSpecification = new EjbJava2PcmChangePropagationSpecification();
    return Collections.<ChangePropagationSpecification>unmodifiableList(CollectionLiterals.<ChangePropagationSpecification>newArrayList(_ejbJava2PcmChangePropagationSpecification));
  }
  
  protected BasicComponent createEjbClass(final String className) {
    try {
      BasicComponent _xblockexpression = null;
      {
        Classifier _createClassInPackage = super.createClassInPackage(this.mainPackage, className);
        final ConcreteClassifier classifier = ((ConcreteClassifier) _createClassInPackage);
        final BasicComponent correspondingBasicComponent = this.<BasicComponent>addAnnotationToClassifier(classifier, EjbJava2PcmTransformationTest.STATELESS_ANNOTATION_NAME, BasicComponent.class, className);
        _xblockexpression = correspondingBasicComponent;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected OperationInterface createEjbInterface(final String interfaceName) {
    try {
      OperationInterface _xblockexpression = null;
      {
        ConcreteClassifier _createJaMoPPInterfaceInPackage = super.createJaMoPPInterfaceInPackage(this.mainPackage.getName(), interfaceName);
        final ConcreteClassifier classifier = ((ConcreteClassifier) _createJaMoPPInterfaceInPackage);
        final OperationInterface correspondingOpInterface = this.<OperationInterface>addAnnotationToClassifier(classifier, EjbJava2PcmTransformationTest.REMOTE_ANNOTATION_NAME, 
          OperationInterface.class, interfaceName);
        _xblockexpression = correspondingOpInterface;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected BasicComponent createPackageEjbClassAndInterface() {
    this.createPackageAndEjbInterface();
    return this.createEjbClass(EjbJava2PcmTransformationTest.TEST_CLASS_NAME);
  }
  
  protected OperationInterface createPackageAndEjbInterface() {
    try {
      OperationInterface _xblockexpression = null;
      {
        super.addRepoContractsAndDatatypesPackage();
        _xblockexpression = this.createEjbInterface(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
