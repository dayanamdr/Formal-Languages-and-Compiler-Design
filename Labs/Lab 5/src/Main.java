import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Grammar grammar = new Grammar("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 5/flcd-lab2/G1.txt");
        Grammar grammar = new Grammar("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 5/flcd-lab2/G1.txt");
        System.out.println(grammar.getNonTerminals());
        System.out.println(grammar.getTerminals());
        System.out.println(grammar.getStartingSymbol());
        System.out.println(grammar.getProductions());
        System.out.println(grammar.getTerminalProductions(List.of("A")));

    }
}