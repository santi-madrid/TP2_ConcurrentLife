package tasks;

import util.Monitor;

public class Agent2Task extends Thread {

  private int customersP7;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private static final int TRANSITION_P7 = 3;
  private static final int delay = 0;

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
        try {
          if (delayEnabled) {
            sleep(delay);
          }
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        customersP7++;
        System.out.println("Agent2 atendió a un cliente (total atendidos: " + customersP7 + ")");
      }
    }
  }

  public int getCustomersP7() {
    return customersP7;
  }
}
