import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.io.*;

public class includeCrawler{

    /*static class WaitClass implements Runnable{

        WaitClass(){}

        public void run(){
            try{
                wait();
            }
            catch(InterruptedException e){
                System.out.println("Caught Wait Exception!");
            }
        }

    }*/

    static int THREADS = 2;
    //static ConcurrentLinkedQueue<Thread> threadlist = new ConcurrentLinkedQueue<Thread> threadlist;
    static ArrayList<Thread> threadlist = new ArrayList<Thread>();

    private static String parseDir(String dir) {
        if (!dir.substring(dir.length() - 1).equals("/")){
            return dir + "/";
        }
        else {
            return dir;
        }
    }

    private static void printDeps(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable, ConcurrentHashMap<String, Boolean> printed, ConcurrentLinkedQueue<String> process) {
        while (!process.isEmpty()){
            String cur = process.poll();
            ConcurrentLinkedQueue<String> deps = deptable.get(cur);

            for (String s : deps){
                if (printed.get(s) != null){
                    continue;
                }
                System.out.print(" " + s);
                printed.put(s, true);
                process.add(s);
            }
        }
    }

    public static void main (String[] args){
        String[] dirs;
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable;
        ConcurrentLinkedQueue<String> workQ;
        String cpath = System.getenv("CPATH");
        String[] cpathdirs = null;
        AtomicInteger count = new AtomicInteger(THREADS);

        long s = System.currentTimeMillis();

        if (System.getenv("CRAWLER_THREADS") != null){
            THREADS = Integer.parseInt(System.getenv("CRAWLER_THREADS"));
        }

        int cpathlen = 0;

        if (cpath != null){
            cpathdirs = cpath.split(":");
            cpathlen = cpathdirs.length;
        }

        int i;
        for (i = 0; i < args.length; i++){
            if (!args[i].substring(0, 2).equals("-I")){
                break;
            }
        }

        int start = i;
        int m = start - 1;
        dirs = new String[m + cpathlen + 3];
        dirs[0] = parseDir("./");
        for (i = 0; i < start; i++){
            dirs[i + 1] = parseDir(args[i].substring(2));
        }

        int j = i;
        if (cpathlen >= 1){
            for (j = i; j - i < cpathdirs.length; j++){
                dirs[j + 1] = parseDir(cpathdirs[j]);
            }
        }

        deptable = new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>(10000);

        workQ = new ConcurrentLinkedQueue<String>();

        for (i = start; i < args.length; i++){
            ConcurrentLinkedQueue<String> al;
            String obj;
            String[] file = args[i].split("\\.");

            if (!file[1].equals("c") && !file[1].equals("y") && !file[1].equals("l")){
                System.out.println("Illegal File Name: " + args[i]);
                System.exit(0);
            }

            obj = file[0] + ".o";

            al = new ConcurrentLinkedQueue<String>();
            al.add(args[i]);
            deptable.put(obj, al);

            workQ.add(args[i]);

            al = new ConcurrentLinkedQueue<String>();
            deptable.put(args[i], al);
        }

        count = new AtomicInteger(THREADS);
        for (int k = 0; k < THREADS; k++){
            WorkerThread w = new WorkerThread(deptable, workQ, dirs, count);
            threadlist.add(new Thread(w));
            threadlist.get(threadlist.size() - 1).start();
        }

        while(!threadlist.isEmpty()){
            try{
                threadlist.remove(0).join();
            }
            catch (InterruptedException e){
                System.out.println("Main Interrupted");
                System.exit(0);
            }
        }

        for (i = start; i < args.length; i++){
            String obj;
            ConcurrentLinkedQueue<String> process = new ConcurrentLinkedQueue<String>();
            ConcurrentHashMap<String, Boolean> printed = new ConcurrentHashMap<String, Boolean>(100);

            String[] file = args[i].split("\\.");
            obj = file[0] + ".o";
            System.out.print(obj + ":");

            process.add(obj);

            printDeps(deptable, printed, process);

            System.out.println();
        }
        long e = System.currentTimeMillis();
        System.out.println("\nElapsed Time: " + (e - s) + " milliseconds");
    }
}
