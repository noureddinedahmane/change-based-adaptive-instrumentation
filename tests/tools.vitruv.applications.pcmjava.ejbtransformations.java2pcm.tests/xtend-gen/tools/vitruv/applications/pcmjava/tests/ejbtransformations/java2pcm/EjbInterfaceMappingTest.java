package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.PrimitiveTypeEnum;
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbJava2PcmTransformationTest;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.applications.pcmjava.tests.util.Pcm2JavaTestUtils;

@SuppressWarnings("all")
public class EjbInterfaceMappingTest extends EjbJava2PcmTransformationTest {
  @Test
  public void testCreateInterfaceAndAddRemoteAnnotation() {
    try {
      super.addRepoContractsAndDatatypesPackage();
      final OperationInterface correspondingOpInterface = this.createEjbInterface(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
      Assert.assertEquals("Created component has different name as added class", correspondingOpInterface.getEntityName(), EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testCreateMethodInEjbInterface() {
    final OperationSignature correspondingOpSignature = this.createPackageEjbInterrfaceAndInterfaceMethod();
    Assert.assertEquals("OperationSiganture has not the expected name ", correspondingOpSignature.getEntityName(), 
      Pcm2JavaTestUtils.OPERATION_SIGNATURE_1_NAME);
  }
  
  @Test
  public void testCreateParameterInMethodInEjbInterface() {
    try {
      final OperationSignature correspondingOpSignature = this.createPackageEjbInterrfaceAndInterfaceMethod();
      final Parameter pcmParam = super.addParameterToSignature(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME, correspondingOpSignature.getEntityName(), "byte[]", "data", null);
      this.assertPCMParam(pcmParam, "data", PrimitiveTypeEnum.BYTE);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testSetReturnTypeInMethodInEjbInterface() {
    try {
      final OperationSignature correspondingOpSignature = this.createPackageEjbInterrfaceAndInterfaceMethod();
      final OperationSignature opSignature = this.addReturnTypeToSignature(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME, correspondingOpSignature.getEntityName(), "byte[]", null);
      Assert.assertEquals("Wrong signature changed", opSignature.getEntityName(), correspondingOpSignature.getEntityName());
      DataType _returnType__OperationSignature = opSignature.getReturnType__OperationSignature();
      Assert.assertTrue("OpSignature returnType is not a collection Data type", (_returnType__OperationSignature instanceof CollectionDataType));
      DataType _returnType__OperationSignature_1 = opSignature.getReturnType__OperationSignature();
      final CollectionDataType cdt = ((CollectionDataType) _returnType__OperationSignature_1);
      DataType _innerType_CollectionDataType = cdt.getInnerType_CollectionDataType();
      Assert.assertTrue("OpSignature returnType: InnerDatatype is not a primitive type", (_innerType_CollectionDataType instanceof PrimitiveDataType));
      DataType _innerType_CollectionDataType_1 = cdt.getInnerType_CollectionDataType();
      final PrimitiveDataType primitiveDataType = ((PrimitiveDataType) _innerType_CollectionDataType_1);
      Assert.assertEquals("OpSignature returnType : InnerDatatype is from type BYTE", PrimitiveTypeEnum.BYTE, primitiveDataType.getType());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testCreateMultipleParametersInMethodInEjbInterface() {
    try {
      final OperationSignature correspondingOpSignature = this.createPackageEjbInterrfaceAndInterfaceMethod();
      final String name = "data";
      final String typeName = "byte[]";
      final Parameter pcmParam = super.addParameterToSignature(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME, correspondingOpSignature.getEntityName(), typeName, name, null);
      this.assertPCMParam(pcmParam, name, PrimitiveTypeEnum.BYTE);
      final ICompilationUnit icu = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME, 
        this.getCurrentTestProject());
      final IMethod iMethod = icu.getType(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME).getMethods()[0];
      final String secondParamname = "additionalData";
      final String typeStringName = "String[]";
      final String secondParameterStr = (((", " + typeStringName) + " ") + secondParamname);
      final Parameter secondPcmParam = super.insertParameterIntoSignature(correspondingOpSignature.getEntityName(), secondParamname, icu, iMethod, secondParameterStr);
      this.assertPCMParam(secondPcmParam, secondParamname, PrimitiveTypeEnum.STRING);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private OperationSignature createPackageEjbInterrfaceAndInterfaceMethod() {
    try {
      OperationSignature _xblockexpression = null;
      {
        super.createPackageAndEjbInterface();
        final OperationSignature correspondingOpSignature = super.addMethodToInterfaceWithCorrespondence(EjbJava2PcmTransformationTest.TEST_INTERFACE_NAME);
        _xblockexpression = correspondingOpSignature;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void assertPCMParam(final Parameter pcmParam, final String expectedName, final PrimitiveTypeEnum expectedPrimiteveTypeEnum) {
    Assert.assertEquals("PCM Parameter has not the expected name ", expectedName, pcmParam.getParameterName());
    DataType _dataType__Parameter = pcmParam.getDataType__Parameter();
    Assert.assertTrue("PCM Parameter Type is not a collection Data type", (_dataType__Parameter instanceof CollectionDataType));
    DataType _dataType__Parameter_1 = pcmParam.getDataType__Parameter();
    final CollectionDataType cdt = ((CollectionDataType) _dataType__Parameter_1);
    DataType _innerType_CollectionDataType = cdt.getInnerType_CollectionDataType();
    Assert.assertTrue("PCM Parameter Type: InnerDatatype is not a primitive type", (_innerType_CollectionDataType instanceof PrimitiveDataType));
    DataType _innerType_CollectionDataType_1 = cdt.getInnerType_CollectionDataType();
    final PrimitiveDataType primitiveDataType = ((PrimitiveDataType) _innerType_CollectionDataType_1);
    Assert.assertEquals("PCM Parameter Type: wrong InnerDatatype", expectedPrimiteveTypeEnum, primitiveDataType.getType());
  }
}
