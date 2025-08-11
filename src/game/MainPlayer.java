package game;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.assimp.StaticMeshesLoader;

public class MainPlayer extends GameItem {

    private final String texturePath = "resources/textures/player.png";

    private final String modelPath = "resources/models/main_player.obj";

    public MainPlayer() throws Exception {
        super();
        Mesh playerMesh = StaticMeshesLoader.load(modelPath, "", 0)[0];
        Texture texture = new Texture(texturePath);
        Material material = new Material(texture, 0);
        playerMesh.setMaterial(material);
        setMesh(playerMesh);
        setPosition(0, 0, 0);
        setScale(0.5f);
    }


}
