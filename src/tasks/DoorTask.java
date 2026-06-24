package tasks;

import util.Monitor;

public class DoorTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";

  private int receivedClients;
  private final int totalClients;
  private final Monitor monitor;
  private final int[] transitionsToFire = {0, 1};

  public DoorTask(Monitor monitor, int totalClients) {
    this.setName("Door");
    this.monitor = monitor;

    this.receivedClients = 0;
    this.totalClients = totalClients;
  }

  @Override
  public void run() {
    while (receivedClients < totalClients) {
      int i = 0;
      while (i < transitionsToFire.length) {
        if (monitor.fireTransition(transitionsToFire[i])) {
          if (transitionsToFire[i] == 0) {
            receivedClients++;
            System.out.println("Entraron " + receivedClients + " personas");
          }
          i++;
        }
      }
    }
    System.out.println(ANSI_GREEN + "Todos los clientes han ingresado!" + ANSI_RESET);
  }
}
