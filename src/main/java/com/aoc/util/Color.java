package com.aoc.util;

import com.googlecode.lanterna.TextColor;

public enum Color {
    RESET(TextColor.ANSI.DEFAULT),
    RED(TextColor.ANSI.RED),
    GREEN(TextColor.ANSI.GREEN),
    YELLOW(TextColor.ANSI.YELLOW),
    BLUE(TextColor.ANSI.BLUE),
    PURPLE(TextColor.ANSI.MAGENTA),
    CYAN(TextColor.ANSI.CYAN),
    WHITE(TextColor.ANSI.WHITE),
    BLACK(TextColor.ANSI.BLACK),
    BROWN(new TextColor.RGB(139, 69, 19)),
    PINK(TextColor.ANSI.MAGENTA_BRIGHT),
    ORANGE(new TextColor.RGB(255, 135, 0)),
    GRAY(TextColor.ANSI.BLACK_BRIGHT),
    BRIGHT_RED(TextColor.ANSI.RED_BRIGHT),
    BRIGHT_GREEN(TextColor.ANSI.GREEN_BRIGHT),
    BRIGHT_YELLOW(TextColor.ANSI.YELLOW_BRIGHT),
    BRIGHT_BLUE(TextColor.ANSI.BLUE_BRIGHT),
    BRIGHT_CYAN(TextColor.ANSI.CYAN_BRIGHT),
    LIME(new TextColor.RGB(118, 255, 0)),
    GOLD(new TextColor.RGB(255, 215, 0)),
    TEAL(new TextColor.RGB(0, 128, 128)),
    MAGENTA(TextColor.ANSI.MAGENTA_BRIGHT),
    DARK_BLUE(new TextColor.RGB(0, 0, 139));

    private final TextColor color;

    Color(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }
}
