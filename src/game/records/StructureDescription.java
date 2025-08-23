package game.records;

import org.joml.Vector2i;

public record StructureDescription(Vector2i size, String objFilePath, boolean canMoveThough) {
}
