import java.util.HashMap;

public class UtilFunctions {
    /**
     * this generic function returns a string of a given hashMap (for printing)
     *
     * @param hashmap - given hashmap
     * @return hashmap to string
     */
    public static <K, V> String HashMapToString(HashMap<K, V> hashmap) {
        StringBuilder output = new StringBuilder();
        for (K name : hashmap.keySet()) {
            String key = name.toString();
            String value = hashmap.get(name).toString();
            output.append(key).append(" : ").append(value).append("\n");
        }
        return output.toString();
    }
}