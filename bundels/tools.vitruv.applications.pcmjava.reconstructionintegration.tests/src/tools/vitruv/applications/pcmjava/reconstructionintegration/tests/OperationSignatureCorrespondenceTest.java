package tools.vitruv.applications.pcmjava.reconstructionintegration.tests;

import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.emftext.language.java.members.Method;
import org.junit.Test;

import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;

/**
 * Class for testing correspondences between PCM operationSignatures and JaMoPP interfaceMethods.
 */
public class OperationSignatureCorrespondenceTest extends BasicCorrespondenceTest {

    /**
     * Test rename operation signature.
     *
     * @throws Throwable
     *             the throwable
     */
    @Test
    public void testRenameOperationSignature() throws Throwable {
        // Get an OperationSignature from PCM repository
        OperationSignature signature = null;
        Iterator<Interface> interfaceInterator = pcmRepo.getInterfaces__Repository().iterator();
        while (interfaceInterator.hasNext() && signature == null) {
            OperationInterface pcmInterface = (OperationInterface) interfaceInterator.next();
            if (!pcmInterface.getSignatures__OperationInterface().isEmpty()) {
                signature = pcmInterface.getSignatures__OperationInterface().get(0);
            }
        }
        assertNotNull("PCM Repository doesn't contain any OperationSignatures!", signature);

        // Start change recorder and rename signature
        signature.setEntityName("SignatureRenamed");

        // Save and execute synchronization
        this.saveAndSynchronizeChanges(signature);

        // Check if corresponding JaMoPP Method is available and has
        // updated correctly
        this.assertSingleCorrespondence(signature, Method.class, signature.getEntityName());
    }
}
