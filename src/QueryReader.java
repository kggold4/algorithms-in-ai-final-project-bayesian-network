import java.util.ArrayList;
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
     * @param query the given query from the text file
     * @return
     */
    public static List<String> bayesBallQuery(String query) {
        List<String> output = new ArrayList<>();
        int i = 0;

        StringBuilder variable = new StringBuilder();

        // getting first variable
        while(i < query.length()) {
            if(query.charAt(i) == '-') {
                i++;
                break;
            }
            variable.append(query.charAt(i));
            i++;
        }

        output.add(variable.toString());

        // clear string builder
        variable.setLength(0);

        // getting second variable
        while(i < query.length()) {
            if(query.charAt(i) == '|') {
                i++;
                break;
            }
            variable.append(query.charAt(i));
            i++;
        }

        output.add(variable.toString());

        // clear string builder
        variable.setLength(0);

        boolean flag = false;

        // getting the rest of variable as evidence
        while(i < query.length()) {
            if(query.charAt(i) == ',') {
                flag = false;
                i++;
                continue;
            }
            if(flag) {
                i++;
                continue;
            }
            if(query.charAt(i) == '=') {
                flag = true;
                output.add(variable.toString());
                variable.setLength(0);
                i++;
                continue;
            }
            variable.append(query.charAt(i));
            i++;
        }

//        System.out.println("Variables Bayes: " + output);

        return output;
    }
}
