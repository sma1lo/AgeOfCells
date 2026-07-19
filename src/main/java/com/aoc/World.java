package com.aoc;


import java.util.Random;

public class World {
    static final int WIDTH = 40;
    static final int HEIGHT = 20;
    static Random rand = new Random();
    static TerrainType [][] grid = new TerrainType[HEIGHT][WIDTH];

    public static void init() {
        fillWater();
        fillGround();
        generateGrid();
    }

    public static void generateGrid(){

        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                if(grid[y][x] == TerrainType.WATER){
                    sb.append("~");
                }
                else{
                    sb.append("0");
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public static void fillWater(){
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = TerrainType.WATER;
            }
        }
    }

    public static void fillGround(){
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if(rand.nextInt(100)< 50){
                    grid[y][x] = TerrainType.GROUND;
                }
            }
        }
    }
}
