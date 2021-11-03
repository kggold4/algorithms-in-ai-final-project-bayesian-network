import java.util.ArrayList;

public class Variable {
    private String name;
    private int outcome_counter;
//    private ArrayList<Variable> parents;

    public Variable(String name, int outcome_counter) {
        this.name = name;
        this.outcome_counter = outcome_counter;
//        this.parents = new ArrayList<>();
    }
}
