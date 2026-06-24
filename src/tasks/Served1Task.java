package tasks;

import util.Monitor;

public class Served1Task extends Thread {
  private final Monitor monitor;
  private static final int TRANSITION_P5 = 5;

  public Served1Task(Monitor monitor) {
    this.setName("Served1");
    this.monitor = monitor;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P5)) {}
    }
  }
}
