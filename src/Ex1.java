import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Ex1 {

    private static final String input_file_name = "input.txt";
    private static final String output_file_name = "output2.txt";
    private static final String split_mark = "split_text";

    /**
     * main function
     */
    public static void main(String[] args) {

        Document doc;
        int counter_line = 0;
        StringBuilder queries = new StringBuilder();
        List<Variable> variables = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(input_file_name))) {
            String line;
            while ((line = br.readLine()) != null) {

                // getting the xml file name from the first line of the text file
                if (counter_line == 0) {

                    // getting the document of the xml file
                    doc = XML.readXMLFile(line);

                    // build the variables for the bayesian network from given document
                    variables = new ArrayList<>(XML.build_variables(doc));

                    // get queries as string from the other lines
                } else {
                    // append query to queries
                    queries.append(line).append(split_mark);
                }

                counter_line++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // building the bayesian network
        Network net = new Network(variables);

        // output text for output file
        String output = "";

        // check queries
        System.out.println(net);

        System.out.println("E_|_B = " + net.bayes_ball("E", "B", null));
        System.out.println("E_|_B|J = " + net.bayes_ball("E", "B", List.of(new String[]{"J"})));

        List<String> split_queries = List.of(queries.toString().split(split_mark));
        System.out.println("queries: ");
        int i = 1;
        for(String q : split_queries) {

            System.out.println(i + ") " + q);
            i++;
        }


        // need to save output to output txt file...
        System.out.println(output);

    }
}
