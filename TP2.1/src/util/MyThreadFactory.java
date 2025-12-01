package util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory {
    private int counter; //numero de hilos creado
    private String name; //nombre de base de cada hilo
    private List<String> stats; //informacion estadistica de los hilos

    public MyThreadFactory(String name) {
        counter = 0;
        this.name = name;
        stats = new ArrayList<String>();
    }
    //unico metodo de ThreadFactory, CREA HILO
    //genera nombre, crea hilo, guarda los datos estadisticos
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r,  "Hilo " + counter);
        counter++;
        //stats.add(String.format("Created thread " + counter + " with name "+ t.getName() +" on %s\n"
        //        , new Date()));
        return t;
    }

    //retorna data estadistica
    /*public String getStats() {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> it = stats.iterator();
        while (it.hasNext()) {
            buffer.append(it.next());
            buffer.append("\n");
        }
        return buffer.toString();
    }*/
}
