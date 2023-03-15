import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FileIO {
    static List<Integer> testList = new ArrayList<>();
    static List<Integer> zbotTestList = new ArrayList<>();
    static Hashtable<String, Integer> hb = new Hashtable<>();

    static List<List<Integer>> zATestDataList = new ArrayList<>();
    static List<List<Integer>> zBotTestDataList = new ArrayList<>();

    static int index = 0;

    //zbot folder has 2136 files
    //winwebsec folder has 4360 files
    //zeroaccess folder has 1305 files
    public static int[] readFolderWithOutNoiseRe(int trainAmount, int testAmount) throws IOException {
        List<Integer> trainList = new ArrayList<>();

        int count = 0;

        File folder = new File("zeroaccess");
        File fileList[] = folder.listFiles();
        Scanner sc = null;
        for(File file : fileList) {
            sc= new Scanner(file);
            String opCode;
            if(count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if (hb.containsKey(opCode)) {
                        trainList.add(hb.get(opCode));
                    } else {
                        hb.put(opCode, index);
                        trainList.add(index);
                        index++;
                    }
                }
            }
            else if(count >= trainAmount && count < trainAmount + testAmount){
                List<Integer> list = new ArrayList<>();
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if (hb.containsKey(opCode)) {
                        list.add(hb.get(opCode));
                    } else {
                        hb.put(opCode, index);
                        list.add(index);
                        index++;
                    }
                }
                zATestDataList.add(list);
            }
            count++;
        }
        System.out.println(index);

        int[] obsTrain = new int[trainList.size()];
        for(int i = 0; i < obsTrain.length; i++) {
            obsTrain[i] = trainList.get(i);
        }
        return obsTrain;
    }

    public static void zbotTest(int testAmount) throws IOException {
        int count = 0;

        File folder = new File("zbot");
        File fileList[] = folder.listFiles();
        Scanner sc = null;

        while(count < testAmount){
            sc= new Scanner(fileList[count]);
            String opCode;
            List<Integer> list = new ArrayList<>();
            while (sc.hasNextLine()) {
                opCode = sc.nextLine();
                if (hb.containsKey(opCode)) {
                    list.add(hb.get(opCode));
                } else {
                    hb.put(opCode, index);
                    list.add(hb.get(opCode));
                    index++;
                }
            }
            zBotTestDataList.add(list);
            count++;
        }
    }
}
