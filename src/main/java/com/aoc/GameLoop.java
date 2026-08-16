package com.aoc;

import com.aoc.config.Config;
import com.aoc.util.Time;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class GameLoop {
    public static void run(Screen screen) throws InterruptedException, IOException {
        World.init();
        TextGraphics status = screen.newTextGraphics();

        while (true) {
            KeyStroke key = screen.pollInput();
            if (key != null) {
                if (key.getKeyType() == KeyType.Character &&
                    Character.toLowerCase(key.getCharacter()) == 'q') {
                    break;
                }
                if (key.getKeyType() == KeyType.EOF) {
                    break;
                }
            }

            Time.tick();
            World.update();

            screen.clear();

            status.setForegroundColor(TextColor.ANSI.WHITE);
            status.setBackgroundColor(TextColor.ANSI.DEFAULT);
            status.putString(new TerminalPosition(0, 0),
                "Game tick: " + Time.getCurrentTick() + " | Active Nations: " + World.getNations().size() + "   [Q] quit");

            World.generateGrid(screen);

            screen.refresh();
            Thread.sleep(Config.get().tickDelayMs());
        }
    }
}
