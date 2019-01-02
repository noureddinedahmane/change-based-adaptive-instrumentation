package tools.vitruv.application.pcmjava.modelrefinement.monitoring.factory;

import java.util.Map;
import java.util.Map.Entry;

import tools.vitruv.application.pcmjava.modelrefinement.monitoring.primitiveattributeextraction.PrimitiveAttributeExtracter;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.primitiveattributeextraction.PrimitiveAttributeExtracterImp;

public class ServiceParametersFactoryImp implements ServiceParamtersFactory{
	private PrimitiveAttributeExtracter extracter;
	
	public ServiceParametersFactoryImp() {
		extracter =  new PrimitiveAttributeExtracterImp();
	}
	
	public ServiceParameters getServiceParameters(Object[] listObjects) {
		ServiceParameters serviceParameters =  new ServiceParameters();
		Map<String, Object> serviceParametersValues = this.extracter.getObjectPrimitiveAttributeValues(listObjects, 5);
		for (Entry<String, Object> entry : serviceParametersValues.entrySet())
		{
			serviceParameters.addValue(entry.getKey().toString(), entry.getValue());
		}
		return serviceParameters;
	}
}
