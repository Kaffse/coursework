import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.io.*;

public class WorkerThread implements Runnable {

    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable;
    LinkedBlockingQueue<String> workQ;
    String[] dirlist;
    ConcurrentCounter count;

    public WorkerThread(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> dt, LinkedBlockingQueue<String> wq, String[] dl, ConcurrentCounter c){
        deptable = dt;
        workQ = wq;
        dirlist = dl;
        count = c;
    }

    /*private String getToProcess(){
        if (workQ.isEmpty()){
            System.out.println("WorkQ Empty...");
            count.getAndDecrement();
            while(workQ.isEmpty()){
                if (count.get() <= 0){
                    System.out.println("Oh! Done!");
                    return "D.O.N.E";
                }
                try{
                    System.out.println("Waiting... Also Count: " + count.get());
                    wait();
                }
                catch (InterruptedException e){
                    System.out.println ("Worker Interrupted");
                    return "D.O.N.E";
                }
                catch (IllegalMonitorStateException e){
                    System.out.println("Illegal Monitor State");
                }
            }
            System.out.println("Oh! More stuff to do!");
            count.getAndIncrement();
        }

        //System.out.println("WorkQ not empty. Getting File");
        String file = workQ.poll();
        return file;
    }*/

    public void run(){
        while (true){
            //System.out.println("Get File...");
            //String file = getToProcess();
            //System.out.println("File Retrieved");
            String file = null;
            ReentrantLock lock = count.getLock();

            lock.lock();
            try{
                count.decrement();
            }
            finally{
                lock.unlock();
            }

            try{
                file = workQ.take();
            }
            catch(InterruptedException e){
                System.out.println("Worker Interrupted");
                return;
            }

            lock.lock();
            try{
                count.increment();
            }
            finally{
                lock.unlock();
            }

            if (file == null){
                return;
            }

            FileReader f = null;

            //System.out.println("Finding File...");
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
            //System.out.println("File Found");

            Scanner s = null;
            s = new Scanner(f);

            ConcurrentLinkedQueue<String> list = deptable.get(file);

            //System.out.println("Processing Lines...");
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
                try{
                    workQ.put(name);
                }
                catch(InterruptedException e){
                    System.out.println("Worker Interrupted");
                    return;
                }

                /*if (count.get() < THREADS){
                    try{
                        notify();
                    }
                    catch (IllegalMonitorStateException e){
                        System.out.println("No Threads Waiting");
                    }
                }*/
                deptable.put(file, list);
            }
            //System.out.println("Processing Finished");
        }
    }
}
