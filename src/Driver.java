import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Driver {

    public static void main(String[] args) throws IOException {
        int trainAmount = 1100;
        int testAmount = 200;
        List<Integer> testList = new ArrayList<>();
        int[] obs = FileIO.readFolder(trainAmount, testAmount, testList);

        int[] zaTestSet = new int[testList.size()];
        for(int i = 0; i < zaTestSet.length; i++) {
            zaTestSet[i] = testList.get(i);
        }

        System.out.print(Arrays.toString(zaTestSet));
        System.out.print(zaTestSet.length);
        /*
        int N = 0;
        int M = 0;
        HMMTrain hmmTrain = new HMMTrain(N, M);

        do{
            hmmTrain.init();
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
            hmmTrain.calculateScore();
        }while(hmmTrain.stop());

        HMMTest hmmTest = new HMMTest();
        hmmTest.forwardAlogrthm();
        hmmTest.calculateScore();
         */

    }
}
