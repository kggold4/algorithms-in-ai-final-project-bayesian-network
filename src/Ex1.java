import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Ex1 {

    private static final String input_file_name = "input.txt";

    /**
     * main function
     */
    public static void main(String[] args) {

        Document doc;
        Network net;
        int counter_line = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(input_file_name))) {
            String line;
            while ((line = br.readLine()) != null) {

                // getting the xml file name from the first line of the text file

                if (counter_line == 0) {

                    // getting the document of the xml file
                    doc = XML.readXMLFile(line);

                    // build the bayes network from the document
                    net = XML.build_network(doc);
                    System.out.println(net);
                    System.out.println("parents and childes:");
                    net.print_childes_parents();
                }

                // get the rest of the text file (queries)

                //...

//                System.out.println("line: " + line);
                counter_line++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
