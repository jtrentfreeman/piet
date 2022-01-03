package com.frejt.piet.command;

import java.util.Stack;

import com.frejt.piet.controller.Program;
import com.frejt.piet.util.Block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandRunner {

    /**
     * The execution stack so far
     */
    private Stack<Integer> stack;

    /**
     * The command that will be ran
     */
    private Command command;

    /**
     * The first Block that will
     */
    private Block older;

    private Block newer;

    private static Program program = Program.getInstance();

    private static final Logger log = LogManager.getLogger(CommandRunner.class);

    public CommandRunner(Stack<Integer> stack, Block older, Block newer) {
        this.stack = stack;
        this.older = older;
        this.newer = newer;
    }

    public Stack<Integer> run() {

        this.command = findCommand();

        return runCommand();

    }

    /**
     * Using the difference between the two latest Blocks, finds the Command that
     * should be ran next.
     * 
     * @return the next Command to be ran.
     */
    public Command findCommand() {

        Integer olderHue = older.getColor().getHue();
        Integer olderLight = older.getColor().getLight();

        Integer newerHue = newer.getColor().getHue();
        Integer newerLight = newer.getColor().getLight();

        Integer hueChange = newerHue - olderHue;
        Integer lightChange = newerLight - olderLight;

        while (hueChange < 0) {
            hueChange += 6;
        }
        while (lightChange < 0) {
            lightChange += 3;
        }

        Command command = Command.getCommand(hueChange, lightChange);
        return command;

    }

    /**
     * Performs this runner's {@link #command} and adds it to the list of commands.
     * 
     * @return the program's stack after the command has been ran
     */
    public Stack<Integer> runCommand() {

        program.addToCommandList(command);
        stack = command.calculate(stack, older, newer);
        log.debug("{} \t {}. {} \t\t {}", String.format("%14s", newer.getCoords()),
                String.format("%3s", program.getCommandList().size()), String.format("%10s", command.toString()),
                stack.toString());

        return stack;

    }

}
