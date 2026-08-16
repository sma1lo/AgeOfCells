package com.aoc;

import com.aoc.config.Config;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.Font;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        Config.load("config.yaml");

        Screen screen = null;
        try {
            DefaultTerminalFactory factory = new DefaultTerminalFactory();

            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("win")) {
                factory.setPreferTerminalEmulator(true);
                Font font = new Font("Consolas", Font.PLAIN, 14);
                if (!"Consolas".equals(font.getFamily())) {
                    font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
                }
                factory.setTerminalEmulatorTitle("Age of Cells");
                factory.setTerminalEmulatorFontConfiguration(
                    SwingTerminalFontConfiguration.newInstance(font));
                factory.setInitialTerminalSize(new TerminalSize(
                    Config.get().width() + 2,
                    Config.get().height() + 3
                ));
            } else {
                factory.setPreferTerminalEmulator(false);
            }

            screen = factory.createScreen();
            screen.startScreen();
            screen.setCursorPosition(null);

            final Screen s = screen;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (s != null) s.stopScreen();
                } catch (IOException ignored) {}
            }));

            GameLoop.run(screen);

        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException ignored) {}
            }
        }
    }
}
