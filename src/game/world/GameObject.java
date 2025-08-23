package game.world;

import engine.Utils;

import java.util.ArrayList;

public class GameObject {
    private final ArrayList<Block> blocks;
    private final ArrayList<Wall> walls;
    private String name;
    private int height;
    private int width;
    public GameObject() {
        blocks = new ArrayList<>();
        walls = new ArrayList<>();
    }
    public GameObject(String name) {
        this();
        this.name = name;
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public Block[] getBlocks() {
        return blocks.toArray(new Block[0]);
    }

    public Wall[] getWalls() {
        return walls.toArray(new Wall[0]);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getName() {
        return name;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setName(String name) {
        this.name = name;
    }
}
