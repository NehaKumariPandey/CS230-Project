package main.java;

import main.java.Constants;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FeatureVectorGenerator {
    static List<List<String>> val = null;
    public static void readOpCode(String absoluteFileName){

        FileInputStream fileInputStream = null;
        ObjectInputStream ois = null;
        try{
            fileInputStream = new FileInputStream(absoluteFileName);
            ois = new ObjectInputStream(fileInputStream);
            val = (List<List<String>>)ois.readObject();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException cf){
            cf.printStackTrace();
        }
        finally{
            try{
                ois.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static Map<Long, List<FeatureVectorNode>> processOpCode(int k, int m) {
        Map<Long, List<FeatureVectorNode>> featureVector = new HashMap<Long, List<FeatureVectorNode>>();
        String str = "";
        int totalKGrams = 0;
        for (List<String> v : val) {
            str = v.toString();
            Pattern pattern1 = Pattern.compile("(?<=[.]class )(.*?)(?=,)");
            Pattern pattern2 = Pattern.compile("(?<=method )(.*?)(?=, [.]|\\])");
            String className = "";
            String methodName = "";
            Matcher mat = pattern1.matcher(str);
            if(mat.find()){
                className =  mat.group(0);
            }
            mat = pattern2.matcher(str);
            while(mat.find()){
                String methodString = mat.group().replaceAll("//,//,",",");
                String[] methodInfo = methodString.split(",");  //At 0 index is tha name of method and opcodes in remaining array
                methodName = methodInfo[0];
                List<String> kGrams = Processing.generateKGrams(methodInfo,k);
                totalKGrams += kGrams.size(); // Required to determine a good m as m >> N where N is number of k-grams extracted from an application
                int count = 0;
                for(String kGram : kGrams){
                    long hash = Processing.djb2(kGram, m);
                    if(!featureVector.containsKey(hash)) {
                        featureVector.put(hash, new ArrayList<FeatureVectorNode>());
                    }
                    featureVector.get(hash).add(new FeatureVectorNode(className, methodName, count));
                    count++;
                }
            }
        }

        return featureVector;
    }

    /* Serialize Feature Vector : Useful for tracing back to the files */
    public static void serializeFeatureVector(Map<Long, List<FeatureVectorNode>> featureVector, String fileName){
        String dir = getCurrentDirectory();
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream oos = null;
        try{
            fileOutputStream = new FileOutputStream(dir+ Constants.PACKAGE_PREFIX + Constants.FV_FOLDER + fileName); //fileName contains .out
            oos = new ObjectOutputStream(fileOutputStream);
            oos.writeObject(featureVector);
            oos.flush();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
    }

    /* Serialize Feature Bit Vector : Required for input to Clustering and Similarity Analysis */
    public static void serializeBitVector(Map<String, BitSet> bitVectors){
        String dir = getCurrentDirectory();
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream oos = null;
        try{
            fileOutputStream = new FileOutputStream(dir+ Constants.PACKAGE_PREFIX  + Constants.BIT_VECTOR_FILE + Constants.OUTPUT_FORMAT);
            oos = new ObjectOutputStream(fileOutputStream);
            oos.writeObject(bitVectors);
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        finally{
            try{
                oos.flush();
            }
            catch(IOException ie){
                ie.printStackTrace();
            }
        }

    }

    public static String getCurrentDirectory(){
        return System.getProperty("user.dir") +"/";
    }

    /* Using Feature Vector Object, it generates BitSet object for each apk for input to similarity and clustering code */
    public static void generateBitVector(Map<Long, List<FeatureVectorNode>> featureVector, Map<String, BitSet> bitVectors, String fileName){
        BitSet bitVector = new BitSet();
        for(long v: featureVector.keySet())
            bitVector.set((int)v);
        bitVectors.put(fileName, bitVector);
    }

    public static void main(String[] args){
        /* Moved this part of code to Execute.java */
        /*
        Map<String, BitSet> bitVectors = new HashMap<String, BitSet>();
        String dir = getCurrentDirectory();
        File folder = new File(dir+ Constants.PACKAGE_PREFIX + Constants.BB_FOLDER);
        String filePath = dir+ Constants.PACKAGE_PREFIX+ Constants.BB_FOLDER;
        for (File opCodeFile : folder.listFiles()) {
            readOpCode(filePath+opCodeFile.getName());
            /* k = 5, m = 30000 *//*
            Map<Long, List<FeatureVectorNode>> featureVector = processOpCode(5, 30000);
            generateBitVector(featureVector, bitVectors, opCodeFile.getName());
            serializeFeatureVector(featureVector, opCodeFile.getName());
            featureVector = null;
        }
        //System.out.println(bitVectors);
        serializeBitVector(bitVectors);
    */

    }


}
