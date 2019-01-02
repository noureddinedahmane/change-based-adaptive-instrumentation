package tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers;

import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.extensions.constructionsimulation.invariantcheckers.InvariantEnforcer;

/**
 * The Class DynamicInvariantEnforcer.
 */
public class InvariantEnforcerFacade extends InvariantEnforcer {

    private PcmElementSelector<?> traversalStrategy; // TODO: make more abstract
    private PcmtoJavaRenameInvariantEnforcer enforcer; // TODO: same here

    /**
     * Instantiates a new dynamic invariant enforcer.
     *
     * @param strategy
     *            the strategy
     * @param enforcer
     *            the enforcer
     */
    public InvariantEnforcerFacade(final PcmElementSelector<?> strategy,
            final PcmtoJavaRenameInvariantEnforcer enforcer) {
        this.setTraversalStrategy(strategy);
        this.setEnforcer(enforcer);

    }

    /**
     * Gets the enforcer.
     *
     * @return the enforcer
     */
    public PcmtoJavaRenameInvariantEnforcer getEnforcer() {
        return this.enforcer;
    }

    /**
     * Sets the enforcer.
     *
     * @param enforcer
     *            the new enforcer
     */
    public void setEnforcer(final PcmtoJavaRenameInvariantEnforcer enforcer) {
        this.enforcer = enforcer;
    }

    /**
     * Gets the traversal strategy.
     *
     * @return the traversal strategy
     */
    public PcmElementSelector<?>getTraversalStrategy() {
        return this.traversalStrategy;
    }

    /**
     * Sets the traversal strategy.
     *
     * @param traversalStrategy
     *            the new traversal strategy
     */
    public void setTraversalStrategy(final PcmElementSelector<?> traversalStrategy) {
        this.traversalStrategy = traversalStrategy;
        traversalStrategy.setParentEnforcer(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * tools.vitruv.integration.invariantChecker.InvariantEnforcer#loadModelRoot(org
     * .eclipse.emf.ecore.resource.Resource)
     */
    @Override
    public void loadModelRoot(final Resource model) {
        this.model = model;
        this.traversalStrategy.loadModelRoot();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * tools.vitruv.integration.invariantChecker.InvariantEnforcer#enforceInvariant()
     */
    @Override
    public void enforceInvariant() {
        this.traversalStrategy.traverseModelAndSolveConflics();

    }

    /*
     * (non-Javadoc)
     *
     * @see tools.vitruv.integration.invariantChecker.InvariantEnforcer#returnModel()
     */
    @Override
    public Resource returnModel() {
        return this.model;
    }

}
