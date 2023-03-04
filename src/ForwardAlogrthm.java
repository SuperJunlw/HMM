import java.util.*;


public class ForwardAlogrthm {
    public static void forwardAlogrthm(Model model, int[] obs){
        List<List<Double>> alpha = new ArrayList<>();
        List<Double> initalList = new ArrayList<>();
        for(int i = 0; i < model.inital.length - 1; i++){
            initalList.add(model.inital[i] * model.obsMatrix[i][obs[0]]);
        }
        alpha.add(initalList);

        List<Double> tempList = new ArrayList<>();
        for(int t = 1; t < obs.length; t++){
            for(int i = 0; i < model.inital.length; i++){
                tempList.add(0.0);
                for(int j = 0; j < model.inital.length; j++){
                    tempList.set(t, tempList.get(i) + (alpha.get(t-1).get(j) * model.transMatrix[j][i]));
                }
                tempList.set(t, tempList.get(i) * obsMatrix[i][obs[t]])
            }
            alpha.add(new ArrayList<>(tempList));
            tempList.clear();
        }
    }
}
