#include "Processing.h"
#include <iostream>
#include <string>

using namespace std;

unsigned long Processing :: djb2(string str, unsigned long clip) { // set default value of clip = 1 (Clip is Optional)
    unsigned long hash = 5381;
    int c;
    int i = 0;
    while (c = str[i++])
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
    return hash % clip;
}

double Processing :: simscore(bitset<32> A, bitset<32> B) {
	// Given two boolean vectors, need to find jaccard distance
	// Jaccard Distance = |A^B|/|AUB|
	bitset<32> x = A^B;
	bitset<32> y = A|B;
	double score = x.count()/y.count();
	return score;
}