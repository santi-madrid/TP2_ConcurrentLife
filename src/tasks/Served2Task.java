package tasks;

import util.Monitor;

public class Served2Task extends Thread {
  private final boolean delayEnabled;
  private final Monitor monitor;
  private static final int TRANSITION_P8 = 4;
  private static final int delay = 100;

  public Served2Task(Monitor monitor, boolean delayEnabled) {
    this.setName("Served2");
    this.monitor = monitor;
    this.delayEnabled = delayEnabled;
  }

  @Override
  public void run() {
    while (true) {
      if (monitor.fireTransition(TRANSITION_P8)) {
        if (delayEnabled) {
          try {
            sleep(delay);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
        // System.out.println("Atendiendo a un cliente en Served2...");
      }
    }
  }
}
