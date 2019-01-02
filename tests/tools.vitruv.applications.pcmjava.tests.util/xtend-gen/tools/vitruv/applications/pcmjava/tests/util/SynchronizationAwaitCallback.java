package tools.vitruv.applications.pcmjava.tests.util;

@SuppressWarnings("all")
public interface SynchronizationAwaitCallback {
  public abstract void waitForSynchronization(final int numberOfExpectedSynchronizationCalls);
}
