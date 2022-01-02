package com.frejt.piet.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.frejt.piet.controller.Interpreter;
import com.frejt.piet.director.Director;
import com.frejt.piet.util.Block;
import com.frejt.piet.util.Color;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * Tests for the {@link CommandRunner} class.
 */
public class CommandRunnerTest {

    @Mock
    private static Interpreter interpreter;

    @Mock
    private static Director director;

    @Mock
    private static Stack<Integer> stack;

    @Mock
    private static List<Command> commandList;

    @Mock
    private static CommandRunner runner;

    @Mock
    private static Block older;

    @Mock
    private static Block newer;

    @BeforeAll
    public static void init() {

        interpreter = mock(Interpreter.class);
        director = mock(Director.class);

        stack = new Stack<>();
        commandList = new ArrayList<>();

        older = mock(Block.class);
        newer = mock(Block.class);

        runner = new CommandRunner(stack, older, newer);

    }

    @AfterEach
    public void clean() {

        Stack<Integer> stack = new Stack<>();
        Whitebox.setInternalState(runner, "stack", stack);

    }

    /**
     * Tests that, when given two Blocks which have no difference in color,
     * that the {@link findCommand} function returns {@link Command#NOP}
     */
    @Test
    public void findCommand_ZeroHueStep_ZeroLightStep_ReturnsNop() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.LIGHT_RED);

        Command expected = Command.NOP;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by one hue,
     * that the {@link findCommand} function returns {@link Command#PUSH}.
     */
    @Test
    public void findCommand_ZeroHueStep_OneLightStep_ReturnsPush() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.RED);

        Command expected = Command.PUSH;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two
     * hues,
     * that the {@link findCommand} function returns {@link Command#POP}.
     */
    @Test
    public void findCommand_ZeroHueStep_TwoLightStep_ReturnsPop() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.DARK_RED);

        Command expected = Command.POP;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by one hue
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#ADD}.
     */
    @Test
    public void findCommand_OneHueStep_ZeroLightStep_ReturnsAdd() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.LIGHT_YELLOW);

        Command expected = Command.ADD;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by one hue
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#PUSH}.
     */
    @Test
    public void findCommand_OneHueStep_OneLightStep_ReturnsSubtract() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.YELLOW);

        Command expected = Command.SUB;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_OneHueStep_TwoLightStep_ReturnsMultiply() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.DARK_YELLOW);

        Command expected = Command.MULT;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_TwoHueStep_ZeroLightStep_ReturnsDivide() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.LIGHT_GREEN);

        Command expected = Command.DIV;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_TwoHueStep_OneLightStep_ReturnsMod() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.GREEN);

        Command expected = Command.MOD;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_TwoHueStep_TwoLightStep_ReturnsNot() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.DARK_GREEN);

        Command expected = Command.NOT;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_ThreeHueStep_ZeroLightStep_ReturnsGreater() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.LIGHT_CYAN);

        Command expected = Command.GREATER;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_ThreeHueStep_OneLightStep_ReturnsPointer() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.CYAN);

        Command expected = Command.POINTER;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_ThreeHueStep_TwoLightStep_ReturnsSwitch() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.DARK_CYAN);

        Command expected = Command.SWITCH;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_FourHueStep_ZeroLightStep_ReturnsDuplicate() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.LIGHT_BLUE);

        Command expected = Command.DUP;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_FourHueStep_OneLightStep_ReturnsRoll() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.BLUE);

        Command expected = Command.ROLL;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_FourHueStep_TwoLightStep_ReturnsInNumber() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.DARK_BLUE);

        Command expected = Command.IN_NUM;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_FiveHueStep_ZeroLightStep_ReturnsInChar() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.LIGHT_MAGENTA);

        Command expected = Command.IN_CHAR;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_FiveHueStep_OneLightStep_ReturnsOutNumber() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.MAGENTA);

        Command expected = Command.OUT_NUM;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when two Blocks, which have colors that are different by two hues
     * and
     * one light step, that the {@link findCommand} function returns
     * {@link Command#MULT}.
     */
    @Test
    public void findCommand_FiveHueStep_TwoLightStep_ReturnsOutChar() {

        when(older.getColor()).thenReturn(Color.LIGHT_RED);
        when(newer.getColor()).thenReturn(Color.DARK_MAGENTA);

        Command expected = Command.OUT_CHAR;

        Command actual = runner.findCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#NOP} command, the stack
     * remains
     * unchanged after the execution of the command.
     */
    @Test
    public void runCommand_Nop_DoesNothing() {

        Stack<Integer> stack = new Stack<>();

        Whitebox.setInternalState(runner, "command", Command.NOP);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = stack;

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#PUSH} command, the stack
     * has
     * the size of the older block pushed to it.
     */
    @Test
    public void runCommand_Push_PushesOlderSize() {

        when(older.getSize()).thenReturn(5);

        Whitebox.setInternalState(runner, "command", Command.PUSH);

        Stack<Integer> expected = new Stack<>();
        expected.add(5);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#POP} command, the stack
     * has
     * a single value popped from it.
     */
    @Test
    public void runCommand_Pop_PopsTopValue() {

        Stack<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);

        Whitebox.setInternalState(runner, "command", Command.POP);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(1);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#ADD} command, the stack
     * has
     * its top two values popped off, calculates [BOTTOM + TOP], and pushes the
     * result
     * back onto the stack.
     */
    @Test
    public void runCommand_Add_AddsTwoValues() {

        Stack<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);

        Whitebox.setInternalState(runner, "command", Command.ADD);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(3);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#SUB} command, the stack
     * has
     * its top two values popped off, calculates [BOTTOM - TOP], and pushes the
     * result
     * back onto the stack.
     */
    @Test
    public void runCommand_Subtract_SubtractsValue() {

        Stack<Integer> stack = new Stack<>();
        stack.add(1);
        stack.add(2);

        Whitebox.setInternalState(runner, "command", Command.SUB);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(-1);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#MULT} command, the stack
     * has
     * its top two values popped off, calculates [BOTTOM * TOP], and pushes the
     * result
     * back onto the stack.
     */
    @Test
    public void runCommand_Multiply_MultipliesValue() {

        Stack<Integer> stack = new Stack<>();
        stack.add(2);
        stack.add(2);

        Whitebox.setInternalState(runner, "command", Command.MULT);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(4);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#DIV} command, the stack
     * has
     * its top two values popped off, calculates [BOTTOM / TOP], and pushes the
     * result
     * back onto the stack.
     */
    @Test
    public void runCommand_Divide_DividesValues() {

        Stack<Integer> stack = new Stack<>();
        stack.add(4);
        stack.add(2);

        Whitebox.setInternalState(runner, "command", Command.DIV);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(2);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#MOD} command, the stack
     * has
     * its top two values popped off, calculates [BOTTOM % TOP], and pushes the
     * result
     * back onto the stack.
     */
    @Test
    public void runCommand_Modulus_ModsValues() {

        Stack<Integer> stack = new Stack<>();
        stack.add(7);
        stack.add(3);

        Whitebox.setInternalState(runner, "command", Command.MOD);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(1);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#NOT} command, and the
     * stack
     * has a top value of 0, replaces the top value with 1.
     */
    @Test
    public void runCommand_Not_ZeroValue_ReplacesWithOne() {

        Stack<Integer> stack = new Stack<>();
        stack.add(0);

        Whitebox.setInternalState(runner, "command", Command.NOT);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(1);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#NOT} command, and the
     * stack
     * has a top value of 1, replaces the top value with 0.
     */
    @Test
    public void runCommand_Not_NonZeroValue_ReplacesWithOne() {

        Stack<Integer> stack = new Stack<>();
        stack.add(5);

        Whitebox.setInternalState(runner, "command", Command.NOT);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(0);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#GREATER} command, and the
     * stack
     * has the top two values popped off, if the bottom value is greater than the
     * top
     * value, then a 1 is pushed onto the stack, and a 0 is pushed otherwise.
     */
    @Test
    public void runCommand_Greater_OlderIsGreater_OneIsPushed() {

        Stack<Integer> stack = new Stack<>();
        stack.add(10);
        stack.add(5);

        Whitebox.setInternalState(runner, "command", Command.GREATER);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(1);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@link Command#GREATER} command, and the
     * stack
     * has the top two values popped off, if the top value is greater than the
     * bottom
     * value, then a 1 is pushed onto the stack, and a 0 is pushed otherwise.
     */
    @Test
    public void runCommand_Greater_NewerIsGreater_OneIsPushed() {

        Stack<Integer> stack = new Stack<>();
        stack.add(5);
        stack.add(10);

        Whitebox.setInternalState(runner, "command", Command.GREATER);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(0);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when the runner has the {@lnik Command#DUP} command, and the
     * stack
     * has a value, the value is duplicated so that the value at the top of the
     * stack
     * now is at the top twice.
     */
    @Test
    public void runCommand_duplicate_ValueDuplicated() {

        Stack<Integer> stack = new Stack<>();
        stack.add(5);

        Whitebox.setInternalState(runner, "command", Command.DUP);
        Whitebox.setInternalState(runner, "stack", stack);

        Stack<Integer> expected = new Stack<>();
        expected.add(5);
        expected.add(5);

        Stack<Integer> actual = runner.runCommand();

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when a positive dividend is "modded" by a lesser divisor, the
     * result
     * is correctly calculated so that the result would be equal to ((p + Nq) mod
     * q).
     */
    @Test
    public void correctMod_PositiveDividendGreater_CorrectlyModded() {

        Integer dividend = 5;
        Integer divisor = 3;

        Integer expected = 2;

        Integer actual = CommandController.correctMod(dividend, divisor);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when a positive dividend is "modded" by a greater divisor, the
     * result
     * is correctly calculated so that the result would be equal to ((p + Nq) mod
     * q).
     */
    @Test
    public void correctMod_PositiveDividendLesser_CorrectlyModded() {

        Integer dividend = 2;
        Integer divisor = 3;

        Integer expected = 2;

        Integer actual = CommandController.correctMod(dividend, divisor);

        assertEquals(expected, actual);

    }

    /**
     * Tests that, when a negative dividend is "modded" by a greater divisor, the
     * result
     * is correctly calculated so that the result would be equal to ((p + Nq) mod
     * q).
     */
    @Test
    public void correctMod_NegativeDividend_CorrectlyModded() {

        Integer dividend = -1;
        Integer divisor = 3;

        Integer expected = 2;

        Integer actual = CommandController.correctMod(dividend, divisor);

        assertEquals(expected, actual);

    }

}
