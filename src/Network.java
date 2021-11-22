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

    /**
     * bayes ball algorithm using BFS algorithm
     * return true if and only if the start_node and the destination_node are independents
     * else, return false
     *
     * @param start_node            starting variable name position of the BFS algorithm
     * @param destination_node      the variable the algorithm is searching for
     * @param evidences_nodes_names evidence variables in the query
     * @return true if and only if the start_node and the destination_node are independents
     */
    public boolean bayes_ball(String start_node, String destination_node, List<String> evidences_nodes_names) {

        System.out.println("start_node: " + start_node + ", destination_node: " + destination_node + ", evidences_nodes_names: " + evidences_nodes_names);

        List<Variable> evidences_nodes = new ArrayList<>();
        if (evidences_nodes_names != null) {
            for (String name : evidences_nodes_names) {
                evidences_nodes.add(this.getVariableByName(name));
            }
        }
        return bayes_ball(getVariableByName(start_node), getVariableByName(destination_node), evidences_nodes);
    }

    /**
     * @param start_node       starting variable position of the BFS algorithm
     * @param destination_node the variable the algorithm is searching for
     * @param evidences_nodes  evidence variables in the query
     * @return true if and only if the start_node and the destination_node are independents
     */
    private boolean bayes_ball(Variable start_node, Variable destination_node, List<Variable> evidences_nodes) {

        if (!this.uninitialized) this.initialize_parents_childes();

        // if the start node and the destination node do not have any parents, and we not have any evidence they are independents
        if (this.parents.get(start_node.getName()).isEmpty() && this.parents.get(destination_node.getName()).isEmpty() && evidences_nodes.isEmpty()) {
            return true;
        }

        // set all the given evidences as shaded
        for (Variable variable : this.variables) {
            variable.setShade(evidences_nodes.contains(variable));
        }

        // for each variable save if visited
        HashMap<Variable, Visited> color = new HashMap<>();
        for (Variable variable : this.variables) color.put(variable, Visited.NO);
        color.put(start_node, Visited.YES);

        Queue<Variable> queue = new LinkedList<>();
        queue.add(start_node);

        // bayes ball algorithm with the using of BFS algorithm
        while (!queue.isEmpty()) {
            Variable v = queue.poll();

            for (Variable u : this.current_direction.get(v.getName())) {
                if (color.get(u) == Visited.NO) {
                    if (u.isShaded()) {

                        // go with parents
                        color.put(v, Visited.NO);
                        this.direction_to_parents = true;
                        this.changeDirection();

                        // if the variable is not evidence mark him as GREY - visited
                    } else color.put(u, Visited.YES);

                    // dependents - found the destination variable
                    if (u == destination_node) return false;
                    queue.add(u);
                }
            }
        }

        // independents
        return true;
    }

    /**
     * change the direction in the bayes ball algorithm in which neighbors of variable they go throw - parent or childes
     */
    private void changeDirection() {
        if (this.direction_to_parents) this.current_direction = this.parents;
        else this.current_direction = this.childes;
    }

    /**
     * variable elimination algorithm function
     *
     * @param hypothesis the variable Q we ask his probability in the query
     * @param evidence   the list of the evidence variables (that we got their outcome values by the query)
     * @param hidden     the hidden variables we want to eliminate
     * @return the probability value of the query
     */
    public double variable_elimination(String hypothesis, List<String> evidence, List<String> hidden) {

        System.out.println("variable elimination FIRST function:");
        System.out.println("hypothesis: " + hypothesis + ", evidence: " + evidence + ", hidden: " + hidden);

        String[] hypothesis_query = hypothesis.split("=");
        Variable hypothesis_variable = getVariableByName(hypothesis_query[0]);
        String hypothesis_value = hypothesis_query[1];

        List<String> evidence_values = new ArrayList<>();
        List<Variable> evidence_variables = new ArrayList<>();
        if (evidence != null) {
            for (String evs : evidence) {
                String[] evidence_queries = evs.split(",");
                for (String ev : evidence_queries) {
                    String[] evidence_query = ev.split("=");
                    evidence_variables.add(getVariableByName(evidence_query[0]));
                    evidence_values.add(evidence_query[1]);
                }
            }
        }

        List<Variable> hidden_variables = new ArrayList<>();
        if (hidden != null) {
            for (String s : hidden) {
                hidden_variables.add(getVariableByName(s));
            }
        }

        return variable_elimination(hypothesis_variable, hypothesis_value, evidence_variables, evidence_values, hidden_variables);
    }


    /**
     * @param hypothesis       the variable Q we ask his probability in the query
     * @param hypothesis_value the variable Q value
     * @param evidence         the list of the evidence variables (that we got their outcome values by the query)
     * @param evidence_values  the outcomes value for each evidence variable
     * @param hidden           the hidden variables we want to eliminate
     * @return the probability value of the query
     */
    private double variable_elimination(Variable hypothesis, String hypothesis_value, List<Variable> evidence, List<String> evidence_values, List<Variable> hidden) {

        System.out.println("variable elimination SECOND function:");
        System.out.println("hypothesis: " + hypothesis + ", hypothesis value: " + hypothesis_value + ", evidence: " + evidence + ", evidence values: " + evidence_values + ", hidden: " + hidden);

        double value = 0.0;

        if (!hidden.isEmpty()) {

            for (Variable variable : hidden) {
                HashMap<String, Double> map = updateLocalCpt(evidence, evidence_values, variable);
                System.out.println(UtilFunctions.hashMapToString(map));
            }

        }
        // getting first hidden variable
//            Variable variable_to_eliminate = hidden.get(0);
//            hidden.remove(0);

//            for (Variable variable : this.variables) {
//                HashMap<String, Double> cpt = variable.getCPT();
//                for (String vars : cpt.keySet()) {
//                    if (vars.contains(variable_to_eliminate.getName())) {
////                        System.out.println("variable: " + variable.getName());
////                        System.out.println("vars: " + vars);
//                    }
//                }
//            }
//        }

        return value;
    }

    /**
     * this function get a hidden variable with all the evidence variables and their values we get in the variable elimination function
     * and return the new factor for the hidden variable
     * (deleting the unrequited values by evidence)
     * for example if we have the evidence A=T, and the hidden variable B factor contains B values and A values we delete all the A=F values from the factor
     *
     * @param evidence list of the evidence variable
     * @param values   list of the values of the evidence variables
     * @param hidden   the hidden variable we eliminate
     * @return the new factor of hidden
     */
    private HashMap<String, Double> updateLocalCpt(List<Variable> evidence, List<String> values, Variable hidden) {

        HashMap<String, Double> hidden_factor = hidden.getCPT();
        HashMap<String, Double> factor = new HashMap<>();

        for (int i = 0; i < evidence.size(); i++) {
            StringBuilder full_evidence = new StringBuilder();
            full_evidence.append(evidence.get(i).getName()).append("=").append(values.get(i));

            for (Map.Entry<String, Double> key : hidden_factor.entrySet()) {
                if (key.getKey().contains(full_evidence.toString())) {
                    String new_key = key.getKey();
                    List<String> new_key_split = new ArrayList<>(List.of(new_key.split(",")));
                    new_key_split.remove(full_evidence.toString());
                    String key_to_change = CPTBuilder.combineWithCommas(new_key_split);
                    factor.put(key_to_change, key.getValue());
                }
            }
        }

        return factor;
    }

    /**
     * to string function
     *
     * @return string represents the network, print each CPT of the variables
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("TO STRING NETWORK:\n");
        for (Variable variable : this.variables) {
            result.append(variable.getName()).append(":\n").append(UtilFunctions.hashMapToString(variable.getCPT()));
        }
        return result.toString();
    }

    public void print_childes_parents() {
        System.out.println("childes are:");
        System.out.println(UtilFunctions.hashMapToString(this.childes));
        System.out.println("parents are:");
        System.out.println(UtilFunctions.hashMapToString(this.parents));
    }
}
