import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;
import java.util.regex.Pattern;

public class MyScanner {
    private String sourceCode;
    private final SymbolTable symbolTable;
    private ProgramInternalForm pif;

    private final ArrayList<String> reservedWords;
    private final ArrayList<String> tokens;
    private Integer index;
    private Integer currentLine;

    /**
     * Constructor for creating a MyScanner instance.
     */
    public MyScanner() {
        this.symbolTable = new SymbolTable(100);
        this.pif = new ProgramInternalForm();
        this.reservedWords = new ArrayList<>();
        this.tokens = new ArrayList<>();

        try {
            readTokens();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reads the tokens file and adds the reserved works into reservedWords and the separators & operators into tokens.
     * @throws IOException if the token file was not found.
     */
    private void readTokens() throws IOException {
        try {
            File file = new File("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 1b/token.in");
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String[] token = fileReader.nextLine().split(" ");
                switch(token[0]) {
                    case "definition", "if", "else", "print", "read", "var", "while", "returns" -> reservedWords.add(token[0]);
                    default -> tokens.add(token[0]);
                }
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Sets the sourceCode with the value passed as param.
     * @param sourceCode string representing the sourceCode which has to be set.
     */
    private void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * Starts the sourceCode scanning and checks for lexical errors.
     * The scanning action includes reading the source code, going through the source code and identifying the
     * identifiers, string constants, integer constants and tokens. Also, all the identified elements are then memorized
     * into the SymbolTable and all their positions into the PIF.
     * If no errors were found, the contents of the SymbolTable and PIF are written into files, and it informs the user
     * that the source code is lexically correct. Otherwise, it informs the user that there are lexical errors, and it
     * specifies the line where the error occurred.
     * @param fileName string representing the file name from which the sourceCode is read.
     */
    public void start(String fileName) {
        try {
            Path file = Path.of("/Users/dayana/Documents/uni3/FLCD/FLCD/lab 1/" + fileName);
            setSourceCode(Files.readString(file));
            index = 0;
            currentLine = 1;
            pif = new ProgramInternalForm();

            while (index < sourceCode.length()) {
                tokenizer();
            }

            // write the PIF
            FileWriter fileWriter = new FileWriter("PIF" + fileName.replace(".txt", ".out"));
            fileWriter.write(pif.toString());
            fileWriter.close();

            // write the ST
            fileWriter = new FileWriter("ST" + fileName.replace(".txt", ".out"));
            fileWriter.write(symbolTable.toString());
            fileWriter.close();

            System.out.println("Lexically correct");
        } catch(IOException | MyScannerException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Skips spaces and increased the line number when the new line char is found.
     */
    private void skipSpaces() {
        while (index < sourceCode.length() && Character.isWhitespace(sourceCode.charAt(index))) {
            if (sourceCode.charAt(index) == '\n') {
                currentLine++;
            }
            index++;
        }
    }

    /**
     * It tokenizes the source code by searching for identifiers, constant integers, constant string and tokens.
     * @throws MyScannerException if none of the searched elements where not found, it throws MyScannerException and
     * tells where the lexical error occurred.
     */
    private void tokenizer() throws MyScannerException {
        skipSpaces();
        if (index == sourceCode.length()) {
            return;
        }
        if (tryIdentifierMatching()) {
            return;
        }
        if(tryIntConstMatching()) {
            return;
        }
        if (tryStringConstMatching()) {
            return;
        }
        if(tryTokenListMatching()) {
            return;
        }

        throw new MyScannerException("Lexical error: invalid token at line " + currentLine + ", index " + index);
    }

    /**
     * Tries to match the current substring indicated by the index with an identifier.
     * The matching flow is made of checking if the beginning of the substring matches format of an identifier by using
     * regular expressions. If there is a match and the identifier is valid, the identifier is added to the SymbolTable
     * (if it is not already contained in the SymbolTable) or it gets its position from the SymbolTable in order to be
     * added to the PIF.
     * @return false, if there is no match or if the identifier is not valid. Otherwise, true.
     */
    private Boolean tryIdentifierMatching() {
        var fa = new FiniteAutomaton("identifier.txt");
        var identifier = fa.getNextAccepted(sourceCode.substring(index));
        if (identifier == null) {
            return false;
        }
        if (!validateIdentifier(identifier, sourceCode.substring(index))) {
            return false;
        }

        index += identifier.length();
        Pair<Integer, Integer> position;
        try {
            position = symbolTable.addIdentifier(identifier);
        } catch (Exception e) {
            position = symbolTable.getIdentifierPosition(identifier);
        }
        pif.add(new Pair<>("identifier", position));
        return true;
    }

    /**
     * Validates the possible identifier by checking if it is a reserved word, it matches the identifier pattern, or if
     * it already is found in the SymbolTable.
     * @param possibleIdentifier string representing the possible identifier
     * @param sourceCodeSubstr string representing a substring of the sourceCode
     * @return true, if the possibleIdentifier is valid. Otherwise, false.
     */
    private boolean validateIdentifier(String possibleIdentifier, String sourceCodeSubstr) {
        if (reservedWords.contains(possibleIdentifier)) {
            return false;
        }
        if (Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*").matcher(sourceCodeSubstr).find()) {
            return true;
        }
        return symbolTable.containsIdentifier(possibleIdentifier);
    }

    /**
     * Tries to match with a token from tokens list. First it checks whether it is a reserved word, then if it is a
     * separator or an operator.
     * @return true, if it is a reserved word or a token from the list. Otherwise, false.
     */
    private boolean tryTokenListMatching() {
        String possibleToken = sourceCode.substring(index).split(" ")[0];

        // checks whether it is a reserved word
        for (var reservedToken: reservedWords) {
            if (possibleToken.startsWith(reservedToken)) {
                var regex = "^" + "[a-zA-Z0-9_]*" + reservedToken + "[a-zA-Z0-9_]+";
                if (Pattern.compile(regex).matcher(possibleToken).find()) {
                    return false;
                }
                index += reservedToken.length();
                pif.add(new Pair<>(reservedToken, new Pair<>(-1, -1)));
                return true;
            }
        }

        // checks whether it is a token (separator or operator)
        for (var token: tokens) {
            if (Objects.equals(token, possibleToken) || possibleToken.startsWith(token)) {
                index += token.length();
                pif.add(new Pair<>(token, new Pair<>(-1, -1)));
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to match with a constant integer.
     * Checks if the current element is a valid number, meaning it only contains digits (no other characters), starts
     * with a digit greater than 0, and it might have the +/- specified in the beginning. Or it is simply 0.
     * If it is a valid number, it is added to the SymbolTable (if it's not already) and added its position to the PIF.
     * @return true, if the number is valid. Otherwise, false.
     */
    private boolean tryIntConstMatching() {
        if (Pattern.compile("^([+-]?[1-9][0-9]*|0)[a-zA-z_]").matcher(sourceCode.substring(index)).find()) {
            return false;
        }
        var fa = new FiniteAutomaton("int_constant.txt");
        var intConstant = fa.getNextAccepted(sourceCode.substring(index));
        if (Objects.equals(intConstant, null)) {
            return false;
        }

        index += intConstant.length();
        Pair<Integer, Integer> position;
        try {
            position = symbolTable.addIntConst(intConstant);
        } catch (Exception e) {
            position = symbolTable.getIntConstPosition(intConstant);
        }
        pif.add(new Pair<>("int const", position));
        return true;
    }

    /**
     * Tries to match with a constant string.
     * Checks whether the current element is a valid string, meaning that the quotes are closed correctly and has no
     * invalid chars.
     * @return true, if the string is valid. Otherwise, false.
     */
    private boolean tryStringConstMatching() {
        var regexForStringConstant = Pattern.compile("^\"[a-zA-z0-9_ ?:*^+=.!]*\"");
        var matcher = regexForStringConstant.matcher(sourceCode.substring(index));
        if (!matcher.find()) {
            if (Pattern.compile("^\"[^\"]\"").matcher(sourceCode.substring(index)).find()) {
                throw new MyScannerException("Invalid string constant at line " + currentLine);
            }
            // maybe delete this part
            if (Pattern.compile("^\"[^\"]").matcher(sourceCode.substring(index)).find()) {
                throw new MyScannerException("Missing \" at line " + currentLine);
            }
            return false;
        }
        var stringConstant = matcher.group(0);
        index += stringConstant.length();
        Pair<Integer, Integer> position;
        try {
            position = symbolTable.addStringConst(stringConstant);
        } catch (Exception e) {
            position = symbolTable.getStringConstPosition(stringConstant);
        }
        pif.add(new Pair<>("str const", position));
        return true;
    }

    /**
     * @return current SymbolTable
     */
    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    /**
     * @return current ProgramInternalForm
     */
    public ProgramInternalForm getPIF() {
        return this.pif;
    }
}
