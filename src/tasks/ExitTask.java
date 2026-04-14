package tasks;

import util.Monitor;

public class ExitTask extends Thread {
  private static final int TRANSITION_P14 = 11;
  private final int totalClients;
  private final Monitor monitor;
  private int servedClients;
  private static final int delay = 0;

  public ExitTask(Monitor monitor, int totalClients) {
    this.setName("Exit");
    this.monitor = monitor;

    this.servedClients = 0;
    this.totalClients = totalClients;
  }

  @Override
  public void run() {
    while (servedClients < totalClients) {
      if (monitor.fireTransition(TRANSITION_P14)) {
        servedClients++;
        try {
          sleep(delay);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
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
