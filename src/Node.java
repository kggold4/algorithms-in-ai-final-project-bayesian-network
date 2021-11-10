import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {
    private String name;
    private List<Node> parents;
    private List<Node> childes;
    private String[] outcomes;
    private double[] values;
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

        if(parents != null) {
            this.parents = new ArrayList<>(List.of(parents));
//            int sumOfOutcomes = this.outcomes.length;
//            for(Node p : this.parents) sumOfOutcomes *= p.outcomes.length;
//            if(sumOfOutcomes != this.values.length) throw new IOException("Invalid number of values inserted");
//            System.out.println("total outcomes: " + sumOfOutcomes + ", sum of values: " + this.values.length);
        }

        // do not have parents
        if(parents == null) {

            for(int i = 0; i < this.outcomes.length; i++) {
                this.cpt.put(this.name + '=' + this.outcomes[i], this.values[i]);
            }

        // have parents
        } else {

            int number_of_parents = this.parents.size();
            // has one parent
            if(number_of_parents == 1) {

                String[][] P = Common.permutation(this.name, this.parents.get(0).name, this.outcomes, this.parents.get(0).outcomes);
//                HashMap<String, Double> Q = Common.convertMatrixToHashMap(P, this.values);
                Common.printMatrix(P);
//                Common.printHashMap(Q);
//                for(int i = 0; i < this.outcomes.length; i++) {
//                    for(int j = 0; j < this.parents[0].outcomes.length; j++) {
//                        this.cpt.put(this.name + '=' + this.outcomes[i] + ',' + this.parents[0].name + '=' + this.parents[0].outcomes[j], this.values[i + j]);
//                    }
//                }

            // has two parents
            } else if(number_of_parents == 2) {



            // have more than three parents
            } else {

                for(int i = 0; i < number_of_parents; i++) {
                    Node temp = this;
                    String[][] P = Common.permutation(temp.name, temp.parents.get(i).name, temp.outcomes, temp.parents.get(i).outcomes);

//                    Common.permutation(temp.name, temp)
                    String[] P2 = Common.flatMatrix(P);


//                    A = permotation(A, this.parents[i])
                }

                // getting parent outcomes
                for(Node p : this.parents) {

                }
            }
        }
        System.out.println("Print " + this.name + "\n" + this);
    }

    @Override
    public String toString() {
        String output = "";
        output += this.name + ":\n";
        output += Common.printHashMap(this.cpt);
        return output;
    }

    public static void main(String[] args) {

//        String[][] P = Common.permutation("A","B" ,new String[]{"T","F"}, new String[]{"v1","v2","v3"});
//        Common.printMatrix(P);

        // single variable (no parents)
//        Node Q = new Node("Q", new String[]{"T", "F"}, new double[]{0.35, 0.65}, null);

        // two variables (X is parent of Y)
        Node X = new Node("X", new String[]{"T", "F"}, new double[]{0.6, 0.4}, null);
        Node Y = new Node("Y", new String[]{"v1", "v2", "v3"}, new double[]{0.02, 0.15, 0.03, 0.15, 0.15, 0.5}, new Node[]{X});

        // three variables (E and B are parents of A)
//        Node E = new Node("E", new String[]{"T", "F"}, new double[]{0.002, 0.998}, null);
//        Node B = new Node("B", new String[]{"T", "F"}, new double[]{0.001, 0.999}, null);
//        Node A = new Node("A", new String[]{"v1", "v2", "v3"}, new double[]{0.95, 0.05, 0.29, 0.71, 0.94, 0.06, 0.001, 0.999}, new Node[]{E, B});
//


    }
}
