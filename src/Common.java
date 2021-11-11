import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static HashMap<String, Double> BuildCPTHashMap(double[] values, List<String[]> outcomes, List<String> names) {
        int n = outcomes.size();
        Stream<String> result = cartesian_outcomes(
                (a, b) -> a + b,
                0 < n ? () -> Stream.of(outcomes.get(0)) : () -> Stream.of(new String[]{""}),
                1 < n ? () -> Stream.of(outcomes.get(1)) : () -> Stream.of(new String[]{""}),
                2 < n ? () -> Stream.of(outcomes.get(2)) : () -> Stream.of(new String[]{""}),
                3 < n ? () -> Stream.of(outcomes.get(3)) : () -> Stream.of(new String[]{""}),
                4 < n ? () -> Stream.of(outcomes.get(4)) : () -> Stream.of(new String[]{""}),
                5 < n ? () -> Stream.of(outcomes.get(5)) : () -> Stream.of(new String[]{""})
        );
        List<String> cartesian_result = result.collect(Collectors.toList());
        HashMap<String, Double> cpt = new HashMap<>();
        for(int i = 0; i < values.length; i++) {
            cpt.put(fixCPTHashMapWithParentsNames(cartesian_result.get(i), names), values[i]);
        }
        return cpt;
    }

    private static String fixCPTHashMapWithParentsNames(String line, List<String> names) {
        String[] split_line = line.split("(?!^)");
        StringBuilder result = new StringBuilder();
        int i = 0;
        for(String name : names) {
            result.append(name + '=');
            result.append(split_line[i]);
            if(i < split_line.length - 1) {
                result.append(',');
            }
            i++;
        }
        return result.toString();
    }

    @SafeVarargs
    private static Stream<String> cartesian_outcomes(BinaryOperator<java.lang.String> aggregator,
                                                     Supplier<Stream<java.lang.String>>... in) {
        return Arrays.stream(in)
                .reduce((v1, v2) -> () -> v1.get()
                .flatMap(u1 -> v2.get()
                .map(u2 -> aggregator.apply(u1, u2))))
                .orElse(Stream::empty).get();
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
}
