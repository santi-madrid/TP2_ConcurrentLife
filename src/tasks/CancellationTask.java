package tasks;

import util.Monitor;

public class CancellationTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";

  private int cancellations;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private final int[] transitionsToFire = {7, 8};
  private final int[] delaysNonBalanced = {0, 100};
  private final int[] delaysBalanced = {0, 50};

  public int getDelayT8(boolean balanced) {
    return balanced ? delaysBalanced[1] : delaysNonBalanced[1];
  }

  private boolean isBalanced;

  public CancellationTask(Monitor monitor, boolean delayEnabled) {
    this.setName("Cancellation");
    this.monitor = monitor;
    this.cancellations = 0;
    this.delayEnabled = delayEnabled;
  }

  @Override
  public void run() {
    while (true) {
      for (int i = 0; i < transitionsToFire.length; i++) {
        if (monitor.fireTransition(transitionsToFire[i])) {
          if (delayEnabled) {
            try {
              isBalanced = monitor.getPolicy() != null && monitor.getPolicy().isBalanced();
              int delay = isBalanced ? delaysBalanced[i] : delaysNonBalanced[i];
              Thread.sleep(delay);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
          }
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
