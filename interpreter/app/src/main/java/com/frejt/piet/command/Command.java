package com.frejt.piet.command;

import java.util.Stack;

import com.frejt.piet.util.Block;
import com.google.gson.GsonBuilder;

/**
 * Commands are defined by the transition of colour from one colour block to hte
 * next as the interpreter travles through the program.
 * 
 * The number of steps along the Hue Cycle and Lightness Cycle in each
 * transition determine the command executed.
 */
public enum Command implements CommandInterface {

    NOP("nop", 0, 0) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.nop(stack, older, newer);
        }
    },
    PUSH("push", 0, 1) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.push(stack, older, newer);
        }
    },
    POP("pop", 0, 2) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.pop(stack, older, newer);
        }
    },
    ADD("add", 1, 0) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.add(stack, older, newer);
        }
    },
    SUB("subtract", 1, 1) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.subtract(stack, older, newer);
        }
    },
    MULT("multiply", 1, 2) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.multiply(stack, older, newer);
        }
    },
    DIV("divide", 2, 0) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.divide(stack, older, newer);
        }
    },
    MOD("modulus", 2, 1) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.modulus(stack, older, newer);
        }
    },
    NOT("not", 2, 2) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.not(stack, older, newer);
        }
    },
    GREATER("greater", 3, 0) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.greater(stack, older, newer);
        }
    },
    POINTER("dp_pointer", 3, 1) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.dp(stack, older, newer);
        }
    },
    SWITCH("switch", 3, 2) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.cc(stack, older, newer);
        }
    },
    DUP("duplicate", 4, 0) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.duplicate(stack, older, newer);
        }
    },
    ROLL("roll", 4, 1) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.roll(stack, older, newer);
        }
    },
    IN_NUM("in_num", 4, 2) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.inNum(stack, older, newer);
        }
    },
    IN_CHAR("in_char", 5, 0) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.inChar(stack, older, newer);
        }
    },
    OUT_NUM("out_num", 5, 1) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.outNum(stack, older, newer);
        }
    },
    OUT_CHAR("out_char", 5, 2) {
        @Override
        public Stack<Integer> calculate(Stack<Integer> stack, Block older, Block newer) {
            return CommandController.outChar(stack, older, newer);
        }
    };

    private String name;
    private Integer hue;
    private Integer light;

    private Command(String name, Integer hue, Integer light) {
        this.name = name;
        this.hue = hue;
        this.light = light;
    }

    public String getName() {
        return this.name;
    }

    public Integer getHue() {
        return this.hue;
    }

    public Integer getLight() {
        return this.light;
    }

    public static Command getCommand(Integer hue, Integer light) {
        for (Command command : Command.values()) {
            if (hue == command.getHue()) {
                if (light == command.getLight()) {
                    return command;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}