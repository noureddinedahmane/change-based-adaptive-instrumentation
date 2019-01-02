package tools.vitruv.applications.pcmjava.linkingintegration.ejbtransformations;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.applications.pcmjava.linkingintegration.CorrespondenceTypeDeciding;
import tools.vitruv.framework.correspondence.CorrespondenceModel;

/**
 * This class decides whether an integrated correspondence should be created during the integration
 * of EJB projects. The current default is no, which means, that we assume that the project that
 * should be integrated does complain the EJB mapping rules. If this is not the case the mapping
 * probably won't work.
 *
 * @author langhamm
 *
 */
public class EjbCorrepondenceTypeDecider implements CorrespondenceTypeDeciding {

    @Override
    public boolean useIntegratedCorrespondence(final EObject objectA, final EObject objectB,
            final CorrespondenceModel cInstance, final List<Resource> jaMoppResources) {
        return false;
    }
}
