package task;

import util.*;

import javax.naming.Name;
import java.util.concurrent.TimeUnit;

public class Agent implements Runnable{
    private Monitor monitor;
    private Counters count;
    private int transition;
    private int agentCount;
    private boolean k;

    public Agent(Monitor M , Counters counter, int i){
        monitor = M;
        count = counter;
        if(i==0){
            transition=2;
        }else if (i==1){
            transition=3;
        }
        agentCount=0;
    }

    @Override
    public void run(){
        while(count.getCount1()<186) {
            k = monitor.fireTransition(transition);
            while (k==false){
                if(count.getExit1()){
                    break;
                }
                k = monitor.fireTransition(transition);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(transition==2) {
                k = monitor.fireTransition(5);
                while(k==false){
                    if(count.getExit1()){
                        break;
                    }
                    k = monitor.fireTransition(5);
                }
            }else if (transition==3){
                k = monitor.fireTransition(4);
                while(k==false){
                    if(count.getExit1()){
                        break;
                    }
                    k = monitor.fireTransition(4);
                }
            }
            System.out.println(count.getCount1()+" personas fueron atendidas");
            agentCount++;
            System.out.println(agentCount + " personas pasaron por el agente " + transition);
        }
        System.out.println("Proceso de agente " + transition + " terminado");
    }
}