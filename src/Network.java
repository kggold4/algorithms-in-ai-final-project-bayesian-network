import java.util.*;

/**
 * this class represents a bayesian network
 */
public class Network {

    private final List<Variable> variables;

    // saving parents for each variable
    private final HashMap<Variable, List<Variable>> parents;

    // saving childes for each variable
    private final HashMap<Variable, List<Variable>> childes;

    private static final List<Variable> empty_list = new ArrayList<>();

    /**
     * empty constructor
     */
    public Network() {
        this.variables = new ArrayList<>();
        this.parents = new HashMap<>();
        this.childes = new HashMap<>();
        initialize_parents_childes();
    }

    /**
     * constructor by given list of variables to fill network
     *
     * @param variables - list of variables
     */
    public Network(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
        this.parents = new HashMap<>();
        this.childes = new HashMap<>();
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

        // fixing hashmaps for variables without parents or childes
        for (Variable variable : this.variables) {
            if (!this.parents.containsKey(variable)) {
                this.parents.put(variable, empty_list);
            }
            if (!this.childes.containsKey(variable)) {
                this.childes.put(variable, empty_list);
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

    /**
     * bayes ball algorithm using BFS algorithm
     * return true if and only if the start_node and the destination_node are independents
     * else, return false
     *
     * @param start_node
     * @param destination_node
     * @param evidences_nodes
     * @return
     */
    public boolean bayes_ball(Variable start_node, Variable destination_node, List<Variable> evidences_nodes) {

        if (this.parents.get(start_node).isEmpty() && this.parents.get(destination_node).isEmpty() && evidences_nodes.isEmpty()) {
            return true;
        }

        // set all the given evidences as shaded
        for (Variable variable : this.variables) {
            if (evidences_nodes.contains(variable)) variable.setShade(true);
            else variable.setShade(false);
        }

        // for each variable save if visited
        HashMap<Variable, Color> color = new HashMap<>();
        for (Variable variable : this.variables) color.put(variable, Color.WHITE);
        color.put(start_node, Color.GREY);

        Queue<Variable> queue = new LinkedList<>();
        queue.add(start_node);

        // bayes ball algorithm with the using of BFS algorithm
        while (!queue.isEmpty()) {
            Variable v = queue.poll();
            for (Variable u : this.childes.get(v)) {
                if (color.get(u) == Color.WHITE) {
                    color.put(u, Color.GREY);
                    queue.add(u);
                }
            }

        }


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
        Iterator<Map.Entry<Variable, List<Variable>>> it_p = this.childes.entrySet().iterator();
        while (it_p.hasNext()) {
            Map.Entry<Variable, List<Variable>> pair = it_p.next();
            System.out.println("node is: " + pair.getKey() + " , childes are: " + pair.getValue());
            it_p.remove();
        }
        Iterator<Map.Entry<Variable, List<Variable>>> it_c = this.parents.entrySet().iterator();
        while (it_c.hasNext()) {
            Map.Entry<Variable, List<Variable>> pair = it_c.next();
            System.out.println("node is: " + pair.getKey() + " , parents are: " + pair.getValue());
            it_c.remove();
        }
    }
}
