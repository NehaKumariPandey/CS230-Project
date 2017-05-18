package SEProj;
import java.util.*;

/**
 * Created by swati.arora on 5/18/17.
 */
public class Execute {

    public static void main(String[] args){
        Processing p1 = new Processing();
        // Testing djb2
        String stringvar = "Hello! this is a sample code.";
        System.out.println("Hash index using djb2 hash for stringvar is " + p1.djb2(stringvar,1));

        // Testing simiscore
        BitSet str1 = strToBitSet("10011001100110011001100110011001");
        BitSet str2 =  strToBitSet("10011001100110011001100110011001");
        System.out.println(str1);
        System.out.println(str2);
        System.out.println("Similarity score between foo and bar is " + p1.simscore(str1, str2)); // 0
    }

    private static BitSet strToBitSet(String str) {
        return BitSet.valueOf(new long[] { Long.parseLong(str, 2) });
    }

}
