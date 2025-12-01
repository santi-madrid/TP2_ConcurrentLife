package core;

import task.*;
import util.*;

public class Main {
    public static void main(String[] args) {
        Counters sharedCounter1 = new Counters();
        Monitor sharedMonitor = new Monitor(sharedCounter1);
        MyThreadFactory factory = new MyThreadFactory("util.MyThreadFactory");
        Door door = new Door(sharedMonitor);
        Confirmation confirmation = new Confirmation(sharedMonitor,sharedCounter1);
        Cancelation cancelation = new Cancelation(sharedMonitor,sharedCounter1);
        Exit exit = new Exit(sharedMonitor);

        Thread thread;
        System.out.print("Starting the Threads\n");

        for(int i=0; i<1; i++){
            thread=factory.newThread(door);//tambien tarea del hilo
            thread.start();
        }

        for(int i=0; i<2; i++){
            thread=factory.newThread(new Agent(sharedMonitor, sharedCounter1 , i));//tambien tarea del hilo
            thread.start();
        }

        for(int i=0; i<1; i++){
            thread=factory.newThread(confirmation);//tambien tarea del hilo
            thread.start();
        }

        for(int i=0; i<1; i++){
            thread=factory.newThread(cancelation);//tambien tarea del hilo
            thread.start();
        }

        for(int i=0; i<1; i++){
            thread=factory.newThread(exit);//tambien tarea del hilo
            thread.start();
        }


    }
}