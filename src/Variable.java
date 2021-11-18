import java.util.*;

/**
 * this class represents a Variable in a
 */
public class Variable {
    private final String name;
    private List<Variable> parents;
    private final List<String> outcomes;
    private HashMap<String, Double> cpt;
    private boolean shaded;
    public boolean uninitialized;

    /**
     * constructor to create a variable
     *
     * @param name     the name of the variable (for instance "A", "E", "VariableA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     * @param values   outcomes values
     * @param parents  the parents variables of this variable
     */
    public Variable(String name, List<String> outcomes, double[] values, Variable[] parents) {
        this.name = name;
        this.outcomes = outcomes;
        this.cpt = new HashMap<>();
        this.shaded = false;
        this.uninitialized = false;
        initialize_parents(values, parents);
    }

    /**
     * constructor to create a variables without initialize parents and values
     *
     * @param name     the name of the variable (for instance "A", "E", "VariableA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     */
    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        this.cpt = new HashMap<>();
        this.shaded = false;
        this.uninitialized = false;
    }

    /**
     * initialize parents after creating the variable
     *
     * @param values  outcomes values
     * @param parents variable parents
     */
    public void initialize_parents(double[] values, Variable[] parents) {

        if (parents != null) this.parents = new ArrayList<>(List.of(parents));

        // do not have parents
        if (parents == null || this.parents.size() == 0) {

            for (int i = 0; i < this.outcomes.size(); i++) {
                this.cpt.put(this.name + '=' + this.outcomes.get(i), values[i]);
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
            this.cpt = CPTBuilder.BuildCPTHashMap(values, all_outcomes, all_names);

        }
        this.uninitialized = true;

    }

    /**
     * @return - parents list
     */
    public List<Variable> getParents() {
        return this.parents;
    }

    /**
     * @return - true if variables has parents, else return false
     */
    public boolean hasParents() {
        return this.parents.size() > 0;
    }

    public String getName() {
        return this.name;
    }

    /**
     * set shaded - using for the bayes-ball algorithm
     *
     * @param shaded - true or false
     */
    public void setShade(boolean shaded) {
        this.shaded = shaded;
    }

    /**
     * @return - shaded status
     */
    public boolean isShaded() {
        return this.shaded;
    }

    /**
     * to string method
     *
     * @return - string represents the variable
     */
    @Override
    public String toString() {
        return "" + this.name;
    }

    public HashMap<String, Double> getCPT() {
        return this.cpt;
    }

    public static void main(String[] args) {

        // examples

//        String[][] P = Common.permutation("A","B" ,new String[]{"T","F"}, new String[]{"v1","v2","v3"});
//        Common.printMatrix(P);

        // single variable (no parents)
//        Variable Q = new Variable("Q", new String[]{"T", "F"}, new double[]{0.35, 0.65}, null);

        // two variables (X is parent of Y)
//        Variable X = new Variable("X", new String[]{"T", "F"}, new double[]{0.6, 0.4}, null);
//        Variable Y = new Variable("Y", new String[]{"v1", "v2", "v3"}, new double[]{0.02, 0.15, 0.03, 0.15, 0.15, 0.5}, new Variable[]{X});

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
