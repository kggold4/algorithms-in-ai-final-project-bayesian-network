import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * this class contains a several functions the can build a CPT tables for the bayesian network variables
 */
public class CPTBuilder {

    /**
     * build and CPT hashmap by given the names, outcomes and values of the variable
     *
     * @param values   - values of outcomes for each variable
     * @param outcomes - possible outcomes for each variable
     * @param names    - name for each variable
     * @return - .
     */
    public static HashMap<String, Double> BuildCPTHashMap(double[] values, List<List<String>> outcomes, List<String> names) {
        int n = outcomes.size();
        var supp = new Supplier[n];
        for (int i = 0; i < n; i++) {
            int k = i;
            supp[i] = () -> Stream.of(outcomes.get(k).toArray());
        }
        Stream<String> result = cartesian_outcomes((a, b) -> a + b, supp);
        List<String> cartesian_result = result.collect(Collectors.toList());
        HashMap<String, Double> cpt = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            cpt.put(fixCPTHashMapWithParentsNames(cartesian_result.get(i), names), values[i]);
        }
        return cpt;
    }

    private static String fixCPTHashMapWithParentsNames(String line, List<String> names) {
        String[] split_line = line.split("(?!^)");
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String name : names) {
            result.append(name).append('=');
            result.append(split_line[i]);
            if (i < split_line.length - 1) {
                result.append(',');
            }
            i++;
        }
        return result.toString();
    }

    /**
     * calculate cartesian of several given outcomes using java streams
     *
     * @param aggregator
     * @param in
     * @return
     */
    private static Stream<String> cartesian_outcomes(BinaryOperator<String> aggregator,
                                                     Supplier<Stream<java.lang.String>>... in) {
        return Arrays.stream(in)
                .reduce((v1, v2) -> () -> v1.get()
                        .flatMap(u1 -> v2.get()
                                .map(u2 -> aggregator.apply(u1, u2))))
                .orElse(Stream::empty).get();
    }

    /**
     * this function returns a string of a given CPT hashMap (for printing)
     *
     * @param map - given CPT hashmap
     * @return CPT hashmap to string
     */
    public static String toString(HashMap<String, Double> map) {
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
