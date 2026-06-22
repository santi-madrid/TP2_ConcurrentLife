package util;

import config.PetriNetConfig;
import java.util.concurrent.Semaphore;

public class Monitor implements MonInterface {
  private static final int TRANSITIONS = PetriNetConfig.TRANSITIONS;
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GRAY = "\u001B[90m";

  private final Semaphore mutex = new Semaphore(1, true);
  private final CL_Queue queue = new CL_Queue();
  private final PetriNet rdp;
  private final boolean debugEnabled;
  private CL_Policy policy;
  private CL_Logger logger;
  private boolean retryFire;

  public Monitor(PetriNet rdp, boolean debugEnabled) {
    this.rdp = rdp;
    this.debugEnabled = debugEnabled;
  }

  @Override
  public boolean fireTransition(int transition) {
    acquireMutex(transition);

    retryFire = true;

    while (retryFire) {
      if (canFireTransition(transition)) {
        fireTransitionAndUpdateState(transition);

        if (hasWaitingThreads()) {
          wakeUpWaitingThreads();
          return true;
        } else {
          retryFire = false; // No hay hilos esperando: no se hace handoff.
          //  El hilo sale del bucle y libera el mutex normalmente (línea 51).
        }
      } else {
        try {
          handleImpossibleTransition(transition);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
    }
    mutex.release();
    return true;
  }

  public void setPolicy(CL_Policy policy) {
    this.policy = policy;
  }

  public CL_Policy getPolicy() {
    return policy;
  }

  public void setLogger(CL_Logger logger) {
    this.logger = logger;
  }

  private void debugLog(String message) {
    if (debugEnabled) {
      System.out.println(ANSI_GRAY + message + ANSI_RESET);
    }
  }

  private void acquireMutex(int transition) {
    try {
      mutex.acquire();
      debugLog(
          "Hilo "
              + Thread.currentThread().getName()
              + " ha adquirido el mutex para T"
              + transition);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private boolean canFireTransition(int transition) {
    int[] firingVector = new int[TRANSITIONS];
    firingVector[transition] = 1;

    return rdp.isFirePossible(firingVector);
  }

  private void handleImpossibleTransition(int transition) throws InterruptedException {
    debugLog(
        "Hilo "
            + Thread.currentThread().getName()
            + " no puede disparar T"
            + transition
            + " y se bloquea");
    mutex.release();
    queue.acquireTransition(transition);
  }

  private void fireTransitionAndUpdateState(int transition) {
    int[] firingVector = new int[TRANSITIONS];
    firingVector[transition] = 1;

    rdp.updatePN(firingVector);

    if (logger != null) {
      logger.logTransition(transition);
    }

    debugLog("Hilo " + Thread.currentThread().getName() + " ha disparado T" + transition);
  }

  private boolean hasWaitingThreads() {
    int[] enabledTransitions = rdp.getEnabledTransitions();
    int[] waitingTransitions = queue.getWaitingTransitions();

    for (int i = 0; i < TRANSITIONS; i++) {
      if (enabledTransitions[i] * waitingTransitions[i] == 1) {
        return true; // Transicion habilitada Y en cola de espera
      }
    }
    return false;
  }

  private void wakeUpWaitingThreads() {
    int[] enabledTransitions = rdp.getEnabledTransitions();
    int[] waitingTransitions = queue.getWaitingTransitions();
    int[] feasibleTransitions = new int[TRANSITIONS];

    for (int i = 0; i < TRANSITIONS; i++) {
      // Cuales transiciones estan habilitadas Y en cola de espera?
      feasibleTransitions[i] = enabledTransitions[i] * waitingTransitions[i];
    }

    int nextTransition = policy.selectTransition(feasibleTransitions);
    queue.releaseTransition(nextTransition);
    debugLog("Hilo " + Thread.currentThread().getName() + " ha despertado T" + nextTransition);
  }
}
