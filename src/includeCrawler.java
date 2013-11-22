import java.util.*;
import java.io.*;

public class includeCrawler{

    private static String parseDir(String dir) {
        if (!dir.substring(dir.length() - 1).equals("/")){
            return dir + "/";
        }
        else {
            return dir;
        }
    }

    private static void process(String file, HashMap<String, ArrayList<String>> deptable, ArrayList<String> list, ArrayList<String> workQ) throws IOException {
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
            deptable.put(name, new ArrayList<String>());
            workQ.add(name);
        }
    }

    private static void printDeps(HashMap<String, ArrayList<String>> deptable, HashMap<String, Boolean> printed, ArrayList<String> process) {
        while (!process.isEmpty()){
            String cur = process.remove(0);
            ArrayList<String> deps = deptable.get(cur);

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
        HashMap<String, ArrayList<String>> deptable;
        ArrayList<String> workQ;

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
        deptable = new HashMap<String, ArrayList<String>>(10000);

        workQ = new ArrayList<String>();

        for (i = start; i < args.length; i++){
            ArrayList<String> al;
            String obj;
            String[] file = args[i].split("\\.");

            if (!file[1].equals("c") && !file[1].equals("y") && !file[1].equals("l")){
                System.out.println("Illegal File Name: " + args[i]);
                System.exit(0);
            }

            obj = file[0] + ".o";

            al = new ArrayList<String>();
            al.add(args[i]);
            deptable.put(obj, al);

            workQ.add(args[i]);

            al = new ArrayList<String>();
            deptable.put(args[i], al);
        }

        while (!workQ.isEmpty()){
            String current = workQ.remove(0);
            ArrayList<String> curlist = deptable.get(current);
            if(curlist == null){
                System.out.println("Mismatch between table and workQ!");
                System.exit(0);
            }
            try{
                process(current, deptable, curlist, workQ);
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
            ArrayList<String> process = new ArrayList<String>();
            HashMap<String, Boolean> printed = new HashMap<String, Boolean>(100);

            String[] file = args[i].split("\\.");
            obj = file[0] + ".o";
            System.out.print(obj + ":");

            process.add(obj);

            printDeps(deptable, printed, process);

            System.out.println();
        }
    }
}
