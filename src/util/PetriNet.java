package util;

import config.PetriNetConfig;

public class PetriNet {

  /* Constantes de clase */
  private static final int PLACES = PetriNetConfig.PLACES;
  private static final int TRANSITIONS = PetriNetConfig.TRANSITIONS;
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GRAY = "\u001B[90m";

  // Variables de instancia
  private int[] marking;
  private int[] enabledTransitions;
  private int lastFiredTransition;
  private final boolean debugEnabled;

  // Matriz de incidencia:
  // filas representan plazas (15)
  // columnas representan transiciones (12)
  private final int[][] incidenceMatrix;

  public PetriNet(boolean debugEnabled) {
    this.debugEnabled = debugEnabled;
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
    int[] expectedSums = {1, 5, 1, 1, 1, 5};
    int[] actualSums = {
      marking[1] + marking[2],
      marking[2] + marking[3] + marking[4],
      marking[5] + marking[6],
      marking[7] + marking[8],
      marking[10] + marking[11] + marking[12] + marking[13],
      marking[0]
          + marking[2]
          + marking[3]
          + marking[5]
          + marking[8]
          + marking[9]
          + marking[11]
          + marking[12]
          + marking[13]
          + marking[14]
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

  public void updatePN(int[] firingVector) {

    if (!isFirePossible(firingVector)) {
      throw new IllegalStateException("The transition cannot be fired due to insufficient tokens.");
    }

    int firedTransition = -1;
    for (int i = 0; i < TRANSITIONS; i++) {
      if (firingVector[i] == 1) {
        firedTransition = i;
        break;
      }
    }

    setMarking(getNewMarking(firingVector));
    lastFiredTransition = firedTransition;

    if (checkPlaceInvariants()) {
      if (debugEnabled) {
        System.out.println(
            ANSI_GRAY
                + "Invariantes de plazas respetados al disparar T"
                + firedTransition
                + ". Marking: "
                + java.util.Arrays.toString(marking)
                + ANSI_RESET);
      }
    } else {
      throw new IllegalStateException(
          "The place invariants have been violated. Current marking: "
              + java.util.Arrays.toString(marking));
    }

    setEnabledTransitions(checkEnabledTransitions());
  }
}
