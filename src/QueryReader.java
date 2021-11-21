import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * this class contain the functions that can read and manipulate queries from the input text file
 */
public class QueryReader {

    /**
     * check the type of the query
     *
     * @param query the given query from the text file
     * @return the type of the query - VE (Variable Elimination) or BAYES (Bayes Ball)
     */
    public static QueryType typeOfQuery(String query) {
        return query.charAt(0) == 'P' ? QueryType.VE : QueryType.BAYES;
    }

    /**
     * getting a bayes ball query and return a list with the variables
     *
     * @param query the given query from the text file
     * @return list with the variables
     */
    public static List<String> bayesBallQuery(String query) {

        String[] half = query.split("\\|");
        String[] first_second = half[0].split("-");

        List<String> output = new ArrayList<>(Arrays.asList(first_second));

        if (half.length > 1) {
            String[] evidence = half[1].split(",");
            for (String s : evidence) {
                StringBuilder ve = new StringBuilder();
                for(int j = 0; j < s.length() - 2; j++) {
                    ve.append(s.charAt(j));
                }
                output.add(ve.toString());
            }
        }

        return output;
    }

    public static List<String> variableEliminationQuery(String query) {
        List<String> output = new ArrayList<>();
        String[] half = query.split("\\|");
        StringBuilder hypothesis = new StringBuilder();
        for(int i = 2; i < half[0].length() - 2; i++) {
            hypothesis.append(half[0].charAt(i));
        }
        output.add(hypothesis.toString());
        String[] split_evidence_order = half[1].split(" ");
        String[] evidence = split_evidence_order[0].split(",");
        for(int i = 0; i < evidence.length; i++) {
            String s = evidence[i];
            StringBuilder ev = new StringBuilder();
            int end = 2;
            if(i == evidence.length - 1) end = 3;
            for(int j = 0; j < s.length() - end; j++) {
                ev.append(s.charAt(j));
            }
            output.add(ev.toString());
        }
        return output;
    }

    public static List<String> variableEliminationQueryVariableOrder(String query) {
        List<String> output = new ArrayList<>();
        String[] half = query.split("\\|");
        String[] split_evidence_order = half[1].split(" ");
        String[] order = split_evidence_order[1].split("-");
        Collections.addAll(output, order);
        return output;
    }


}
