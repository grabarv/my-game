package game.world;

import game.world.map.Block;
import game.world.map.Structure;
import game.world.map.Wall;
import org.joml.Vector2i;

import java.util.ArrayList;

public class GameObject {
    private final ArrayList<Block> blocks;
    private final ArrayList<Wall> walls;
    private final ArrayList<Structure> structures;
    private String name;
    private Vector2i size;
    private boolean clearArea;
    public GameObject() {
        blocks = new ArrayList<>();
        walls = new ArrayList<>();
        structures = new ArrayList<>();
        size = new Vector2i();
    }
    public GameObject(String name) {
        this();
        this.name = name;
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public Structure[] getStructures() {
        return structures.toArray(new Structure[0]);
    }

    public Block[] getBlocks() {
        return blocks.toArray(new Block[0]);
    }

    public Wall[] getWalls() {
        return walls.toArray(new Wall[0]);
    }

    public Vector2i getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setSize(Vector2i size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClearArea(boolean clearArea) {
        this.clearArea = clearArea;
    }

    public boolean isClearArea() {
        return clearArea;
    }
}
