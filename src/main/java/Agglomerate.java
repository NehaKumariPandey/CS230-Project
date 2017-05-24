package main.java;

import java.util.*;
import java.io.*;

public class Agglomerate{
    private String linkageString;

    public double euclideanDistance(BitSet a, BitSet b){
        BitSet x = (BitSet)a.clone();
        BitSet y = (BitSet)a.clone();

        x.xor(b); // Result placed in x
        y.or(b); // Result placed in y

        if(y.cardinality() == 0)
            System.out.println("Bitvector cannot be zero. apk must have some feature atleast");
        double xc = x.cardinality();
        double yc = y.cardinality();

        double score = 1.0 - xc/yc;
        return score;
    }

    public void fillEuclideanDistances(double[][] matrix, int numOfItems, ItemT[] items) {
        for (int i = 0; i < numOfItems; ++i) {
            for (int j = 0; j < numOfItems; ++j) {
                matrix[i][j] = euclideanDistance(items[i].coord, items[j].coord);
                matrix[j][i] = matrix[i][j];
            }
        }
    }

    public double[][] generateDistanceMatrix(int numOfItems, final ItemT[] items){
        double[][] matrix = new double[numOfItems][numOfItems];
        if (matrix != null) {
            fillEuclideanDistances(matrix, numOfItems, items);
        }
        else
            System.err.println("Failed to allocate memory for distance matrix"); /* Although alloc_fail operates on low level
                                                                           memory level - which java don't */
        return matrix;
    }

