package main.java;

import java.util.*;

class Agglomerate{
    public float euclideanDistance(BitSet a, BitSet b){
        BitSet x = (BitSet)a.clone();
        BitSet y = (BitSet)a.clone();

        x.xor(b); // Result placed in x
        y.or(b); // Result placed in y

        if(y.cardinality() == 0)
            System.out.println("Bitvector cannot be zero. apk must have some feature atleast");
        //System.out.println("x.count is: "+x.count());
        //System.out.println("y.count is: "+y.count());
        float xc = x.cardinality();
        float yc = y.cardinality();

        float score = 1.0 - xc/yc;
        //System.out.println("Score is: "+score);
        return score;
    }

    public void fillEuclideanDistances(float[][] matrix, int numOfItems, final ItemT[] items) {
        for (int i = 0; i < numOfItems; ++i) {
            for (int j = 0; j < numOfItems; ++j) {
                matrix[i][j] = euclideanDistance(items[i].coord, items[j].coord);
                matrix[j][i] = matrix[i][j];
                //System.out.println(matrix[i][j]+" ");
            }
            //System.out.println();
        }
    }

    public float[][] generateDistanceMatrix(int numOfItems, final itemT[] items) throws Exception {
        float[][] matrix = new float[numOfItems][numOfItems]; /*  Keep in mind -- alloc_mem(num_items, float *); */
        if (matrix != null) {
            fillEuclideanDistances(matrix, numOfItems, items);
        }
        else
            System.err.println("Failed to allocate memory for distance matrix"); /* Although alloc_fail operates on low level
                                                                           memory level - which java don't */
        return matrix;
    }

