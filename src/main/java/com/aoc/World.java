package com.aoc;

public class World {
    static final int WIDTH = 40;
    static final int HEIGHT = 20;
    static int [][] grid = new int [HEIGHT][WIDTH];

    public static void init() {
        generate();
    }

    public static void generate(){
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                sb.append("0");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
