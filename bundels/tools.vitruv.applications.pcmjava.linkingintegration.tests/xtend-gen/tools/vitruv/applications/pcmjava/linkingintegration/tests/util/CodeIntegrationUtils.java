package tools.vitruv.applications.pcmjava.linkingintegration.tests.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.osgi.framework.Bundle;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.DoneFlagProgressMonitor;
import tools.vitruv.applications.pcmjava.linkingintegration.ui.commands.IntegrateProjectHandler;

@SuppressWarnings("all")
public class CodeIntegrationUtils {
  private CodeIntegrationUtils() {
  }
  
  public static void integratProject(final IProject project) {
    final IntegrateProjectHandler integrateProjectHander = new IntegrateProjectHandler();
    integrateProjectHander.integrateProject(project);
  }
  
  public static void importTestProjectFromBundleData(final IWorkspace workspace, final String testProjectName, final String testBundleName, final String testSourceAndModelFolder) throws IOException, URISyntaxException, InvocationTargetException, InterruptedException {
    final IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
      @Override
      public String queryOverwrite(final String file) {
        return IOverwriteQuery.ALL;
      }
    };
    final IPath workspacePath = workspace.getRoot().getFullPath().append(("/" + testProjectName));
    final Bundle bundle = Platform.getBundle(testBundleName);
    final URL projectBluePrintBundleURL = bundle.getEntry(testSourceAndModelFolder);
    final URL fileURL = FileLocator.resolve(projectBluePrintBundleURL);
    URI _uRI = fileURL.toURI();
    final File file = new File(_uRI);
    final String baseDir = file.getAbsolutePath();
    File _file = new File(baseDir);
    final ImportOperation importOperation = new ImportOperation(workspacePath, _file, 
      FileSystemStructureProvider.INSTANCE, overwriteQuery);
    importOperation.setCreateContainerStructure(false);
    final DoneFlagProgressMonitor progress = new DoneFlagProgressMonitor();
    importOperation.run(progress);
    while ((!progress.isDone())) {
      Thread.sleep(100);
    }
  }
}
