package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm;

import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbJava2PcmTransformationTest;

@SuppressWarnings("all")
public class EjbImplementsMappingTest extends EjbJava2PcmTransformationTest {
  @Test
  public void testAddImplementsToComponentClass() {
    try {
      super.createPackageEjbClassAndInterface();
      final OperationProvidedRole opr = super.addImplementsCorrespondingToOperationProvidedRoleToClass(EjbJava2PcmTransformationTest.TEST_CLASS_NAME, EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
      Assert.assertEquals("OperationProvidedRole has wrong name", ((EjbJava2PcmTransformationTest.TEST_CLASS_NAME + "_provides_") + EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME), opr.getEntityName());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
