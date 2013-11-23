import java.util.*;
import java.util.concurrent.*;
import java.io.*;

class WorkerThread implements Runnable {

    String file;
    ConcurrentLinkedQueue<String> list;
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable;
    ConcurrentLinkedQueue<String> workQ;

    public WorkerThread(String file, ConcurrentLinkedQueue<String> list, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable, ConcurrentLinkedQueue<String> workQ){
        file = f;
        list = l;
    }

    public void run(String file, ConcurrentLinkedQueue<String> list, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable, ConcurrentLinkedQueue<String> workQ) throws IOException {
        Scanner s = new Scanner(new FileReader(file));

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
        }
    }
}

public class includeCrawler{

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
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> deptable;
        ConcurrentLinkedQueue<String> workQ;
        String cpath = System.getenv("CPATH");
        String[] cpathdirs = null;

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
        String[] dirs;
        dirs = new String[m + cpathlen + 3];
        dirs[0] = parseDir("./");
        for (i = 1; i < start; i++){
            dirs[i] = parseDir(args[i].substring(2));
        }

        int j = i;
        if (cpathlen > 1){
            for (j = i; j - i < cpathdirs.length; j++){
                dirs[j] = parseDir(cpathdirs[j]);
            }
        }

        dirs[j] = null;
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

        while (!workQ.isEmpty()){
            String current = workQ.poll();
            ConcurrentLinkedQueue<String> curlist = deptable.get(current);
            if(curlist == null){
                System.out.println("Mismatch between table and workQ!");
                System.exit(0);
            }
            try{
                WorkerThread w = new WorkerThread();
                new Thead(w).start(current, curlist, deptable, workQ);
            }
            catch (IOException e){
                System.out.println("Error Reading file " + current + "!");
                System.exit(0);
            }
            deptable.put(current, curlist);
            curlist = deptable.get(current);
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
    }
}
