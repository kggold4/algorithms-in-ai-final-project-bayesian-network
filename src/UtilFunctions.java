import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UtilFunctions {
    /**
     * this generic function returns a string of a given hashMap (for printing)
     *
     * @param map - given hashmap
     * @return hashmap to string
     */
    public static <K, V> String HashMapToString(HashMap<K, V> map) {
        StringBuilder output = new StringBuilder();
        for (K name: map.keySet()) {
            String key = name.toString();
            String value = map.get(name).toString();
            output.append(key).append(" : ").append(value).append("\n");
        }
        return output.toString();
    }
}
