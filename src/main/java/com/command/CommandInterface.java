package com.command;

import com.util.Codel;
import java.util.Stack;

public interface CommandInterface {
    public Stack<Integer> calculate(Stack<Integer> stack, Codel older, Codel newer);
}