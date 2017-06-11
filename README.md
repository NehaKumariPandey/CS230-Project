# Team UnwrAPP

## Project Towards Robust Malware and Piracy Detection in Android Applications

In this project, we present an implementation of *Juxtapp*, a robust system developed at UC Berkeley in alliance with Intel Labs, which performs code similarity analysis to discover instances of buggy codes and signals for malware intrusion and piracy violations. Amidst this exploding growth in the number of applications on Play Store, reuse of vulnerable code, malware incidents and piracy are some of the major obstacles which hinder a healthy marketplace. The current smartphone markets, unfortunately, largely depend on a reactive approach to moderate content, where users report bugs, crashes, insufficient functionality and rate application quality after a brief period of usage. This leads to a proliferation of security threats and many poor quality and pirated applications escape the review process undetected.
In this paper, we reimplement an application called ‘Juxtapp’ which is an opcode based application similarity detection tool. It uses k-grams of opcodes to extract app features used for malware and piracy detection. Further, we identify some imporvements for the current tool and suggest approaches which can overcome that. We evaluate the performance of our implementation of JuxtApp in detecting malware intrusion by constructing a dataset of 107 original and repackaged Android applications, named Maldroid and run clustering and containment analysis on it. Further, to evaluate its performance in detecting pirated applications, we use the DroidKin dataset consisting of 407 original and pirated Android applications.

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
└───extracted
│   │   
│   └───original
│   |  
│   └───malware
│       
└───FVFile
```
2. Run ApkProcessor.java to extract apk file and generate opcode files
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
------------- | ----------------------
Maldroid      | 107
Droidkin      | 407
```

* Maldroid
For evaluating how effectively Juxtapp can detect Android apps with malware, we construct our validation dataset called Maldroid. 107 android applications were selected from the Google PlayStore, out of which 30 were manually repackaged after malware injection to provide a ‘ground truth’ data for our experimentation purposes. The two seeds of the malware are retained and are part of this dataset.

It can be retrieved from the following link:
[Maldroid dataset](https://github.com/twinklegupta/CS230-Project/tree/master/testing/malware%20with%20some%20original)


* Droidkin
For evaluating how effectively Juxtapp can detect Android apps with piracy, we have used Droidkin dataset. *Pending*

It can be retrieved from the following link:
[Maldroid dataset](http://www.cl.cam.ac.uk/research/dtg/attarchive/facedatabase.html)


### Methodology 

### Results

## Authors

* **[Shubham Mittal](https://sites.google.com/site/sodhanipranav)**
* **[Pranav Sodhani](https://sites.google.com/site/sodhanipranav)**
* **[Swati Arora](https://sites.google.com/site/sodhanipranav)**
* **[Brendon Faleiro](https://sites.google.com/site/sodhanipranav)**
* **[Twinkle Gupta](https://sites.google.com/site/sodhanipranav)**
* **[Anshita Mehrotra](https://sites.google.com/site/sodhanipranav)**
