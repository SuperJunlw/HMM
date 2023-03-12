import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileIO {

    //zbot folder has 2136 files
    //winwebsec folder has 4360 files
    //zeroaccess folder has 1305 files
    public static int[] readFolder(int trainAmount, int testAmount, List<Integer> testList) throws IOException {
        Hashtable<String, Integer> hb = new Hashtable<>();
        List<Integer> trainList = new ArrayList<>();

        int index = 0, count = 0, num = 0;

        File folder = new File("zeroaccess");
        File fileList[] = folder.listFiles();
        Scanner sc = null;
        for(File file : fileList) {
            sc= new Scanner(file);
            String opCode;
            if(count < trainAmount) {
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    num++;
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
                while (sc.hasNextLine()) {
                    opCode = sc.nextLine();
                    if (hb.containsKey(opCode)) {
                        testList.add(hb.get(opCode));
                    } else {
                        hb.put(opCode, index);
                        testList.add(index);
                        index++;
                    }
                }
            }
            count++;
        }
        System.out.println(count);
        System.out.println(num);

        int[] obsTrain = new int[trainList.size()];
        for(int i = 0; i < obsTrain.length; i++) {
            obsTrain[i] = trainList.get(i);
        }
        return obsTrain;
    }
}
