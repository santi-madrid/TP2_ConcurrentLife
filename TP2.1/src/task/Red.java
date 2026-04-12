package task;

import util.*;

// ! Agent y Red son extremadamente parecidos, la diferencia es que Red solamente dispara las
// ! transiciones 2 y 3, mientras que Agent tambien dispara las transiciones 4 y 5. Ademas, Red no
// ! tiene un contador de personas atendidas, mientras que Agent si lo tiene.

public class Red implements Runnable {
  private Monitor monitor;
  private Counters count;
  private int transition;
  private boolean k;

  public Red(Monitor M, Counters counter, int i) {
    monitor = M;
    count = counter;
    if (i == 0) {
      transition = 2;
    } else if (i == 1) {
      transition = 3;
    }
  }

  public void run() {
    while (count.getCount1() < 186) {
      k = monitor.fireTransition(transition);
      while (k == false) {
        if (count.getExit1()) {
          break;
        }
        k = monitor.fireTransition(transition);
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Proceso de hilo rojo " + transition + " terminado");
  }
}
