package game.records;

import org.joml.Vector2i;
import org.joml.Vector3f;

public record StructureDescription(Vector2i size, String objFilePath, boolean canMoveThough, boolean canStandOn, Vector3f modelSize) {

}
