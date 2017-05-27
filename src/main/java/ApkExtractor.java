package main.java;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Shubham Mittal on 5/26/17.
 */
public class ApkExtractor {

    public static void main(String[] args) {

        final File folder = new File(Constants.PACKAGE_PREFIX + Constants.ORIGINAL);
        final int NUM_CORES = Runtime.getRuntime().availableProcessors();

        ManifestParser parser = new ManifestParser();

        ExecutorService es = Executors.newFixedThreadPool(NUM_CORES);
        List<Future<String>> futures = new LinkedList<>();

        for (File apkFile : folder.listFiles()) {
            Callable callable = new Callable() {
                @Override
                public Object call() throws Exception {
                    String apkName = apkFile.toString().substring(apkFile.toString().lastIndexOf("/")+1, apkFile.toString().lastIndexOf("."));
                    String destDir = Constants.PACKAGE_PREFIX + Constants.APK_EXTRACTED_FOLDER + apkName;
                    String cmd = "java -jar " + Constants.PACKAGE_PREFIX + "apktool_2.2.2.jar d " + apkFile + " -o " + destDir;
                    Process p = Runtime.getRuntime().exec(cmd);
                    p.waitFor();
                    return apkName;
                }
            };
            futures.add(es.submit(callable));
        }

        es.shutdown();

        while (!futures.isEmpty()) {
            Future<String> future = futures.remove(0);
            if (future.isDone()) {
                String apkName = null;
                try {
                    apkName = future.get();
                    parser.parseManifest(apkName);
                } catch (Exception e) {
                    System.out.println("Unable to parse manifest !!");
                    e.printStackTrace();
                }
            } else {
                futures.add(future);
            }
        }
    }
}
