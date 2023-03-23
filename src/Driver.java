import java.lang.*;
import java.io.IOException;
import java.util.*;
public class Driver {

    public static void main(String[] args) throws Exception {

        int trainAmount = 3300; //about 80% of files in winwebsec
        int testAmount = 150;
        int testLength = 500; //set fixed test sequence length
        int M = 50;
        int N = 2;


        //preprocess data without noise reduction
        //trainAndTestWithOutNoise(trainAmount, testAmount, testLength, N);

        //preprocess data with noise reduction
        trainAndTestWithNoise(trainAmount, testAmount, testLength, M, N);


    }

    //read, train and test the model without noise reduction
    public static void trainAndTestWithOutNoise(int trainAmount, int testAmount, int testLength, int N) throws IOException {

        //read the folder and get the training and testing set
        FileIO.readFolderWithOutNoiseRe();
        int[] obs = FileIO.getTrainAndTestNoNoise(trainAmount, testAmount, testLength); //get the observation sequence
        FileIO.secondTestNoNoiseRe(testAmount, testLength);

        int M = FileIO.index;

        //train the model
        HMMTrain hmmTrain = new HMMTrain(N, M, obs);
        hmmTrain.init();
        do{
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
        }while(hmmTrain.continues());
        hmmTrain.printOut();

        //use the model to test files from two different class
        System.out.println("Trained test set scores: \n");
        getTestDataScores(FileIO.firstTestDataList, hmmTrain);
        System.out.println("\nUntrained test set scores: \n");
        getTestDataScores(FileIO.secondTestDataList, hmmTrain);
    }

    //read, train and test the model with noise reduction
    public static void trainAndTestWithNoise(int trainAmount, int testAmount, int testLength, int numSymbols, int N) throws IOException{
        //read the folder and get the training and testing set
        FileIO.countSymbolFre(numSymbols);
        int[] obs = FileIO.readFolderWithNoiseRe(numSymbols, trainAmount, testAmount, testLength);
        FileIO.secondTestWithNoise(numSymbols, testAmount, testLength);

        int M = numSymbols;

        //train the model
        HMMTrain hmmTrain = new HMMTrain(N, M, obs);
        hmmTrain.init();
        do{
            hmmTrain.forwardAlogrthm();
            hmmTrain.backwardAlogrthm();
            hmmTrain.gammas();
            hmmTrain.reEstimation();
        }while(hmmTrain.continues());
        hmmTrain.printOut();

        // test the model with noise reduction
        System.out.println("Trained test set scores: \n");
        getTestDataScores(FileIO.firstTestDataListNoise, hmmTrain);
        System.out.println("\nUntrained test set scores: \n");
        getTestDataScores(FileIO.secondTestDataListNoise, hmmTrain);
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
