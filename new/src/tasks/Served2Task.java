package tasks;

import Monitor.Monitor;

public class Served2Task extends Thread {
  private static final int TRANSITION_P8 = 4;
  private final Monitor monitor;
  private static final int delay = 100;

  public Served2Task(Monitor monitor) {
    this.setName("Served2");
    this.monitor = monitor;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P8)) {
        try {
          sleep(delay);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        // System.out.println("Atendiendo a un cliente en Served2...");
      }
    }
  }
}
