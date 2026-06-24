package tasks;

import util.Monitor;

public class ConfirmationTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";

  private int confirmations;
  private final Monitor monitor;
  private final int[] transitionsToFire = {6, 9, 10};

  public ConfirmationTask(Monitor monitor) {
    this.setName("Confirmation");
    this.monitor = monitor;
    this.confirmations = 0;
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
      confirmations++; // Un ciclo de transiciones equivale a una confirmacion
      System.out.println(
          ANSI_GREEN + "Confirmacion [" + confirmations + "] realizada" + ANSI_RESET);
    }
  }

  public int getConfirmations() {
    return confirmations;
  }
}
