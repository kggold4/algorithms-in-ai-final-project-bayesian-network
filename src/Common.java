import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Common {
    public static String[][] permutation(String X_name, String Y_name, String[] X_outcomes, String[] Y_outcomes) {
        String[][] P = new String[X_outcomes.length][Y_outcomes.length];
        for(int i = 0; i < X_outcomes.length; i++) {
            for(int j = 0; j < Y_outcomes.length; j++) {
                P[i][j] = X_name + '=' + X_outcomes[i] + ',' + Y_name + '=' + Y_outcomes[j];
            }
        }
        return P;
    }

    public static String[] flatMatrix(String[][] X) {
        String[] P = new String[X.length * X[0].length];
        int k = 0;
        for(int i = 0; i < X.length; i++) {
            for(int j = 0; j < X[i].length; j++) {
                P[k++] = X[i][j];
            }
        }
        return P;
    }

    public static void printMatrix(String[][] X) {
        for(int i = 0; i < X.length; i++) {
            for(int j = 0; j < X[i].length; j++) {
                System.out.print(X[i][j] + ", ");
            }
            System.out.println();
        }
    }

    public static String printHashMap(HashMap<String, Double> map) {
        String output = "";
        Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Double> pair = iter.next();
            output += pair.getKey() + " : " + pair.getValue() + "\n";
            iter.remove();
        }
        return output;
    }
}
