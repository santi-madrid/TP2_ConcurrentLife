package tasks;

import util.Monitor;

public class CancellationTask extends Thread {
  private int cancellations;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private final int[] transitionsToFire = {7, 8};
  private final int[] delaysNonBalanced = {0, 277};
  private final int[] delaysBalanced = {0, 100};
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
          try {
            if (delayEnabled) {
              isBalanced = monitor.getPolicy() != null && monitor.getPolicy().isBalanced();
              int delay = isBalanced ? delaysBalanced[i] : delaysNonBalanced[i];
              Thread.sleep(delay);
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }
      cancellations++; // Un ciclo de transiciones equivale a una cancelacion
      System.out.println("Cancelacion realizada (total de cancelaciones: " + cancellations + ")");
    }
  }

  public int getCancellations() {
    return cancellations;
  }
}
