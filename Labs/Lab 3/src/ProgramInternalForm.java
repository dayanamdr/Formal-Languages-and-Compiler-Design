import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private final List<Pair<String, Pair<Integer, Integer>>> tokenPositionPair;

    /**
     * Constructor for instantiating a PIF.
     */
    public ProgramInternalForm() {
        this.tokenPositionPair = new ArrayList<>();
    }

    /**
     * Adds a new token pair.
     * @param tokenPair indicates the token (identifier, reserved words, separators or operators) and its position.
     */
    public void add(Pair<String, Pair<Integer, Integer>> tokenPair) {
        this.tokenPositionPair.add(tokenPair);
    }

    /**
     * Prints the PIF as a string.
     * @return all the elements of the PIF as: element --> (listIndex, elemIndexInList)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.tokenPositionPair.size(); i++) {
            sb.append(this.tokenPositionPair.get(i).getFirstItem())
                    .append(" --> (")
                    .append(this.tokenPositionPair.get(i).getSecondItem().getSecondItem()).append(", ")
                    .append(this.tokenPositionPair.get(i).getSecondItem().getSecondItem()).append(")").append("\n");
        }
        return sb.toString();
    }
}
