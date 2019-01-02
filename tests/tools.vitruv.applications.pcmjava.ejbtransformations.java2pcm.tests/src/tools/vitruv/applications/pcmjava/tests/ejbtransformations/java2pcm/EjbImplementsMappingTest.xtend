package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm

import org.junit.Test
import org.palladiosimulator.pcm.repository.OperationProvidedRole

import static org.junit.Assert.assertEquals

class EjbImplementsMappingTest extends EjbJava2PcmTransformationTest{
	
	@Test
	def testAddImplementsToComponentClass(){
		super.createPackageEjbClassAndInterface()
		
		val OperationProvidedRole opr = super.addImplementsCorrespondingToOperationProvidedRoleToClass(TEST_CLASS_NAME, TEST_INTERFACE_NAME)
		
		assertEquals( "OperationProvidedRole has wrong name", TEST_CLASS_NAME + "_provides_" + TEST_INTERFACE_NAME, opr.entityName)
	}
	
}