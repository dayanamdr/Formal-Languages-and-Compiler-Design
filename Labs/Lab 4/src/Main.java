import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Choose an option:");
        System.out.println("1. Scanner");
        System.out.println("2. Finite Automaton");

        Scanner scanner = new Scanner(System.in);
        int selectedOption = scanner.nextInt();

        switch (selectedOption) {
            case 1 -> runScanner();
            case 2 -> optionsForDFA();
            default -> System.out.println("Invalid command! Type 1 or 2.");
        }

    }

    private static void runScanner() {
        MyScanner myScanner = new MyScanner();
        myScanner.start("p1.txt");
        myScanner.start("p1err.txt");
        myScanner.start("p2.txt");
        myScanner.start("p3.txt");
    }

    private static void printMenu() {
        System.out.println("0. STOP.");
        System.out.println("1. Print states.");
        System.out.println("2. Print alphabet.");
        System.out.println("3. Print output states.");
        System.out.println("4. Print transitions.");
        System.out.println("5. Print initial state.");
        System.out.println("6. Print deterministic status.");
        System.out.println("7. Check if word is accepted.");
        System.out.println("8. Get accepted substring.");
    }

    private static void optionsForDFA() {
        FiniteAutomaton finiteAutomaton = new FiniteAutomaton("FA_in.txt");//path here
        //System.out.println("The FA file is being read.");
        printMenu();

        int selectedOption = -1;
        while (selectedOption != 0) {
            System.out.println("Input an option: ");

            Scanner scanner = new Scanner(System.in);
            selectedOption = scanner.nextInt();

            switch (selectedOption) {
                case 1 -> finiteAutomaton.printStates();
                case 2 -> finiteAutomaton.printAlphabet();
                case 3 -> finiteAutomaton.printFinalStates();
                case 4 -> finiteAutomaton.printTransitions();
                case 5 -> finiteAutomaton.printInitialState();
                case 6 -> finiteAutomaton.printDeterministicStatus();
                case 7 -> {
                    Scanner wordScanner = new Scanner(System.in);
                    String word = wordScanner.nextLine();
                    boolean isAccepted = finiteAutomaton.checkAccepted(word);
                    if (isAccepted) {
                        System.out.println("The word is ACCEPTED.");
                    } else {
                        System.out.println("The word is NOT accepted.");
                    }
                }
                case 8 -> {
                    Scanner wordScanner = new Scanner(System.in);
                    String word = wordScanner.nextLine();
                    var wordAccepted = finiteAutomaton.getNextAccepted(word);
                    if (Objects.equals(wordAccepted, "")) {
                        System.out.println("The word has NO MATCHING substring.");
                    } else {
                        System.out.println("The matching substring is '" + wordAccepted + "'.");
                    }
                }
                default -> System.out.println("Invalid command. Enter a valid command between 0-8.");
            }
        }


    }
}