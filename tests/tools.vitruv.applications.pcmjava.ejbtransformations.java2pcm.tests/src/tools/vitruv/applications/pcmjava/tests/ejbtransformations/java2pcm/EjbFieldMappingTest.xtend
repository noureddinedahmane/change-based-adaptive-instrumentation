package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm

import org.junit.Test
import org.palladiosimulator.pcm.repository.OperationRequiredRole

import static org.junit.Assert.*

class EjbFieldMappingTest extends EjbJava2PcmTransformationTest {
	
	@Test
	def void annotateField() {
		val basicComponent = super.createPackageEjbClassAndInterface()
		
		super.addFieldToClassWithName(TEST_CLASS_NAME, TEST_INTERFACE_NAME, TEST_FIELD_NAME, null)
		val opRequiredRole = super.addAnnotationToField(TEST_FIELD_NAME, EJB_FIELD_ANNOTATION_NAME, OperationRequiredRole, TEST_CLASS_NAME)
		
		assertEquals("BasicComponent of required role is wrong", basicComponent.id, opRequiredRole.requiringEntity_RequiredRole.id)
		assertEquals("Required Interface has wrong name", TEST_INTERFACE_NAME, opRequiredRole.requiredInterface__OperationRequiredRole.entityName)
	}
}
