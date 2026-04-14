package tasks;

import util.Monitor;

public class Agent2Task extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_YELLOW = "\u001B[33m";

  private int customersP7;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private static final int TRANSITION_P7 = 3;
  private static final int DELAY = 0;

  public Agent2Task(Monitor monitor, boolean delayEnabled) {
    this.setName("Agent2");
    this.monitor = monitor;
    this.customersP7 = 0;
    this.delayEnabled = delayEnabled;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P7)) {
        if (delayEnabled) {
          try {
            sleep(DELAY);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }

        customersP7++;
        System.out.println(
            ANSI_YELLOW + "Agent2 atiende a su cliente [" + customersP7 + "]" + ANSI_RESET);
      }
    }
  }

  public int getCustomersP7() {
    return customersP7;
  }
}
