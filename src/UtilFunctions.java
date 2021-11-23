import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

    /**
     * swapping two hashmaps
     *
     * @param X first hashmap
     * @param Y second hashmap
     */
    public static void swap(LinkedHashMap<String, Double> X, LinkedHashMap<String, Double> Y) {
        LinkedHashMap<String, Double> T = X;
        X = new LinkedHashMap<String, Double>(Y);
        Y = T;
    }
}
