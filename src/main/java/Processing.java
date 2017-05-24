package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author : pranav.sodhani, swati.arora
 */

public class Processing {
    private static String relativeDirectoryPath = "data/opcodes";

    /* Setter function for directoryPath */
    public static void setRelativeDirectoryPath(String str){
        relativeDirectoryPath = str;
    }

    public static String getCompletePath(){
        String dir = System.getProperty("user.dir");
        return (dir+"/../"+relativeDirectoryPath);
    }

    /* djb2 Hash function */
    public static long djb2(String str, long clip) { // set default value of clip = 1 (Clip is Optional)
        long hash = 5381;
        int c;
        for(int i=0; i< str.length(); i++) {
            hash = ((hash << 5) + hash) + str.charAt(i); /* hash * 33 + c */
            //System.out.println("Hash is : "+hash);
        }
        return hash % clip;
    }

    /* Similarity score between two BitSet vectors by using Jaccard distance
    * Jaccard Distance = |A^B|/|AUB|
    * */
    public static double simScore(BitSet a, BitSet b) {
        BitSet c = (BitSet)a.clone();
        BitSet d = (BitSet)a.clone();

        c.xor(b); // Result placed in c
        System.out.println("C is : "+c);

        d.or(b); // Result placed in d
        System.out.println("D is : "+d);

        double score = c.cardinality()/d.cardinality();
        return score;
    }

    /* Utility function */
    public static BitSet strToBitSet(String str) {
        return BitSet.valueOf(new long[] { Long.parseLong(str, 2) });
    }

    /* Opcodes are being generated as text file inside data/opcodes directory and this method will
     * read it from there
     * It reads each file from target directory and generate k-grams for each opcode file
     * For now - this returns List of grams for each file i.e. List<List<String>> - TBD on further processing
     */
    public static List<List<String>> opCodeToKGrams(int numApps, int k) throws Exception{
        List<List<String>> allKGrams = new ArrayList<List<String>>();

        File f = new File(getCompletePath());
        int numFiles = f.listFiles().length;

/*
        if(numFiles != numApps)
            throw new Exception("Files are not equal to number of applications");
*/
        File[] listOfFiles = f.listFiles();
        for(File file: listOfFiles){
            if(!file.isFile())
                throw new Exception("Invalid object(Not a file)");

            if(!file.getName().contains("txt"))
                continue;

            List<String> obj = generateKGrams(file.getName(),k);
            allKGrams.add(obj);
        }
        return allKGrams;
    }

    /* Generate and return list of kGrams for input file */
    public static List<String> generateKGrams(String fileName, int k){
        List<String> kGrams = new ArrayList<String>();
        Scanner scan;
        try {
            scan = new Scanner(new File(getCompletePath()+"/"+fileName));
            scan.useDelimiter("\\Z");
            String content = scan.next();

            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length - k + 1; i++)
                kGrams.add(concatLines(lines, i, i + k));
        }
        catch(FileNotFoundException fe){
            fe.printStackTrace();
        }
        return kGrams;
    }

    /* Utility function : used by generateKGrams */
    public static String concatLines(String[] lines, int startOffset, int endOffset) {
        StringBuilder sb = new StringBuilder();
        for (int i = startOffset; i < endOffset; i++)
            sb.append((i > endOffset ? " " : "") + lines[i]);
        return sb.toString();
    }


}
