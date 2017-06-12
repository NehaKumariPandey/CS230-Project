# Team UnwrAPP

## Project Towards Robust Malware and Piracy Detection in Android Applications

<p align="justify">
In this project, we present an implementation of Juxtapp, a robust system developed at UC Berkeley in alliance with Intel Labs, which performs code similarity analysis to discover instances of buggy codes and signals for malware intrusion and piracy violations. Amidst the exploding growth in the number of applications on Google Play Store, reuse of vulnerable code, malware incidents and piracy are some of the major obstacles which hinder a healthy marketplace. The current smartphone markets, unfortunately, largely depend on a reactive approach to moderate content, where users report bugs, crashes, insufficient functionality and rate application quality after a brief period of usage. This leads to a proliferation of security threats and many poor quality and pirated applications escape the review process undetected.
In this paper, we reimplement Juxtapp and identify some improvements for the current implementation and suggest approaches which can overcome that. Juxtapp is an opcode based application similarity detection tool. It uses k-grams of opcodes to extract app features used for malware and piracy detection. Further, we evaluate the performance of our implementation of JuxtApp in detecting malware intrusion by constructing a dataset of 107 original and repackaged Android applications, named Maldroid and run clustering and containment analysis on it. Further, to evaluate its performance in detecting pirated applications, we use the DroidKin dataset consisting of 407 original and pirated Android applications.
</p>

### Getting Started

1. Navigate to src/main/resources and create the directory structure as follows:
```
src/main/resources
│       
└───original
│       A.apk B.apk
│       ...       
└───extracted
│   │   
│   └───original
│   |  
│   └───malware
|
└───BBFile     
│             
└───permissions
│   │   
│   └───original
│   |  
│   └───malware
│       
└───FVFile
│       
└───malware
```
2. Run ApkExtractor.java to extract apk files kept under src/main/resources/original and generate "serialized" opcode files
3. Run Execute.java to generate feature vector and perform clustering
    1. Enter value for k - Enter 5
    2. Enter value for m - Enter 240007
    3. Enter Number of clusters - Enter 1
    4. Enter Linkage type - Enter s
4. To traceback to verify similarity observed for any two applications, Traceback.java can be run 

### About the datasets
We perform our evaluation on two different datasets. 
```
Dataset Name  | Total number of applications
------------- | ----------------------------
Maldroid      | 107
Droidkin      | 407
```

* **Maldroid**
&nbsp;&nbsp;<p align="justify">
For evaluating how effectively Juxtapp can detect Android apps with malware, we construct our validation dataset called Maldroid. 107 android applications were selected from the Google PlayStore, out of which 30 were manually repackaged after malware injection to provide a ‘ground truth’ data for our experimentation purposes. The two seeds of the malware were retained as well and are part of this dataset.
</p>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It can be retrieved from the following link:
[Maldroid dataset](https://github.com/twinklegupta/CS230-Project/tree/master/testing/malware%20with%20some%20original)

* **Droidkin**
&nbsp;&nbsp;<p align="justify">
For evaluating how effectively Juxtapp can detect Android apps with piracy, we have used Droidkin dataset. Their original dataset consists of 72 unique Android apps from different sources. To investigate the impact of obfuscation on similarity detection, this set of unique apps underwent the following transformations: Repackaging, Re-repackaging, String url modifications, Junk code insertion, File alignment to 4 and 8 bytes, Icon change, Junk files addition. The final set of 792 samples included original 72 apps, their altered versions transformed using the methods mentioned above and their combinations. For our evaluation, we use a subset of DroidKin which consists of 37 original apps and their transformed versions. A total of 407 Android apps were used by us.
</p>

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It can be retrieved from the following link:
[Droidkin dataset](http://www.unb.ca/cic/research/datasets/android-validation.html)


### Methodology 

**Application Pre-processing**
<p align="justify">
In this project, we build an efficient automated Java-based Android package analyzer called ApkExtractor which we use in conjunction with ApkTool for reverse engineering of APK files. Since ApkTool can take from 30s to 2 minutes to decompile an application, depending on the application size, we parallelize the execution using the Executor Service (multi-threaded library) support in Java. This step results in a sequence of opcode for each processed application
</p>

**Feature Extraction**
<p align="justify">
In this project, after obtaining the code-based features(opcodes), we use k-grams and feature hashing to obtain features from applications. For each application, we use a moving window of size k to obtain datagrams. Each obtained k-gram is hashed using djb2 hash as it gives good distribution and corresponding bit is set in the bit vector. Alongwith the bit, information related to the k-gram i.e. information about class and method in opcode and window offset in moving window from which this k-gram originated is stored. This information plays an important role to trace back the results obtained after clustering back to the code and provide a way to verify our findings.
</p>

**Similarity Computation**
<p align="justify">
To compute the similarity between two applications, we compute the similarity between their bit vectors. We use Jaccard similarity(J(A,B)) for every pair of applications and we use union and intersection bit vectors operations to compute it. The Jaccard distance(opposite of Jaccard similarity i.e. 1 - J(A,B)) gives a measure of distance between the applications in feature space and is used in clustering.
</p>

**Clustering**
<p align="justify">
To club similar applications into same cluster, we use agglomerative hierarchical clustering on the bit vector obtained post feature hashing. The Jaccard distance(opposite of Jaccard similarity) is used as a measure to decide closeness between applications.
</p>

**Permission-based Malware Classification**
<p align="justify">
To address some of the limitations of JuxtApp in context of Malware detection, we use our efficient ApkExtractor to extract permissions from the Manifest file. We create a 150 length bit vector for each application and apply feature selection using Information Gain principle to extract the most influential features (permissions). We apply a 10-fold stratified cross validation technique and test the model using a Naive Bayes classifier.
</p>
Steps to run:

1. Ensure you have the same project directory structure as described above.
2. Run ApkExtractor.java to extract the APK files present in original and malware folders and create corresponding BB files.
3. Run Execute.java to apply clustering and evaluating JuxtApp
4. To perform feature based classification, ensure that you have the benign application in the original folder and malicious      applications in the malware folder. 
    1. Run InformationGain.java for feature selection
    2. Run PermissionModelClassifier.java for evaluating the model
5. To invoke the web service, run mvn spring-boot:run from the repository root


## Authors

* **[Shubham Mittal](https://www.linkedin.com/in/mitshubh/)**
* **[Pranav Sodhani](https://www.linkedin.com/in/sodhanipranav)**
* **[Swati Arora](https://www.linkedin.com/in/swatiarora2)**
* **[Brendon Faleiro](https://www.linkedin.com/in/brendonfaleiro)**
* **[Twinkle Gupta](https://www.linkedin.com/in/twinkle-gupta-0096967a/)**
* **[Anshita Mehrotra](https://www.linkedin.com/in/anshitamehrotra/)**
