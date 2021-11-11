import java.util.*;

public class Common {
    public static String[][] permutation(String X_name, String Y_name, String[] X_outcomes, String[] Y_outcomes) {
        String[][] P = new String[X_outcomes.length][Y_outcomes.length];
        for (int i = 0; i < X_outcomes.length; i++) {
            for (int j = 0; j < Y_outcomes.length; j++) {
                P[i][j] = X_name + '=' + X_outcomes[i] + ',' + Y_name + '=' + Y_outcomes[j];
            }
        }
        return P;
    }

    public static String[] flatMatrix(String[][] X) {
        String[] P = new String[X.length * X[0].length];
        int k = 0;
        for (String[] x : X) {
            for (String s : x) {
                P[k++] = s;
            }
        }
        return P;
    }

    public static HashMap<String, Double> convertMatrixToHashMap(String[][] matrix, double[] values) {
        HashMap<String, Double> map = new HashMap<>();
        for (int i = 0, k = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                map.put(matrix[i][j], values[k++]);
            }
        }
        return map;
    }

    public static String printMatrix(String[][] X) {
        StringBuilder output = new StringBuilder();
        for (String[] x : X) {
            for (String s : x) {
                output.append(s).append(", ");
            }
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * this class returns a string of a given hashMap (for printing)
     * @param map - given hashmap
     * @return hashmap to string
     */
    public static String printHashMap(HashMap<String, Double> map) {
        StringBuilder output = new StringBuilder();
        Iterator<Map.Entry<String, Double>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Double> pair = iter.next();
            output.append(pair.getKey()).append(" : ").append(pair.getValue()).append("\n");
            iter.remove();
        }
        return output.toString();
    }

    public static List<Double> split_table_line(String line) {
        String[] split_line = line.split(" ");
        List<Double> result = new ArrayList<>();
        for(String value : split_line) {
            result.add(Double.parseDouble(value));
        }
        return result;

    }
}
