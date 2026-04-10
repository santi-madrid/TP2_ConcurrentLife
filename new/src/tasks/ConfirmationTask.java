package tasks;

import Monitor.Monitor;

public class ConfirmationTask extends Thread {

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
      for (int transition : transitionsToFire) {
        if (monitor.fireTransition(transition)) {}
      }
      confirmations++; // Un ciclo de transiciones equivale a una confirmacion
      System.out.println("Confirmacion realizada (total de confirmaciones: " + confirmations + ")");
    }
  }

  public int getConfirmations() {
    return confirmations;
  }
}
