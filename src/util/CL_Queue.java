package util;

import config.PetriNetConfig;
import java.util.concurrent.Semaphore;

public class CL_Queue {

  /* Constantes de clase */
  private static final int TRANSITIONS = PetriNetConfig.TRANSITIONS;

  private final Semaphore[] waitingThreads;
  private final int[] waitingTransitions;

  public CL_Queue() {
    this.waitingThreads = new Semaphore[TRANSITIONS];
    this.waitingTransitions = new int[TRANSITIONS];

    for (int i = 0; i < TRANSITIONS; i++) {
      this.waitingThreads[i] = new Semaphore(0, true);
    }
  }

  public int[] getWaitingTransitions() {
    return waitingTransitions;
  }

  public void acquireTransition(int transition) throws InterruptedException {
    waitingTransitions[transition] = 1;
    waitingThreads[transition].acquire();
  }

  public void releaseTransition(int transition) {
    if (waitingTransitions[transition] == 1) {
      waitingTransitions[transition] = 0;
      waitingThreads[transition].release();
    }
  }
}
