import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ex1 {

    private static final String input_file_name = "input.txt";
    private static final String output_file_name = "output.txt";
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
                    doc = XMLReader.readXMLFile(line);

                    // build the variables for the bayesian network from given document
                    variables = new ArrayList<>(XMLReader.build_variables(doc));

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
        StringBuilder output = new StringBuilder();

        // check queries
        System.out.println(net);

        List<String> split_queries = new ArrayList<>(Arrays.asList(queries.toString().split(split_mark)));

        System.out.println("queries: ");
        int i = 1;
        for (String q : split_queries) {

            System.out.println(i + ") " + q + ", type: " + QueryReader.typeOfQuery(q));

            // using bayes ball algorithm
            if (QueryReader.typeOfQuery(q).equals(QueryType.BAYES)) {
                List<String> ball_variables = QueryReader.bayesBallQuery(q);
                String first_variable = ball_variables.get(0);
                String second_variable = ball_variables.get(1);
                List<String> evidence_variables = new ArrayList<>();
                for (int j = 2; j < ball_variables.size(); j++) {
                    evidence_variables.add(ball_variables.get(j));
                }
                boolean independents = net.bayes_ball(first_variable, second_variable, evidence_variables);
                System.out.println(first_variable + "_|_" + second_variable + "|" + evidence_variables + " = " + independents);
                if (independents) {
                    output.append("yes");
                } else {
                    output.append("no");
                }

                // using variable elimination algorithm
            } else if (QueryReader.typeOfQuery(q).equals(QueryType.VE)) {

                List<String> elimination_variables = QueryReader.variableEliminationQuery(q);
                List<String> hidden = QueryReader.variableEliminationQueryHidden(q);
                String hypothesis = elimination_variables.get(0);
                List<String> evidence = new ArrayList<>();
                for(int j = 1; j < elimination_variables.size(); j++) {
                    evidence.add(elimination_variables.get(j));
                }
                List<Double> ve_result = net.variable_elimination(hypothesis, evidence, hidden);
                output.append(UtilFunctions.roundFiveDecimalPlaces(ve_result.get(0)));
                output.append(",");
                output.append((long)Math.floor(ve_result.get(1)));
                output.append(",");
                output.append((long)Math.floor(ve_result.get(2)));

            }
            if(i != split_queries.size()) output.append("\n");

            i++;
        }

        // need to save output to output txt file...
        System.out.println("output:\n" + output);

        try (PrintWriter out = new PrintWriter(output_file_name)) {
            out.println(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
