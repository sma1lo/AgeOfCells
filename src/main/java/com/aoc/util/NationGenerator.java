package com.aoc.util;

import com.aoc.config.Config;
import com.aoc.nation.Nation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class NationGenerator {
    private static int nameCounter = 0;

    private NationGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void generate(List<Nation> nationsList) {
        nationsList.clear();
        nameCounter = 0;
        int nationCount = Config.get().nations();
        List<Color> colorPool = new ArrayList<>(Arrays.asList(Nation.AVAILABLE_COLORS));
        Collections.shuffle(colorPool, ThreadLocalRandom.current());
        int colorIndex = 0;

        for (int i = 0; i < nationCount; i++) {
            String name = generateLetterName();
            long startingPower = 50 + Rng.nextInt(100);

            Color nationColor = colorPool.get(colorIndex % colorPool.size());
            colorIndex++;

            nationsList.add(new Nation(name, startingPower, nationColor));
        }
    }

    private static String generateLetterName() {
        nameCounter++;
        return generateNameFromNumber(nameCounter);
    }

    private static String generateNameFromNumber(int number) {
        StringBuilder name = new StringBuilder();
        while (number > 0) {
            number--;
            char letter = (char) ('A' + (number % 26));
            name.insert(0, letter);
            number = number / 26;
        }
        return name.toString();
    }
}
