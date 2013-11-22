import java.util.*;

public class includeCrawler{

    private static String parseDir(String dir) {
        if (!dir.substring(dir.length() - 1).equals("/")){
            return dir + "/";
        }
        else {
            return dir;
        }
    }

    private static ArrayList<String> process(String file) {
        Scanner s = new Scanner(new File(file));
        ArrayList<String> list = new ArrayList<String>();

        while (s.hasNext()){
            String line = s.nextLine();

            int i = 1;

            while (i <= line.length() && line.substring(i - 1, i).equals(" ")){
                i++;
            }

            if (!line.substring(i, i + 9).equals("#include")){
                continue;
            }

            i = i + 9;

            while (i < line.length() && line.substring(i, i + 1).equals(" ")){
                i++;
            }

            i++;

            if (!line.substring(i, i + 1).equals("\"")){
                continue;
            }

            int j = ++i;

            while (i < line.length() && !line.substring(i, i + 1).equals("\"")){
                i++;
            }
            String name = line.substring(j, i);
            list.add(name);
            if (deptable.get(name) != null){
                continue;
            }
            deptable.add(name, new ArrayList<String>());
        }
    }

    private static void printDeps(String name) {
        System.out.println("LOL");
    }

    public static void main (String[] args){
        HashMap<String, ArrayList<String>> deptable;
        ArrayList<String> workQ;

        String cpath = System.getenv("CPATH");
        String[] cpathdirs;


        if (cpath != null){
            cpathdirs = cpath.split(":");
        }

        int i;
        for (i = 1; i < args.lenght; i++){
            if (!arg.substring(0, 2).equals("-I")){
                break;
            }
        }

        start = i;
        m = start -1;
        String[] dirs;
        dirs[0] = parseDir("./");
        for (i = 1; i < start; i++){
            dir[i] = parseDir(arg[i].substring(2));
        }

        if (cpathdirs.length > 0){
            for (j = i; j - i < cpathdirs.length; j++){
                dir[j] = parseDir(cpathdirs[j]);
            }
        }

        dir[j] = NULL;
        deptable = new HashMap<String, ArrayList>(Integer.MAX_VALUE);

        workQ = new ArrayList<String>();

        for (i = start; i < args.length; i++){
        ArrayList<String> al;
        String obj;
        String[] file = args[i].split("\\.");
        
        if (ext.equals("c") || ext.equals("y") || ext.equals("l")){
            System.println("Illegal File Name: " + arg[i]);
            System.exit(0);
        }

        obj = root + ".o";

        al = new ArrayList<String>();
        al.add(arg[i]);
        deptable.put(obj, al);

        workQ.add(arg[i]);

        al = new ArrayList<String>();
        deptable.put(arg[i], al);
        }

        while (!workQ.isEmpty()){
            String current = workQ.remove(0);
            ArrayList<String> curlist = deptable.get(current);
            if(curlist.isEmpty()){
                System.out.println("Mismatch between table and workQ!");
                System.exit(0);
            }
            process(current, curlist);
            deplist.put(current, curlist);
        }

        for (i = start; i < args.length; i++){
            String obj;
            String[] file = arg[i].split("\\.");
            obj = file[0] + ".obj";
            System.out.print(file[0] + ": ");
            printDeps(arg[i]);
            System.out.println();
        }
    }
}
