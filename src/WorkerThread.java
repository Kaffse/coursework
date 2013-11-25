import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class WorkerThread implements Runnable {

    String file;
    ConcurrentLinkedQueue<String> list;
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable;
    ConcurrentLinkedQueue<String> workQ;
    String[] dirlist;
    Thread wt;

    public WorkerThread(String f, ConcurrentLinkedQueue<String> l, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> dt, ConcurrentLinkedQueue<String> wq, String[] dl, Thread wait ){
        file = f;
        list = l;
        deptable = dt;
        workQ = wq;
        dirlist = dl;
        wt = wait;
    }

    public void run(){
        FileReader f = null;

        for (String d : dirlist){
            try {
                f = new FileReader(d + file);
            }
            catch (FileNotFoundException e) {
                continue;
            }
            break;
        }
        if (f == null){
            System.out.println("Error," + file + " not found!");
            System.exit(0);
        }

        Scanner s = null;
        s = new Scanner(f);

        while (s.hasNext()){
            String line = s.nextLine();

            int i = 1;

            if (!line.contains("#include")){
                continue;
            }

            while (i <= line.length() && line.substring(i - 1, i).equals(" ")){
                i++;
            }

            if (!line.substring(i, i + 7).equals("include")){
                continue;
            }

            i = i + 7;

            while (i < line.length() && line.substring(i, i + 1).equals(" ")){
                i++;
            }

            i++;

            if (!line.substring(i - 1, i).equals("\"")){
                continue;
            }

            int j = i++;

            while (i < line.length() && !line.substring(i, i + 1).equals("\"")){
                i++;
            }
            String name = line.substring(j, i);
            list.add(name);
            if (deptable.get(name) != null){
                continue;
            }
            deptable.put(name, new ConcurrentLinkedQueue<String>());
            workQ.add(name);
            deptable.put(file, list);
            wt.notify();
        }
    }
}
