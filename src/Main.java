import static java.lang.Thread.sleep;

import config.PetriNetConfig;
import java.io.IOException;
import tasks.Agent1Task;
import tasks.Agent2Task;
import tasks.CancellationTask;
import tasks.ConfirmationTask;
import tasks.DoorTask;
import tasks.ExitTask;
import tasks.Served1Task;
import tasks.Served2Task;
import util.CL_Logger;
import util.CL_Policy;
import util.Monitor;
import util.PetriNet;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    int totalClients = PetriNetConfig.INITIAL_TOKENS;
    boolean balancedPolicy = true;
    boolean delayEnabled = true;
    boolean debugEnabled = false;

    for (String arg : args) {
      if ("--debug".equals(arg)) {
        debugEnabled = true;
      }
    }

    PetriNet rdp = new PetriNet();
    Monitor monitor = new Monitor(rdp, debugEnabled);
    try {
      CL_Logger logger = new CL_Logger("logs");
      monitor.setLogger(logger);
    } catch (IOException e) {
      System.err.println("No se pudo inicializar el logger: " + e.getMessage());
      System.exit(1);
    }

    DoorTask door = new DoorTask(monitor, totalClients, delayEnabled);
    Agent1Task agent1 = new Agent1Task(monitor, delayEnabled);
    Agent2Task agent2 = new Agent2Task(monitor, delayEnabled);
    Served1Task served1 = new Served1Task(monitor, delayEnabled);
    Served2Task served2 = new Served2Task(monitor, delayEnabled);
    ConfirmationTask confirmation = new ConfirmationTask(monitor, delayEnabled);
    CancellationTask cancellation = new CancellationTask(monitor, delayEnabled);
    ExitTask exit = new ExitTask(monitor, totalClients, delayEnabled);

    CL_Policy policy = new CL_Policy(balancedPolicy, agent1, agent2, confirmation, cancellation);
    monitor.setPolicy(policy);

    Thread[] threads = {door, agent1, agent2, served1, served2, confirmation, cancellation, exit};

    long startTime = System.currentTimeMillis();
    for (Thread thread : threads) {
      thread.start();
    }

    try {
      exit.join();
      sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Ejecución interrumpida: " + e.getMessage());
    }

    final String cfgTitle = " CONFIGURACION ";
    final String resTitle = " RESULTADOS ";
    int width = Math.max(cfgTitle.length(), resTitle.length()) + 40;
    String line = "=".repeat(width);
    String cfgLine =
        "=".repeat((width - cfgTitle.length()) / 2)
            + cfgTitle
            + "=".repeat(width - cfgTitle.length() - (width - cfgTitle.length()) / 2);
    String resLine =
        "=".repeat((width - resTitle.length()) / 2)
            + resTitle
            + "=".repeat(width - resTitle.length() - (width - resTitle.length()) / 2);

    System.out.println(cfgLine);
    System.out.printf("INITIAL_TOKENS = %d%n", totalClients);
    System.out.printf("delayEnabled   = %b%n", delayEnabled);
    System.out.printf("balancedPolicy = %b%n", balancedPolicy);
    System.out.printf("debugEnabled   = %b%n", debugEnabled);
    System.out.println(resLine);

    final String statsFormat = "%-40s %4d (%6.2f%%)%n";

    System.out.printf(
        statsFormat,
        "Clientes atendidos por el agente en P6:",
        agent1.getCustomersP6(),
        (agent1.getCustomersP6() * 100.0) / (agent1.getCustomersP6() + agent2.getCustomersP7()));

    System.out.printf(
        statsFormat,
        "Clientes atendidos por el agente en P7:",
        agent2.getCustomersP7(),
        (agent2.getCustomersP7() * 100.0) / (agent1.getCustomersP6() + agent2.getCustomersP7()));

    System.out.printf(
        statsFormat,
        "Reservas confirmadas:",
        confirmation.getConfirmations(),
        (confirmation.getConfirmations() * 100.0)
            / (confirmation.getConfirmations() + cancellation.getCancellations()));

    System.out.printf(
        statsFormat,
        "Reservas canceladas:",
        cancellation.getCancellations(),
        (cancellation.getCancellations() * 100.0)
            / (confirmation.getConfirmations() + cancellation.getCancellations()));
    System.out.printf(
        "\nTIEMPO TOTAL DE EJECUCIÓN: %d ms%n", System.currentTimeMillis() - startTime);
    System.out.println(line);

    System.exit(0);
  }
}
