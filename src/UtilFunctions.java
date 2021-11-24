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
     * swapping function
     *
     * @param X first element
     * @param Y second element
     */
    public static <T> void swap(T X, T Y) {
        T temp = X;
        X = Y;
        Y = temp;
    }
}
