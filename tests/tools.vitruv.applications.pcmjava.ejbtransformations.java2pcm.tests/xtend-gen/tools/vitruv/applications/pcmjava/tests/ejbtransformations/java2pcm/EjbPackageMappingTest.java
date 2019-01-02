package tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm;

import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbJava2PcmTransformationTest;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

@SuppressWarnings("all")
public class EjbPackageMappingTest extends EjbJava2PcmTransformationTest {
  @Test
  public void testCreatePackage() {
    try {
      super.addRepoContractsAndDatatypesPackage();
      final Repository correspondingRepo = IterableUtil.<Set<Repository>, Repository>claimOne(CorrespondenceModelUtil.<Repository, Correspondence>getCorrespondingEObjectsByType(this.getCorrespondenceModel(), this.mainPackage, Repository.class));
      Assert.assertEquals("Corresponding Repository has not the same name as the main package", correspondingRepo.getEntityName(), this.mainPackage.getName());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
