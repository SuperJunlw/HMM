import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Driver {

    public static void main(String[] args) throws IOException {
        int trainAmount = 1100;
        int testAmount = 200;
        int secondAmount = 550;

        int[] obs = FileIO.readFolder(trainAmount, testAmount); //get the observation sequence
        int[] zaTestSet = new int[FileIO.testList.size()]; //get 1st test set
        for(int i = 0; i < zaTestSet.length; i++) {
            zaTestSet[i] = FileIO.testList.get(i);
        }
        int[] zbotTestSet = FileIO.zbotTest(secondAmount); //get 2nd test set

        //System.out.print(Arrays.toString(secondTestSet));
        //System.out.println(secondTestSet.length);
        //System.out.println(zaTestSet.length);

        int N = 2;
        int M = obs.length;
        HMMTrain hmmTrain = new HMMTrain(N, M, obs);

        hmmTrain.init();

        do{
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
            hmmTrain.calculateScore();
        }while(hmmTrain.continues());

        HMMTest hmmTest = new HMMTest(zaTestSet, hmmTrain.inital, hmmTrain.transMatrix, hmmTrain.obsMatrix);
        hmmTest.forwardAlogrthm();
        System.out.println(hmmTest.calculateScore());


    }
}
