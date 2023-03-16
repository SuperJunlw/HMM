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
        File folder = new File("zeroaccess");
        File fileList[] = folder.listFiles();
        Scanner sc = null;
        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            while (sc.hasNextLine()) {
                opCode = sc.nextLine();
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

        File folder = new File("zeroaccess");
        File fileList[] = folder.listFiles();
        Scanner sc = null;
        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            if (count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    trainList.add(hb.get(opCode));
                }
            }
            else if (count >= trainAmount && count < trainAmount + testAmount) {
                List<Integer> list = new ArrayList<>();
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    list.add(hb.get(opCode));
                }
                zATestDataList.add(list);
            }
            count++;
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

        File folder = new File("zbot");
        File fileList[] = folder.listFiles();
        Scanner sc = null;

        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            List<Integer> list = new ArrayList<>();
            if(count < testAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if(hb.containsKey(opCode))
                        list.add(hb.get(opCode));
                }
                zBotTestDataList.add(list);
            }
            else
                break;
            count++;
        }
    }

    //-----------------------------------------------------------------------------------------------------------
    //for noisy reduction
    //-----------------------------------------------------------------------------------------------------------

    public static void countSymbolFre(int M) throws IOException {
        HashMap<String, Integer> countHM = new HashMap<>();
        int count = 0;

        File folder = new File("zeroaccess");
        File fileList[] = folder.listFiles();
        Scanner sc = null;

        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;

            while (sc.hasNextLine()) {
                opCode = sc.nextLine();
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
        //create a map with the most frequent opcode
        //HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            if(count < M) {
                //temp.put(aa.getKey(), aa.getValue());
                indexHM.put(aa.getKey(), count);
            }
            else
                break;
            count++;
        }
    }

    public static int[] readFolderWithNoiseRe(int M, int trainAmount, int testAmount)throws IOException{
        List<Integer> trainList = new ArrayList<>();
        int count = 0;

        File folder = new File("zeroaccess");
        File fileList[] = folder.listFiles();
        Scanner sc = null;
        assert fileList != null;

        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            if (count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if (indexHM.containsKey(opCode)) {
                        trainList.add(indexHM.get(opCode));
                    } else {
                        trainList.add(M);
                    }
                }
            }
            else if (count >= trainAmount && count < trainAmount + testAmount) {
                List<Integer> list = new ArrayList<>();
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if (indexHM.containsKey(opCode)) {
                        list.add(indexHM.get(opCode));
                    } else {
                        list.add(M);
                    }
                }
                zATestDataListNoise.add(list);
            }
            count++;
        }
        int[] obsTrain = new int[trainList.size()];
        for (int i = 0; i < obsTrain.length; i++) {
            obsTrain[i] = trainList.get(i);
        }
        return obsTrain;
    }

    public static void zbotTestWithNoise(int M, int testAmount) throws IOException{
        int count = 0;

        File folder = new File("zbot");
        File fileList[] = folder.listFiles();
        Scanner sc = null;

        assert fileList != null;
        for (File file : fileList) {
            sc = new Scanner(file);
            String opCode;
            List<Integer> list = new ArrayList<>();
            if(count < testAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if (indexHM.containsKey(opCode)) {
                        list.add(indexHM.get(opCode));
                    } else {
                        list.add(M);
                    }
                }
                zBotTestDataListNoise.add(list);
            }
            else
                break;
            count++;
        }
    }
}
