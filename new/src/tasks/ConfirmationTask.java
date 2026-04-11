package tasks;

import Monitor.Monitor;

public class ConfirmationTask extends Thread {

  private int confirmations;
  private final Monitor monitor;
  private final int[] transitionsToFire = {6, 9, 10};
  private final int[] delaysNonBalanced = {0, 54, 44};
  private final int[] delaysBalanced = {0, 100, 100};

  public ConfirmationTask(Monitor monitor) {
    this.setName("Confirmation");
    this.monitor = monitor;
    this.confirmations = 0;
  }

  @Override
  public void run() {
    while (true) {
      for (int i = 0; i < transitionsToFire.length; i++) {
        if (monitor.fireTransition(transitionsToFire[i])) {
          try {
            int delay = monitor.getPolicy().isBalanced() ? delaysBalanced[i] : delaysNonBalanced[i];
            Thread.sleep(delay);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
