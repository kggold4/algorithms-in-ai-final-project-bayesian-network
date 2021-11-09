import java.sql.Array;
import java.util.ArrayList;

public class Node {
    private String name;
    private String[] outcomes;
    private Node[] parents;
    private String a;

    /**
     * constructor to create a node
     * @param name the name of the node (for instance "A", "E", "NodeA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     * @param parents the parents variables of this variable
     */
    public Node(String name, String[] outcomes, Node[] parents, double[] values) {
        this.name = name;
        this.outcomes = outcomes;
        this.parents = parents;

        // do not have parents
        if(parents == null) {

        // have parents
        } else {
            // getting parent outcomes
            for(Node p : this.parents) {
                for(String o : p.outcomes) {
                    System.out.println("outcomes is: " + p.name + " : " + o);
                }
            }
        }
    }

    public String[] getOutcomes() {
        return this.outcomes;
    }

    public static void main(String[] args) {
        Node E = new Node("E", new String[]{"T", "F"}, null, new double[]{0.002, 0.998});
        Node B = new Node("B", new String[]{"T", "F"}, null, new double[]{0.001, 0.999});
        Node A = new Node("A", new String[]{"v1", "v2", "v3"}, new Node[]{E, B}, new double[]{0.95, 0.05, 0.29, 0.71, 0.94, 0.06, 0.001, 0.999});
    }
}
