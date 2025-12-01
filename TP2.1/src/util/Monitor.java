package util;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Monitor implements MonitorI {
    private Semaphore mutex;
    private final Semaphore tran0;
    private final Semaphore tran1;
    private final Semaphore tran2;
    private final Semaphore tran3;
    private final Semaphore tran4;
    private final Semaphore tran5;
    private final Semaphore tran6;
    private final Semaphore tran7;
    private final Semaphore tran8;
    private final Semaphore tran9;
    private final Semaphore tran10;
    private final Semaphore tran11;
    private boolean k;
    private boolean f;
    private final RdP rdp;
    private final ArrayList<Semaphore> cola;
    Counters sharedCounter;

    public Monitor(Counters counter) {
        mutex = new Semaphore(1);
        tran0 = new Semaphore(0);
        tran1 = new Semaphore(0);
        tran2 = new Semaphore(0);
        tran3 = new Semaphore(0);
        tran4 = new Semaphore(0);
        tran5 = new Semaphore(0);
        tran6 = new Semaphore(0);
        tran7 = new Semaphore(0);
        tran8 = new Semaphore(0);
        tran9 = new Semaphore(0);
        tran10 = new Semaphore(0);
        tran11 = new Semaphore(0);
        rdp = new RdP();
        cola = new ArrayList<>();
        cola.add(0,tran0);
        cola.add(1,tran1);
        cola.add(2,tran2);
        cola.add(3,tran3);
        cola.add(4,tran4);
        cola.add(5,tran5);
        cola.add(6,tran6);
        cola.add(7,tran7);
        cola.add(8,tran8);
        cola.add(9,tran9);
        cola.add(10,tran10);
        cola.add(11,tran11);
        sharedCounter = counter;
        f = true;
    }

    @Override
    public boolean fireTransition(int transition) {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        k = rdp.fireTransition(transition);
        if (k == true) {
            if (transition == 2 || transition == 3 || transition == 6 || transition == 7) {
                sharedCounter.increment(transition);
            }
            Vector<Integer> m = getM();
            int s = 0;
            for (int i = 0; i < m.size(); i++) {
                s = s + m.get(i);
            }
            if (s > 0) {
                int a = getPolitics(m);
                cola.get(a).release();
                if(sharedCounter.getExit1() && sharedCounter.getExit2()){
                    for (int i=0; i<cola.size(); i++){
                        cola.get(i).release();
                    }
                }
                mutex.release();
                return true;
            } else {
                mutex.release();
                return true;
            }
        } else {
            mutex.release();
            try {
                cola.get(transition).acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    }

    private Vector<Integer> getWaiting() {
        Vector<Integer> waiting = new Vector<>();
        for (Semaphore i : cola) {
            if (i.getQueueLength() > 0) {
                waiting.add(1);
            } else {
                waiting.add(0);
            }
        }
        return waiting;
    }

    private Vector<Integer> getM() {
        Vector<Integer> m = new Vector<>();
        Vector<Integer> s = rdp.getSenTransitions();
        Vector<Integer> c = getWaiting();
        int n = 0;
        for (int i = 0; i < getWaiting().size(); i++) {
            n = s.get(i) * c.get(i);
            m.add(n);
        }
        System.out.println(m + "vector m" ); //Disponibles para disparar en cola
        System.out.println(s + "vector s" ); //sensibilizadas
        System.out.println(c + "vector c" ); //son las transiciones que están esperando
        return m;
    }

    private int getPolitics(Vector<Integer> m) {
        Vector<Integer> p = sharedCounter.priorities();
        Vector<Integer> j = new Vector<>();
        int t;
        int f;
        for (int i = 0; i < m.size(); i++) {
            f = m.get(i) * p.get(i);
            j.add(f);
        }
        System.out.println(p + "vector p" ); //prioridad
        System.out.println(j + "vector j" ); //siguiente a disparar
        for(int i = 0; i < j.size(); i++) {
            if(j.get(i)==1){
               return i;
            }
        }
        for(int i = 0; i < m.size(); i++) {
            if (m.get(i) != 0) {
                return i;
            }
        }
        return 0;
    }

}