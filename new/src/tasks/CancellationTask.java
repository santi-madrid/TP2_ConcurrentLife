package tasks;

import Monitor.Monitor;

public class CancellationTask extends Thread {
  private int cancellations;
  private final Monitor monitor;
  private final int[] transitionsToFire = {7, 8};

  public CancellationTask(Monitor monitor) {
    this.setName("Cancellation");
    this.monitor = monitor;
    this.cancellations = 0;
  }

  @Override
  public void run() {
    while (true) {
      for (int transition : transitionsToFire) {
        if (monitor.fireTransition(transition)) {}
      }
      cancellations++; // Un ciclo de transiciones equivale a una cancelacion
      System.out.println("Cancelacion realizada (total de cancelaciones: " + cancellations + ")");
    }
  }

  public int getCancellations() {
    return cancellations;
  }
}
