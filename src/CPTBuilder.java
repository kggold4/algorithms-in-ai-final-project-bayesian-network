
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

    /**
     * join a list of factors to one
     *
     * @param cpt_to_join the list of the factors to join
     * @param hidden      the hidden variable
     * @return joined factor
     */
    public static LinkedHashMap<String, Double> joinFactors(List<LinkedHashMap<String, Double>> cpt_to_join, Variable hidden) {

        LinkedHashMap<String, Double> factor = cpt_to_join.get(0);

        // for each pair of factors join them
        for (int i = 1; i < cpt_to_join.size(); i++) {
            factor = joinTwoFactors(factor, cpt_to_join.get(i), hidden);
        }

        return factor;
    }

    /**
     * join two factors function
     *
     * @param X      the first factor
     * @param Y      the second factor
     * @param hidden the hidden variable
     * @return new factor of X and Y combined
     */
    private static LinkedHashMap<String, Double> joinTwoFactors(LinkedHashMap<String, Double> X, LinkedHashMap<String, Double> Y, Variable hidden) {

        if (X.size() > Y.size()) {

            LinkedHashMap<String, Double> T = new LinkedHashMap<>(X);
            X = new LinkedHashMap<>(Y);
            Y = new LinkedHashMap<>(T);

        }

        System.out.println("////////////////////////////////////////////");
        System.out.println("X:");
        System.out.println(UtilFunctions.hashMapToString(X));
        System.out.println("Y:");
        System.out.println(UtilFunctions.hashMapToString(Y));
        System.out.println("////////////////////////////////////////////");

        int i;

        // get the outcome hashmaps for X and Y
        HashMap<String, List<String>> X_outcomes = getNamesAndOutcomes(X);
        HashMap<String, List<String>> Y_outcomes = getNamesAndOutcomes(Y);

        Set<String> X_names_set = X_outcomes.keySet();
        List<String> X_names = new ArrayList<>(X_names_set);
        Set<String> Y_names_set = Y_outcomes.keySet();
        List<String> Y_names = new ArrayList<>(Y_names_set);

        // get the names of all the variables that the factor will contain
        List<String> X_Y_names_union = UtilFunctions.union(X_names, Y_names);
        List<String> names = new ArrayList<>(X_Y_names_union);

        List<String> X_Y_names_intersection = UtilFunctions.intersection(X_names, Y_names);

        HashMap<String, List<String>> variables_outcomes = new HashMap<>();

        for (String name : X_names) {
            List<String> variable_outcomes = getNamesAndOutcomes(X).get(name);
            variables_outcomes.put(name, variable_outcomes);
        }

        for (String name : Y_names) {
            List<String> variable_outcomes = getNamesAndOutcomes(Y).get(name);
            variables_outcomes.put(name, variable_outcomes);
        }

        // outcomes of all the variables hidden, X and Y
        List<List<String>> outcomes = new ArrayList<>();

        for (Map.Entry<String, List<String>> vo : variables_outcomes.entrySet()) {
            outcomes.add(vo.getValue());
        }

        for (List<String> o : outcomes) {
        }

        double[] X_values = new double[X.size()];
        i = 0;
        for (Map.Entry<String, Double> x : X.entrySet()) {
            X_values[i++] = x.getValue();
        }

        double[] Y_values = new double[Y.size()];
        i = 0;
        for (Map.Entry<String, Double> y : Y.entrySet()) {
            Y_values[i++] = y.getValue();
        }


        int factor_size = 1;
        for (String name : X_Y_names_union) {
            int size = variables_outcomes.get(name).size();
            factor_size *= size;
        }

        double[] values = new double[factor_size];
        int values_index = 0;

        for (; values_index < factor_size; values_index++) {
            values[values_index] = 1.0;
        }

        values_index = 0;
        for (Map.Entry<String, Double> y : Y.entrySet()) {
            LinkedHashMap<String, String> t = UtilFunctions.splitKeysToVariablesAndOutcomes(y.getKey());
            List<String> s = new ArrayList<>();
            for (String name : X_Y_names_intersection) {
                s.add(name + "=" + t.get(name));
            }
            boolean b = true;
            for (Map.Entry<String, Double> x : X.entrySet()) {
                for (String name : s) {
                    if (!x.getKey().contains(name)) {
                        b = false;
                    }
                }

                // found the wanted row
                if (b) {
                    double u = y.getValue();
                    double v = x.getValue();
                    double r = u * v;
                    values[values_index] *= r;
                    values_index++;
                }
                b = true;
            }
        }

        return CPTBuilder.buildCPTLinkedHashMap(values, outcomes, names);
    }

    /**
     * @param cpt factor
     * @return hashmap when the keys are the name of the variables that are in the factor and the values are the outcomes of those variables
     */
    public static LinkedHashMap<String, List<String>> getNamesAndOutcomes(LinkedHashMap<String, Double> cpt) {
        LinkedHashMap<String, List<String>> outcomes = new LinkedHashMap<>();

        List<String> names = new ArrayList<>();
        for (Map.Entry<String, Double> line : cpt.entrySet()) {
            LinkedHashMap<String, String> line_split = UtilFunctions.splitKeysToVariablesAndOutcomes(line.getKey());
            for (Map.Entry<String, String> inner : line_split.entrySet()) {
                names.add(inner.getKey());
            }
            break;
        }

        for (String name : names) {
            outcomes.put(name, new ArrayList<>());
        }

        for (Map.Entry<String, Double> line : cpt.entrySet()) {
            LinkedHashMap<String, String> line_split = UtilFunctions.splitKeysToVariablesAndOutcomes(line.getKey());
            for (Map.Entry<String, String> inner : line_split.entrySet()) {
                if (!outcomes.get(inner.getKey()).contains(inner.getValue())) {
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
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i != list.size() - 1) result.append(",");
        }
        return result.toString();
    }

    public static LinkedHashMap<String, Double> eliminate(LinkedHashMap<String, Double> factor, Variable hidden) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        // build list with all the outcomes of hidden and his name, for example: {"A=T", "A=F"}
        List<String> outcomes = hidden.getOutcomes();
        List<String> values = new ArrayList<>();
        for (String outcome : outcomes) {
            values.add(hidden.getName() + "=" + outcome);
        }

        for (Map.Entry<String, Double> line : factor.entrySet()) {
            for (String value : values) {
                if (line.getKey().contains(value)) {

                    // build the new key without the value
                    List<String> split_new_key = new ArrayList<>(List.of(line.getKey().split(",")));
                    split_new_key.remove(value);
                    StringBuilder new_key = new StringBuilder();
                    for (int i = 0; i < split_new_key.size(); i++) {
                        new_key.append(split_new_key.get(i));
                        if (i != split_new_key.size() - 1) {
                            new_key.append(",");
                        }
                    }

                    double variable_value = 0;
                    if(result.containsKey(new_key.toString())) {
                        variable_value = result.get(new_key.toString());
                    }

                    for (Map.Entry<String, Double> line_again : factor.entrySet()) {

                        if (line_again.getKey().contains(new_key.toString())) {
                            variable_value += line_again.getValue();
                            System.out.println();
                        }
                    }

                    result.put(new_key.toString(), variable_value);
                }
            }
        }
        return result;
    }

    private static double getSumValues(LinkedHashMap<String, Double> factor, String value) {
        System.out.println("FACTOR TO GET SUM ON " + value + "\n" + UtilFunctions.hashMapToString(factor));
        double result = 0.0;

        return result;
    }
}
