package tools.vitruv.applications.pcmjava.seffstatements.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import tools.vitruv.applications.pcmjava.seffstatements.tests.ejbtransformations.PcmJavaSeffstatementsEjbTransformationsTestSuite;
import tools.vitruv.applications.pcmjava.seffstatements.tests.pojotransformations.PcmJavaSeffstatementsPojoTransformationsTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PcmJavaSeffstatementsPojoTransformationsTestSuite.class, PcmJavaSeffstatementsEjbTransformationsTestSuite.class })
@SuppressWarnings("all")
public class PcmJavaSeffstatementsTestSuite {
}
