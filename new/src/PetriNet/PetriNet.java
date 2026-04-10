package PetriNet;

import config.PetriNetConfig;
import java.util.concurrent.LinkedBlockingQueue;

public class PetriNet {
  private final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

  /* Constantes de clase */
  private static final int PLACES = PetriNetConfig.PLACES;
  private static final int TRANSITIONS = PetriNetConfig.TRANSITIONS;

  // Variables de instancia
  private int[] marking;
  private int[] enabledTransitions;
  private int lastFiredTransition;

  // Matriz de incidencia:
  // filas representan plazas (15)
  // columnas representan transiciones (12)
  private final int[][] incidenceMatrix;

  public PetriNet() {
    // marking.length == PLACES
    marking = new int[] {5, 1, 0, 0, 5, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0};
    enabledTransitions = new int[TRANSITIONS];
    incidenceMatrix =
        new int[][] {
          {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
          {-1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0},
          {-1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0},
          {0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, -1, -1, 1, 0, 1, 0},
          {0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, -1},
        };

    setEnabledTransitions(checkEnabledTransitions());
  }

  public LinkedBlockingQueue<Integer> getQueue() {
    return queue;
  }

  public int[] getMarking() {
    return marking;
  }

  public void setMarking(int[] marking) {
    this.marking = marking;
  }

  public int[] getEnabledTransitions() {
    return enabledTransitions;
  }

  public void setEnabledTransitions(int[] enabledTransitions) {
    this.enabledTransitions = enabledTransitions;
  }

  public int getLastFiredTransition() {
    return lastFiredTransition;
  }

  private int[] doEcFundamental(int[] firingVector) {
    int[] markingChange = new int[PLACES];
    for (int i = 0; i < PLACES; i++) {
      for (int j = 0; j < TRANSITIONS; j++) {
        markingChange[i] += incidenceMatrix[i][j] * firingVector[j];
      }
    }

    return markingChange;
  }

  private int[] getNewMarking(int[] firingVector) {
    int[] newMarking = doEcFundamental(firingVector);
    for (int i = 0; i < PLACES; i++) {
      newMarking[i] += marking[i];
    }
    return newMarking;
  }

  public boolean isFirePossible(int[] firingVector) {
    int[] newMarking = getNewMarking(firingVector);
    for (int i = 0; i < PLACES; i++) {
      if (newMarking[i] < 0) {
        return false;
      }
    }
    return true;
  }

  private boolean checkPlaceInvariants() {
    int[] expectedSums = {5, 1, 5, 1, 1, 1};
    int[] actualSums = {
      marking[0]
          + marking[2]
          + marking[3]
          + marking[5]
          + marking[8]
          + marking[9]
          + marking[11]
          + marking[12]
          + marking[13]
          + marking[14],
      marking[1] + marking[2],
      marking[2] + marking[3] + marking[4],
      marking[5] + marking[6],
      marking[7] + marking[8],
      marking[10] + marking[11] + marking[12] + marking[13]
    };

    for (int i = 0; i < expectedSums.length; i++) {
      if (actualSums[i] != expectedSums[i]) {
        return false;
      }
    }
    return true;
  }

  private void markTransitionIfEnabled(
      int[] enabledTransitions, int transitionIndex, boolean isEnabled) {
    if (!isEnabled) {
      return;
    }

    enabledTransitions[transitionIndex] = 1;

    // todo: TEMPORAL PETRI NET
  }

  public int[] checkEnabledTransitions() {
    int[] calcEnTransitions = new int[TRANSITIONS];

    markTransitionIfEnabled(calcEnTransitions, 0, marking[0] >= 1 && marking[4] >= 1);
    markTransitionIfEnabled(calcEnTransitions, 1, marking[2] == 1);
    markTransitionIfEnabled(calcEnTransitions, 2, marking[3] >= 1 && marking[6] == 1);
    markTransitionIfEnabled(calcEnTransitions, 3, marking[3] >= 1 && marking[7] == 1);
    markTransitionIfEnabled(calcEnTransitions, 4, marking[8] == 1);
    markTransitionIfEnabled(calcEnTransitions, 5, marking[5] == 1);
    markTransitionIfEnabled(calcEnTransitions, 6, marking[9] >= 1 && marking[10] == 1);
    markTransitionIfEnabled(calcEnTransitions, 7, marking[9] >= 1 && marking[10] == 1);
    markTransitionIfEnabled(calcEnTransitions, 8, marking[12] == 1);
    markTransitionIfEnabled(calcEnTransitions, 9, marking[11] == 1);
    markTransitionIfEnabled(calcEnTransitions, 10, marking[13] == 1);
    markTransitionIfEnabled(calcEnTransitions, 11, marking[14] >= 1);

    return calcEnTransitions;
  }

  private int getFiredTransition(int[] firingVector) {
    int firedTransition = -1;
    int firedCount = 0;

    for (int i = 0; i < TRANSITIONS; i++) {
      if (firingVector[i] == 1) {
        firedTransition = i;
        firedCount++;
      }
    }

    if (firedCount != 1) {
      throw new IllegalArgumentException(
          "The firing vector must contain exactly one active transition.");
    }

    return firedTransition;
  }

  public void updatePN(int[] firingVector) {

    if (!isFirePossible(firingVector)) {
      throw new IllegalStateException("The transition cannot be fired due to insufficient tokens.");
    }

    setMarking(getNewMarking(firingVector));
    queue.offer(getFiredTransition(firingVector));

    if (checkPlaceInvariants()) {
      System.out.println(
          "Transition "
              + lastFiredTransition
              + " fired successfully. Current marking: "
              + java.util.Arrays.toString(marking));
    } else {
      throw new IllegalStateException(
          "The place invariants have been violated. Current marking: "
              + java.util.Arrays.toString(marking));
    }

    setEnabledTransitions(checkEnabledTransitions());
  }

  // todo TEMPORAL PN METHODS

  public boolean isInTimeWindow(int transitionIndex) {
    return true; // Placeholder
  }

  public int getTimeWindow(int transitionIndex) {
    return 0; // Placeholder
  }
}
