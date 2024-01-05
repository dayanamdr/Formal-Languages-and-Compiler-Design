import parser.Grammar;
import parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Parser p = new Parser(State.NORMAL, 0, new ArrayList<>(), new ArrayList<>());
        Parser p = new Parser();
        String ss = "accbcc";
        String[] s = ss.split("");
        p.checkSequence();
    }
}