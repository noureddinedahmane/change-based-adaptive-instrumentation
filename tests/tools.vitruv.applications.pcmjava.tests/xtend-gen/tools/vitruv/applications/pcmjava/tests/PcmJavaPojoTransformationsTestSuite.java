package tools.vitruv.applications.pcmjava.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import tools.vitruv.applications.pcmjava.tests.pojotransformations.java2pcm.Java2PcmTestSuite;
import tools.vitruv.applications.pcmjava.tests.pojotransformations.pcm2java.Pcm2JavaTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ Pcm2JavaTestSuite.class, Java2PcmTestSuite.class })
@SuppressWarnings("all")
public class PcmJavaPojoTransformationsTestSuite {
}
