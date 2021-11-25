import java.util.*;

public class UtilFunctions {
    /**
     * this generic function returns a string of a given hashMap (for printing)
     *
     * @param hashmap - given hashmap
     * @return hashmap to string
     */
    public static <K, V> String hashMapToString(LinkedHashMap<K, V> hashmap) {
        if (hashmap.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        hashmap.forEach((key, value) -> {
            output.append(key);
            output.append(" : ");
            output.append(value);
            output.append("\n");
        });
        return output.toString();
    }

    /**
     * @param keys is a key string from a CPT table, for example: "A=T,B=F,C=v1"
     * @return hashmap when the keys are the variables name ("A", "B", "C") and the values of them are the outcomes ("T", "F", "v1")
     */
    public static LinkedHashMap<String, String> splitKeysToVariablesAndOutcomes(String keys) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        String[] keys_split = keys.split(",");
        for (String key : keys_split) {
            String[] key_split = key.split("=");
            result.put(key_split[0], key_split[1]);
        }
        return result;
    }

    public static <T> List<T> union(List<T> X, List<T> Y) {
        Set<T> result = new HashSet<>();
        result.addAll(X);
        result.addAll(Y);
        return new ArrayList<>(result);
    }

    public static <T> List<T> intersection(List<T> X, List<T> Y) {
        List<T> result = new ArrayList<>();
        for (T x : X) if (Y.contains(x)) result.add(x);
        return result;
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

    public static List<String> separateByCommas(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    public static LinkedHashMap<String, Double> fixingDuplicatesValuesInKeys(LinkedHashMap<String, Double> factor) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        LinkedHashMap<String, List<String>> outcomes = CPTBuilder.getNamesAndOutcomes(factor);
        List<String> unWelcomeValues = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry : outcomes.entrySet()) {
            if(entry.getValue().size() == 1) {
                String value = entry.getKey() + "=" + entry.getValue().get(0);
                unWelcomeValues.add(value);
                System.out.println("\t\t\t\t\t\tvalue is: " + value);
            }
        }

        for(Map.Entry<String, Double> entry : factor.entrySet()) {
            StringBuilder new_key = new StringBuilder();
            List<String> new_key_split = separateByCommas(entry.getKey());
            for(String key : new_key_split) {
                if(!unWelcomeValues.contains(key)) {
                    new_key.append(key);
                }
            }
            result.put(new_key.toString(), entry.getValue());
        }

        return result;
    }
}
