package com.util;

public enum Command {
    NOP("nop", 0, 0),
    PUSH("push", 0, 1),
    POP("pop", 0, 2),
    ADD("add", 1, 0),
    SUB("subtract", 1, 1),
    MULT("multiply", 1, 2),
    DIV("divide", 2, 0),
    MOD("modulus", 2, 1),
    NOT("not", 2, 2),
    GREATER("greater", 3, 0),
    POINTER("dp_pointer", 3, 1),
    SWITCH("switch", 3, 2),
    DUP("duplicate", 4, 0),
    ROLL("roll", 4, 1),
    IN_NUM("in_num", 4, 2),
    IN_CHAR("in_char", 5, 0),
    OUT_NUM("out_num", 5, 1),
    OUT_CHAR("out_char", 5, 2);

    private String name;
    private Integer hue;
    private Integer light;

    Command(String name, Integer hue, Integer light) {
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
}