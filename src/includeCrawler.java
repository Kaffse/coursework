import java.util.*;

public class includeCrawler{
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
        String[m + n + 2] dirs;
        dirs[0] = formatDir("./");
        for (i = 1; i < start; i++){
            dir[i] = formatDir(arg[i].substring(2));
        }

        if (cpathdirs.length > 0){
            for (j = i; j - i < cpathdirs.length; j++){
                dir[j] = formatDir(cpathdirs[j]);
            }
        }

        dir[j] = NULL;
        deptable = new HashMap<String, ArrayList>(Integer.MAX_VALUE);

        workQ = new ArrayList<String>;

        for (i = start; i < args.length; i++){
        ArrayList<String> al;
        String obj;
        String[] file = args[i].split(".");
        
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
            String[] file = arg[i].split(".");
