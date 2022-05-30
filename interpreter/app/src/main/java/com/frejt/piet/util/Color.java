package com.frejt.piet.util;

import com.frejt.piet.exception.ColorNotFoundException;
import com.google.gson.GsonBuilder;

/**
 * Piet uses 20 distinct colours.
 * There are 6 hues with 3 lights each, which are related cyclically as shown
 * below, as well as white and black.
 * <br>
 * Hue Cycle:
 * <br>
 * &emsp;
 * {@code RED -> YELLOW -> GREEN -> CYAN -> BLUE -> MAGENTA -> RED}
 * <br>
 * Light Cycle:
 * <br>
 * &emsp;
 * {@code LIGHT -> NORMAL -> DARK -> LIGHT}
 */
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

    LIGHT_CYAN("light cyan", 0xC0, 0xFF, 0xFF),
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

    /**
     * Returns a json string representation of the Java object
     */
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    /**
     * Given a color name, return its hue level
     * 
     * @param name the name of the color
     * @return the hue's "step" in the Hue Cycle
     */
    public Integer setHueFromName(String name) {
        if (name.contains("red")) {
            return 0;
        } else if (name.contains("yellow")) {
            return 1;
        } else if (name.contains("green")) {
            return 2;
        } else if (name.contains("cyan")) {
            return 3;
        } else if (name.contains("blue")) {
            return 4;
        } else if (name.contains("magenta")) {
            return 5;
        }
        return -1;
    }

    /**
     * Given a color name, return it lightness level
     * 
     * @param name the name of the color
     * @return the light's "step" in the Light Cycle
     */
    public Integer setLightFromName(String name) {
        if (name.contains("light")) {
            return 0;
        } else if (name.contains("dark")) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Given a red, green, and blue value (0-255), returns the {@link Color}
     * matching those values
     * 
     * @param red   the amount of red in the color
     * @param green the amount of green in the color
     * @param blue  the amount of blue in the color
     * @return the Color represented by the provided RGB values
     * @throws ColorNotFoundException if the RGB values could not be matched to a Color
     */
    public static Color getColorFromValues(Integer red, Integer green, Integer blue) throws ColorNotFoundException {
        String rgb = Color.getColorFromRGB(red, green, blue);

        for (Color c : Color.values()) {
            if (c.rgb.equals(rgb)) {
                return c;
            }
        }

        throw new ColorNotFoundException("Value " + rgb + " at is not a defined color!");
    }

    /**
     * Converts RGB (0-255) values into a hex string representing it.
     * 
     * @param red   the amount of red in the color
     * @param green the amount of green in the color
     * @param blue  the amount of blue in the color
     * @return a String representing the
     */
    public static String getColorFromRGB(Integer red, Integer green, Integer blue) {
        return String.format("#%02x%02x%02x", red, green, blue);
    }
}