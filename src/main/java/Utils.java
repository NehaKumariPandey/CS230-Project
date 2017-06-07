package main.java;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Shubham Mittal on 6/6/17.
 */
public class Utils {

    static Map<String, Integer> globalPermMap;  // Map of permission mapping to the number of times they appeared over the course
    static Map<String, Integer> permIndexMap;   // Map of permission pointing to the index in the feature vector
    static List<int[]> globalFeatureMatrix;
    static String header = null;
    static int benignCount = 0;
    static int malwareCount = 0;

    static {
        globalFeatureMatrix = new ArrayList<>();
        String permissionPath = Constants.PACKAGE_PREFIX + Constants.PERMISSIONS_FILE + Constants.PERMISSION_OUT;
        File file = new File(permissionPath);
        /*if (file.exists()) {
            try {
                deSerialize(permissionPath);
            } catch (Exception e) {
                System.out.println("Unable to deserialize PermissionList");
                e.printStackTrace();
            }
        }
        else {*/
            globalPermMap = new ConcurrentHashMap<>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(Constants.PACKAGE_PREFIX + Constants.PERMISSIONS_FILE + Constants.CSV));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine())!=null) {
                    sb.append(line.trim());
                    sb.append(Constants.COMMA_DELIMITER);
                    globalPermMap.putIfAbsent(line.trim(), 0);
                }
                //sb.delete(sb.length()-2, sb.length());
                header = sb.toString();
                br.close();
            } catch (FileNotFoundException e) {
                System.out.println("No such file as " + Constants.PACKAGE_PREFIX + Constants.PERMISSIONS_FILE + Constants.CSV);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Line could not be read!");
                e.printStackTrace();
            }
        //}

        //Drain the globalPermMap into a map to allow indexing
        permIndexMap = new HashMap<>();

        loadPermissionIndexMap(permIndexMap);
    }

    public static void serialize(Object object, String outputPath) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
        ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
        oos.writeObject(object);
        oos.flush();
        oos.close();
    }

    public static Map deSerialize(String inputPath) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(inputPath);
        ObjectInputStream ois = new ObjectInputStream(fileInputStream);
        globalPermMap = (Map<String, Integer>) ois.readObject();
        ois.close();
        return globalPermMap;
    }

    private static void loadPermissionIndexMap(Map<String, Integer> permIndexMap) {
        int iter=0;
        for (String key : globalPermMap.keySet()) {
            permIndexMap.put(key, iter++);
        }
    }
}
