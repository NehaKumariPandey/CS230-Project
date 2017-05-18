// Processing.h
#ifndef PROCESSING_H
#define PROCESSING_H

#include <iostream>
#include <bitset>
#include <string>
#include <climits>

class Processing
{
  public:
  double simscore(std::bitset<32>, std::bitset<32>);
  unsigned long djb2(std::string, unsigned long clip = ULONG_MAX);
};

#endif