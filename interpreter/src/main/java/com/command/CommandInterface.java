package com.command;

import com.util.Block;
import java.util.Stack;

public interface CommandInterface {

    /**
     * Calculates the appropriate command given the two blocks, and adds it to the stack
     * @param stack - the stack to modify 
     * @param older - the "oldest" block to be detected
     * @param newer - the "newest" block to be compared to the older block
     * @return the stack with the new command added to it
     */
    public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer);
}