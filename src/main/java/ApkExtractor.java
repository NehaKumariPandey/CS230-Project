package main.java;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Shubham Mittal on 5/26/17.
 */

import static main.java.Utils.benignCount;
import static main.java.Utils.globalFeatureMatrix;
import static main.java.Utils.globalPermMap;

public class ApkExtractor {

    public static void main(String[] args) throws IOException {

        List<String> strList = new ArrayList<>();
        strList.add(Constants.ORIGINAL);
        strList.add(Constants.MALWARE);

        for (String sampledDataset: strList) {
            final File folder = new File(Constants.PACKAGE_PREFIX + sampledDataset);
            final int NUM_CORES = Runtime.getRuntime().availableProcessors();

            ManifestParser parser = new ManifestParser();

            ExecutorService es = Executors.newFixedThreadPool(NUM_CORES);
            List<Future<String>> futures = new LinkedList<>();

            for (File apkFile : folder.listFiles()) {
                if (apkFile.toString().contains(Constants.MAC_STORE)) {
                    continue;
                }
                Callable callable = () -> {
                    String apkName = apkFile.toString().substring(apkFile.toString().lastIndexOf("/") + 1, apkFile.toString().lastIndexOf("."));
                    String destDir = Constants.PACKAGE_PREFIX + Constants.APK_EXTRACTED_FOLDER + sampledDataset + apkName;
                    String cmd = "java -jar " + Constants.PACKAGE_PREFIX + "apktool_2.2.2.jar d " + apkFile + " -o " + destDir;
                    Process p = Runtime.getRuntime().exec(cmd);
                    p.waitFor();
                    return apkName;
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
                        parser.parseManifest(apkName, sampledDataset);
                    } catch (Exception e) {
                        System.out.println("Corrupt manifest !!");
                        e.printStackTrace();
                    }
                } else {
                    futures.add(future);
                }
            }
        }
        try {
            Utils.serialize(globalPermMap, Constants.PACKAGE_PREFIX + Constants.FEATURE_FOLDER + Constants.PERMISSIONS_FILE + Constants.PERMISSION_OUT);
            Utils.serialize(globalFeatureMatrix, Constants.PACKAGE_PREFIX + Constants.FEATURE_FOLDER + Constants.FEATURE_MATRIX + Constants.OUTPUT_FORMAT);
            FileWriter writer = new FileWriter(Constants.PACKAGE_PREFIX + Constants.FEATURE_FOLDER + Constants.FEATURE_MATRIX + Constants.CSV);
            StringBuilder sb = new StringBuilder();
            sb.append(Utils.header);
            sb.append(Constants.LABELS);
            for (int[] featureVec: globalFeatureMatrix) {
                int i=0;
                sb.append(Constants.NEWLINE_CHARACTER);
                for (i=0;i<featureVec.length;i++) {
                    sb.append(featureVec[i]);
                    sb.append(Constants.COMMA_DELIMITER);
                }
                if (benignCount-->0) {sb.append(Constants.BENIGN_LABEL);}
                else {sb.append(Constants.MALWARE_LABEL);}
            }
            writer.append(sb.toString());
            writer.flush();
            writer.close();
            Utils.covertCSV2Arff(Constants.PACKAGE_PREFIX + Constants.FEATURE_FOLDER + Constants.FEATURE_MATRIX + Constants.CSV, Constants.PACKAGE_PREFIX + Constants.FEATURE_FOLDER + Constants.FEATURE_MATRIX + Constants.ARFF_FORMAT);
        } catch (Exception e) {
            System.out.println("Unable to write global permission map or feature matrix !!");
            e.printStackTrace();
        }
    }
}