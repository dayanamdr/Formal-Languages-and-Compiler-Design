package parser;

import parser.Grammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Parser {

    private enum State {
        NORMAL, ERROR, BACK, FINAL
    }

    State state;
    int index;
    int maxIndex;
    Grammar grammar;
    private String[] sequence;
    List<String> inputStack;
    List<List<String>> workingStack;
    private final boolean leftRecursive;


    public Parser() {
        this.state = State.NORMAL;
        this.index = 0;
        this.maxIndex = 0;
        this.grammar = new Grammar("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 7/flcd-lab2/G1.txt");
        this.workingStack = new ArrayList<>();
        this.inputStack = new ArrayList<>(List.of(grammar.getStartingSymbol()));
        this.sequence = null;
        this.leftRecursive = checkGrammarLeftRecursive();
    }



    public boolean checkGrammarLeftRecursive() {
        for (Map.Entry<List<String>, List<List<String>>> entry : grammar.getProductions().entrySet()) {
            String nonTerminal = entry.getKey().get(0);
            List<List<String>> productions = new ArrayList<>(entry.getValue());
            for (List<String> prod : productions) {
                if (prod.get(0).equals(nonTerminal)) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * Changes the state to BACK(B).
     */
    public void momentaryInsuccess() {
        System.out.println("Momentary insuccess");
        this.state = State.BACK;
        System.out.println("Changed state to BACK.");
    }

    /**
     * Changes the state to FINAL(F).
     */
    public void success() {
        System.out.println("Success!");
        this.state = State.FINAL;
        System.out.println("Changed state to FINAL.");
    }

    /**
     * Increases the index with the value 1.
     * Pushes the top element of the inputStack into the workingStack.
     * Pops the element from the inputStack.
     */
    public void advance() {
        System.out.println("Advance");
        index += 1;
        if (index > maxIndex) {
            maxIndex = index;
        }
        this.workingStack.add(List.of(this.inputStack.get(0)));
        this.inputStack.remove(0);
    }

    /**
     * Takes the non-terminal from the inputStack and searches for the first production.
     * Pushes on the workingStack the first production of the non-terminal as a list containing the non-terminal and the
     * first production.
     * Pops the non-terminal from the inputStack and pushes the first production into the inputStack.
     */
    public void expand() {
        System.out.println("Expand");
        String nonTerminal = this.inputStack.get(0);
        List<String> firstProduction = new ArrayList<>(grammar.getNonTerminalProductions(List.of(nonTerminal))).get(0);

        this.workingStack.add(new ArrayList<>(List.of(nonTerminal, String.join("", firstProduction))));

        this.inputStack.remove(0);
        this.inputStack.addAll(0, firstProduction);
    }

    public void back() {
        System.out.println("Back");
        this.index -= 1;
        String terminal = this.workingStack.get(this.workingStack.size() - 1).get(0);

        this.inputStack.add(0, terminal);
        this.workingStack.remove(this.workingStack.size() - 1);
    }

    public void anotherTry() {
        System.out.println("Another try");

        List<String> lastProduction = this.workingStack.get(workingStack.size() - 1);
        String nonTerminal = lastProduction.get(0);

        String next = this.grammar.getNextProduction(lastProduction.get(1), nonTerminal);
        //System.out.println("NEXT: " + next + "   LAST PROD: " + lastProduction);
        if (next != null) {
            System.out.println("Changing state to NORMAL");
            this.state = State.NORMAL;
            this.workingStack.remove(this.workingStack.size() - 1);
            this.workingStack.add(new ArrayList<>(List.of(nonTerminal, String.join("", next))));

            // Clear the input stack and add the new sequence
            this.inputStack.subList(0, lastProduction.get(1).length()).clear();
            this.inputStack.addAll(0, List.of(next.split("")));
        } else if (index == 0 && lastProduction.get(0).equals(grammar.getStartingSymbol())) {
            System.out.println("Changing state to ERROR");
            System.out.println("Error around term " + sequence[maxIndex] + " (index = " + (maxIndex + 1) + ")");
            state = State.ERROR;
        } else {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            this.workingStack.remove(this.workingStack.size() - 1);

            // Clear the input stack and add the last production
            this.inputStack.subList(0, lastProduction.get(1).length()).clear();
            this.inputStack.addAll(0, List.of(lastProduction.get(1).split("")));
        }
    }

    private String[] readSequence() throws IOException {
        Path file = Path.of("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 7/flcd-lab2/seq.txt");
        String seq = Files.readString(file);
        return seq.split("");
    }


    public boolean checkSequence() {
        try {
            sequence = readSequence();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        print();
        if (leftRecursive) {
            System.out.println("Grammar is left recursive.");
            return false;
        }

        for (String elem : sequence) { // every element in the sequence exists in the terminals
            if (!grammar.getTerminals().contains(elem)) {
                System.out.println("e");
                print();
                System.out.println("Changing state to Error.");
                System.out.println("Error around term " + elem + " as it is not a terminal");
                this.state = State.ERROR;
            }
        }

        this.sequence = sequence;

        while (state != State.FINAL && state != State.ERROR) {
            // if s == q
            if (state == State.NORMAL) {
                // then if isEmpty(beta) and (i == n + 1)
                if (inputStack.isEmpty() && index == sequence.length) {
                    // then Success
                    success();
                    print();
                }
                else {
                    // if Head(beta) == A
                    if (!inputStack.isEmpty() && grammar.getNonTerminals().contains(inputStack.get(0)) && index < sequence.length) {
                        // Then Expand
                        expand();
                        print();
                    }
                    else {
                        // if Head(beta) == ai;   ai - sequence(index)
                        if (!inputStack.isEmpty() && index < sequence.length && inputStack.get(0).equals(sequence[index])) {
                            // then Advance
                            advance();
                            print();
                        } else {
                            // Then MomentaryInsuccess
                            momentaryInsuccess();
                            print();
                        }
                    }
                }
            }
            // else
            else {
                // if s == b
                if (state == State.BACK) {
                    List<String> terminals = new ArrayList<>(grammar.getTerminals());
                    // if Head(alpha) = a;   head of beta is a terminal
                    if (terminals.contains(workingStack.get(workingStack.size() - 1).get(0))) {
                        // then Back
                        back();
                        print();
                    } else {
                        // else AnotherTry
                        anotherTry();
                        print();
                    }
                }

            }
        }
        // endWhile

        // if s == e
        if (state == State.ERROR) {
            // then message "Error"
            System.out.println("Error");
        }
        else {
            // else message "Sequence accepted"
            System.out.println("Sequence accepted");

            ParserOutput po = new ParserOutput(grammar, workingStack);
            po.parsingTable();
            try {
                po.printParsingTable();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
    public void print() {
        System.out.println("---------");
        System.out.println("state: " + state);
        System.out.println("index:" + index);
        System.out.println("alpha:" + workingStack);
        System.out.println("beta:" + inputStack);
        System.out.println("---------");
    }
}