    // takes two clusters and returns the min distance between 2 items - one from cluster a and the other from cluster b
    public float singleLinkage(float[][] distances, final int[] a, final int[] b, int m, int n) {
        // /* Issue --- Guessing m is length of a and n of b - yet to confirm
        float min = FLT_MAX;    // Issue --- where is FLT_MAX defined ??
        float d = 0.0;
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
    public float completeLinkage(float[][] distances, final int[] a, final int[] b, int m, int n){
        // /* Issue --- Guessing m is length of a and n of b - yet to confirm
        float d = 0.0;
        float max = 0.0;  /* assuming distances are positive */
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
    public float averageLinkage(float[][] distances, final int[] a, final int[] b, int m, int n) {
        float total = 0.0;
        for (int i = 0; i < m; ++i){
            for (int j = 0; j < n; ++j) {
                total += distances[a[i]][b[j]];
            }
        }
        return total / (m * n);
    }

    public float getDistance(ClusterT cluster, int index, int target) {
        /* if both are leaves, just use the distances matrix */
        if (index < cluster.numOfItems && target < cluster.numOfItems)
            return cluster.distances[index][target];
        else {
            ClusterNodeT a = cluster.nodes[index];
            ClusterNodeT b = cluster.nodes[target];
            return distanceFptr(cluster.distances, a.items, b.items, a.numOfItems, b.numOfItems);
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

    public NeighbourT addNeighbour(ClusterT cluster, int index, int target)
    {
        NeighbourT neighbour = new NeighbourT();
        /*  // redundant for java --- Issue
        if (neighbour == null) {
            System.err.println("Failed to allocate memory for neighbour node");
            return neighbour;
        }
        */
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

    public ClusterT updateNeighbours(ClusterT cluster, int index)
    {
        ClusterNodeT node = cluster.nodes[index];
        if (node.type == AgglomerateConstants.NOT_USED) {
            System.err.println("Invalid cluster node at index "+index);
            cluster = NULL;
        }
        else {
            int rootClustersSeen = 1;
            int target = index;
            while (rootClustersSeen < cluster.numOfClusters) {
                ClusterNodeT temp = cluster.nodes[--target];
                if (temp.type == AgglomerateConstants.NOT_USED) {
                    System.err.println("Invalid cluster node at index "+index);
                    cluster = NULL;
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
        node.label = new String(item.label)   // Issue ---- remember to initilaise item label
        node.centroid = item.coord;
        node.type = AgglomerateConstants.LEAF_NODE;
        node.isRoot = 1;
        node.height = 0;
        node.numOfItems = 1;
        node.items[0] = cluster.numOfNodes++;
    }

    public ClusterNodeT addLeaf(ClusterT cluster, final ItemT item) {
        ClusterNodeT leaf = cluster.nodes[cluster.numOfNodes]; // Issue ---- should be numOfNodes-1
        leaf.items = new int[1];
        if (leaf.items != null) {
            intialiseLeaf(cluster, leaf, item);
            cluster.numOfClusters++;
        }
        else {
            System.err.println("Failed to allocate memory for node items");
            leaf = NULL;
        }
        return leaf;
    }

    public ClusterT addLeaves(ClusterT cluster, ItemT items) {
        for (int i = 0; i < cluster.numOfItems; ++i) {
            if (addLeaf(cluster, items[i]) != null) {
                updateNeighbours(cluster, i);
            }
            else {
                cluster = NULL;
                break;
            }
        }
        return cluster;
    }

    public void printClusterItems(ClusterT cluster, int index) {
        ClusterNodeT node = cluster.nodes[index];
        System.out.println("Items: ");
        if (node.numOfItems > 0) {
            System.out.print(cluster.nodes[node.items[0]].label +" ");
            for (int i = 1; i < node->num_items; ++i)
                System.out.print(cluster.nodes[node.items[i]].label);
        }
        System.out.println();
    }

    public void printClusterNode(ClusterT cluster, int index) {
        ClusterNodeT node = cluster.nodes[index];
        /*fprintf(stdout, "Node %d - height: %d, centroid: (%s)\n", index, node->height, (node->centroid).to_string()); */
        if (node.label != null)
            System.out.print("\tLeaf: "+ node.label+ "\n\t");
        else
            System.out.print("\tMerged: "+node.merged[0]+" "+node.merged[1]+"\n\t");
        printClusterItems(cluster, index);
        System.out.print("\tNeighbours: ");
        NeighbourT t = node.neighbours;
        while (t != null) {
            System.out.fprintf("\n\t\t%2d: %5.3f", t->target, t->distance);
            t = t.next;
        }
        System.out.println();
    }


}




/* Yet to port */

/*
#define alloc_mem(N, T) (T *) calloc(N, sizeof(T))
#define alloc_fail(M) fprintf(stderr,  \"Failed to allocate memory for %s.\n", M)
#define read_fail(M) fprintf(stderr, "Failed to read %s from file.\n", M)
#define invalid_node(I) fprintf(stderr, \"Invalid cluster node at index %d.\n", I)





#undef init_leaf







public void mergeItems(ClusterT cluster, ClusterNodeT node, ClusterNodeT[] toMerge) {
        node.type = AgglomerateConstants.A_MERGER;
        node.isRoot = 1;
        node.height = -1;
        List<BitSet> cent;

        */
/* copy leaf indexes from merged clusters *//*

        int k = 0, idx;
        bitset<8> centroid("00000000");
        for (int i = 0; i < 2; ++i) {
        cluster_node_t *t = to_merge[i];
        t->is_root = 0; */
/* no longer root: merged *//*

        if (node->height == -1 ||
        node->height < t->height)
        node->height = t->height;
        for (int j = 0; j < t->num_items; ++j) {
        idx = t->items[j];
        node->items[k++] = idx;
        }
        //centroid.x += t->num_items * t->centroid
        //centroid.y += t->num_items * t->centroid.y;
        cent.push_back(t->centroid);
        }
        for(int i = 0; i < 8; i++)
        {
        int count = 0;
        for(int j = 0; j < cent.size(); j++)
        {
        if(cent[j][i]) count++;
        }
        if(count > 4) centroid[i] = 1;
        }
        */
/* calculate centroid *//*

        //node->centroid.x = centroid.x / k;
        //node->centroid.y = centroid.y / k;
        node->centroid = centroid;
        node->height++;
        }



#define merge_to_one(cluster, to_merge, node, node_idx)         \
do {                                                    \
node->num_items = to_merge[0]->num_items +      \
to_merge[1]->num_items;                 \
node->items = alloc_mem(node->num_items, int);  \
if (node->items) {                              \
merge_items(cluster, node, to_merge);   \
cluster->num_nodes++;                   \
cluster->num_clusters--;                \
update_neighbours(cluster, node_idx);   \
} else {                                        \
alloc_fail("array of merged items");    \
free(node->merged);                     \
node = NULL;                            \
}                                               \
} while(0)                                              \

cluster_node_t *merge(cluster_t *cluster, int first, int second)
{
int new_idx = cluster->num_nodes;
cluster_node_t *node = &(cluster->nodes[new_idx]);
node->merged = alloc_mem(2, int);
if (node->merged) {
cluster_node_t *to_merge[2] = {
&(cluster->nodes[first]),
&(cluster->nodes[second])
};
node->merged[0] = first;
node->merged[1] = second;
merge_to_one(cluster, to_merge, node, new_idx);
} else {
alloc_fail("array of merged nodes");
node = NULL;
}
return node;
}

#undef merge_to_one

void find_best_distance_neighbour(cluster_node_t *nodes,
int node_idx,
neighbour_t *neighbour,
float *best_distance,
int *first, int *second)
{
while (neighbour) {
if (nodes[neighbour->target].is_root) {
if (*first == -1 ||
neighbour->distance < *best_distance) {
*first = node_idx;
*second = neighbour->target;
*best_distance = neighbour->distance;
}
break;
}
neighbour = neighbour->next;
}
}


int find_clusters_to_merge(cluster_t *cluster, int *first, int *second)
{
float best_distance = 0.0;
int root_clusters_seen = 0;
int j = cluster->num_nodes; */
/* traverse hierarchy top-down *//*

*first = -1;
while (root_clusters_seen < cluster->num_clusters) {
cluster_node_t *node = &(cluster->nodes[--j]);
if (node->type == NOT_USED || !node->is_root)
continue;
++root_clusters_seen;
find_best_distance_neighbour(cluster->nodes, j,
node->neighbours,
&best_distance,
first, second);
}
return *first;
}

cluster_t *merge_clusters(cluster_t *cluster)
{
int first, second;
while (cluster->num_clusters > 1) {
if (find_clusters_to_merge(cluster, &first, &second) != -1)
merge(cluster, first, second);
}
return cluster;
}

#define init_cluster(cluster, num_items, items)                         \
do {                                                            \
cluster->distances =                                    \
generate_distance_matrix(num_items, items);     \
if (!cluster->distances)                                \
goto cleanup;                                   \
cluster->num_items = num_items;                         \
cluster->num_nodes = 0;                                 \
cluster->num_clusters = 0;                              \
if (add_leaves(cluster, items))                         \
merge_clusters(cluster);                        \
else                                                    \
goto cleanup;                                   \
} while (0)                                                     \

cluster_t *agglomerate(int num_items, item_t *items)
{
cluster_t *cluster = alloc_mem(1, cluster_t);
if (cluster) {
cluster->nodes = alloc_mem(2 * num_items - 1, cluster_node_t);
if (cluster->nodes){
init_cluster(cluster, num_items, items);
//cout<<"Distances Generated .."<<endl;
}
else {
alloc_fail("cluster nodes");
goto cleanup;
}
} else
alloc_fail("cluster");
goto done;

cleanup:
free_cluster(cluster);
cluster = NULL;

done:
return cluster;
}

#undef init_cluster

int print_root_children(cluster_t *cluster, int i, int nodes_to_discard)
{
cluster_node_t *node = &(cluster->nodes[i]);
int roots_found = 0;
if (node->type == A_MERGER) {
for (int j = 0; j < 2; ++j) {
int t = node->merged[j];
if (t < nodes_to_discard) {
print_cluster_items(cluster, t);
++roots_found;
}
}
}
return roots_found;
}

void get_k_clusters(cluster_t *cluster, int k)
{
if (k < 1)
return;
if (k > cluster->num_items)
k = cluster->num_items;

int i = cluster->num_nodes - 1;
int roots_found = 0;
int nodes_to_discard = cluster->num_nodes - k + 1;
while (k) {
if (i < nodes_to_discard) {
print_cluster_items(cluster, i);
roots_found = 1;
} else
roots_found = print_root_children(cluster, i,
nodes_to_discard);
k -= roots_found;
--i;
}
}

void print_cluster(cluster_t *cluster)
{
for (int i = 0; i < cluster->num_nodes; ++i)
print_cluster_node(cluster, i);
}

int read_items(int count, item_t *items, FILE *f)
{
//cout<<count<<endl;
for (int i = 0; i < count; ++i) {
item_t *t = &(items[i]);
char temp[9];
if (fscanf(f, "%[^|]| %s\n",t->label, temp))
{
//cout << "Got inside"<<endl;
string temp1(temp);
bitset<8> bv (temp1);
t->coord = bv;
//cout<<(t->coord).to_string()<<endl;
continue;
}
//cout << "Got outside"<<endl;
read_fail("item line");
return i;
}
//cout<<"Read items .."<<endl;
return count;
}

int read_items_from_file(item_t **items, FILE *f)
{
int count, r;
r = fscanf(f, "%5d\n", &count);
//cout<<count<<endl;
if (r == 0) {
read_fail("number of lines");
return 0;
}
if (count) {
*items = alloc_mem(count, item_t);
if (*items)
read_items(count, *items, f);
//if (read_items(count, *items, f) != count)
//free(items);
else
alloc_fail("items array");
}
//cout<<"Returning count .."<<endl;
return count;
}

void set_linkage(char linkage_type)
{
switch (linkage_type) {
case AVERAGE_LINKAGE:
distance_fptr = average_linkage;
break;
case COMPLETE_LINKAGE:
distance_fptr = complete_linkage;
break;
//case CENTROID_LINKAGE:
//      distance_fptr = centroid_linkage;
//    break;
case SINGLE_LINKAGE:
default: distance_fptr = single_linkage;
}
}

int process_input(item_t **items, const char *fname)
{
int count = 0;
FILE *f = fopen(fname, "r");
if (f) {
count = read_items_from_file(items, f);
fclose(f);
} else
fprintf(stderr, "Failed to open input file %s.\n", fname);
//cout<<"Processed Input .."<<endl;
return count;
}

int main(int argc, char **argv)
{
if (argc != 4) {
fprintf(stderr, "Usage: %s <input file> <num clusters> "
"<linkage type>\n", argv[0]);
exit(1);
} else {
item_t *items = NULL;
//cout<<"Processing input .."<<endl;
int num_items = process_input(&items, argv[1]);
set_linkage(argv[3][0]);
if (num_items) {
//cout<<"Staring Agglomeration .."<<endl;
cluster_t *cluster = agglomerate(num_items, items);
//cout<<"Ending Agglomeration & Printing.."<<endl;
//free(items);

if (cluster) {
fprintf(stdout, "CLUSTER HIERARCHY\n"
"--------------------\n");
print_cluster(cluster);

int k = atoi(argv[2]);
fprintf(stdout, "\n\n%d CLUSTERS\n"
"--------------------\n", k);
get_k_clusters(cluster, k);
//free_cluster(cluster);
}
}
}
return 0;
}


*/
