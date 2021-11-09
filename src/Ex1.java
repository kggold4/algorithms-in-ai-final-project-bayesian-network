import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ex1 {

    private static final String input_file_name = "input.txt";

    /**
     * main function
     */
    public static void main(String[] args) {

        Document doc;
        Network net = null;
        int counter_line = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(input_file_name))) {
            String line;
            while ((line = br.readLine()) != null) {

                // getting the xml file name from the first line of the text file

                if (counter_line == 0) {

                    // getting the document of the xml file
                    doc = readXMLFile(line);

                    // build the bayes network from the document
                    net = build_network(doc);
                }

                // get the rest of the text file (queries)

                //...

                System.out.println("line: " + line);
                counter_line++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this function return a xml document by a given xml file name
     *
     * @param file_name xml file
     * @return document
     */
    private static Document readXMLFile(String file_name) {
        // xml file
        File inputFile = new File(file_name);

        // create factory for reading xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // create builder for reading xml
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // create document form xml file (parse with builder)
        Document doc = null;
        try {
            if (builder == null) {
                throw new IOException();
            }
            doc = builder.parse(inputFile);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private static Network build_network(Document doc) {
        return null;
    }

}
