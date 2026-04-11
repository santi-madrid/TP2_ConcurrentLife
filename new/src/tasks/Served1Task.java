package tasks;

import Monitor.Monitor;

public class Served1Task extends Thread {
  private static final int TRANSITION_P5 = 5;
  private final Monitor monitor;
  private static final int delay = 100;

  public Served1Task(Monitor monitor) {
    this.setName("Served1");
    this.monitor = monitor;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P5)) {
        try {
          sleep(delay);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        // System.out.println("Atendiendo a un cliente en Served1...");
      }
    }
  }
}
