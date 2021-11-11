import java.util.*;

public class Node {
    private final String name;
    private List<Node> parents;
    private final List<Node> childes;
    private final String[] outcomes;
    private final double[] values;
    private HashMap<String, Double> cpt;

    /**
     * constructor to create a node
     * @param name the name of the node (for instance "A", "E", "NodeA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     * @param parents the parents variables of this variable
     */
    public Node(String name, String[] outcomes, double[] values, Node[] parents) {
        this.name = name;
        this.childes = new ArrayList<>();
        this.outcomes = outcomes;
        this.values = values;
        this.cpt = new HashMap<>();

        if(parents != null) this.parents = new ArrayList<>(List.of(parents));

        // do not have parents
        if(parents == null || this.parents.size() == 0) {

            for(int i = 0; i < this.outcomes.length; i++) {
                this.cpt.put(this.name + '=' + this.outcomes[i], this.values[i]);
            }

        // have parents
        } else {

            List<String[]> all_outcomes = new ArrayList<>();
            List<String> all_names = new ArrayList<>();

            for (Node p : this.parents) {
                all_outcomes.add(p.outcomes);
                all_names.add(p.name);
            }
            all_outcomes.add(this.outcomes);
            all_names.add(this.name);
            this.cpt = Common.BuildCPTHashMap(this.values, all_outcomes, all_names);

        }
        System.out.println(this);
    }

    public void addChildes(Node[] childes) {
        this.childes.addAll(List.of(childes));
    }

    public void addChildes(Node child) {
        this.childes.add(child);
    }

    @Override
    public String toString() {
        String output = "";
        output += this.name + ":\n";
        output += Common.printHashMap(this.cpt);
        return output;
    }

    public static void main(String[] args) {

        // examples

//        String[][] P = Common.permutation("A","B" ,new String[]{"T","F"}, new String[]{"v1","v2","v3"});
//        Common.printMatrix(P);

        // single variable (no parents)
//        Node Q = new Node("Q", new String[]{"T", "F"}, new double[]{0.35, 0.65}, null);

        // two variables (X is parent of Y)
//        Node X = new Node("X", new String[]{"T", "F"}, new double[]{0.6, 0.4}, null);
//        Node Y = new Node("Y", new String[]{"v1", "v2", "v3"}, new double[]{0.02, 0.15, 0.03, 0.15, 0.15, 0.5}, new Node[]{X});

        // three variables (E and B are parents of A)
//        Node E = new Node("E", new String[]{"T", "F"}, new double[]{0.002, 0.998}, null);
//        Node B = new Node("B", new String[]{"T", "F"}, new double[]{0.001, 0.999}, null);
//        Node A = new Node("A", new String[]{"v1", "v2", "v3"}, new double[]{0.95, 0.05, 0.29, 0.71, 0.94, 0.06, 0.001, 0.999}, new Node[]{E, B});
//

        // example from lecture
        Node Weather = new Node("Weather", new String[]{"T", "F"}, new double[]{0.4, 0.6}, null);
        System.out.println(Weather);

        Node Cavity = new Node("Cavity", new String[]{"T", "F"}, new double[]{0.8, 0.2}, null);
        System.out.println(Cavity);

        Node Toothache = new Node("Toothache", new String[]{"T", "F"}, new double[]{0.8, 0.2, 0.4, 0.6}, new Node[]{Cavity});
        System.out.println(Toothache);

        Node Catch = new Node("Catch", new String[]{"T", "F"}, new double[]{0.9, 0.1, 0.05, 0.95}, new Node[]{Cavity});
        System.out.println(Catch);


    }
}
