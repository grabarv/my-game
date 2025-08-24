package engine.items;

import org.joml.Vector4f;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import utils.loaders.assimp.StaticMeshesLoader;

public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super(false);
        Mesh skyBoxMesh = StaticMeshesLoader.load(objModel, "")[0];
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0f);
    }

    public SkyBox(String objModel, Vector4f colour) throws Exception {
        super(false);
        Mesh skyBoxMesh = StaticMeshesLoader.load(objModel, "", 0)[0];
        Material material = new Material(colour, 0);
        skyBoxMesh.setMaterial(material);
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }
}
