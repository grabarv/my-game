package game;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import engine.MouseInput;
import org.lwjgl.nanovg.NVGColor;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;
import utils.Utils;
import engine.Window;

public class Hud {

    private static final String FONT_NAME = "BOLD";

    private long vg;

    private NVGColor colour;

    private ByteBuffer fontBuffer;

    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private DoubleBuffer posx;

    private DoubleBuffer posy;


    int backgroundImage;

    Map<String, Image> imageMap;

    NVGPaint imgPaint;

     private HUDResults hudResult;



    public void init(Window window, String[] items) throws Exception {
        hudResult = new HUDResults();
        this.vg = window.getOptions().antialiasing ? nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES) : nvgCreate(NVG_STENCIL_STROKES);
        if (this.vg == NULL) {
            throw new Exception("Could not init nanovg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("resources/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        colour = NVGColor.create();

        posx = MemoryUtil.memAllocDouble(1);
        posy = MemoryUtil.memAllocDouble(1);

        backgroundImage = nvgCreateImage(vg, "resources/textures/map_objects/struct_grass.png", NVG_IMAGE_REPEATX | NVG_IMAGE_REPEATY);
        if (backgroundImage == 0) {
            throw new Exception("Could not load image.");
        }

        imageMap = new HashMap<>();

        for(String item : items) {
            if(item == null || item.isEmpty()) {
                continue;
            }
            int imageId = nvgCreateImage(vg, item, NVG_IMAGE_REPEATX | NVG_IMAGE_REPEATY);
            if (imageId == 0) {
                throw new Exception("Could not load image: " + item);
            }

            Image image = new Image(Utils.getImageSize(item), imageId);
            if(image.sizeInPixels == null) {
                throw new Exception("Could not get image size.");
            }
            imageMap.put(item.substring(item.lastIndexOf('/') + 1, item.lastIndexOf('.')), image);

        }

        imgPaint = NVGPaint.calloc();




    }

    private void startRender(Window window) {
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
    }

    private void endRender(Window window) {
        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }


    public void render(Window window) {
        startRender(window);

        // Upper ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 100, window.getWidth(), 50);
        nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
        nvgFill(vg);

        // Lower ribbon
        nvgBeginPath(vg);
        nvgRect(vg, 0, window.getHeight() - 50, window.getWidth(), 10);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
        int xcenter = 50;
        int ycenter = window.getHeight() - 75;
        int radius = 20;
        int x = (int) posx.get(0);
        int y = (int) posy.get(0);
        boolean hover = Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(vg);
        nvgCircle(vg, xcenter, ycenter, radius);
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
        nvgFill(vg);

        // Clicks Text
        nvgFontSize(vg, 25.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour));
        } else {
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

        }
//        nvgText(vg, 50, window.getHeight() - 87, String.format("%02d", counter));

        // Render hour text
        nvgFontSize(vg, 40.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
        nvgText(vg, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));

