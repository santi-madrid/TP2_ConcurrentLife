package tasks;

import util.Monitor;

public class Served2Task extends Thread {
  private final Monitor monitor;
  private static final int TRANSITION_P8 = 4;

  public Served2Task(Monitor monitor) {
    this.setName("Served2");
    this.monitor = monitor;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P8)) {}
    }
  }
}
