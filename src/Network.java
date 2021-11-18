import java.util.*;

/**
 * this class represents a bayesian network
 */
public class Network {

    private final List<Variable> variables;

    // saving parents for each variable
    private final HashMap<String, List<Variable>> parents;

    // saving childes for each variable
    private final HashMap<String, List<Variable>> childes;

    // direction of going throw childes or parents in bayes ball algorithm
    private boolean direction_to_parents;
    private boolean uninitialized;

    // this hashmap will contain the parents or the childes hashmaps depends on the direction_to_parents boolean value
    private HashMap<String, List<Variable>> current_direction;

    private static final List<Variable> empty_list = new ArrayList<>();

    /**
     * constructor by given list of variables to fill network
     *
     * @param variables - list of variables
     */
    public Network(List<Variable> variables) {
        this.variables = new ArrayList<>();
        this.variables.addAll(variables);

        this.parents = new HashMap<>();
        this.childes = new HashMap<>();
        this.current_direction = new HashMap<>();
        this.direction_to_parents = false;
        this.uninitialized = false;
        initialize_parents_childes();
    }

    /**
     * initialize parents and childes to all variables in the network
     */
    private void initialize_parents_childes() {

        for (Variable variable : this.variables) {
            List<Variable> variable_parents = variable.getParents();

            // add parents for current variable
            this.parents.put(variable.getName(), variable_parents);

            // add child for each parent of current variable
            for (Variable parent : variable_parents) {

                // if current parent already has child
                if (this.childes.containsKey(parent.getName())) {

                    // add current variable as child
                    this.childes.get(parent.getName()).add(variable);

                    // if current parent do not had childes
                } else {

                    // create new list and add current variable as child to this parent
                    List<Variable> new_list = new ArrayList<>();
                    new_list.add(variable);
                    this.childes.put(parent.getName(), new_list);
                }
            }
        }

        // fixing hashmaps for variables without parents or childes
        for (Variable variable : this.variables) {
            if (!this.parents.containsKey(variable.getName())) {
                this.parents.put(variable.getName(), empty_list);
            }
            if (!this.childes.containsKey(variable.getName())) {
                this.childes.put(variable.getName(), empty_list);
            }
        }
        this.changeDirection();
        this.uninitialized = true;
    }

    /**
     * @return - number of variables in the network - |V|
     */
    public int size() {
        return this.variables.size();
    }

    private void changeDirection() {
        if (this.direction_to_parents) {
            this.current_direction = this.parents;
        } else {
            this.current_direction = this.childes;
        }
    }

    /**
     * getting a variable by his given name
     *
     * @param name the name of the variable
     * @return the variable
     */
    private Variable getVariableByName(String name) {
        for (int i = 0; i < this.size(); i++) {
            Variable variable = this.variables.get(i);
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

    public void CALL() {
        print_childes_parents();
    }

    /**
     * @param start_node            starting variable name position of the BFS algorithm
     * @param destination_node      the variable the algorithm is searching for
     * @param evidences_nodes_names evidence variables in the query
     * @return true if and only if the start_node and the destination_node are independents
     */
    public boolean bayes_ball(String start_node, String destination_node, List<String> evidences_nodes_names) {

        List<Variable> evidences_nodes = new ArrayList<>();
        if (evidences_nodes_names != null) {
            for (String name : evidences_nodes_names) {
                evidences_nodes.add(this.getVariableByName(name));
            }
        }
        return bayes_ball(getVariableByName(start_node), getVariableByName(destination_node), evidences_nodes);
    }

    /**
     * bayes ball algorithm using BFS algorithm
     * return true if and only if the start_node and the destination_node are independents
     * else, return false
     *
     * @param start_node       starting variable position of the BFS algorithm
     * @param destination_node the variable the algorithm is searching for
     * @param evidences_nodes  evidence variables in the query
     * @return true if and only if the start_node and the destination_node are independents
     */
    public boolean bayes_ball(Variable start_node, Variable destination_node, List<Variable> evidences_nodes) {

        if (!this.uninitialized) this.initialize_parents_childes();

         if (this.parents.get(start_node.getName()).isEmpty() && this.parents.get(destination_node.getName()).isEmpty() && evidences_nodes.isEmpty()) {
             return true;
         }

        // set all the given evidences as shaded
        for (Variable variable : this.variables) {
            variable.setShade(evidences_nodes.contains(variable));
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

            for (Variable u : this.current_direction.get(v.getName())) {

                if (color.get(u) == Color.WHITE) {

                    if (u.isShaded()) {
                        // go with parents
                        this.direction_to_parents = true;
                        this.changeDirection();

                    } else {

                        // if the variable is not evidence mark him as GREY - visited
                        color.put(u, Color.GREY);
                    }

                    // dependents - found the destination variable
                    if (u == destination_node) {
                        return false;
                    }

                    queue.add(u);
                }
            }

            // done with variable v
            color.put(v, Color.BLACK);
        }

        // independents
        return true;
    }


    //    public double variable_elimination() {
    //        return 0.0;
    //    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("TO STRING NETWORK:\n");
        for (Variable variable : this.variables) {
            result.append(variable.getName()).append(":\n").append(UtilFunctions.HashMapToString(variable.getCPT()));
        }
        return result.toString();
    }

    public void print_childes_parents() {
        System.out.println("childes are:");
        System.out.println(UtilFunctions.HashMapToString(this.childes));
        System.out.println("parents are:");
        System.out.println(UtilFunctions.HashMapToString(this.parents));
    }
}
