package tools.vitruv.applications.pcmjava.pojotransformations.java2pcm;

import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.TuidUpdatePreprocessor;
import tools.vitruv.applications.pcmjava.util.java2pcm.Java2PcmPackagePreprocessor;

@SuppressWarnings("all")
public class Java2PcmChangePropagationSpecification extends mir.reactions.java2Pcm.Java2PcmChangePropagationSpecification {
  @Override
  protected void setup() {
    TuidUpdatePreprocessor _tuidUpdatePreprocessor = new TuidUpdatePreprocessor();
    this.addChangeMainprocessor(_tuidUpdatePreprocessor);
    super.setup();
    Java2PcmPackagePreprocessor _java2PcmPackagePreprocessor = new Java2PcmPackagePreprocessor();
    this.addChangePreprocessor(_java2PcmPackagePreprocessor);
  }
}
