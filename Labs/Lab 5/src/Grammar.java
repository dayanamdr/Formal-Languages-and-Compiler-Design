import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private Set<String> nonTerminals;
    private Set<String> terminals;
    private String startingSymbol;
    private Map<List<String>, Set<List<String>>> productions;
    private boolean CFG;
    private String filePath;

    private final String ELEMENT_SEPARATOR = "&";

    private final String PRODUCTION_SEPARATOR = "\\|";
    private final String TERMINALS_SEPARATOR = " ";
    private final String EPSILON = "EPS";
    private final String SEPARATOR_LEFT_RIGHT_HAND_SIDE = "->";

    public Grammar(String filePath) {
        //recursive descendent
        this.filePath = filePath;
        this.nonTerminals = new HashSet<>();
        this.terminals = new HashSet<>();
        this.productions = new HashMap<>();
        readGrammarFile();
    }

    private void readGrammarFile() {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            this.nonTerminals.addAll(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            this.terminals.addAll(List.of(scanner.nextLine().split(this.ELEMENT_SEPARATOR)));
            this.startingSymbol = scanner.nextLine();

            while (scanner.hasNextLine()) {
                this.parseProduction(scanner.nextLine());
            }

            this.CFG = this.isCFG();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parseProduction(String production) {
        String[] splitLRHS = production.split(this.SEPARATOR_LEFT_RIGHT_HAND_SIDE);
        List<String> splitLHS = List.of(splitLRHS[0].split(this.TERMINALS_SEPARATOR));
        String[] splitRHS = splitLRHS[1].split(this.PRODUCTION_SEPARATOR);

        this.productions.putIfAbsent(splitLHS, new HashSet<>());
        for (int i = 0; i < splitRHS.length; i++) {
            String[] terminals = splitRHS[i].split(this.TERMINALS_SEPARATOR);
            this.productions.get(splitLHS).add(Arrays.stream(terminals).collect(Collectors.toList()));
        }
    }

    public Set<List<String>> getTerminalProductions(List<String> terminal) {
        return this.productions.get(terminal);
    }

    private boolean isCFG() {
        if (!this.nonTerminals.contains(this.startingSymbol)) {
            return false;
        }

        for (List<String> LHS : this.productions.keySet()) {
            if (LHS.size() != 1 || !this.nonTerminals.contains(LHS.get(0))) {
                return false;
            }

            for (List<String> potentialNextMoves : this.productions.get(LHS)) {
                for (String potentialNextMove : potentialNextMoves) {
                    if (!this.nonTerminals.contains(potentialNextMove) && !this.terminals.contains(potentialNextMove) && !potentialNextMove.equals(this.EPSILON)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public Map<List<String>, Set<List<String>>> getProductions() {
        return productions;
    }

    public boolean getIsCFG() {
        return CFG;
    }
}