    // takes two clusters and returns the min distance between 2 items - one from cluster a and the other from cluster b
    public double singleLinkage(double[][] distances, final int[] a, final int[] b, int m, int n) {
        double min = Double.MAX_VALUE; 
        double d = 0.0;
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                d = distances[a[i]][b[j]];
                if (d < min)
                    min = d;
            }
        }
        return min;
    }

    // takes two clusters and returns the max distance between 2 items - one from cluster a and the other from cluster b
    public double completeLinkage(double[][] distances, final int[] a, final int[] b, int m, int n){
        double d = 0.0;
        double max = 0.0;  /* assuming distances are positive */
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                d = distances[a[i]][b[j]];
                if (d > max)
                    max = d;
            }
        }
        return max;
    }

    // return average distance between 2 clusters
    public double averageLinkage(double[][] distances, final int[] a, final int[] b, int m, int n) {
        double total = 0.0;
        for (int i = 0; i < m; ++i){
            for (int j = 0; j < n; ++j) {
                total += distances[a[i]][b[j]];
            }
        }
        return total /(m * n);
    }

    public double getDistance(ClusterT cluster, int index, int target) {
        /* if both are leaves, just use the distances matrix */
        if (index < cluster.numOfItems && target < cluster.numOfItems)
            return cluster.distances[index][target];
        else {
            ClusterNodeT a = cluster.nodes[index];
            ClusterNodeT b = cluster.nodes[target];
            switch (linkageString) {
                case AgglomerateConstants.AVERAGE_LINKAGE:
                    return averageLinkage(cluster.distances, a.items, b.items, a.numOfItems, b.numOfItems);
                case AgglomerateConstants.COMPLETE_LINKAGE:
                    return completeLinkage(cluster.distances, a.items, b.items, a.numOfItems, b.numOfItems);
                /*
                case AgglomerateConstants.CENTROID_LINKAGE:
                      distance_fptr = centroid_linkage;
                    break;
                */

            }
            return singleLinkage(cluster.distances, a.items, b.items, a.numOfItems, b.numOfItems);
        }
    }

    public void insertBefore(NeighbourT current, NeighbourT neighbours, ClusterNodeT node) {
        neighbours.next = current;
        if (current.prev != null) {
            current.prev.next = neighbours;
            neighbours.prev = current.prev;
        } else
            node.neighbours = neighbours;
        current.prev = neighbours;
    }

    public void insertAfter(NeighbourT current, NeighbourT neighbours) {
        neighbours.prev = current;
        current.next = neighbours;
    }

    public void insertSorted(ClusterNodeT node, NeighbourT neighbours) {
        NeighbourT temp = node.neighbours;
        while (temp.next != null) {
            if (temp.distance >= neighbours.distance) {
                insertBefore(temp, neighbours, node);
                return;
            }
            temp = temp.next;
        }
        if (neighbours.distance < temp.distance)
            insertBefore(temp, neighbours, node);
        else
            insertAfter(temp, neighbours);
    }

    public NeighbourT addNeighbour(ClusterT cluster, int index, int target) {
        NeighbourT neighbour = new NeighbourT();
        neighbour.target = target;
        neighbour.distance = getDistance(cluster, index, target);
        ClusterNodeT node = cluster.nodes[index];
        if (node.neighbours != null) {
            insertSorted(node, neighbour);
        }
        else {
            node.neighbours = neighbour;
        }
        return neighbour;
    }

    public ClusterT updateNeighbours(ClusterT cluster, int index) {
        ClusterNodeT node = cluster.nodes[index];
        if (node.type == AgglomerateConstants.NOT_USED) {
            System.err.println("Invalid cluster node at index "+index);
            cluster = null;
        }
        else {
            int rootClustersSeen = 1;
            int target = index;
            while (rootClustersSeen < cluster.numOfClusters) {
                ClusterNodeT temp = cluster.nodes[--target];
                if (temp.type == AgglomerateConstants.NOT_USED) {
                    System.err.println("Invalid cluster node at index "+index);
                    cluster = null;
                    break;
                }
                if (temp.isRoot) {
                    ++rootClustersSeen;
                    addNeighbour(cluster, index, target);
                }
            }
        }
        return cluster;
    }

    public void intialiseLeaf(ClusterT cluster, ClusterNodeT node, final ItemT item){
        node.label = new String(item.label);
        node.centroid = item.coord;
        node.type = AgglomerateConstants.LEAF_NODE;
        node.isRoot = true;
        node.height = 0;
        node.numOfItems = 1;
        node.items[0] = cluster.numOfNodes++;
    }

    public ClusterNodeT addLeaf(ClusterT cluster, final ItemT item) {
        ClusterNodeT leaf = cluster.nodes[cluster.numOfNodes];
        leaf.items = new int[1];
        if (leaf.items != null) {
            intialiseLeaf(cluster, leaf, item);
            cluster.numOfClusters++;
        }
        else {
            System.err.println("Failed to allocate memory for node items");
            leaf = null;
        }
        return leaf;
    }

    public ClusterT addLeaves(ClusterT cluster, ItemT[] items) {
        for (int i = 0; i < cluster.numOfItems; ++i) {
            if (addLeaf(cluster, items[i]) != null) {
                updateNeighbours(cluster, i);
            }
            else {
                cluster = null;
                break;
            }
        }
        return cluster;
    }

    public void printClusterItems(ClusterT cluster, int index) {
        ClusterNodeT node = cluster.nodes[index];
        System.out.print("Items: ");
        if (node.numOfItems > 0) {
            System.out.print(cluster.nodes[node.items[0]].label +" ");
            for (int i = 1; i < node.numOfItems; ++i)
                System.out.print(", "+cluster.nodes[node.items[i]].label);
        }
        System.out.println();
    }

    public void printClusterNode(ClusterT cluster, int index) {
        ClusterNodeT node = cluster.nodes[index];
        if (node.label != null)
            System.out.print("\tLeaf: "+ node.label+ "\n\t");
        else
            System.out.print("\tMerged: "+node.merged[0]+" "+node.merged[1]+"\n\t");
        printClusterItems(cluster, index);
        System.out.print("\tNeighbours: ");
        NeighbourT t = node.neighbours;
        while (t != null) {
            System.out.format("\n\t\t%2d: %5.3f", t.target, t.distance);
            t = t.next;
        }
        System.out.println();
    }

    public void mergeItems(ClusterT cluster, ClusterNodeT node, ClusterNodeT[] toMerge) {
        node.type = AgglomerateConstants.A_MERGER;
        node.isRoot = true;
        node.height = -1;
        List<BitSet> cent = new ArrayList<BitSet>();

        /* copy leaf indexes from merged clusters */
        int k = 0, idx;
        BitSet centroid = Processing.strToBitSet("00000000");
        for (int i = 0; i < 2; ++i) {
            ClusterNodeT t = toMerge[i];
            t.isRoot = false;
            /* no longer root: merged */
            if (node.height == -1 || node.height < t.height)
                node.height = t.height;
                for (int j = 0; j < t.numOfItems; ++j) {
                    idx = t.items[j];
                    node.items[k++] = idx;
                }
            cent.add(t.centroid);
        }
        for(int i = 0; i < 8; i++) {
            int count = 0;
            for(int j = 0; j < cent.size(); j++) {
                if(cent.get(j).get(i))
                    count++;
            }
            if(count > 4)
                centroid.set(i);
        }

        /* calculate centroid */
        node.centroid = centroid;
        node.height++;
        }


    public ClusterNodeT merge(ClusterT cluster, Distance dObj) {
        int newIdx = cluster.numOfNodes;
        ClusterNodeT node = cluster.nodes[newIdx];
        node.merged = new int [2];
        if (node.merged != null) {
            ClusterNodeT[] toMerge = new ClusterNodeT[2];
            toMerge[0] = cluster.nodes[dObj.first];
            toMerge[1] = cluster.nodes[dObj.second];
            node.merged[0] = dObj.first;
            node.merged[1] = dObj.second;
            mergeToOne(cluster, toMerge, node, newIdx);
        } else {
            System.err.println("Failed to allocate memory for array of merged nodes");
            node = null;
        }
        return node;
    }

    public void mergeToOne(ClusterT cluster, ClusterNodeT[] toMerge, ClusterNodeT node, int nodeIdx){
        node.numOfItems = toMerge[0].numOfItems + toMerge[1].numOfItems;
        node.items = new int[node.numOfItems];
        if (node.items != null) {
            mergeItems(cluster, node, toMerge);
            cluster.numOfNodes++;
            cluster.numOfClusters--;
            updateNeighbours(cluster, nodeIdx);
        } else {
            System.err.println("Failed to allocate memory for array of merged items");
            node = null;
        }
    }

    public void findBestDistanceNeighbour(ClusterNodeT[] nodes, int nodeIdx, NeighbourT neighbour, Distance obj) {
        while (neighbour != null) {
            if (nodes[neighbour.target].isRoot != false) {
                if (obj.first == -1 || neighbour.distance < obj.bestDistance) {
                    obj.first = nodeIdx;
                    obj.second = neighbour.target;
                    obj.bestDistance = neighbour.distance;
                }
                break;
            }
            neighbour = neighbour.next;
        }
    }

    public int findClustersToMerge(ClusterT cluster, Distance dObj) {
        double bestDistance = 0.0;
        int rootClustersSeen = 0;
        int j = cluster.numOfNodes;
        /* traverse hierarchy top-down */
        dObj.first = -1;
        while (rootClustersSeen < cluster.numOfClusters) {
            ClusterNodeT node = cluster.nodes[--j];
            if (node.type == AgglomerateConstants.NOT_USED || !node.isRoot)
                continue;
            ++rootClustersSeen;
            findBestDistanceNeighbour(cluster.nodes, j, node.neighbours, dObj);
        }
        return dObj.first;
    }

    public ClusterT mergeClusters(ClusterT cluster) {
        Distance dObj = new Distance();
        while (cluster.numOfClusters > 1) {
            if (findClustersToMerge(cluster, dObj) != -1)
                merge(cluster, dObj);
        }
        return cluster;
    }

    public void initCluster(ClusterT cluster, int numOfItems, ItemT[] items){
        cluster.distances = generateDistanceMatrix(numOfItems, items);
        if (cluster.distances == null) {
            cluster = null;
        }
        cluster.numOfItems = numOfItems;
        cluster.numOfNodes = 0;
        cluster.numOfClusters = 0;
        if (addLeaves(cluster, items) != null) {
            mergeClusters(cluster);
        }
        else
            cluster = null;
    }

    public ClusterT agglomerate(int numOfItems, ItemT[] items) {
        ClusterT cluster = new ClusterT();
        if (cluster != null) {
            cluster.nodes = new ClusterNodeT[2 * numOfItems - 1];  // Ques : Why 2 * numOfItems-1??? Why twice ??
            for(int i =0; i < (2 * numOfItems - 1); i++){
                cluster.nodes[i] = new ClusterNodeT();
            }
            if (cluster.nodes != null){
                initCluster(cluster, numOfItems, items);
            }
            else {
                System.err.println("Failed to allocate memory for cluster nodes");
                cluster = null;
            }
        }
        else
            System.err.println("Failed to allocate memory for cluster");
        return cluster;
    }

    public int printRootChildren(ClusterT cluster, int i, int nodesToDiscard) {
        ClusterNodeT node = cluster.nodes[i];
        int rootsFound = 0;
        if (node.type == AgglomerateConstants.A_MERGER) {
            for (int j = 0; j < 2; ++j) {
                int t = node.merged[j];
                if (t < nodesToDiscard) {
                    printClusterItems(cluster, t);
                    ++rootsFound;
                }
            }
        }
        return rootsFound;
    }

    public void getKClusters(ClusterT cluster, int k) {
        if (k < 1)
            return;
        if (k > cluster.numOfItems)
            k = cluster.numOfItems;

        int i = cluster.numOfNodes - 1;
        int rootsFound = 0;
        int nodesToDiscard = cluster.numOfNodes - k + 1;
        while (k > 0) {
            if (i < nodesToDiscard) {
                printClusterItems(cluster, i);
                rootsFound = 1;
            }
            else {
                rootsFound = printRootChildren(cluster, i, nodesToDiscard);
            }
            k -= rootsFound;
            --i;
        }
    }

    void printCluster(ClusterT cluster) {
        for (int i = 0; i < cluster.numOfNodes; ++i)
            printClusterNode(cluster, i);
    }

    void setLinkage(String linkageType){
        linkageString = linkageType;
    }

    public ItemT[] processInput(ItemT[] items, String fileName) {
        int numOfItems = 0;
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(new File(fileName)));
            numOfItems = Integer.parseInt(br.readLine());
            items = new ItemT[numOfItems];
            for (int i = 0; i < numOfItems; ++i) {
                String[] itemInfo = br.readLine().split(" ");
                ItemT t = new ItemT();
                t.label = itemInfo[0].replaceAll("[^A-Z]", "");
                t.coord = Processing.strToBitSet(itemInfo[1].trim());
                items[i]= t;
            }
        }
        catch(FileNotFoundException fe){
            fe.printStackTrace();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        return items;
    }

    public static void main(String[] args) {
        /*int argc, char **argv */
        Agglomerate ag = new Agglomerate();
        Scanner s = new Scanner(System.in);
        String[] argv = s.nextLine().split(" ");
        if (argv.length != 4) {
            System.err.println("Usage: " + argv[0] + " <input file> <num clusters> <linkage type>");
            System.exit(1);
        }
        else {
            ItemT[] items = null;
            String dir = System.getProperty("user.dir");
            items = ag.processInput(items, dir+"/../data/"+argv[1]);
            int numOfItems = items.length;
            ag.setLinkage(argv[3]);
            if (numOfItems > 0) {
                ClusterT cluster = ag.agglomerate(numOfItems, items);
                if (cluster != null) {
                    System.out.println("CLUSTER HIERARCHY \n--------------------");
                    ag.printCluster(cluster);

                    int k = Integer.parseInt(argv[2]);
                    System.out.println(k + " CLUSTERS \n--------------------");
                    ag.getKClusters(cluster, k);
                }
            }
        }
    }

}