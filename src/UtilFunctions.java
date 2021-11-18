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
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> pair = iter.next();
            output.append(pair.getKey()).append(" : ").append(pair.getValue()).append("\n");
            iter.remove();
        }
        return output.toString();
    }
}
