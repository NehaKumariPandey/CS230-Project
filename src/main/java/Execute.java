package main.java;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Execute {

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        System.out.println("Enter k(k-Grams) : Example :- 5");
        int k = Integer.parseInt(s.nextLine());
        System.out.println("Enter m(Bit vector size) : Example :- 30000");
        int m = Integer.parseInt(s.nextLine());
        System.out.println("Enter number of clusters : Example :- 1");
        int numClusters = Integer.parseInt(s.nextLine());
        System.out.println("Enter Linkage type : Example :- c");
        String linkageInputType = s.nextLine();
        System.out.println("Entered values are : "+k+" "+m+" "+numClusters+" "+linkageInputType);

        /* Feature Vector Generation */
        Map<String, BitSet> bitVectors = new HashMap<String, BitSet>();
        String dir = FeatureVectorGenerator.getCurrentDirectory();
        File folder = new File(dir + Constants.PACKAGE_PREFIX + Constants.BB_FOLDER);
        String filePath = dir + Constants.PACKAGE_PREFIX + Constants.BB_FOLDER;
        for (File opCodeFile : folder.listFiles()) {
            //System.out.println("Input to readOpCode : "+filePath+" "+opCodeFile.getName());
            FeatureVectorGenerator.readOpCode(filePath + opCodeFile.getName());
            /* k = 5, m = 30000 */
            Map<Long, List<FeatureVectorNode>> featureVector = FeatureVectorGenerator.processOpCode(k, m);
            FeatureVectorGenerator.generateBitVector(featureVector, bitVectors, opCodeFile.getName());
            FeatureVectorGenerator.serializeFeatureVector(featureVector, opCodeFile.getName());
            featureVector = null;
        }
        FeatureVectorGenerator.serializeBitVector(bitVectors);


        /* Clustering */
        ItemT[] items = null;
        dir = FeatureVectorGenerator.getCurrentDirectory() + Constants.PACKAGE_PREFIX;
        //System.out.println("File being read : " + dir + Constants.BIT_VECTOR_FILE + Constants.OUTPUT_FORMAT);
        items = Agglomerate.processInput(items, dir + Constants.BIT_VECTOR_FILE + Constants.OUTPUT_FORMAT);
        int numOfItems = items.length;
        Agglomerate.setLinkage(linkageInputType);
        if (numOfItems > 0) {
            ClusterT cluster = Agglomerate.agglomerate(numOfItems, items);
            if (cluster != null) {
                System.out.println("CLUSTER HIERARCHY \n--------------------");
                Agglomerate.printCluster(cluster);
                System.out.println(numClusters + " CLUSTERS \n--------------------");
                Agglomerate.getKClusters(cluster, numClusters);
            }
        }

    }

}
