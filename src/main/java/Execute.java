package main.java;
import java.util.*;

/**
 * @author : pranav.sodhani, swati.arora
 */

public class Execute {

    public static void main(String[] args){
        // Testing djb2
        String stringvar = "Hello! this is a sample code.";
        System.out.println("Hash index using djb2 hash for stringvar is " + Processing.djb2(stringvar,1));

        // Testing simiscore
        BitSet str1 = Processing.strToBitSet("10011001100110011001100110011001");
        BitSet str2 = Processing.strToBitSet("10011001100110011001100110011001");
        System.out.println("Similarity score between foo and bar is " + Processing.simScore(str1, str2)); // 0

        //Testing path for files
        System.out.println(Processing.getCompletePath());

        //Testing k-grams
        // Created four files("1.txt", "2.txt", "3.txt", "4.txt") in data/opcodes directory
        try{
            System.out.println(Processing.opCodeToKGrams(4,5));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
