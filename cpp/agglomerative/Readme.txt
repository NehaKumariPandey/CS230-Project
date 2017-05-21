How to run ?
g++ agg.cpp -std=c++11 -o a
./a ex1.txt 1 c > output.txt

ex1.txt: Input file
1: Final number of clusters desired
c - complete linkage (can be 's' or 'a' as well - single or average linkage)
See results in output.txt

About ex1.txt:
- First line contains number of bitvectors to expect
- Second line:
	- Label | 00011001 (bitset<8> - we need to change this to bitset<m>)