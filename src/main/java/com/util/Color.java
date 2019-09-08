package com.util;

public enum Color {
    BLACK("black", 0x00, 0x00, 0x00),
    WHITE("white", 0xFF, 0xFF, 0xFF),

    LIGHT_RED("light red", 0xFF, 0xC0, 0xC0),
    RED("red", 0xFF, 0x00, 0x00),
    DARK_RED("dark red", 0xC0, 0x00, 0x00),
    
    LIGHT_YELLOW("light yellow", 0xFF, 0xFF, 0xC0),
    YELLOW("yellow", 0xFF, 0xFF, 0x00),
    DARK_YELLOW("dark yellow", 0xC0, 0xC0, 0x00),
    
    LIGHT_GREEN("light green", 0xC0, 0xFF, 0xC0),
    GREEN("green", 0x00, 0xFF, 0x00),
    DARK_GREEN("dark green", 0x00, 0xC0, 0x00),
    
    LIGHT_CYAON("light cyan", 0xC0, 0xFF, 0xFF),
    CYAN("cyan", 0x00, 0xFF, 0xFF),
    DARK_CYAN("dark cyan", 0x00, 0xC0, 0xC0),
    
    LIGHT_BLUE("light blue", 0xC0, 0xC0, 0xFF),
    BLUE("blue", 0x00, 0x00, 0xFF),
    DARK_BLUE("dark blue", 0x00, 0x00, 0xC0),
    
    LIGHT_MAGENTA("light magenta", 0xFF, 0xC0, 0xFF),
    MAGENTA("magenta", 0xFF, 0x00, 0xFF),
    DARK_MAGENTA("dark magenta", 0xC0, 0x00, 0xC0);

    private String name;
    private Integer red;
    private Integer blue;
    private Integer green;
    private String rgb;

    private Integer hue;
    private Integer light;

    Color(String name, Integer red, Integer green, Integer blue) {
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.rgb = Color.getColorFromRGB(red, green, blue);
        this.hue = setHueFromName(name);
        this.light = setLightFromName(name);
        // System.out.println(this.toString());
    }

    public String getName() {
        return this.name;
    }

    public String getRBG() {
        return this.rgb;
    }

    public Integer getRed() {
        return this.red;
    }

    public Integer getBlue() {
        return this.blue;
    }

    public Integer getGreen() {
        return this.green;
    }

    public Integer getHue() {
        return this.hue;
    }

    public Integer getLight() {
        return this.light;
    }

    
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(
            "{\n" +
            "\tname: " + this.name + "\n" +
            "\trgb: " + this.rgb + "\n" +
            "\thue: " + this.hue + "\n" +
            "\tlight: " + this.light + "\n" +
            "}"
        );
        return str.toString();
    }

    /**
     * 
     * @param name
     * @return
     */
    public Integer setHueFromName(String name) {
        if(name.contains("red")) {
            return 0;
        } else if(name.contains("yellow")) {
            return 1;
        } else if(name.contains("green")) {
            return 2;
        } else if(name.contains("cyan")) {
            return 3;
        } else if (name.contains("blue")) {
            return 4;
        } else if (name.contains("magenta")) {
            return 5;
        }
        return -1;
    }

    /**
     * 
     * @param name
     * @return
     */
    public Integer setLightFromName(String name) {
        if(name.contains("light")) {
            return 0;
        } else if(name.contains("dark")) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 
     * @param red
     * @param green
     * @param blue
     * @return
     * @throws ColorNotFoundException
     */
    public static Color getColorFromValues(Integer red, Integer green, Integer blue) throws ColorNotFoundException {
        String rgb = Color.getColorFromRGB(red, green, blue);

        for(Color c : Color.values()) {
            if(c.rgb.equals(rgb)) {
                return c;
            }
        }

        throw new ColorNotFoundException("Value " + rgb + " is not a defined color!");
    }

    /**
     * 
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static String getColorFromRGB(Integer red, Integer green, Integer blue) {
        return String.format("#%02x%02x%02x", red, green, blue);
    }
}