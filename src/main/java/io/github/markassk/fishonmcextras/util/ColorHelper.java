package io.github.markassk.fishonmcextras.util;

import me.shedaniel.math.Color;

public class ColorHelper {
    public static int getColorFromNbt(String value) {
        try {
            int r = Integer.parseInt(value.substring(0, value.indexOf(",")).trim());
            int g = Integer.parseInt(value.substring(value.indexOf(",") + 1, TextHelper.ordinalIndexOf(value, ",", 2)).trim());
            int b = Integer.parseInt(value.substring(value.lastIndexOf(",") + 1).trim());
            return Color.ofRGB(r, g, b).getColor();
        } catch (Exception e) {
            return Color.ofRGB(255, 255, 255).getColor();
        }
    }
}
