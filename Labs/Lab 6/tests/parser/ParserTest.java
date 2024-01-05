package parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @org.junit.jupiter.api.Test
    void momentaryInsuccess() {
        Parser p = new Parser(State.NORMAL, 0, new ArrayList<>(), new ArrayList<>());
        // changes state to BACK
        Assertions.assertEquals(p.getState(), State.NORMAL);
        p.momentaryInsuccess();
        Assertions.assertEquals(p.getState(), State.BACK);
    }

    @org.junit.jupiter.api.Test
    void success() {
        Parser p = new Parser(State.NORMAL, 0, new ArrayList<>(), new ArrayList<>());
        // changes state to FINAL
        Assertions.assertNotEquals(p.getState(), State.FINAL);
        p.success();
        Assertions.assertEquals(p.getState(), State.FINAL);
    }

    @org.junit.jupiter.api.Test
    void advance() {
        List<String> inputStack = new ArrayList<>(List.of("1", "A")); // [1, A]
        List<List<String>> workingStack = new ArrayList<>(List.of(List.of("S", "1A")));// [[S, 1A]]

        Parser p = new Parser(State.NORMAL, 0, workingStack, inputStack);

        Assertions.assertEquals(0, p.getIndex());
        Assertions.assertEquals(State.NORMAL, p.getState());
        Assertions.assertEquals("1", p.getFirstElemFromInputList());
        Assertions.assertEquals(List.of("S", "1A"), p.getLastElemFromWorkingStack());

        p.advance();

        Assertions.assertEquals(1, p.getIndex()); // index is increased
        Assertions.assertEquals(State.NORMAL, p.getState()); // state is the same
        // moves the terminal from the input stack to the working stack
        Assertions.assertEquals("A", p.getFirstElemFromInputList());
        Assertions.assertEquals(List.of("A"), inputStack); // [A]
        Assertions.assertEquals(List.of("1"), p.getLastElemFromWorkingStack());
    }

    @org.junit.jupiter.api.Test
    void expand() {
        List<String> inputStack = new ArrayList<>(List.of("S")); // [S]
        List<List<String>> workingStack = new ArrayList<>();// []
        //workingStack.add(List.of("1"));

        Parser p = new Parser(State.NORMAL, 0, workingStack, inputStack);

        Assertions.assertEquals(0, p.getIndex());
        Assertions.assertEquals(State.NORMAL, p.getState());
        Assertions.assertEquals("S", p.getFirstElemFromInputList());
        Assertions.assertEquals(1, inputStack.size());
        Assertions.assertEquals(0, workingStack.size());

        p.expand();

        Assertions.assertEquals(0, p.getIndex());
        Assertions.assertEquals(State.NORMAL, p.getState());
        Assertions.assertEquals("1", p.getFirstElemFromInputList());
        Assertions.assertEquals(List.of("1", "S"), inputStack); // [1, A]
        Assertions.assertEquals(List.of(List.of("S", "1S")), workingStack);
    }

    @org.junit.jupiter.api.Test
    void back() {
        List<String> inputStack = new ArrayList<>(List.of("A")); // [S]
        List<List<String>> workingStack = new ArrayList<>(List.of(List.of("S", "1"))); // [S, 1]
        workingStack.add(List.of("1"));

        Parser p = new Parser(State.BACK, 1, workingStack, inputStack);

        Assertions.assertEquals(1, p.getIndex());
        Assertions.assertEquals(State.BACK, p.getState());
        Assertions.assertEquals("A", p.getFirstElemFromInputList());
        Assertions.assertEquals(1, inputStack.size());
        Assertions.assertEquals(List.of("1"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(2, workingStack.size()); // checking size

        p.back();
        Assertions.assertEquals(0, p.getIndex());
        Assertions.assertEquals(State.BACK, p.getState());
        Assertions.assertEquals("1", p.getFirstElemFromInputList());
        Assertions.assertEquals(2, inputStack.size());
        Assertions.assertEquals(List.of("S", "1"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(1, workingStack.size());
    }

    @org.junit.jupiter.api.Test
    void anotherTryChangingToNormal() {
        List<String> inputStack = new ArrayList<>(List.of("1", "S")); // [1, S]
        List<List<String>> workingStack = new ArrayList<>(List.of(List.of("S", "1S"))); // [S, 1S], [1], [S, 1S]
        workingStack.add(List.of("1"));
        workingStack.add(List.of("S", "1S"));

        Parser p = new Parser(State.BACK, 1, workingStack, inputStack);

        Assertions.assertEquals(State.BACK, p.getState());
        Assertions.assertEquals(List.of("S", "1S"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(List.of("1", "S"), inputStack);

        p.anotherTry();

        Assertions.assertEquals(State.NORMAL, p.getState());
        Assertions.assertEquals(List.of("S", "2A"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(List.of("2", "A"), inputStack);

    }

    @org.junit.jupiter.api.Test
    void anotherTryException() {
        List<String> inputStack = new ArrayList<>(List.of("2")); // [2]
        List<List<String>> workingStack = new ArrayList<>(List.of(List.of("S", "2A"))); // [S, 2A], [2], [A, 2]
        workingStack.add(List.of("2"));
        workingStack.add(List.of("A", "2"));

        Parser p = new Parser(State.BACK, 1, workingStack, inputStack);

        Assertions.assertEquals(State.BACK, p.getState());
        Assertions.assertEquals(List.of("A", "2"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(3, workingStack.size());
        Assertions.assertEquals(List.of("2"), inputStack);
        Assertions.assertEquals(1, inputStack.size());

        p.anotherTry();

        Assertions.assertEquals(State.BACK, p.getState());
        Assertions.assertEquals(List.of("2"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(2, workingStack.size());
        Assertions.assertEquals(List.of("A", "2"), inputStack);
        Assertions.assertEquals(2, inputStack.size());
    }

    @org.junit.jupiter.api.Test
    void anotherTryError() {
        List<String> inputStack = new ArrayList<>(List.of("3", "A")); // [3, A]
        List<List<String>> workingStack = new ArrayList<>(List.of(List.of("S", "3A"))); // [S, 3A]

        Parser p = new Parser(State.BACK, 0, workingStack, inputStack);

        Assertions.assertEquals(State.BACK, p.getState());
        Assertions.assertEquals(List.of("S", "3A"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(1, workingStack.size());
        Assertions.assertEquals(List.of("3", "A"), inputStack);
        Assertions.assertEquals(2, inputStack.size());

        p.anotherTry();

        Assertions.assertEquals(State.ERROR, p.getState());
        Assertions.assertEquals(List.of("S", "3A"), p.getLastElemFromWorkingStack());
        Assertions.assertEquals(1, workingStack.size());
        Assertions.assertEquals(List.of("3", "A"), inputStack);
        Assertions.assertEquals(2, inputStack.size());
    }


}