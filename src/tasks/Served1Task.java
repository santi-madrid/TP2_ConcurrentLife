package tasks;

import util.Monitor;

public class Served1Task extends Thread {
  private final boolean delayEnabled;
  private final Monitor monitor;
  private static final int TRANSITION_P5 = 5;
  private static final int DELAY = 100;

  public Served1Task(Monitor monitor, boolean delayEnabled) {
    this.setName("Served1");
    this.monitor = monitor;
    this.delayEnabled = delayEnabled;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P5)) {
        if (delayEnabled) {
          try {
            sleep(DELAY);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    }
  }
}
