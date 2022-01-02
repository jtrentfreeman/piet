package com.frejt.piet.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Stack;

import com.frejt.piet.entity.Board;
import com.frejt.piet.util.Block;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Tests for the {@link CommandController} class.
 */
public class CommandControllerTest {

    @Mock
    private static Board board;

    @Mock
    private static Block older;

    @Mock
    private static Block newer;

    @BeforeAll
    public static void init() {

        // Define the board
        board = mock(Board.class);

        older = mock(Block.class);
        when(older.getSize()).thenReturn(5);

        newer = mock(Block.class);
        when(newer.getSize()).thenReturn(10);

    }

    /**
     * Tests that, when a null stack is passed into the nop function, a null stack
     * is returned.
     */
    @Test
    public void nop_NullStack_ReturnsNull() {

        Stack<Integer> expected = null;

        Block older = null;
        Block newer = null;
        Stack<Integer> stack = null;

        Stack<Integer> actual = CommandController.nop(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when a stack with values is passed into the nop function, the
     * same stack is returned.
     */
    @Test
    public void nop_ValidStack_ReturnsStack() {

        Stack<Integer> expected = new Stack<>();
        expected.push(1);
        expected.push(2);

        Block older = null;
        Block newer = null;

        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);

        Stack<Integer> actual = CommandController.nop(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the older block is passed into the push function, the size
     * of the block is pushed onto the stack.
     */
    @Test
    public void push_OlderBlockHasSize_PushesSize() {

        Stack<Integer> expected = new Stack<>();
        expected.push(5);

        Stack<Integer> stack = new Stack<>();

        Stack<Integer> actual = CommandController.push(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the stack has no values in it, but the pop function is
     * called, no errors occur that halt the program.
     */
    @Test
    public void pop_StackIsEmpty_NoExceptionThrown() {

        Stack<Integer> expected = new Stack<>();

        Stack<Integer> stack = new Stack<>();

        Stack<Integer> actual = CommandController.pop(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the stack has multiple values in it, and the pop function is
     * called, then only the top value is removed from the stack.
     */
    @Test
    public void pop_StackHasValues_PopsSingleValue() {

        Stack<Integer> expected = new Stack<>();
        expected.add(1);
        expected.add(2);

        Stack<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);
        stack.add(3);

        Stack<Integer> actual = CommandController.pop(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the stack is empty, and the add function is called,
     * the stack is returned as is and no exception is thrown.
     */
    @Test
    public void add_StackIsEmpty_NoExceptionThrown() {

        Stack<Integer> expected = new Stack<>();

        Stack<Integer> stack = new Stack<>();

        Stack<Integer> actual = CommandController.add(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the stack has only one value, and the add function is
     * called, the stack is returned as is and no exception is thrown.
     */
    @Test
    public void add_StackHasOneValue_StackIsSame() {

        Stack<Integer> expected = new Stack<>();
        expected.add(1);

        Stack<Integer> stack = new Stack<>();
        stack.add(1);

        Stack<Integer> actual = CommandController.add(stack, older, newer);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the stack has at least two values, and the add function is
     * called, the top two values in the stack are popped off, added together, and
     * pushed back onto the stack.
     */
    @Test
    public void add_StackHasManyValues_ValuesAreAdded() {

        Stack<Integer> expected = new Stack<>();
        expected.add(3);

        Stack<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);

        Stack<Integer> actual = CommandController.add(stack, older, newer);

        assertEquals(expected, actual);

    }

}
