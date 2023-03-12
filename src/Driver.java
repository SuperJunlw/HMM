import java.io.IOException;

public class Driver {

    public static void main(String[] args) throws IOException {
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
        System.out.print(FileIO.readZbot());
    }
}
