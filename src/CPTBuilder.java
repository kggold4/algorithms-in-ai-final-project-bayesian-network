import java.lang.reflect.Array;
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
    public static LinkedHashMap<String, Double> buildCPTLinkedHashMap(double[] values, List<List<String>> outcomes, List<String> names) {
        int n = outcomes.size();
        var supp = new Supplier[n];
        for (int i = 0; i < n; i++) {
            int k = i;
            supp[i] = () -> Stream.of(outcomes.get(k).toArray());
        }
        Stream<String> result = cartesian_outcomes((a, b) -> a + b, supp);
        List<String> cartesian_result = result.collect(Collectors.toList());
        LinkedHashMap<String, Double> cpt = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i++) {
            cpt.put(fixCPTLinkedHashMapWithParentsNames(cartesian_result.get(i), names), values[i]);
        }
        return cpt;
    }

    private static String fixCPTLinkedHashMapWithParentsNames(String line, List<String> names) {
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
     * @param aggregator stream operator
     * @param in         stream values
     * @return stream with cartesian outcomes of the cpt
     */
    @SafeVarargs
    private static Stream<String> cartesian_outcomes(BinaryOperator<String> aggregator,
                                                     Supplier<Stream<java.lang.String>>... in) {
        return Arrays.stream(in)
                .reduce((v1, v2) -> () -> v1.get()
                        .flatMap(u1 -> v2.get()
                                .map(u2 -> aggregator.apply(u1, u2))))
                .orElse(Stream::empty).get();
    }

    public static LinkedHashMap<String, Double> joinFactors(List<LinkedHashMap<String, Double>> cpt_to_join, Variable hidden, Network net, HashMap<String, String> evidence) {

        LinkedHashMap<String, Double> factor = cpt_to_join.get(0);

        // for each pair of factors join them
        for(int i = 1; i < cpt_to_join.size(); i++) {
            factor = joinTwoFactors(factor, cpt_to_join.get(i), hidden, net, evidence);
        }

        for(LinkedHashMap<String, Double> cpt : cpt_to_join) {
        }

        return factor;
    }

    private static LinkedHashMap<String, Double> joinTwoFactors(LinkedHashMap<String, Double> X, LinkedHashMap<String, Double> Y, Variable hidden, Network net, HashMap<String, String> evidence) {

        int i;

        // get the outcome hashmaps for X and Y
        HashMap<String, List<String>> X_outcomes = getNamesAndOutcomes(X);
        HashMap<String, List<String>> Y_outcomes = getNamesAndOutcomes(Y);

        // get the names of variables for X and Y
        Set<String> X_names_set = X_outcomes.keySet();
        List<String> X_names = new ArrayList<>(X_names_set);
        Set<String> Y_names_set = Y_outcomes.keySet();
        List<String> Y_names = new ArrayList<>(Y_names_set);

        // remove the hidden name from both names of X and Y
        X_names.remove(hidden.getName());
        Y_names.remove(hidden.getName());

        // get the names of all the variables that the factor will contain
        List<String> names = new ArrayList<>();
        names.add(hidden.getName());
        names.addAll(X_names);
        names.addAll(Y_names);

        HashMap<String, List<String>> variables_outcomes = new HashMap<>();

        for(String name : X_names) {
            List<String> variable_outcomes = getNamesAndOutcomes(X).get(name);
            variables_outcomes.put(name, variable_outcomes);
        }

        for(String name : Y_names) {
            List<String> variable_outcomes = getNamesAndOutcomes(Y).get(name);
            variables_outcomes.put(name, variable_outcomes);
        }

        // outcomes of all the variables hidden, X and Y
        List<List<String>> outcomes = new ArrayList<>();
        outcomes.add(hidden.getOutcomes());

        for(Map.Entry<String, List<String>> vo : variables_outcomes.entrySet()) {
            outcomes.add(vo.getValue());
        }

        double[] X_values = new double[X.size()];
        i = 0;
        for(Map.Entry<String, Double> x : X.entrySet()) {
            X_values[i++] = x.getValue();
        }

        double[] Y_values = new double[Y.size()];
        i = 0;
        for(Map.Entry<String, Double> y : Y.entrySet()) {
            Y_values[i++] = y.getValue();
        }

        double[][] res = UtilFunctions.cartesianTwoDoubleArrays(X_values, Y_values);

        for(i = 0; i < res.length; i++) {
            for(int j = 0; j < res[i].length; j++) {
            }
        }

        int X_outcomes_size = 0;
        for(String name : X_names) {
            List<String> o = variables_outcomes.get(name);
            X_outcomes_size += o.size();
        }

        int Y_outcomes_size = 0;
        for(String name : X_names) {
            List<String> o = variables_outcomes.get(name);
            Y_outcomes_size += o.size();
        }

        int H_outcomes_size = hidden.getOutcomes().size();

        System.out.println("X_values: " + Arrays.toString(X_values));
        System.out.println("Y_values: " + Arrays.toString(Y_values));

        double[] values = new double[H_outcomes_size * X_outcomes_size * Y_outcomes_size];

        int k = 0, n = 0, m = 0;
        for(int h = 0; h < H_outcomes_size; h++) {
            for(int x = 0; x < X_outcomes_size; x++) {
                for(int y = 0; y < Y_outcomes_size; y++) {
                    values[k++] = X_values[x + n] * Y_values[y + m];
                }
            }
            n += X_outcomes_size;
            m += Y_outcomes_size;
        }

        System.out.println("names are: " + names);
        System.out.println("outcomes:");
        for(List<String> outcome : outcomes) {
            System.out.println(outcome);
        }
        System.out.println("values: " + Arrays.toString(values));

        // the new factor of X and Y
        LinkedHashMap<String, Double> factor = CPTBuilder.buildCPTLinkedHashMap(values, outcomes, names);
        System.out.println("new factor: ");
        System.out.println(UtilFunctions.hashMapToString(factor));
        return null;
    }

    private static HashMap<String, List<String>> getNamesAndOutcomes(LinkedHashMap<String, Double> cpt) {
        HashMap<String, List<String>> outcomes = new HashMap<>();

        List<String> names = new ArrayList<>();
        for(Map.Entry<String, Double> line : cpt.entrySet()) {
            LinkedHashMap<String, String> line_split = UtilFunctions.splitKeysToVariablesAndOutcomes(line.getKey());
            for(Map.Entry<String, String> inner : line_split.entrySet()) {
                names.add(inner.getKey());
            }
            break;
        }

        for(String name : names) {
            outcomes.put(name, new ArrayList<>());
        }

        for(Map.Entry<String, Double> line : cpt.entrySet()) {
            LinkedHashMap<String, String> line_split = UtilFunctions.splitKeysToVariablesAndOutcomes(line.getKey());
            for(Map.Entry<String, String> inner : line_split.entrySet()) {
                if(!outcomes.get(inner.getKey()).contains(inner.getValue())) {
                    outcomes.get(inner.getKey()).add(inner.getValue());
                }
            }
        }
        return outcomes;
    }

    /**
     * @param list of strings
     * @return string of the list strings seperated by commas
     */
    public static String combineWithCommas(List<String> list) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            output.append(list.get(i));
            if (i != list.size() - 1) output.append(",");
        }
        return output.toString();
    }
}