        endRender(window);
    }

    public void renderPlayerInventory(Window window, String[] inventoryItems) {
        float width;
        float height;
        float x;
        float y;

        x = 0;
        y = 0;
        width = window.getWidth();
        height = window.getHeight();

        if(inventoryItems != null && inventoryItems.length == 10) {

            startRender(window);

            int cellSize = 64;
            int padding = 10;
            float totalGridWidth = inventoryItems.length * cellSize + (inventoryItems.length - 1) * padding;
            x += (width - totalGridWidth) / 2f;
            y += height - cellSize - 5;

            NVGColor cellBackgroundColor = rgba(0x20, 0x20, 0x20, 200, NVGColor.create());

            for (int i = 0; i < inventoryItems.length; i++) {

                String itemTexturePath = inventoryItems[i]; // Assuming inventoryItems contains texture paths
                int quantity = 1; // Placeholder for item quantity

                try {
                    renderInventoryCell(x, y, cellSize, cellSize, itemTexturePath, quantity, cellBackgroundColor);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                x += cellSize + padding;
            }
            endRender(window);
        }


    }

    private void renderInventoryCell(float x, float y, int width, int height, String item, int quantity, NVGColor backgroundColor) throws Exception {
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, 10);
        // Transparent fill
        nvgFillColor(vg, backgroundColor);
        nvgFill(vg);

        if(item == null || item.isEmpty()) {
            return;
        }

        Image image = imageMap.get(item);
        if(image == null) {
            throw new Exception("Image not found in map: " + item);
        }

        nvgBeginPath(vg);
        nvgRect(vg, x + 0.1f * width,y + 0.1f * height, 0.8f * width, 0.8f * height);
        nvgImagePattern(vg, x, y, image.sizeInPixels[0], image.sizeInPixels[1], 0.0f, image.textureId, 1.0f, imgPaint);
        nvgFillPaint(vg, imgPaint);
        nvgFill(vg);

        nvgFontSize(vg, 20.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_BOTTOM | NVG_ALIGN_RIGHT);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, NVGColor.create()));
        nvgText(vg, x + width, y + height, "" + quantity);
    }

    /**
     * Renders the start window with buttons for starting a new game, loading a game, opening settings, and exiting.
     * @param window the application window
     * @param mouseInput the mouse input handler
     */

    public void renderStartWindow(Window window, MouseInput mouseInput) {

        float width;
        float height;
        float x;
        float y;

        x = 0;
        y = 0;
        width = 1024;
        height = 1024;

        startRender(window);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);

        nvgBeginPath(vg);
        // Background image
        nvgRect(vg, 0,0, window.getWidth(), window.getHeight());
        nvgImagePattern(vg, x, y, width, height, 0.0f, backgroundImage, 1.0f, imgPaint);
        nvgFillPaint(vg, imgPaint);
        nvgFill(vg);

        // Start button
        width = window.getWidth() / 8f;
        height = window.getHeight() / 16f;
        x = window.getWidth() / 2f - width / 2f;
        y = window.getHeight() / 2f - height / 2f * 7f;




        createButtonOnStartMenu(x, y, width, height, "START");

        if(isHoveringButton(x, y, width, height) && mouseInput.isLeftButtonPressed()) {


            hudResult.startGame = true;
        }

        y += height + 20;

        createButtonOnStartMenu(x, y, width, height, "LOAD");

        if(isHoveringButton(x, y, width, height) && mouseInput.isLeftButtonPressed()) {

            hudResult.loadGame = true;
        }

        y += height + 20;

        createButtonOnStartMenu(x, y, width, height, "SETTINGS");

        if(isHoveringButton(x, y, width, height) && mouseInput.isLeftButtonPressed()) {

            hudResult.openSettings = true;
        }
        y += height + 20;

        createButtonOnStartMenu(x, y, width, height, "EXIT");

        if(isHoveringButton(x, y, width, height) && mouseInput.isLeftButtonPressed()) {
            hudResult.exitGame = true;
        }


       /* x = window.getWidth() / 2f + 100;
        y = window.getHeight() / 2f - window.getHeight() / 16f;
        width = window.getWidth() / 8f;
        height = window.getHeight() / 8f;


        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, 30);
        nvgFillColor(vg, rgba(0xff, 0xff, 0x00, 500, rectColour));
        nvgFill(vg);*/

        endRender(window);
    }

    /**
     * Creates a button on the start menu.
     * @param x position x of button
     * @param y position y of button
     * @param width width of button
     * @param height height of button
     * @param text text to display on button
     *
     */
    public void createButtonOnStartMenu(float x, float y, float width, float height, String text) {
        boolean hover = (posx != null && posy != null) && (posx.get(0) > x && posx.get(0) < x + width && posy.get(0) > y && posy.get(0) < y + height);
        NVGColor rectColour = hover ? rgba(0x50, 0x50, 0x50, 255, NVGColor.create()) : rgba(0x50, 0x50, 0x50, 0, NVGColor.create());
        NVGColor borderColour = hover ? rgba(0x20, 0x20, 0x20, 255, NVGColor.create()) : rgba(0x20, 0x20, 0x20, 150, NVGColor.create());
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, 30);
        // Transparent fill
        nvgFillColor(vg, rectColour);
        nvgFill(vg);
        // Colored border
        nvgStrokeColor(vg, borderColour);
        nvgStrokeWidth(vg, 4.0f);
        nvgStroke(vg);

        nvgFontSize(vg, 40.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, rectColour));
        nvgText(vg, x + width/2f, y + height / 2f, text);

    }

    public boolean isHoveringButton(float x, float y, float width, float height) {
        return (posx != null && posy != null) &&
                (posx.get(0) > x && posx.get(0) < x + width && posy.get(0) > y && posy.get(0) < y + height);
    }


    private NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
        colour.r(r / 255.0f);
        colour.g(g / 255.0f);
        colour.b(b / 255.0f);
        colour.a(a / 255.0f);

        return colour;
    }

    public void cleanup() {
        imgPaint.free();
        nvgDelete(vg);
        if (posx != null) {
            MemoryUtil.memFree(posx);
        }
        if (posy != null) {
            MemoryUtil.memFree(posy);
        }
    }
    public class HUDResults {
        public boolean startGame;
        public boolean loadGame;
        public boolean openSettings;
        public boolean exitGame;
        public HUDResults() {
            startGame = false;
            loadGame = false;
            openSettings = false;
            exitGame = false;
        }
    }

    public HUDResults getHudResult() {
        return hudResult;
    }

    private class Image {
        int[] sizeInPixels;
        int textureId;
        public Image(int[] sizeInPixels, int textureId) {
            this.sizeInPixels = sizeInPixels;
            this.textureId = textureId;
        }
    }

}

