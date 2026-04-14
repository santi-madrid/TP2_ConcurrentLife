import static java.lang.Thread.sleep;

import Monitor.Monitor;
import PetriNet.PetriNet;
import Policy.CL_Policy;
import config.PetriNetConfig;
import tasks.Agent1Task;
import tasks.Agent2Task;
import tasks.CancellationTask;
import tasks.ConfirmationTask;
import tasks.DoorTask;
import tasks.ExitTask;
import tasks.Served1Task;
import tasks.Served2Task;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    int totalClients = PetriNetConfig.INITIAL_TOKENS;
    boolean balancedPolicy = false;

    PetriNet rdp = new PetriNet();
    Monitor monitor = new Monitor(rdp);

    DoorTask door = new DoorTask(monitor, totalClients);
    Agent1Task agent1 = new Agent1Task(monitor);
    Agent2Task agent2 = new Agent2Task(monitor);
    Served1Task served1 = new Served1Task(monitor);
    Served2Task served2 = new Served2Task(monitor);
    ConfirmationTask confirmation = new ConfirmationTask(monitor);
    CancellationTask cancellation = new CancellationTask(monitor);
    ExitTask exit = new ExitTask(monitor, totalClients);

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
      e.printStackTrace();
    }

    System.out.println("=======================================================");
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
    System.out.print(
        "=======================================================\nTIEMPO TOTAL DE EJECUCIÓN: ");
    System.out.println(System.currentTimeMillis() - startTime + " ms\n");

    System.exit(0);
  }
}
