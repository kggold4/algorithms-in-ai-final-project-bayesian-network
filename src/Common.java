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
        for(String[] x : X) {
            for(String s : x) {
                P[k++] = s;
            }
        }
        return P;
    }

    public static void printMatrix(String[][] X) {
        for(String[] x : X) {
            for(String s : x) {
                System.out.print(s + ", ");
            }
            System.out.println();
        }
    }

    public static String printHashMap(HashMap<String, Double> map) {
        StringBuilder output = new StringBuilder();
        Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Double> pair = iter.next();
            output.append(pair.getKey()).append(" : ").append(pair.getValue()).append("\n");
            iter.remove();
        }
        return output.toString();
    }

    public static HashMap<String, Double> convertMatrixToHashMap(String[][] matrix, double[] values) {
        HashMap<String, Double> map = new HashMap<>();
        for(int i = 0; i < matrix.length; i++) {
            StringBuilder in = new StringBuilder();
            for(int j = 0; j < matrix[0].length; j++) {
                in.append(matrix[i][j]);
            }
            map.put(in.toString(), values[i]);

        }
        return map;
    }
}
