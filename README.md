# Project - UnwrAPP

Scalable system for detecting code reuse Among android applications

### Functions added

```
Processing::djb2
```
djb2 takes in a string and an optional clipping parameter as input. The output is the hashed 32-bit integer. Note that the clip, in our case, is the size of the bitvector, m.

```
Processing::simscore
```
simscore takes in 2 bitvectors as input and calculates the Jaccard similarity index between the 2 bitvectors.
