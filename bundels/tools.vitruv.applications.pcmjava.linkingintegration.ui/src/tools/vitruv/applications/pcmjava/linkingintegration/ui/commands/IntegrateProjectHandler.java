package tools.vitruv.applications.pcmjava.linkingintegration.ui.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jst.ws.internal.common.ResourceUtils;
import org.eclipse.ui.handlers.HandlerUtil;

import tools.vitruv.applications.pcmjava.util.PcmJavaRepositoryCreationUtil;
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaCorrespondenceModelTransformation;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;

@SuppressWarnings("restriction")
public class IntegrateProjectHandler extends AbstractHandler {
	
	private Logger logger = Logger.getLogger(IntegrateProjectHandler.class.getName());
	
	public IntegrateProjectHandler() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.setLevel(Level.ALL);

        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

        final Object firstElement = structuredSelection.getFirstElement();
        final IJavaProject javaProject = (IJavaProject) firstElement;
        final IProject project = javaProject.getProject();

        integrateProject(project);
        
        return null;
	}

	public void integrateProject(final IProject project) {
		// IPath projectPath = project.getFullPath(); // workspace relative Path
        final IPath projectPath = project.getLocation(); // absolute path
        final IPath models = projectPath.append("model").addTrailingSeparator().append("internal_architecture_model");

        final IPath scdmPath = models.addFileExtension("sourcecodedecorator");
        final IPath pcmPath = models.addFileExtension("repository");
        
        IPath[] srcPaths = ResourceUtils.getAllJavaSourceLocations(project);
        List<IPath> jamoppPaths = new ArrayList<>();
        for (IPath path : srcPaths) {
        	IPath projectRelative = path.removeFirstSegments(1);
        	IPath abs = project.getLocation().append(projectRelative);
        	jamoppPaths.add(abs);
        }
        
        final IPath projectBase = projectPath.removeLastSegments(1);

        final File f = scdmPath.toFile();
        if (!f.exists()) {
            throw new IllegalArgumentException("Run SoMoX first!");
        }

        final Iterable<VitruvDomain> metamodels = PcmJavaRepositoryCreationUtil.createPcmJamoppMetamodels();
        VirtualModelConfiguration config = new VirtualModelConfiguration();
        for (VitruvDomain metamodel : metamodels) {
        	config.addMetamodel(metamodel);
        } 
        // TODO HK Use other name
        File vsumFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "vitruvius.meta");
        final InternalVirtualModel vsum = new VirtualModelImpl(vsumFolder, UserInteractionFactory.instance.createDialogUserInteractor(), config);

        final PcmJavaCorrespondenceModelTransformation transformation = new PcmJavaCorrespondenceModelTransformation(
                scdmPath.toString(), pcmPath.toString(), jamoppPaths, vsum, projectBase);

        transformation.createCorrespondences();
	}

}
