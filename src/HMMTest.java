public class HMMTest {
    int[] obs;
    double[] inital;
    double[][] transMatrix;
    double[][] obsMatrix;
    double[][] alpha;
    double[] scaling;
    double newLogProb = 0;

    public HMMTest(int[] obs, double[] inital, double[][] transMatrix, double[][] obsMatrix){
        this.obs = obs;
        this.inital = inital;
        this.transMatrix = transMatrix;
        this.obsMatrix = obsMatrix;
        this.alpha = new double[this.obs.length][this.inital.length];
        this.scaling = new double[this.obs.length];
    }

    public void forwardAlogrthm(){
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

    public void calculateScore(){
        double logProb = 0;
        for(int i = 0; i < obs.length; i++){
            logProb += Math.log(scaling[i]);
        }
        logProb *= -1;
        newLogProb = logProb;
    }

}
