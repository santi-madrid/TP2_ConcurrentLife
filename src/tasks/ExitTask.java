package tasks;

import util.Monitor;

public class ExitTask extends Thread {
  private int servedClients;
  private final boolean delayEnabled;
  private final int totalClients;
  private final Monitor monitor;
  private static final int TRANSITION_P14 = 11;
  private static final int delay = 0;

  public ExitTask(Monitor monitor, int totalClients, boolean delayEnabled) {
    this.setName("Exit");
    this.monitor = monitor;
    this.delayEnabled = delayEnabled;

    this.servedClients = 0;
    this.totalClients = totalClients;
  }

  @Override
  public void run() {
    while (servedClients < totalClients) {
      if (monitor.fireTransition(TRANSITION_P14)) {
        servedClients++;
        if (delayEnabled) {
          try {
            sleep(delay);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
        System.out.println(
            "Salieron "
                + servedClients
                + " personas ("
                + servedClients / (totalClients * 1.0) * 100
                + "%)");
      }
    }
    System.out.println("Todos los clientes han sido atendidos!");
  }
}
