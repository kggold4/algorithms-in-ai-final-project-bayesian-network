import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Ex1 {

    private static final String input_file_name = "input.txt";
    private static Network net = null;

    /**
     * main function
     */
    public static void main(String[] args) {

        Document doc;
//        Network net = null;
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

                    // build the bayes network from the document
                    variables = new ArrayList<>(XML.build_variables(doc));
                }

                // append query to queries
                queries.append(line);
                counter_line++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        net = new Network(variables);
        System.out.println(net);

        System.out.println("E_|_B = " + net.bayes_ball("A", "B", null));
        System.out.println("E_|_B|J = " + net.bayes_ball("A", "B", List.of(new String[]{"J"})));

    }
}
