import java.io.*;
import java.util.*;

public class FileIO {
    // no noise reduce data structures
    static HashMap<String, Integer> hb = new HashMap<>();
    static List<List<Integer>> zATestDataList = new ArrayList<>();
    static List<List<Integer>> zBotTestDataList = new ArrayList<>();
    static int index = 0;

    //noise reduce data structures
    static HashMap<String, Integer> indexHM = new HashMap<String, Integer>();
    static List<List<Integer>> zATestDataListNoise = new ArrayList<>();
    static List<List<Integer>> zBotTestDataListNoise = new ArrayList<>();

    //zbot folder has 2136 files
    //winwebsec folder has 4360 files
    //zeroaccess folder has 1305 files
    //zbot and zeroaccess has the same unique opcodes
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
        //System.out.println(FileIO.index);
    }
    public static int[] getZATrainAndTestNoNoise(int trainAmount, int testAmount) throws IOException {
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
            if (count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    trainList.add(hb.get(opCode));
                }
                count++;
            }
            else if (count >= trainAmount && testCount < testAmount) {
                List<Integer> list = new ArrayList<>();
                int i = 0;
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    list.add(hb.get(opCode));
                    i++;
                    if(i == 500) {
                        zATestDataList.add(list);
                        testCount++;
                        break;
                    }
                }
            }
            else
                break;
        }
        //System.out.println(index);
        int[] obsTrain = new int[trainList.size()];
        for (int i = 0; i < obsTrain.length; i++) {
            obsTrain[i] = trainList.get(i);
        }
        return obsTrain;
    }

    public static void zbotTest(int testAmount) throws IOException {
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
                    if(i == 500) {
                        zBotTestDataList.add(list);
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
    //for noisy reduction
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
        //HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            if(count < M-1) {
                //temp.put(aa.getKey(), aa.getValue());
                indexHM.put(aa.getKey(), count);
            }
            else
                break;
            count++;
        }
    }

    //read the files from a folder, sprites the files to training set and test set
    public static int[] readFolderWithNoiseRe(int M, int trainAmount, int testAmount)throws IOException{
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
            else if (count >= trainAmount && testCount < testAmount) {
                List<Integer> list = new ArrayList<>();
                int i = 0;
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine().trim();
                    list.add(indexHM.getOrDefault(opCode, M-1));
                    i++;
                    if(i == 500) {
                        zATestDataListNoise.add(list);
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

    //read the files of another folder, use some of the files for the second test set
    public static void zbotTestWithNoise(int M, int testAmount) throws IOException{
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

                    if(i == 500) {
                        zBotTestDataListNoise.add(list);
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
