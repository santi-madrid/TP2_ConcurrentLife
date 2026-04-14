package tasks;

import Monitor.Monitor;

public class DoorTask extends Thread {
  private final int[] transitionsToFire = {0, 1};
  private final int[] delays = {0, 100};
  private final int totalClients;
  private final Monitor monitor;
  private int receivedClients;

  public DoorTask(Monitor monitor, int totalClients) {
    this.setName("Door");
    this.monitor = monitor;

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
          try {
            sleep(delays[transition]);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }
      System.out.println("Todos los clientes han ingresado!");
    }
  }
}
