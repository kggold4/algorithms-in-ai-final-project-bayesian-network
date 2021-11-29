import java.util.*;

/**
 * this class represents a Variable in a
 */
public class Variable {
    private final String name;
    private List<Variable> parents;
    private final List<String> outcomes;
    private LinkedHashMap<String, Double> cpt;
    private boolean shaded;
    public boolean uninitialized;

    /**
     * constructor to create a variables without initialize parents and values
     *
     * @param name     the name of the variable (for instance "A", "E", "VariableA" ...)
     * @param outcomes the outcomes this variable can get (for instance [T, F] or [v1, v2, v3]...)
     */
    public Variable(String name, List<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        this.cpt = new LinkedHashMap<>();
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

        List<Variable> parents_list = new ArrayList<>();
        for(int i = 0; i < parents.length; i++) {
            parents_list.add(parents[i]);
        }

        if (parents != null) this.parents = parents_list;

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
            this.cpt = CPTBuilder.buildCPTLinkedHashMap(values, all_outcomes, all_names);

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

    public LinkedHashMap<String, Double> getCPT() {
        return this.cpt;
    }

    public List<String> getOutcomes() {
        return this.outcomes;
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
}
