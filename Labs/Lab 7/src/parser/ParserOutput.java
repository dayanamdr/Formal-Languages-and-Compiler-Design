package parser;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Node {
    String name;
    int parent;
    int index;
    int sibling;
    List<Node> children;

    public Node(String name) {
        this.name = name;
        this.parent = -1;
        this.sibling = 0;
        this.index = 0;
        this.children = new ArrayList<>();
    }

    public void addChild(Node child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("index: ").append(index)
                .append("  name: ").append(name)
                .append("  parent: ").append(parent)
                .append("  sibling: ").append(sibling);
        return sb.toString();
    }
}

public class ParserOutput {
    private Grammar grammar;
    private List<List<String>> workingStack;
    private Node root;

    private List<Node> tree;

    private int nodeIndex = 1;

    public ParserOutput(Grammar grammar, List<List<String>> workingStack) {
        this.grammar = grammar;
        this.workingStack = workingStack;
        this.root = null;
        this.tree = new ArrayList<>();
    }

    public List<List<String>> parsingProductionString() {
        List<List<String>> rules = new ArrayList<>();
        for (List<String> production : workingStack) {
            if (production.size() > 1) {
                rules.add(production); // [S, aSbS]
            }
        }
        return rules;
    }

    public void parsingTable() {
        List<List<String>> rules = parsingProductionString();

        if (rules.isEmpty()) {
            root = new Node("empty");
        } else {
            int ruleIndex = 0;
            this.root = new Node(rules.get(0).get(0));
            this.root.index = 1;
            this.root.sibling = 0;
            this.root.parent = 0;
            tree.add(this.root);
            parsingTableRec(root, rules, ruleIndex);
        }
    }

    private int parsingTableRec(Node father, List<List<String>> rules, int ruleIndex) {
        if (ruleIndex == rules.size()) {
            return rules.size();
        }
        String prod = rules.get(ruleIndex).get(1);
        System.out.println("prod = " + prod);
        Node sibling = null;
        for (int i = 0; i < prod.length(); i++) {
            String term = String.valueOf(prod.charAt(i));
            System.out.println("term = " + term);

            Node newChild = new Node(term);
            nodeIndex++;
            newChild.index = nodeIndex;
            newChild.parent = father.index;
            if (sibling != null) {
                newChild.sibling = sibling.index;
            }
            tree.add(newChild);
            sibling = newChild;

            father.addChild(sibling);
            if (grammar.getNonTerminals().contains(term)) {
                ruleIndex = parsingTableRec(newChild, rules, ruleIndex + 1);
            }
        }
        return ruleIndex;
    }

    public void printParsingTable() throws IOException {
        for (int i = 0; i < tree.size(); i++) {
            System.out.println(tree.get(i).toString());
        }
        printParsingTableToFile();
    }

    private void printParsingTableToFile() throws IOException {
        FileWriter fileWriter = new FileWriter("parsingTable.txt");
        for (int i = 0; i < tree.size(); i++) {
            fileWriter.append(tree.get(i).toString() + "\n");
        }
        fileWriter.close();
    }
}

