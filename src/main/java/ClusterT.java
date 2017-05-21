package main.java;

class ClusterT{
    int numOfItems; /* number of items that was clustered */
    int numOfClusters; /* current number of root clusters */
    int numOfNodes; /* number of leaf and merged clusters */
    ClusterNodeT[] nodes; /* leaf and merged clusters */
    float[][] distances; /* distance between leaves */
}