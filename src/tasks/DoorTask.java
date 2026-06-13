package tasks;

import util.Monitor;

public class DoorTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";

  private int receivedClients;
  private final boolean delayEnabled;
  private final int totalClients;
  private final Monitor monitor;
  private final int[] transitionsToFire = {0, 1};
  private final int[] delays = {0, 60};

  public int getDelayT1() {
    return delays[1];
  }

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
            System.out.println("Entraron " + receivedClients + " personas");
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
    }
    System.out.println(ANSI_GREEN + "Todos los clientes han ingresado!" + ANSI_RESET);
  }
}
