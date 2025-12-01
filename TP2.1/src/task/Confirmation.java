package task;

import util.*;

import java.util.concurrent.TimeUnit;

public class Confirmation implements Runnable{

    private Monitor monitor;
    private Counters count;
    private int transition;
    private int confirmationCount;
    private boolean k;

    public Confirmation(Monitor M , Counters counter){
        monitor = M;
        count = counter;
        transition = 6;
    }

    @Override
    public void run(){
        while(count.getCount2()<186) {
            k = monitor.fireTransition(transition);
            while(k==false){
                if(count.getExit2()){
                    break;
                }
                k = monitor.fireTransition(transition);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            k= monitor.fireTransition(9);
            while(k==false){
                if(count.getExit2()){
                    break;
                }
                k = monitor.fireTransition(9);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            k= monitor.fireTransition(10);
            while(k==false){
                if(count.getExit2()){
                    break;
                }
                k = monitor.fireTransition(10);
            }
            confirmationCount++;
            System.out.println(confirmationCount + " reservas confirmadas");
        }
        System.out.println("Proceso de confirmacion terminado");
    }
}