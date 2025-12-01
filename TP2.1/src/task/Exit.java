package task;
import util.*;
import java.util.concurrent.TimeUnit;

public class Exit implements Runnable {
    private Monitor monitor;
    private int transition;
    private int count;
    private boolean k;

    public Exit(Monitor M) {
        monitor = M;
        transition = 11;
        count = 0;
    }

    //duermen 1 segundo
    @Override
    public void run() {
        while(count<186) {
            k = monitor.fireTransition(transition);
            while(k==false){                               //Espera aqui hasta que se dispare T0
                k = monitor.fireTransition(transition);
            }
            
            count ++; //Aumenta contador y comienza de vuelta
            System.out.println("Salieron " + count + " personas");
        }
        System.out.println("Proceso de salida terminado");
    }
}
