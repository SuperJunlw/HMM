import java.util.*;


public class HMMTrain {
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
    int iters;
    double oldLogProb;
    double newLogProb;
    int M;
    int N;

    public HMMTrain(int N, int M, int[] obs){
        this.N = N;
        this.M = M;
        this.obs = obs;
        inital = new double[N];
        transMatrix = new double[N][N];
        obsMatrix = new double[N][M];
        alpha = new double[this.obs.length][N];
        beta = new double[this.obs.length][N];
        gammas = new double[this.obs.length][N];
        diGammas = new double[this.obs.length][N][N];
        scaling = new double[obs.length];
    }

    public void init(){
        maxIters = 100;
        iters = 0;
        oldLogProb = -1 * Double.MAX_VALUE;
        newLogProb = 0;

        for(int i = 0; i < inital.length; i++){
            inital[i] = 1 / (N + ((Math.random() * (0.1 - -0.1)) + -0.1));
        }

        for(int i = 0; i < transMatrix.length; i++){
            for(int j = 0; j < transMatrix[0].length; j++){
                transMatrix[i][j] = 1 / (N + ((Math.random() * (0.1 - -0.1)) + -0.1));
            }
        }

        for(int i = 0; i < obsMatrix.length; i++){
            for(int j = 0; j < obsMatrix[0].length; j++){
                obsMatrix[i][j] = 1 / (M + ((Math.random() * (0.1 - -0.1)) + -0.1));
            }
        }
    }

    public void forwardAlogrthm(){
        scaling[0] = 0;

        for(int i = 0; i < N; i++){
            alpha[0][i] = inital[i] * obsMatrix[i][obs[0]];
            scaling[0] += alpha[0][i];
        }

        scaling[0] = 1/scaling[0];
        for(int i = 0; i < N; i++){
            alpha[0][i] *= scaling[0];
        }

        for(int t = 1; t < obs.length; t++){
            scaling[t] = 0;

            for(int i = 0; i < N; i++) {
                alpha[t][i] = 0;

                for (int j = 0; j < N; j++) {
                    alpha[t][i] += alpha[t - 1][j] * transMatrix[j][i];
                }

                alpha[t][i] *= obsMatrix[i][obs[t]];
                scaling[t] += alpha[t][i];
            }

            scaling[t] = 1/scaling[t];
            for (int i = 0; i < N; i++){
                alpha[t][i] *= scaling[t];
            }
        }
    }

    public void backwardAlogrthm(){
        for(int i = 0; i < N; i++){
            beta[obs.length-1][i] = scaling[obs.length-1];
        }

        for(int t = obs.length - 2; t >= 0; t--) {
            for (int i = 0; i < N; i++) {
                beta[t][i] = 0;

                for (int j = 0; j < N; j++) {
                    beta[t][i] += transMatrix[i][j] *  obsMatrix[j][obs[t+1]] * beta[t+1][j];
                }

                beta[t][i] *= scaling[t];
            }
        }
    }

    public void gammas(){
        for(int t = 0; t < obs.length - 1; t++){
            double denom = 0;
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    denom += alpha[t][i] * transMatrix[i][j] * obsMatrix[j][obs[t+1]] * beta[t+1][j];
                }
            }

            for(int i = 0; i < N; i++){
                gammas[t][i] = 0;
                for(int j = 0; j < N; j++){
                    diGammas[t][i][j] = (alpha[t][i] * transMatrix[i][j] * obsMatrix[j][obs[t+1]] * beta[t+1][j]) / denom;
                    gammas[t][i] += diGammas[t][i][j];
                }
            }
        }

        double denom = 0;
        for (int i = 0; i < N; i++){
            denom += alpha[obs.length - 1][i];
        }
        for(int i = 0; i < N; i++){
            gammas[obs.length - 1][i] = alpha[obs.length - 1][i] / denom;
        }
    }

    public void reEstimation(){
        for(int i = 0; i < N; i++){
            inital[i] = gammas[0][i];
        }

        for(int i = 0; i < N; i++){
            double denom = 0;
            for(int t = 0; t < obs.length - 1; t++){
                denom += gammas[t][i];
            }
            for(int j = 0; j < N; j++){
                double numer = 0;
                for(int t = 0; t < obs.length - 1; t++){
                    numer += diGammas[t][i][j];
                }
                transMatrix[i][j] = numer / denom;
            }
        }

        for(int i = 0; i < N; i++){
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

    public void calculateScore(){
        double logProb = 0;
        for(int i = 0; i < obs.length; i++){
            logProb += Math.log(scaling[i]);
        }
        logProb *= -1;
        newLogProb = logProb;
    }

    public boolean continues(){

        if(iters < maxIters && newLogProb > oldLogProb){
            oldLogProb = newLogProb;
            iters++;
            return true;
        }
        else {
            //printOut();
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
}
