package tasks;

import Monitor.Monitor;

public class Served2Task extends Thread {
  private static final int TRANSITION_P8 = 4;
  private final Monitor monitor;

  public Served2Task(Monitor monitor) {
    this.setName("Served2");
    this.monitor = monitor;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P8)) {
        // System.out.println("Atendiendo a un cliente en Served2...");
      }
    }
  }
}
