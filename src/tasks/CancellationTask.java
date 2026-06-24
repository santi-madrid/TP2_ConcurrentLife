package tasks;

import util.Monitor;

public class CancellationTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";

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
      int i = 0;
      while (i < transitionsToFire.length) {
        if (monitor.fireTransition(transitionsToFire[i])) {
          i++;
        }
      }
      cancellations++; // Un ciclo de transiciones equivale a una cancelacion
      System.out.println(ANSI_RED + "Cancelacion [" + cancellations + "] realizada" + ANSI_RESET);
    }
  }

  public int getCancellations() {
    return cancellations;
  }
}
