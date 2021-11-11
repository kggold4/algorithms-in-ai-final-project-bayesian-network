import java.util.*;

/**
 * this class represents a Variable in a
 */
public class Variable {
    private final String name;
    private List<Variable> parents;
    private final List<Variable> childes;
    private final List<String> outcomes;
    private double[] values;
    private HashMap<String, Double> cpt;

    /**
     * constructor to create a node
     * @param name the name of the node (for instance "A", "E", "NodeA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     * @param parents the parents variables of this variable
     */
    public Variable(String name, List<String> outcomes, double[] values, Variable[] parents) {
        this.name = name;
        this.childes = new ArrayList<>();
        this.outcomes = outcomes;
        this.cpt = new HashMap<>();
        initialize_parents(values, parents);
    }

    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.childes = new ArrayList<>();
        this.outcomes = outcomes;
        this.cpt = new HashMap<>();
    }

    public void initialize_parents(double[] values, Variable[] parents) {

        this.values = values;
        if(parents != null) this.parents = new ArrayList<>(List.of(parents));

        // do not have parents
        if(parents == null || this.parents.size() == 0) {

            for(int i = 0; i < this.outcomes.size(); i++) {
                this.cpt.put(this.name + '=' + this.outcomes.get(i), this.values[i]);
            }

            // have parents
        } else {

            List<List<String>> all_outcomes = new ArrayList<>();
            List<String> all_names = new ArrayList<>();

            for (Variable p : this.parents) {
                all_outcomes.add(p.outcomes);
                all_names.add(p.name);
            }
            all_outcomes.add(this.outcomes);
            all_names.add(this.name);
            this.cpt = CPTBuilder.BuildCPTHashMap(this.values, all_outcomes, all_names);

        }
    }

    public void addChildes(Variable[] childes) {
        this.childes.addAll(List.of(childes));
    }

    public void addChildes(Variable child) {
        this.childes.add(child);
    }

    public List<Variable> getChildes() {
        return this.childes;
    }

    public List<Variable> getParents() {
        return this.parents;
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
        Variable E = new Variable("E", List.of(new String[]{"T", "F"}), new double[]{0.002, 0.998}, null);
        Variable B = new Variable("B", List.of(new String[]{"T", "F"}), new double[]{0.001, 0.999}, null);
        Variable A = new Variable("A", List.of(new String[]{"x", "y", "z"}), new double[]{0.95, 0.05, 0.29, 0.71, 0.94, 0.06, 0.001, 0.999}, new Variable[]{E, B});


        // example from lecture
//        Variable Weather = new Variable("Weather", new String[]{"T", "F"}, new double[]{0.4, 0.6}, null);
//        System.out.println(Weather);
//
//        Variable Cavity = new Variable("Cavity", new String[]{"T", "F"}, new double[]{0.8, 0.2}, null);
//        System.out.println(Cavity);
//
//        Variable Toothache = new Variable("Toothache", new String[]{"T", "F"}, new double[]{0.8, 0.2, 0.4, 0.6}, new Variable[]{Cavity});
//        System.out.println(Toothache);
//
//        Variable Catch = new Variable("Catch", new String[]{"T", "F"}, new double[]{0.9, 0.1, 0.05, 0.95}, new Variable[]{Cavity});
//        System.out.println(Catch);


    }
}