package Monitor;

import PetriNet.PetriNet;
import Policy.CL_Policy;
import Queue.CL_Queue;
import config.PetriNetConfig;
import java.util.concurrent.Semaphore;

public class Monitor implements MonInterface {
  private static final int TRANSITIONS = PetriNetConfig.TRANSITIONS;

  private final Semaphore mutex = new Semaphore(1, true);
  private final CL_Queue queue = new CL_Queue();
  private final PetriNet rdp;
  private CL_Policy policy;
  private boolean retryFire;

  public Monitor(PetriNet rdp) {
    this.rdp = rdp;
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
          retryFire =
              false; // No hay hilos esperando, el hilo actual puede continuar sin liberar el mutex
        }
      } else {
        handleImpossibleTransition(transition);
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

  private void acquireMutex(int transition) {
    try {
      mutex.acquire();
      System.out.println(
          "Hilo "
              + Thread.currentThread().getName()
              + " ha adquirido el mutex para T"
              + transition);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean canFireTransition(int transition) {
    int[] firingVector = new int[TRANSITIONS];
    firingVector[transition] = 1;

    return rdp.isFirePossible(firingVector);
  }

  private void handleImpossibleTransition(int transition) {
    System.out.println(
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
    System.out.println("Hilo " + Thread.currentThread().getName() + " ha disparado T" + transition);
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
    System.out.println(
        "Hilo " + Thread.currentThread().getName() + " ha despertado T" + nextTransition);
  }
}
