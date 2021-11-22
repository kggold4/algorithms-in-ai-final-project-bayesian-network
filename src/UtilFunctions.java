import java.util.HashMap;

public class UtilFunctions {
    /**
     * this generic function returns a string of a given hashMap (for printing)
     *
     * @param hashmap - given hashmap
     * @return hashmap to string
     */
    public static <K, V> String hashMapToString(HashMap<K, V> hashmap) {
        if(hashmap.isEmpty()) return "";
        StringBuilder output = new StringBuilder();
        hashmap.forEach((key, value) -> {
            output.append(key);
            output.append(" : ");
            output.append(value);
            output.append("\n");
        });
        return output.toString();
    }
}
