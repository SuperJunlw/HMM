import weka.classifiers.bayes.HMM;
import weka.core.WekaPackageManager;
import java.lang.*;
import java.io.IOException;
import java.util.*;
public class Driver {

    public static void main(String[] args) throws Exception {

        int trainAmount = 3300;
        int testAmount = 200;
        int M = 30;
        int N = 2;


        //preprocess data without noise reduction
        //trainAndTestWithOutNoise(trainAmount, testAmount, N);

        //preprocess data with noise reduction
        trainAndTestWithNoise(trainAmount, testAmount, M, N);


    }

    public static void trainAndTestWithOutNoise(int trainAmount, int testAmount, int N) throws IOException {
        FileIO.readFolderWithOutNoiseRe();
        int[] obs = FileIO.getZATrainAndTestNoNoise(trainAmount, testAmount); //get the observation sequence
        FileIO.zbotTest(testAmount);

        int M = FileIO.index;

        HMMTrain hmmTrain = new HMMTrain(N, M, obs);
        hmmTrain.init();
        do{
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
            //hmmTrain.calculateScore();
        }while(hmmTrain.continues());
        hmmTrain.printOut();

        System.out.println("Trained test set scores: \n");
        getTestDataScores(FileIO.zATestDataList, hmmTrain);
        System.out.println("\nUntrained test set scores: \n");
        getTestDataScores(FileIO.zBotTestDataList, hmmTrain);
    }

    public static void trainAndTestWithNoise(int trainAmount, int testAmount, int numSymbols, int N) throws IOException{
        FileIO.countSymbolFre(numSymbols);
        int[] obs = FileIO.readFolderWithNoiseRe(numSymbols, trainAmount, testAmount);
        FileIO.zbotTestWithNoise(numSymbols, testAmount);

        int M = numSymbols;

        HMMTrain hmmTrain = new HMMTrain(N, M, obs);
        hmmTrain.init();
        do{
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
            //hmmTrain.calculateScore();
        }while(hmmTrain.continues());
        hmmTrain.printOut();

        //with noise reduction
        System.out.println("Trained test set scores: \n");
        getTestDataScores(FileIO.zATestDataListNoise, hmmTrain);
        System.out.println("\nUntrained test set scores: \n");
        getTestDataScores(FileIO.zBotTestDataListNoise, hmmTrain);
    }
    public static void getTestDataScores(List<List<Integer>> dataset, HMMTrain model){
        for(List<Integer> list : dataset){
            int[] sequence = new int[list.size()];
            for(int i = 0; i < sequence.length; i++){
                sequence[i] = list.get(i);
            }
            //System.out.println(Arrays.toString(sequence) + "\n");
            HMMTest test = new HMMTest(sequence, model.inital, model.transMatrix, model.obsMatrix);
            test.forwardAlogrthm();
            System.out.println(test.calculateScore());
        }
    }
}
