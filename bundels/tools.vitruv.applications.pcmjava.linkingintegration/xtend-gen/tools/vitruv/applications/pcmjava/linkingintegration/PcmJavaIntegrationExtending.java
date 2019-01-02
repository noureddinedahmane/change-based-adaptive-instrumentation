package tools.vitruv.applications.pcmjava.linkingintegration;

import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaCorrespondenceModelTransformation;

@SuppressWarnings("all")
public interface PcmJavaIntegrationExtending {
  public final static String ID = "tools.vitruv.applications.pcmjava.linkingintegration.pcmjamoppintegrationextending.afterbasictransformation";
  
  public abstract void afterBasicTransformations(final PcmJavaCorrespondenceModelTransformation transformation);
}
