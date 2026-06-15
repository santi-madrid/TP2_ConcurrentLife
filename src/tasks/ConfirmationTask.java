package tasks;

import util.Monitor;

public class ConfirmationTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";

  private int confirmations;
  private final boolean delayEnabled;
  private final Monitor monitor;
  private final int[] transitionsToFire = {6, 9, 10};
  private final int[] delaysNonBalanced = {0, 30, 20};
  private final int[] delaysBalanced = {0, 50, 50};

  public int getDelayT9(boolean balanced) {
    return balanced ? delaysBalanced[1] : delaysNonBalanced[1];
  }

  public int getDelayT10(boolean balanced) {
    return balanced ? delaysBalanced[2] : delaysNonBalanced[2];
  }

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
      System.out.println(
          ANSI_GREEN + "Confirmacion [" + confirmations + "] realizada" + ANSI_RESET);
    }
  }

  public int getConfirmations() {
    return confirmations;
  }
}
