import java.lang.*;
import java.io.IOException;
import java.util.*;
public class Driver {

    public static void main(String[] args) throws Exception {

        int trainAmount = 3300;
        int testAmount = 150;
        int M = 40;
        int N = 2;


        //preprocess data without noise reduction
        trainAndTestWithOutNoise(trainAmount, testAmount, N);

        //preprocess data with noise reduction
        //trainAndTestWithNoise(trainAmount, testAmount, M, N);


    }

    //read, train and test the model without noise reduction
    public static void trainAndTestWithOutNoise(int trainAmount, int testAmount, int N) throws IOException {

        //read the folder and get the training and testing set
        FileIO.readFolderWithOutNoiseRe();
        int[] obs = FileIO.getZATrainAndTestNoNoise(trainAmount, testAmount); //get the observation sequence
        FileIO.zbotTest(testAmount);

        int M = FileIO.index;

        //train the model
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

        //use the model to test files from two different class
        System.out.println("Trained test set scores: \n");
        getTestDataScores(FileIO.zATestDataList, hmmTrain);
        System.out.println("\nUntrained test set scores: \n");
        getTestDataScores(FileIO.zBotTestDataList, hmmTrain);
    }

    //read, train and test the model without noise reduction
    public static void trainAndTestWithNoise(int trainAmount, int testAmount, int numSymbols, int N) throws IOException{
        //read the folder and get the training and testing set
        FileIO.countSymbolFre(numSymbols);
        int[] obs = FileIO.readFolderWithNoiseRe(numSymbols, trainAmount, testAmount);
        FileIO.zbotTestWithNoise(numSymbols, testAmount);

        int M = numSymbols;

        //train the model
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

        // test the model with noise reduction
        System.out.println("Trained test set scores: \n");
        getTestDataScores(FileIO.zATestDataListNoise, hmmTrain);
        System.out.println("\nUntrained test set scores: \n");
        getTestDataScores(FileIO.zBotTestDataListNoise, hmmTrain);
    }

    //test each testing sequence with the HMMTest class
    public static void getTestDataScores(List<List<Integer>> dataset, HMMTrain model){
        for(List<Integer> list : dataset){
            int[] sequence = new int[list.size()];
            for(int i = 0; i < sequence.length; i++){
                sequence[i] = list.get(i);
            }

            HMMTest test = new HMMTest(sequence, model.inital, model.transMatrix, model.obsMatrix);
            test.forwardAlogrthm();
            System.out.println(test.calculateScore());
        }
    }
}
