package game.records;

import org.joml.Vector2i;
import org.joml.Vector3f;

public record StructureDescription(Vector2i size, String objFilePath, boolean canMoveThough, Vector3f modelSize) {
}
