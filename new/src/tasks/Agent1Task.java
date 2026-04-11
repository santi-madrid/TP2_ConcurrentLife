package tasks;

import Monitor.Monitor;

public class Agent1Task extends Thread {

  private int customersP6;
  private final Monitor monitor;
  private static final int TRANSITION_P6 = 2;
  private static final int delay = 0;

  public Agent1Task(Monitor monitor) {
    this.setName("Agent1");
    this.monitor = monitor;
    this.customersP6 = 0;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P6)) {
        try {
          sleep(delay);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }

        customersP6++;
        System.out.println("Agent1 atendió a un cliente (total atendidos: " + customersP6 + ")");
      }
    }
  }

  public int getCustomersP6() {
    return customersP6;
  }
}
