package util;

import java.util.concurrent.ThreadLocalRandom;
import tasks.Agent1Task;
import tasks.Agent2Task;
import tasks.CancellationTask;
import tasks.ConfirmationTask;

public class CL_Policy {
  private static final int TRANSITION_P6 = 2;
  private static final int TRANSITION_P7 = 3;
  private static final int TRANSITION_P11 = 6;
  private static final int TRANSITION_P12 = 7;

  private static final double P6_PRIORITY_THRESHOLD = 0.75;
  private static final double P11_PRIORITY_THRESHOLD = 0.80;

  private final boolean balanced;

  private final Agent1Task agent1;
  private final Agent2Task agent2;
  private final ConfirmationTask confirmation;
  private final CancellationTask cancellation;

  public CL_Policy(
      boolean balanced,
      Agent1Task agent1,
      Agent2Task agent2,
      ConfirmationTask confirmation,
      CancellationTask cancellation) {
    this.balanced = balanced;
    this.agent1 = agent1;
    this.agent2 = agent2;
    this.confirmation = confirmation;
    this.cancellation = cancellation;
  }

  public boolean isBalanced() {
    return balanced;
  }

  private int chooseRandomTransition(int[] feasibleTransitions) {
    int chosenTransition;
    do {
      chosenTransition = ThreadLocalRandom.current().nextInt(feasibleTransitions.length);
    } while (feasibleTransitions[chosenTransition] != 1);

    return chosenTransition;
  }

  private Integer resolveP6n7Conflict(int[] feasibleTransitions) {
    if (feasibleTransitions[TRANSITION_P6] != 1 || feasibleTransitions[TRANSITION_P7] != 1) {
      return null; // No hay conflicto
    }

    if (balanced) {
      return agent1.getCustomersP6() < agent2.getCustomersP7() ? TRANSITION_P6 : TRANSITION_P7;
    }

    int totalCustomers = agent1.getCustomersP6() + agent2.getCustomersP7();
    return agent1.getCustomersP6() <= totalCustomers * P6_PRIORITY_THRESHOLD
        ? TRANSITION_P6
        : TRANSITION_P7;
  }

  private Integer resolveP11n12Conflict(int[] feasibleTransitions) {
    if (feasibleTransitions[TRANSITION_P11] != 1 || feasibleTransitions[TRANSITION_P12] != 1) {
      return null;
    }

    if (balanced) {
      return confirmation.getConfirmations() < cancellation.getCancellations()
          ? TRANSITION_P11
          : TRANSITION_P12;
    }

    int totalDecisions = confirmation.getConfirmations() + cancellation.getCancellations();
    return confirmation.getConfirmations() <= totalDecisions * P11_PRIORITY_THRESHOLD
        ? TRANSITION_P11
        : TRANSITION_P12;
  }

  public int selectTransition(int[] feasibleTransitions) {
    Integer p6n7Decision = resolveP6n7Conflict(feasibleTransitions);
    if (p6n7Decision != null) {
      return p6n7Decision;
    }

    Integer p11n12Decision = resolveP11n12Conflict(feasibleTransitions);
    if (p11n12Decision != null) {
      return p11n12Decision;
    }

    return chooseRandomTransition(feasibleTransitions);
  }
}
