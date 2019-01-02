package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm;

import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbJava2PcmTransformationTest;

@SuppressWarnings("all")
public class EjbFieldMappingTest extends EjbJava2PcmTransformationTest {
  @Test
  public void annotateField() {
    try {
      final BasicComponent basicComponent = super.createPackageEjbClassAndInterface();
      super.<Object>addFieldToClassWithName(EjbJava2PcmTransformationTest.TEST_CLASS_NAME, EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME, EjbJava2PcmTransformationTest.TEST_FIELD_NAME, null);
      final OperationRequiredRole opRequiredRole = super.<OperationRequiredRole>addAnnotationToField(EjbJava2PcmTransformationTest.TEST_FIELD_NAME, EjbJava2PcmTransformationTest.EJB_FIELD_ANNOTATION_NAME, OperationRequiredRole.class, EjbJava2PcmTransformationTest.TEST_CLASS_NAME);
      Assert.assertEquals("BasicComponent of required role is wrong", basicComponent.getId(), opRequiredRole.getRequiringEntity_RequiredRole().getId());
      Assert.assertEquals("Required Interface has wrong name", EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME, opRequiredRole.getRequiredInterface__OperationRequiredRole().getEntityName());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
