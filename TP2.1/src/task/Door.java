package task;
import util.*;
import java.util.concurrent.TimeUnit;

public class Door implements Runnable {
    private Monitor monitor;
    private int transition;
    private int count;
    private boolean k;

    public Door(Monitor M) {
        monitor = M;
        transition = 0;
        count = 0;
    }

    //duermen 1 segundo
    @Override
    public void run() {
        while(count<186) {
            k = monitor.fireTransition(transition);
            while(k==false){                                //Espera aqui hasta que se dispare T0
                k = monitor.fireTransition(transition);
            }

            try {
                Thread.sleep(50);                       //Simula el tiempo de tarea
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            k = monitor.fireTransition(1);                  //Dispara transicion T1
            count ++;                                      //Aumenta contador y comienza de vuelta
            System.out.println(count + " personas pasaron por la puerta");
        }
        System.out.println("Proceso de puerta terminado");
    }
}
