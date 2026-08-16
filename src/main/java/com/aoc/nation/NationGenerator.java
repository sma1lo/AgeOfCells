package com.aoc.nation;

import com.aoc.config.Config;

import java.util.*;

public class NationGenerator {
    private static final Random random = new Random();
    private static int nameCounter = 0;

    public static void generate(List<Nation> nationsList) {
        nationsList.clear();
        nameCounter = 0;

        int nationCount = Config.get().nations();

        for (int i = 0; i < nationCount; i++) {
            String name = generateLetterName();
            long startingPower = 50 + random.nextInt(100);

            nationsList.add(new Nation(name, startingPower));
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
