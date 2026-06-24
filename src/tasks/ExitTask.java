package tasks;

import util.Monitor;

public class ExitTask extends Thread {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";

  private int servedClients;
  private final int totalClients;
  private final Monitor monitor;
  private static final int TRANSITION_P14 = 11;

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
        System.out.println("Salieron " + servedClients + " personas");
      }
    }
    System.out.println(ANSI_GREEN + "Todos los clientes han sido atendidos!" + ANSI_RESET);
  }
}
