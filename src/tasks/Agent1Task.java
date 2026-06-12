package tasks;

import util.Monitor;

public class Agent1Task extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_YELLOW = "\u001B[33m";

  private int customersP6;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private static final int TRANSITION_P6 = 2;
  private static final int DELAY = 0;

  public Agent1Task(Monitor monitor, boolean delayEnabled) {
    this.setName("Agent1");
    this.monitor = monitor;
    this.customersP6 = 0;
    this.delayEnabled = delayEnabled;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P6)) {
        if (delayEnabled) {
          try {
            sleep(DELAY);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }

        customersP6++;
        System.out.println(
            ANSI_YELLOW + "Agent1 atiende a su cliente [" + customersP6 + "]" + ANSI_RESET);
      }
    }
  }

  public int getCustomersP6() {
    return customersP6;
  }
}
