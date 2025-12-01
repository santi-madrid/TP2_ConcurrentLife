package task;

import util.*;

import java.util.concurrent.TimeUnit;

public class Cancelation implements Runnable{
    private Monitor monitor;
    private Counters count;
    private int transition;
    private int cancelationCount;
    private boolean k;

    public Cancelation(Monitor M , Counters counter){
        monitor = M;
        count = counter;
        transition = 7;
    }
    //duermen 1 segundo
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
            k = monitor.fireTransition(8);
            while(k==false){
                if(count.getExit2()){
                    break;
                }
                k = monitor.fireTransition(8);
            }
            cancelationCount++;
            System.out.println(cancelationCount + " reservas canceladas ");
        }
        System.out.println("Proceso de cancelacion terminado");
    }
}