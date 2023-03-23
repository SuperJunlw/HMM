import java.io.*;
import java.util.*;

//This class read the malware folder, convert opcodes to index and split data to training and testing sets
public class FileIO {
    // no noise reduce data structures
    static HashMap<String, Integer> hb = new HashMap<>();
    static List<List<Integer>> firstTestDataList = new ArrayList<>();
    static List<List<Integer>> secondTestDataList = new ArrayList<>();
    static int index = 0;

    //noise reduce data structures
    static HashMap<String, Integer> indexHM = new HashMap<String, Integer>();
    static List<List<Integer>> firstTestDataListNoise = new ArrayList<>();
    static List<List<Integer>> secondTestDataListNoise = new ArrayList<>();

    //zbot folder has 2136 files
    //winwebsec folder has 4360 files
    //zeroaccess folder has 1305 files
    //zbot and zeroaccess has the same unique opcodes


    //**********************************************************************************************************
    //preprocessing the dataset without noise reduction
    //**********************************************************************************************************

    //read a malware folder and assign an index to all the unique opcode
    public static void readFolderWithOutNoiseRe()throws IOException{
        File folder = new File("winwebsec");
        File[] fileList = folder.listFiles();
        Scanner sc = null;
        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            while (sc.hasNextLine()) {
                opCode = sc.nextLine().trim();
                if (!hb.containsKey(opCode)) {
                    hb.put(opCode, index);
                    index++;
                }
            }
        }
    }

    //read the winwebsec folder, get a observation sequence with index for training by combined certain amount of files
    //then get testing sequences from the same class
    public static int[] getTrainAndTestNoNoise(int trainAmount, int testAmount, int testLength) throws IOException {
        List<Integer> trainList = new ArrayList<>();
        int count = 0;
        int testCount = 0;

        File folder = new File("winwebsec");
        File[] fileList = folder.listFiles();
        Scanner sc = null;
        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            //get the training sequence
            if (count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    trainList.add(hb.get(opCode));
                }
                count++;
            }
            //get the testing sequence
            else if (count >= trainAmount && testCount < testAmount) {
                List<Integer> list = new ArrayList<>();
                int i = 0;
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    list.add(hb.get(opCode));
                    i++;
                    if(i == testLength) {
                        firstTestDataList.add(list);
                        testCount++;
                        break;
                    }
                }
            }
            else
                break;
        }

        int[] obsTrain = new int[trainList.size()];
        for (int i = 0; i < obsTrain.length; i++) {
            obsTrain[i] = trainList.get(i);
        }
        return obsTrain;
    }

    //read another malware folder and get the testing sequences
    public static void secondTestNoNoiseRe(int testAmount, int testLength) throws IOException {
        int count = 0;

        File folder = new File("zeroaccess");
        File[] fileList = folder.listFiles();
        Scanner sc = null;

        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            List<Integer> list = new ArrayList<>();
            if(count < testAmount) {
                int i = 0;
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    if(hb.containsKey(opCode)) {
                        list.add(hb.get(opCode));
                        i++;
                    }
                    if(i == testLength) {
                        secondTestDataList.add(list);
                        count++;
                        break;
                    }
                }
            }
            else
                break;
        }
    }

    //-----------------------------------------------------------------------------------------------------------
    //for noise reduction
    //-----------------------------------------------------------------------------------------------------------

    //count the frequency of each opcode in the folder
    public static void countSymbolFre(int M) throws IOException {
        HashMap<String, Integer> countHM = new HashMap<>();
        int count = 0;

        File folder = new File("winwebsec");
        File[] fileList = folder.listFiles();
        Scanner sc = null;

        //count the frequency of each opcode
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;

            while (sc.hasNextLine()) {
                opCode = sc.nextLine().trim();
                if (countHM.containsKey(opCode)) {
                    countHM.put(opCode, countHM.get(opCode) + 1);
                } else {
                    countHM.put(opCode, 1);
                }
            }
        }

        //sort map based on the values
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(countHM.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        //create a map with the most frequent opcode, store the most frequent opcodes to the map
        for (Map.Entry<String, Integer> aa : list) {
            if(count < M-1) {
                indexHM.put(aa.getKey(), count);
            }
            else
                break;
            count++;
        }
    }

    //read the files from a folder, sprites the files to training set and test set
    public static int[] readFolderWithNoiseRe(int M, int trainAmount, int testAmount, int testLength)throws IOException{
        List<Integer> trainList = new ArrayList<>();
        int count = 0;
        int testCount = 0;

        File folder = new File("winwebsec");
        File[] fileList = folder.listFiles();
        Scanner sc = null;
        assert fileList != null;

        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            //get the training sequence
            if (count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    if (indexHM.containsKey(opCode)) {
                        trainList.add(indexHM.get(opCode));
                    } else {
                        trainList.add(M-1);
                    }
                }
                count++;
            }
            //get the testing sequences
            else if (count >= trainAmount && testCount < testAmount) {
                List<Integer> list = new ArrayList<>();
                int i = 0;
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    list.add(indexHM.getOrDefault(opCode, M-1));
                    i++;
                    if(i == testLength) {
                        firstTestDataListNoise.add(list);
                        testCount++;
                        break;
                    }
                }
            }
            else
                break;
        }
        int[] obsTrain = new int[trainList.size()];
        for (int i = 0; i < obsTrain.length; i++) {
            obsTrain[i] = trainList.get(i);
        }
        return obsTrain;
    }

    //read the files of another folder, and get testing sequences
    public static void secondTestWithNoise(int M, int testAmount, int testLength) throws IOException{
        int count = 0;

        File folder = new File("zeroaccess");
        File[] fileList = folder.listFiles();
        Scanner sc = null;

        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            List<Integer> list = new ArrayList<>();

            if(count < testAmount) {
                int i = 0;

                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    list.add(indexHM.getOrDefault(opCode, M-1));
                    i++;

                    if(i == testLength) {
                        secondTestDataListNoise.add(list);
                        count++;
                        break;
                    }
                }
            }
            else
                break;
        }
    }
}
