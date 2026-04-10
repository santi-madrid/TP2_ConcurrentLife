package Queue;

import config.PetriNetConfig;
import java.util.concurrent.Semaphore;

public class CL_Queue {

  /* Constantes de clase */
  private static final int TRANSITIONS = PetriNetConfig.TRANSITIONS;

  private Semaphore[] waitingThreads;
  private int[] waitingTransitions;

  public CL_Queue() {
    this.waitingThreads = new Semaphore[TRANSITIONS];
    this.waitingTransitions = new int[TRANSITIONS];

    for (int i = 0; i < TRANSITIONS; i++) {
      this.waitingThreads[i] = new Semaphore(0, true);
    }
  }

  public boolean hasWaitingThreads() {
    for (int i = 0; i < TRANSITIONS; i++) {
      if (waitingThreads[i].hasQueuedThreads()) {
        return false;
      }
    }
    return true;
  }

  public int[] getWaitingTransitions() {
    return waitingTransitions;
  }

  public void acquireTransition(int transition) {
    waitingTransitions[transition] = 1;
    try {
      waitingThreads[transition].acquire();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void releaseTransition(int transition) {
    if (waitingTransitions[transition] == 1) {
      waitingTransitions[transition] = 0;
      waitingThreads[transition].release();
    }
  }
}
