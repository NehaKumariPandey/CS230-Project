package SEProj;

import java.util.*;

/**
 * Created by swati.arora on 5/18/17.
 */
public class Processing {

    public long djb2(String str, long clip) { // set default value of clip = 1 (Clip is Optional)
        long hash = 5381;
        int c;
        for(int i=0; i< str.length(); i++)
            hash = ((hash << 5) + hash) + str.charAt(i); /* hash * 33 + c */
        return hash % clip;
    }

    public double simscore(BitSet a, BitSet b) {
        // Given two boolean vectors, need to find jaccard distance
        // Jaccard Distance = |A^B|/|AUB|

        BitSet c = (BitSet)a.clone();
        BitSet d = (BitSet)a.clone();

        c.or(b); // Result placed in c
        System.out.println("C is : "+c);
        
        d.xor(b); // Result placed in d
        System.out.println("D is : "+d);

        //BitSet x = A^B;
        //BitSet y = A|B;
        double score = c.cardinality()/d.cardinality();
        return score;
    }
}
