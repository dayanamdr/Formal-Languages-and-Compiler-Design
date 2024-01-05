import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FiniteAutomaton {
    private final String fileName;
    private String initialState;
    private List<String> alphabet;
    private List<String> states;
    private final List<Transition> transitions;
    private List<String> finalStates;
    private final boolean isDeterministic;

    public FiniteAutomaton(String fileName) {
        this.fileName = fileName;
        this.initialState = "";
        this.alphabet = new ArrayList<>();
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.finalStates = new ArrayList<>();

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("The FA file could not be read.");
        }

        this.isDeterministic = checkIfDeterministic();
    }

    /***
     * Reads the automaton from the files and identifies the states, alphabet, final states and transitions.
     * @throws Exception if the content of the file does not respect the structure.
     */
    private void init() throws Exception {
        var regex = Pattern.compile("^([a-z_]*)=");
        for (String line: Files.readAllLines(Paths.get("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 4/flcd-lab2/"  + fileName))) {
            var matcher = regex.matcher(line);

            if (!matcher.find()) {
                throw new Exception("Invalid line: " + line);
            }

            var category = matcher.group(0);

            if (category == null) {
                throw new Exception("The line is not valid: " + line);
            }

            switch (category) {
                case "states=" -> {
                    var statesInCurlyBrackets = line.substring(line.indexOf("=") + 1);
                    var states = statesInCurlyBrackets.substring(1, statesInCurlyBrackets.length() - 1).trim();
                    this.states = List.of(states.split(", *"));
                }
                case "alphabet=" -> {
                    var alphabetInCurlyBrackets = line.substring(line.indexOf("=") + 1);
                    var alphabet = alphabetInCurlyBrackets.substring(1, alphabetInCurlyBrackets.length() - 1).trim();
                    this.alphabet = List.of(alphabet.split(", *"));
                }
                case "final_states=" -> {
                    var finalStatesInCurlyBrackets = line.substring(line.indexOf("=") + 1);
                    var finalStates = finalStatesInCurlyBrackets.substring(1, finalStatesInCurlyBrackets.length() - 1).trim();
                    this.finalStates = List.of(finalStates.split(", *"));
                }
                case "initial_state=" -> this.initialState = line.substring(line.indexOf("=") + 1).trim();
                case "transitions=" -> {
                    var transitionsInCurlyBrackets = line.substring(line.indexOf("=") + 1);
                    var transitions = transitionsInCurlyBrackets.substring(1, transitionsInCurlyBrackets.length() - 1).trim();
                    var transitionsList = List.of(transitions.split("; *"));
                    for (String transition : transitionsList) {
                        var transitionWithoutParentheses = transition.substring(1, transition.length() - 1).trim();
                        var individualValues = List.of(transitionWithoutParentheses.split(", *"));
                        this.transitions.add(new Transition(individualValues.get(0), individualValues.get(1), individualValues.get(2)));
                    }
                }
                default -> throw new Exception("Invalid line. It does not represent any category.");
            }
        }
    }

    /**
     * Prints a list of string elements.
     * @param listName the name of the list that is printed.
     * @param list the list whose elements are printed.
     */
    private void printList(String listName, List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(listName).append(" = {");

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(", ");
            } else {
                sb.append("}");
            }
        }
        sb.append('\n');
        System.out.println(sb);
    }

    /***
     * Checks whether the automaton is deterministic or not.
     * @return true, if it is deterministic. Otherwise, false.
     */
    public boolean checkIfDeterministic() {
        // check whether a transaction has multiple labels with same value
        for (int i = 0; i < transitions.size(); i++) {
            for (int j = i + 1; j < transitions.size(); j++) {
                Transition firstTran = transitions.get(i);
                Transition secondTran = transitions.get(j);
                if (firstTran.getFrom().equals(secondTran.getFrom())) {
                    if (!firstTran.getTo().equals(secondTran.getTo())) {
                        if (firstTran.getLabel().equals(secondTran.getLabel())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Prints the status of the automaton being deterministic or not.
     */
    public void printDeterministicStatus() {
        if (isDeterministic) {
            System.out.println("It is deterministic.");
        } else {
            System.out.println("It is NOT deterministic.");
        }
    }

    /**
     * Prints the alphabet.
     */
    public void printAlphabet() {
        printList("alphabet", alphabet);
    }

    /**
     * Prints the states.
     */
    public void printStates() {
        printList("states", states);
    }

    /**
     * Prints the final states.
     */
    public void printFinalStates() {
        printList("final_states", finalStates);
    }

    /***
     * Prints the initial state.
     */
    public void printInitialState() {
        System.out.println("initial_state = " + initialState);
    }

    /***
     * Prints the transitions.
     */
    public void printTransitions() {
        StringBuilder sb = new StringBuilder();
        sb.append("transactions = {");

        for (int i = 0; i < transitions.size(); i++) {
            sb.append("(")
                    .append(transitions.get(i).getFrom())
                    .append(", ")
                    .append(transitions.get(i).getTo())
                    .append(", ")
                    .append(transitions.get(i).getLabel());

            if (i == transitions.size() - 1) {
                sb.append(")}");
            } else {
                sb.append(");");
            }
        }
        sb.append('\n');
        System.out.println(sb);
    }

    /***
     * It checks whether a word is accepted or not.
     * A word is accepted when for each character it contains, there is chain of transitions whose labels are equal with
     * the characters of the word (respecting the same order) and the last state is among the final states.
     * @param word string that has to be checked
     * @return true, if the word is accepted. Otherwise, false.
     */
    public boolean checkAccepted(String word) {
        List<String> wordAsList = List.of(word.split(""));
        var currentState = initialState;
        for (String c: wordAsList) {
            var found = false;
            for (Transition transition: transitions) {
                if (transition.getFrom().equals(currentState) && transition.getLabel().equals(c)) {
                    currentState = transition.getTo();
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return finalStates.contains(currentState);
    }

    /***
     * Identifies an accepted substring of the given word.
     * @param word string that has to be checked
     * @return the substring which is accepted. If there is no accepted substring, it returns null.
     */
    public String getNextAccepted(String word) {
        var currentState = initialState;
        StringBuilder acceptedWord = new StringBuilder();
        List<String> wordAsList = List.of(word.split(""));
        for (String c: wordAsList) {
            String newState = null;
            for (Transition transition: transitions) {
                if (transition.getFrom().equals(currentState) && transition.getLabel().equals(c)) {
                    newState = transition.getTo();
                    acceptedWord.append(c);
                    break;
                }
            }
            if (newState == null) { // no valid transition was found for the current character
                if (!finalStates.contains(currentState)) { // the last state is NOT a final state
                    return null;
                } else {
                    return acceptedWord.toString();
                }
            }
            currentState = newState;
        }
        return acceptedWord.toString();
    }

}
