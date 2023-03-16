import java.io.IOException;
import java.util.*;


public class Driver {

    public static void main(String[] args) throws IOException {
        int trainAmount = 1100;
        int testAmount = 205;

        //preprocess data with noise reduction
        int numSymbols = 40;

        FileIO.countSymbolFre(numSymbols);
        int[] obs = FileIO.readFolderWithNoiseRe(numSymbols, trainAmount, testAmount);
        FileIO.zbotTestWithNoise(numSymbols, testAmount);

        int N = 2;
        int M = numSymbols + 1;


        //preprocess data without noise reduction
/*
        FileIO.readFolderWithOutNoiseRe();
        int[] obs = FileIO.getZATrainAndTestNoNoise(trainAmount, testAmount); //get the observation sequence
        FileIO.zbotTest(testAmount);

        int N = 2;
        int M = FileIO.index;

 */
        HMMTrain hmmTrain = new HMMTrain(N, M, obs);
        hmmTrain.init();
        do{
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
            hmmTrain.calculateScore();
        }while(hmmTrain.continues());
        hmmTrain.printOut();

        //without noise reduction
/*
        System.out.println("Zeroaccess test set scores: \n");
        getTestDataScores(FileIO.zATestDataList, hmmTrain);
        System.out.println("\nZbot test set scores: \n");
        getTestDataScores(FileIO.zBotTestDataList, hmmTrain);

 */
        //with noise reduction
        System.out.println("Zeroaccess test set scores: \n");
        getTestDataScores(FileIO.zATestDataListNoise, hmmTrain);
        System.out.println("\nZbot test set scores: \n");
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
