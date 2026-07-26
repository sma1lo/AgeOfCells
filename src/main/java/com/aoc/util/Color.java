package com.aoc.util;

public enum Color {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    BLACK("\u001B[30m"),
    BROWN("\u001B[33;5;94m"),
    PINK("\u001B[95m"),
    ORANGE("\u001B[38;5;208m"),
    GRAY("\u001B[90m"),
    BRIGHT_RED("\u001B[91m"),
    BRIGHT_GREEN("\u001B[92m"),
    BRIGHT_YELLOW("\u001B[93m"),
    BRIGHT_BLUE("\u001B[94m"),
    BRIGHT_CYAN("\u001B[96m"),
    LIME("\u001B[38;5;118m"),
    GOLD("\u001B[38;5;220m"),
    TEAL("\u001B[38;5;30m"),
    MAGENTA("\u001B[35;1m"),
    DARK_BLUE("\u001B[38;5;19m");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
