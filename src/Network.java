import java.util.*;

/**
 * this class represents a bayesian network
 */
public class Network {

    private final List<Variable> variables;

    // saving parents for each variable
    private final LinkedHashMap<String, List<Variable>> parents;

    // saving childes for each variable
    private final LinkedHashMap<String, List<Variable>> childes;

    // direction of going throw childes or parents in bayes ball algorithm
    private boolean direction_to_parents;
    private boolean uninitialized;

    // this hashmap will contain the parents or the childes hashmaps depends on the direction_to_parents boolean value
    private LinkedHashMap<String, List<Variable>> current_direction;

    private static final List<Variable> empty_list = new ArrayList<>();

    /**
     * constructor by given list of variables to fill network
     *
     * @param variables - list of variables
     */
    public Network(List<Variable> variables) {
        this.variables = new ArrayList<>();
        this.variables.addAll(variables);

        this.parents = new LinkedHashMap<>();
        this.childes = new LinkedHashMap<>();
        this.current_direction = new LinkedHashMap<>();
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
    public Variable getVariableByName(String name) {
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
        LinkedHashMap<Variable, Visited> color = new LinkedHashMap<>();
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
     * @param hypothesis         the variable Q we ask his probability in the query
     * @param hypothesis_value   the variable Q value (for example: {"T", "F"} or {"v1", "v2", "v3"})
     * @param evidence_variables the list of the evidence variables (that we got their outcome values by the query)
     * @param evidence_values    the outcomes value for each evidence variable (for example: {"T", "F"} or {"v1", "v2", "v3"})
     * @param hidden             the hidden variables we want to eliminate
     * @return the probability value of the query
     */
    private double variable_elimination(Variable hypothesis, String hypothesis_value, List<Variable> evidence_variables, List<String> evidence_values, List<Variable> hidden) {

        // insert evidence variables and outcome value to one hashmap
        LinkedHashMap<String, String> evidence = new LinkedHashMap<>();
        for (int i = 0; i < evidence_values.size(); i++) {
            evidence.put(evidence_variables.get(i).getName(), evidence_values.get(i));
        }

        // store local hashmap of factors for each variable
        // the key is the name of the variable and the value is his factor
        LinkedHashMap<String, LinkedHashMap<String, Double>> factors = new LinkedHashMap<>();

        // store local hashmaps of factors in factors
        if (!hidden.isEmpty()) {
            for (Variable h : hidden) {

                // update factor by evidence for each hidden variable
                LinkedHashMap<String, Double> hashmap = updateLocalCpt(evidence_variables, evidence_values, h);

                if (!hashmap.isEmpty()) factors.put(h.getName(), hashmap);

                // each factor that contains the hidden variable values
                for (Variable variable : this.variables) {

                    // getting the current cpt of the variable
                    LinkedHashMap<String, Double> cpt = new LinkedHashMap<>(variable.getCPT());

                    // getting the names and outcomes of the cpt
                    LinkedHashMap<String, List<String>> namesAndOutcomes = CPTBuilder.getNamesAndOutcomes(cpt);

                    // if hidden is in the cpt
                    if (namesAndOutcomes.containsKey(h.getName())) {

                        // if the factor is not already contain the cpt for this cpt
                        if (!factors.containsKey(variable.getName())) {

                            // add the factor to factors
                            factors.put(variable.getName(), cpt);

                        }
                    }
                }
            }
        }

        // for each evidence variable fix his cpt with his outcome value
        for (Map.Entry<String, String> evidence_variable : evidence.entrySet()) {

            // get deep copy of evidence current cpt
            LinkedHashMap<String, Double> evidence_cpt = new LinkedHashMap<>(getVariableByName(evidence_variable.getKey()).getCPT());
            LinkedHashMap<String, Double> new_cpt = new LinkedHashMap<>();

            // each line in the current evidence cpt check if contains the string value of evidence, for example "E=T"
            for (Map.Entry<String, Double> cpt_line : evidence_cpt.entrySet()) {
                StringBuilder q = new StringBuilder();
                q.append(evidence_variable.getKey()).append("=").append(evidence_variable.getValue());

                // if the line in the cpt contains the evidence value string add the line without the string to the new cpt
                if (cpt_line.getKey().contains(q)) {
                    String new_key = cpt_line.getKey();
                    List<String> new_key_split = new ArrayList<>(List.of(new_key.split(",")));
                    String key_to_change = UtilFunctions.combineWithCommas(new_key_split);
                    new_cpt.put(key_to_change, cpt_line.getValue());
                }
            }

            // add the new cpt to factors
            factors.put(evidence_variable.getKey(), new_cpt);
        }

        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("FACTORS ADDED:");
        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
            System.out.println(f.getKey() + ":");
            System.out.println(UtilFunctions.hashMapToString(f.getValue()));
        }
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");


        // join all factors for each hidden variable
        if (!hidden.isEmpty()) {
            for (Variable h : hidden) {

                // the CPTs that the hidden variable is in
                List<LinkedHashMap<String, Double>> cpt_to_join = new ArrayList<>();
                List<String> variables_names_to_join = new ArrayList<>();

                for (Map.Entry<String, LinkedHashMap<String, Double>> entry : factors.entrySet()) {
                  if(CPTBuilder.getNames(entry.getValue()).contains(h.getName())) {
                        cpt_to_join.add(entry.getValue());
                        variables_names_to_join.add(entry.getKey());
                    }
                }

                String last_name = "empty";
                for (String name : variables_names_to_join) {
                    factors.remove(name);
                    last_name = name;
                }

                if (!cpt_to_join.isEmpty()) {

                    cpt_to_join = CPTBuilder.sortFactors(cpt_to_join);

                    System.out.println("factor to join with " + h.getName());
                    for (LinkedHashMap<String, Double> c : cpt_to_join) {
                        System.out.println(UtilFunctions.hashMapToString(c));
                    }

                    // join cpt_to_join (all the factors that mentioning h) to one factor
                    LinkedHashMap<String, Double> new_factor = CPTBuilder.joinFactors(cpt_to_join);

                    System.out.println("\tFactor BEFORE Eliminate on " + h.getName() + "\n");
                    System.out.println(UtilFunctions.hashMapToString(new_factor));

                    // eliminate factor
                    if(CPTBuilder.getNames(new_factor).size() > 1) {
                        new_factor = CPTBuilder.eliminate(new_factor, h);
                    }

                    System.out.println("\tFactor AFTER Eliminate on " + h.getName() + "\n");
                    System.out.println(UtilFunctions.hashMapToString(new_factor));

                    System.out.println("last_name: " + last_name);
                    factors.put(last_name, new_factor);


                    System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
                    System.out.println("FACTORS:");
                    for(Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
                        System.out.println("NAME: " + f.getKey());
                        System.out.println(UtilFunctions.hashMapToString(f.getValue()));
                    }
                    System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
                }
            }
        }

        // removing the factors with size of one or less
        LinkedHashMap<String, Integer> sizes = new LinkedHashMap<>();
        for (Map.Entry<String, LinkedHashMap<String, Double>> factor : factors.entrySet()) {
            sizes.put(factor.getKey(), factor.getValue().size());
        }

        for (Map.Entry<String, Integer> factor : sizes.entrySet()) {
            if (factor.getValue() <= 1) {
                factors.remove(factor.getKey());
            }
        }

        // eliminate again if still the factor contains other different outcomes of
        System.out.println("-------------------------------BEFORE END PRINT FACTORS----------------------------------");
        for (Map.Entry<String, LinkedHashMap<String, Double>> e : factors.entrySet()) {
            System.out.println("factor for " + e.getKey() + ", is:");
            System.out.println(UtilFunctions.hashMapToString(e.getValue()));
        }
        System.out.println("-----------------------------------------------------------------------------------------");

        // get the last factor
        LinkedHashMap<String, Double> last_factor = new LinkedHashMap<>();

        for (Map.Entry<String, LinkedHashMap<String, Double>> e : factors.entrySet()) {
            last_factor = new LinkedHashMap<>(e.getValue());
        }

        hypothesis_value = hypothesis.getName() + "=" + hypothesis_value;

        LinkedHashMap<String, Double> hypothesis_factor = getVariableByName(hypothesis.getName()).getCPT();
        LinkedHashMap<String, Double> hypothesis_new_factor = new LinkedHashMap<>(hypothesis_factor);
        LinkedHashMap<String, Double> final_factor = CPTBuilder.joinTwoFactors(hypothesis_new_factor, last_factor);

        while(CPTBuilder.getNames(final_factor).size() > 1) {
            String hidden_name = "";
            List<String> final_factor_names = CPTBuilder.getNames(final_factor);
            for(String name : final_factor_names) {
                if(!name.equals(hypothesis.getName())) {
                    hidden_name = name;
                }
            }
            final_factor = CPTBuilder.eliminate(final_factor, getVariableByName(hidden_name));
        }

        System.out.println("final_factor: ");
        System.out.println(UtilFunctions.hashMapToString(final_factor));

        // normalize the final_factor
        final_factor = CPTBuilder.normalize(final_factor);
        System.out.println("final_factor: (after normalize)");
        System.out.println(UtilFunctions.hashMapToString(final_factor));

        double value = 0.0;
        for (Map.Entry<String, Double> entry : final_factor.entrySet()) {
            if (entry.getKey().contains(hypothesis_value)) {
                value = entry.getValue();
                System.out.println("entry.getValue(): " + entry.getValue());
                break;
            }
        }

        System.out.println("FINAL VALUE IS " + value);

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
    private LinkedHashMap<String, Double> updateLocalCpt(List<Variable> evidence, List<String> values, Variable hidden) {

        LinkedHashMap<String, Double> hidden_factor = hidden.getCPT();
        LinkedHashMap<String, Double> factor = new LinkedHashMap<>();

        for (int i = 0; i < evidence.size(); i++) {
            StringBuilder full_evidence = new StringBuilder();
            full_evidence.append(evidence.get(i).getName()).append("=").append(values.get(i));

            for (Map.Entry<String, Double> key : hidden_factor.entrySet()) {
                if (key.getKey().contains(full_evidence.toString())) {
                    String new_key = key.getKey();
                    List<String> new_key_split = new ArrayList<>(List.of(new_key.split(",")));
                    new_key_split.remove(full_evidence.toString());
                    String key_to_change = UtilFunctions.combineWithCommas(new_key_split);
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
