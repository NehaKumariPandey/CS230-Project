#include <iostream>
#include <string>
using namespace std;
#include "Processing.h"

int main()
{
	Processing p1;
	// Testing djb2
	string stringvar = "Hello! this is a sample code.";
	cout << "Hash index using djb2 hash for stringvar is " << p1.djb2(stringvar) <<endl;
	
	// Testing simiscore
	bitset<32> foo (std::string("10011001100110011001100110011001"));
	bitset<32> bar (std::string("10011001100110011001100110011001"));
	cout << "Similarity score between foo and bar is " << p1.simscore(foo, bar) << endl; // 0

	return 0;
}