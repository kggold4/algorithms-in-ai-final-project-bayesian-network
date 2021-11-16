import java.util.*;

/**
 * this class represents a bayesian network
 */
public class Network {

    private final List<Variable> variables;

    // saving parents for each variable
    private HashMap<Variable, List<Variable>> parents;

    // saving childes for each variable
    private HashMap<Variable, List<Variable>> childes;

    /**
     * empty constructor
     */
    public Network() {
        this.variables = new ArrayList<>();
        initialize_parents_childes();
    }

    /**
     * constructor by given list of variables to fill network
     *
     * @param variables - list of variables
     */
    public Network(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
        initialize_parents_childes();
    }

    /**
     * initialize parents and childes to all variables in the network
     */
    private void initialize_parents_childes() {
        for (Variable variable : this.variables) {
            List<Variable> variable_parents = variable.getParents();

            // add parents for current variable
            this.parents.put(variable, variable_parents);

            // add child for each parent of current variable
            for (Variable parent : variable_parents) {

                // if current parent already has child
                if (this.childes.containsKey(parent)) {

                    // add current variable as child
                    this.childes.get(parent).add(variable);

                    // if current parent do not had childes
                } else {

                    // create new list and add current variable as child to this parent
                    List<Variable> new_list = new ArrayList<>();
                    new_list.add(variable);
                    this.childes.put(parent, new_list);
                }
            }
        }
    }

    public void addNode(String name, List<String> outcomes, double[] values, Variable[] parents) {
        this.variables.add(new Variable(name, outcomes, values, parents));
    }

    /**
     * @return - number of variables in the network - |V|
     */
    public int size() {
        return this.variables.size();
    }

    public boolean bayes_ball() {
        return true;
    }

    public double variable_elimination() {
        return 0.0;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Variable variable : variables) result.append(variable);
        return result.toString();
    }

    public void print_childes_parents() {
        Iterator it_p = this.parents.entrySet().iterator();
        while (it_p.hasNext()) {
            Map.Entry pair = (Map.Entry)it_p.next();
            System.out.println("node is: " + pair.getKey() + " , childes are: " + pair.getValue());
            it_p.remove();
        }
        Iterator it_c = this.childes.entrySet().iterator();
        while (it_c.hasNext()) {
            Map.Entry pair = (Map.Entry)it_c.next();
            System.out.println("node is: " + pair.getKey() + " , parents are: " + pair.getValue());
            it_c.remove();
        }
    }
}
