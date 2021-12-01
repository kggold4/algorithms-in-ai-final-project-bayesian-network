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

            if (variable_parents != null) {

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
    public List<Double> variable_elimination(String hypothesis, List<String> evidence, List<String> hidden) {

        System.out.println("hypothesis: " + hypothesis + ", evidence: " + evidence + ", hidden" + hidden);

        String[] hypothesis_query = hypothesis.split("=");
        Variable hypothesis_variable = getVariableByName(hypothesis_query[0]);
        String hypothesis_value = hypothesis_query[1];

        List<String> evidence_values = new ArrayList<>();
        List<Variable> evidence_variables = new ArrayList<>();
        if (evidence != null && evidence.size() > 0) {
            for (String evs : evidence) {
                String[] evidence_queries = evs.split(",");
                if (evidence_queries.length > 0) {
                    for (String ev : evidence_queries) {
                        String[] evidence_query = ev.split("=");
                        if (evidence_query.length > 0) {
                            evidence_variables.add(getVariableByName(evidence_query[0]));
                            evidence_values.add(evidence_query[1]);
                        }
                    }
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
    private List<Double> variable_elimination(Variable hypothesis, String hypothesis_value, List<Variable> evidence_variables, List<String> evidence_values, List<Variable> hidden) {

        System.out.println("hypothesis: " + hypothesis + ", hypothesis_value: " + hypothesis_value + ", evidence_variables: " + evidence_variables + ", evidence_values: " + evidence_values + ", hidden: " + hidden);

        // factor counter that count the number of addition and multiplication operations in variable elimination algorithm
        FactorCounter factorCounter = new FactorCounter();

        // store local hashmap of factors for each variable
        // the key is the name of the variable and the value is his factor
        LinkedHashMap<String, LinkedHashMap<String, Double>> factors = new LinkedHashMap<>();

        List<String> evidence_variables_names = new ArrayList<>();
        for (Variable vd : evidence_variables) {
            evidence_variables_names.add(vd.getName());
        }

        for (Variable variable : this.variables) {
            factors.put(variable.getName(), updateLocalCpt(evidence_variables_names, evidence_values, variable.getCPT()));
        }

        // checking for each hidden variable if he is parent or grandparent of variable in checking (hypothesis + evidence)
        List<Variable> checking = new ArrayList<>();
        checking.add(hypothesis);
        checking.addAll(evidence_variables);

        LinkedHashMap<String, Boolean> not_grandparents = new LinkedHashMap<>();
        for (Variable h : hidden) {
            not_grandparents.put(h.getName(), false);
        }

        for (Variable v : checking) {
            for (Variable h : hidden) {
                boolean b = not_grandparents.get(h.getName());
                not_grandparents.put(h.getName(), b | v.isGrandParent(h));
            }
        }

        // if we found hidden variable that he is not parent of grandparent of hypothesis and evidence we delete hom from factors
        for (Map.Entry<String, Boolean> entry : not_grandparents.entrySet()) {
            if (!entry.getValue()) {
                factors.remove(entry.getKey());
            }
        }

        if (evidence_variables.isEmpty()) {
            // checking if one of the factors contains the needed value of hypothesis alone
            for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
                for (Map.Entry<String, Double> line : f.getValue().entrySet()) {
                    if (line.getKey().equals(hypothesis_value)) {
                        List<Double> surprise_result = new ArrayList<>();
                        surprise_result.add(line.getValue());
                        surprise_result.add(0.0);
                        surprise_result.add(0.0);
                        System.out.println("FINAL VALUE IS " + line.getValue());
                        return surprise_result;
                    }
                }
            }

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
                    if (CPTBuilder.getNames(entry.getValue()).contains(h.getName())) {
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

                    if(cpt_to_join.size() > 1) {

                        System.out.println("factor to join with " + h.getName());
                        for (LinkedHashMap<String, Double> cpt : cpt_to_join) {
                            System.out.println(UtilFunctions.hashMapToString(cpt));
                        }

                        // join cpt_to_join (all the factors that mentioning h) to one factor
                        LinkedHashMap<String, Double> new_factor = CPTBuilder.joinFactors(cpt_to_join, factorCounter);

                        System.out.println("\tFactor BEFORE Eliminate on " + h.getName() + "\n");
                        System.out.println("factorCounter: " + factorCounter);
                        System.out.println(UtilFunctions.hashMapToString(new_factor));

                        boolean factor_to_add = true;

                        // eliminate factor
                        if (CPTBuilder.getNames(new_factor).size() > 1) {
                            new_factor = CPTBuilder.eliminate(new_factor, h, factorCounter);
                        } else if (CPTBuilder.getNames(new_factor).size() == 1) factor_to_add = false;


                        System.out.println("\tFactor AFTER Eliminate on " + h.getName() + "\n");
                        System.out.println("factorCounter: " + factorCounter);
                        System.out.println(UtilFunctions.hashMapToString(new_factor));

                        System.out.println("last_name: " + last_name);

                        if (factor_to_add) factors.put(last_name, new_factor);


                        System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
                        System.out.println("FACTORS:");
                        for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
                            System.out.println("NAME: " + f.getKey());
                            System.out.println(UtilFunctions.hashMapToString(f.getValue()));
                        }
                        System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
                    }
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

        // if there left more than one factor
        if (factors.size() > 1) {
            List<LinkedHashMap<String, Double>> factors_left = new ArrayList<>();

            for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
                factors_left.add(f.getValue());
            }
            last_factor = CPTBuilder.joinFactors(factors_left, factorCounter);


            // else - getting the one left factor
        } else {

            for (Map.Entry<String, LinkedHashMap<String, Double>> f : factors.entrySet()) {
                last_factor = new LinkedHashMap<>(f.getValue());
                System.out.println("2) LAST FACTOR:");
                System.out.println(UtilFunctions.hashMapToString(last_factor));
                break;
            }
        }

        // normalize the final_factor
        last_factor = normalize(last_factor, factorCounter);
        System.out.println("final_factor: (after normalize)");
        System.out.println("factorCounter: " + factorCounter);
        System.out.println(UtilFunctions.hashMapToString(last_factor));

        double value = 0.0;
        for (Map.Entry<String, Double> entry : last_factor.entrySet()) {
            if (entry.getKey().contains(hypothesis_value)) {
                value = entry.getValue();
                System.out.println("entry.getValue(): " + entry.getValue());
                break;
            }
        }

        System.out.println("FINAL VALUE IS " + value);

        List<Double> result = new ArrayList<>();
        result.add(value);
        result.add((double) factorCounter.getSumCount());
        result.add((double) factorCounter.getMulCount());

        return result;
    }

    /**
     * this function get a hidden variable with all the evidence variables and their values we get in the variable elimination function
     * and return the new factor for the hidden variable
     * (deleting the unrequited values by evidence)
     * for example if we have the evidence A=T, and the hidden variable B factor contains B values and A values we delete all the A=F values from the factor
     *
     * @param evidence list of the evidence variable
     * @param values   list of the values of the evidence variables
     * @param factor   the factor we eliminate the evidence values
     * @return the new factor of hidden
     */
    public static LinkedHashMap<String, Double> updateLocalCpt(List<String> evidence, List<String> values, LinkedHashMap<String, Double> factor) {

        List<String> variables_name_in_factor = CPTBuilder.getNames(factor);
        List<String> evidence_variable_in_factor = UtilFunctions.intersection(variables_name_in_factor, evidence);

        List<String> relevant_evidence = new ArrayList<>();
        List<String> relevant_values = new ArrayList<>();

        for (int i = 0; i < evidence.size(); i++) {
            String name = evidence.get(i);
            if (evidence_variable_in_factor.contains(name)) {
                relevant_evidence.add(evidence.get(i));
                relevant_values.add(values.get(i));
            }
        }

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : factor.entrySet()) {
            boolean b = true;
            for (int i = 0; i < relevant_evidence.size(); i++) {
                StringBuilder evidence_value = new StringBuilder();
                evidence_value.append(relevant_evidence.get(i)).append("=").append(relevant_values.get(i));
                b &= entry.getKey().contains(evidence_value);
            }
            if (b) {
                result.put(entry.getKey(), entry.getValue());
            }

        }

        return result;
    }

    /**
     * this function get a factor (assuming with one variable) and return it normalized
     * for example if the input factor is:
     * B=T 0.00059224259
     * B=F 0.00149185665
     * the output factor will be:
     * B=T 0.284171971
     * B=F 0.715828028
     * By Calculating:
     * exp = 1 / 0.00059224259 + 0.00149185665 = 479.8236
     * 0.00059224259 * 479.8236 = 0.2841719716
     * 0.00149185665 * 479.8236 = 0.7158280285
     *
     * @param factor input factor
     * @return normalized given factor
     */
    public LinkedHashMap<String, Double> normalize(LinkedHashMap<String, Double> factor, FactorCounter factorCounter) {

        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        factor = UtilFunctions.fixingDuplicatesValuesInKeys(factor);

        if (CPTBuilder.getNames(factor).size() == 1 && factor.size() == 1) {
            List<String> outcomes = getVariableByName(CPTBuilder.getNames(factor).get(0)).getOutcomes();
            double value = 0.0;
            String key = "";
            for (Map.Entry<String, Double> entry : factor.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                break;
            }
            double q = 1 - value;
            double k = q / (outcomes.size() - 1);
            double exp = q + k * (outcomes.size() + 1);
            exp = 1 / exp;
            result.put(key, value * exp);

        }

        LinkedHashMap<String, List<String>> outcomes = CPTBuilder.getNamesAndOutcomes(factor);
        String variable_name = "";
        for (Map.Entry<String, List<String>> entry : outcomes.entrySet()) {
            variable_name = entry.getKey();
        }
        List<String> variable_outcomes = outcomes.get(variable_name);
        List<String> variable_outcomes_keys = new ArrayList<>();
        for (String outcome : variable_outcomes) {
            String value = variable_name + "=" + outcome;
            variable_outcomes_keys.add(value);
        }
        List<Double> values = new ArrayList<>();

        for (String outcome : variable_outcomes_keys) {
            values.add(factor.get(outcome));
        }

        // number of values we added less one is the number of addition operations
        factorCounter.sumAdd(values.size() - 1);

        double exp = 0.0;
        for (Double value : values) {
            exp += value;
        }
        exp = 1 / exp;
        for (Map.Entry<String, Double> entry : factor.entrySet()) {
            result.put(entry.getKey(), entry.getValue() * exp);
        }
        return result;

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
