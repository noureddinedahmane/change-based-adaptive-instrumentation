package tools.vitruv.applications.pcmjava.linkingintegration.change2command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.internal.IntegrationChange2CommandTransformer;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.framework.change.description.CompositeTransactionalChange;
import tools.vitruv.framework.change.description.ConcreteChange;
import tools.vitruv.framework.change.description.TransactionalChange;
import tools.vitruv.framework.change.processing.impl.AbstractChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.userinteraction.UserInteractor;
import tools.vitruv.framework.util.command.ResourceAccess;

@SuppressWarnings("all")
public class CodeIntegrationChangeProcessor extends AbstractChangePropagationSpecification {
  private final IntegrationChange2CommandTransformer integrationTransformer;
  
  public CodeIntegrationChangeProcessor() {
    super(new JavaDomainProvider().getDomain(), new PcmDomainProvider().getDomain());
    UserInteractor _userInteractor = this.getUserInteractor();
    IntegrationChange2CommandTransformer _integrationChange2CommandTransformer = new IntegrationChange2CommandTransformer(_userInteractor);
    this.integrationTransformer = _integrationChange2CommandTransformer;
  }
  
  @Override
  public boolean doesHandleChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel) {
    return true;
  }
  
  @Override
  public void propagateChange(final TransactionalChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    this.performIntegration(change, correspondenceModel, resourceAccess);
  }
  
  protected boolean _performIntegration(final CompositeTransactionalChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    final ArrayList<TransactionalChange> integratedChanges = new ArrayList<TransactionalChange>();
    boolean performedIntegration = true;
    List<TransactionalChange> _changes = change.getChanges();
    for (final TransactionalChange innerChange : _changes) {
      {
        final boolean integrationResult = this.performIntegration(innerChange, correspondenceModel, resourceAccess);
        if (integrationResult) {
          integratedChanges.add(innerChange);
        } else {
          performedIntegration = false;
        }
      }
    }
    for (final TransactionalChange integratedChange : integratedChanges) {
      change.removeChange(integratedChange);
    }
    return performedIntegration;
  }
  
  protected boolean _performIntegration(final ConcreteChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    return this.integrationTransformer.compute(change, correspondenceModel, resourceAccess);
  }
  
  public boolean performIntegration(final TransactionalChange change, final CorrespondenceModel correspondenceModel, final ResourceAccess resourceAccess) {
    if (change instanceof CompositeTransactionalChange) {
      return _performIntegration((CompositeTransactionalChange)change, correspondenceModel, resourceAccess);
    } else if (change instanceof ConcreteChange) {
      return _performIntegration((ConcreteChange)change, correspondenceModel, resourceAccess);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(change, correspondenceModel, resourceAccess).toString());
    }
  }
}
