package util;


import java.util.Vector;

public class Counters {
    private volatile int count;
    private volatile Vector<Integer> priorities;
    private volatile int count1;
    private volatile int count2;
    private volatile int countAgent1;
    private volatile int countAgent2;
    private volatile int countConfirm;
    private volatile int countCancel;
    private volatile boolean exit1;
    private volatile boolean exit2;

    public Counters(){
        count1=0;
        count2=0;
        countAgent1 = 0;
        countAgent2 = 0;
        countConfirm = 0;
        countCancel = 0;
        exit1=false;
        exit2=false;
        priorities = new Vector<>();
        for(int i = 0; i < 12 ; i++){
            priorities.add(i,0);
        }
    }

    public boolean getExit1(){
        return exit1;
    }
    public boolean getExit2(){
        return exit2;
    }
    public void setExit1(){
        exit1=true;
    }
    public void setExit2(){
        exit2=true;
    }

    public int getCount1(){return count1;}
    public int getCount2(){return count2;}

    public synchronized void increment(int transition) {
        if (transition == 2) {
            countAgent1++;
            count1++;

            if(count1==186){
                setExit1();
            }
        } else if (transition == 3) {
            countAgent2++;
            count1++;

            if(count1==186){
                setExit1();
            }
        } else if (transition == 6) {
            countConfirm++;
            count2++;

            if(count2==186){
                setExit2();
            }
        } else if (transition == 7){
            countCancel++;
            count2++;

            if(count2==186){
                setExit2();
            }
        }
    }

    public synchronized Vector<Integer> priorities(){
        if(countAgent1>countAgent2){
            priorities.set(3,1);
            priorities.set(2,0);
        }else{
            priorities.set(2,1);
            priorities.set(3,0);
        }
        if(countConfirm>countCancel){
            priorities.set(7,1);
            priorities.set(6,0);
        }else{
            priorities.set(6,1);
            priorities.set(7,0);
        }
        return  priorities;
    }
    //sincronizar y ver cual aumento el contador
}
