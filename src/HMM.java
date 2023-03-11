import java.util.*;


public class HMM {
    int[] obs;
    double[] inital;
    double[][] transMatrix;
    double[][] obsMatrix;
    double[][] alpha;
    double[][] beta;
    double[][] gammas;
    double[][][] diGammas;
    double[] scaling;
    int maxIters;
    int iters = 0;
    double oldLogProb = Double.MIN_VALUE;

    int M;

    public HMM(){
        init();
    }

    public void init(){

    }
    public void forwardAlogrthm(){
        alpha = new double[obs.length][inital.length];
        scaling[0] = 0;

        for(int i = 0; i < inital.length; i++){
            alpha[0][i] = inital[i] * obsMatrix[i][obs[0]];
            scaling[0] += alpha[0][i];
        }

        for(int t = 1; t < obs.length; t++){
            scaling[t] = 0;

            for(int i = 0; i < inital.length; i++) {
                alpha[t][i] = 0;

                for (int j = 0; j < inital.length; j++) {
                    alpha[t][i] += alpha[t - 1][j] * transMatrix[j][i];
                }

                alpha[t][i] *= obsMatrix[i][obs[t]];
                scaling[t] += alpha[t][i];
            }

            scaling[t] = 1/scaling[t];
            for (int i = 0; i < inital.length; i++){
                alpha[t][i] *= scaling[t];
            }
        }
    }

    public void backwardAlogrthm(){
        beta = new double[obs.length][inital.length];

        for(int i = 0; i < inital.length; i++){
            beta[obs.length-1][i] = scaling[obs.length-1];
        }

        for(int t = obs.length - 2; t >= 0; t--) {
            for (int i = 0; i < inital.length; i++) {
                beta[t][i] = 0;

                for (int j = 0; j < inital.length; j++) {
                    beta[t][i] += transMatrix[i][j] *  obsMatrix[j][obs[t+1]] * beta[t+1][j];
                }

                beta[t][i] *= scaling[t];
            }
        }
    }

    public void gammas(){
        gammas = new double[obs.length][inital.length];
        diGammas = new double[obs.length][inital.length][inital.length];
        for(int t = 0; t < obs.length - 1; t++){
            double denom = 0;
            for(int i = 0; i < inital.length; i++){
                for(int j = 0; j < inital.length; j++){
                    denom += alpha[t][i] * transMatrix[i][j] * obsMatrix[j][obs[t+1]] * beta[t+1][j];
                }
            }

            for(int i = 0; i < inital.length; i++){
                gammas[t][i] = 0;
                for(int j = 0; j < inital.length; j++){
                    diGammas[t][i][j] = (alpha[t][i] * transMatrix[i][j] * obsMatrix[j][obs[t+1]] * beta[t+1][j]) / denom;
                    gammas[t][i] += diGammas[t][i][j];
                }
            }
        }

        double denom = 0;
        for (int i = 0; i < inital.length; i++){
            denom += alpha[obs.length - 1][i];
        }
        for(int i = 0; i < inital.length; i++){
            gammas[obs.length - 1][i] = alpha[obs.length - 1][i] / denom;
        }
    }

    public void reEstimation(){
        for(int i = 0; i < inital.length; i++){
            inital[i] = gammas[0][i];
        }

        for(int i = 0; i < inital.length; i++){
            double denom = 0;
            for(int t = 0; t < obs.length - 1; t++){
                denom += gammas[t][i];
            }
            for(int j = 0; j < inital.length; j++){
                double numer = 0;
                for(int t = 0; t < obs.length - 1; t++){
                    numer += diGammas[t][i][j];
                }
                transMatrix[i][j] = numer / denom;
            }
        }

        for(int i = 0; i < inital.length; i++){
            double denom = 0;
            for(int t = 0; t < obs.length; t++){
                denom += gammas[t][i];
            }
            for(int j = 0; j < M; j++){
                double numer = 0;
                for(int t = 0; t < obs.length; t++){
                    if(obs[t] == j){
                        numer += gammas[t][i];
                    }
                }
                obsMatrix[i][j] = numer/denom;
            }
        }
    }

    public boolean stop(){
        double logProb = 0;
        for(int i = 0; i < obs.length; i++){
            logProb += oldLogProb;
        }
        logProb *= -1;

        iters++;
        if(iters < maxIters && logProb > oldLogProb){
            oldLogProb = logProb;
            return true;
        }
        else {
            printOut();
            return false;
        }
    }

    public void printOut(){
        System.out.print("Initial state distribution\n");
        System.out.print(Arrays.toString(inital));

        System.out.print("\nState Transition Matrix\n");
        for(double[] row: transMatrix){
            System.out.println(Arrays.toString(row));
        }

        System.out.print("\nObservation Matrix\n");
        for(double[] row: obsMatrix){
            System.out.println(Arrays.toString(row));
        }
    }
    public static void main(String[] args){
        HMM hmm = new HMM();

        do{
            hmm.forwardAlogrthm();
            hmm.backwardAlogrthm();
            hmm.gammas();
            hmm.reEstimation();
        }while(hmm.stop());
    }
}
