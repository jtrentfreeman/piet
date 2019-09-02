package com.util;

public enum Color {
    BLACK("black", 0),
    WHITE("white", 1),

    LIGHT_RED("light red", 10),
    RED("red", 11),
    DARK_RED("dark red", 12),
    
    LIGHT_YELLOW("light yellow", 20),
    YELLOW("yellow", 21),
    DARK_YELLOW("dark yellow", 22),
    
    LIGHT_GREEN("light green", 30),
    GREEN("green", 31),
    DARK_GREEN("dark green", 32),
    
    LIGHT_CYAON("light cyan", 40),
    CYAN("cyan", 41),
    DARK_CYAN("dark cyan", 42),
    
    LIGH_BLUE("light blue", 50),
    BLUE("blue", 51),
    DARK_BLUE("dark blue", 52),
    
    LIGHT_MAGENTA("light magenta", 60),
    MAGENTA("magenta", 61),
    DARK_MAGENTA("dark magenta", 62);

    private String name;
    private Integer value;

    Color(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Integer getValue() {
        return this.value;
    }

}