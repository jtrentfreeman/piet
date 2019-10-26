package com.command;

import com.util.Block;
import java.util.Stack;

public interface CommandInterface {
    public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer);
}