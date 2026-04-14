package tasks;

import util.Monitor;

public class ConfirmationTask extends Thread {

  private int confirmations;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private final int[] transitionsToFire = {6, 9, 10};
  private final int[] delaysNonBalanced = {0, 54, 44};
  private final int[] delaysBalanced = {0, 100, 100};

  public ConfirmationTask(Monitor monitor, boolean delayEnabled) {
    this.setName("Confirmation");
    this.monitor = monitor;
    this.confirmations = 0;
    this.delayEnabled = delayEnabled;
  }

  @Override
  public void run() {
    while (true) {
      for (int i = 0; i < transitionsToFire.length; i++) {
        if (monitor.fireTransition(transitionsToFire[i])) {
          if (delayEnabled) {
            try {
              int delay =
                  monitor.getPolicy().isBalanced() ? delaysBalanced[i] : delaysNonBalanced[i];
              Thread.sleep(delay);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
          }
        }
      }
      confirmations++; // Un ciclo de transiciones equivale a una confirmacion
      System.out.println("Confirmacion realizada (total de confirmaciones: " + confirmations + ")");
    }
  }

  public int getConfirmations() {
    return confirmations;
  }
}
