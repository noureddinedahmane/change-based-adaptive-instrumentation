package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm;

import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbJava2PcmTransformationTest;

@SuppressWarnings("all")
public class EjbClassMappingTest extends EjbJava2PcmTransformationTest {
  @Test
  public void testCreateClassAndAddStatelessAnnotation() {
    try {
      super.addRepoContractsAndDatatypesPackage();
      final BasicComponent correspondingBasicComponent = this.createEjbClass(EjbJava2PcmTransformationTest.TEST_CLASS_NAME);
      Assert.assertEquals("Created component has different name as added class", correspondingBasicComponent.getEntityName(), EjbJava2PcmTransformationTest.TEST_CLASS_NAME);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testCreateMethodThatOverridesInterfaceMethod() {
    try {
      super.createPackageEjbClassAndInterface();
      super.addImplementsCorrespondingToOperationProvidedRoleToClass(EjbJava2PcmTransformationTest.TEST_CLASS_NAME, EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
      final OperationSignature correspondingOpSignature = super.addMethodToInterfaceWithCorrespondence(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
      final ResourceDemandingSEFF rdSEFF = super.addClassMethodToClassThatOverridesInterfaceMethod(EjbJava2PcmTransformationTest.TEST_CLASS_NAME, correspondingOpSignature.getEntityName());
      Assert.assertEquals("RDSEFF describes wrong service", rdSEFF.getDescribedService__SEFF().getId(), correspondingOpSignature.getId());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
