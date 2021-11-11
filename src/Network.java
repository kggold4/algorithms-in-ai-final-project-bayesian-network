import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a bayesian network
 */
public class Network {

    private List<Variable> variables;

    public Network() {
        this.variables = new ArrayList<>();
    }

    public Network(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
    }

    public void addNode(String name, List<String> outcomes, double[] values, Variable[] parents) {
        this.variables.add(new Variable(name, outcomes, values, parents));
    }

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
        for(Variable variable : variables) {
            result.append(variable);
        }
        return result.toString();
    }
}
