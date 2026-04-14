package tasks;

import util.Monitor;

public class DoorTask extends Thread {
  private int receivedClients;
  private final boolean delayEnabled;
  private final int totalClients;
  private final Monitor monitor;
  private final int[] transitionsToFire = {0, 1};
  private final int[] delays = {0, 100};

  public DoorTask(Monitor monitor, int totalClients, boolean delayEnabled) {
    this.setName("Door");
    this.monitor = monitor;
    this.delayEnabled = delayEnabled;

    this.receivedClients = 0;
    this.totalClients = totalClients;
  }

  @Override
  public void run() {
    while (receivedClients < totalClients) {
      for (int transition : transitionsToFire) {
        if (monitor.fireTransition(transition)) {
          if (transition == 0) {
            receivedClients++;
            System.out.println(
                "Entraron "
                    + receivedClients
                    + " personas ("
                    + receivedClients / (totalClients * 1.0) * 100
                    + "%)");
          }
          if (delayEnabled) {
            try {
              sleep(delays[transition]);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
          }
        }
      }
      System.out.println("Todos los clientes han ingresado!");
    }
  }
}
