1. Create 3 empty folders with names : 
extracted(malware,original), 
BBFile, 
FVFile, 
permissions (malware,original) in main/resources
2. Run ApkProcessor.java to extract apk file and generate opcode files
3. Run Execute.java to generate feature vector and clustering
    1. Enter value for k - Enter 5
    2. Enter value for m - Enter 30000(for now)
    3. Enter Number of clusters - Enter 1
    4. Enter Linkage type - Enter c (Possible values are c for COMPLETE_LINKAGE, s for SINGLE_LINKAGE, a for AVERAGE_LINKAGE)
