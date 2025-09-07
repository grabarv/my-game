package game;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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


    Map<String, Image> imageMap;

    NVGPaint imgPaint;

     private HUDResults hudResult;

    private boolean canCloseSettingsInGame = false;

    private boolean canOpenSettingsInGame = false;



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

        imageMap = new HashMap<>();

        int backgroundImage = nvgCreateImage(vg, "resources/textures/map_objects/struct_grass.png", NVG_IMAGE_REPEATX | NVG_IMAGE_REPEATY);
        if (backgroundImage == 0) {
            throw new Exception("Could not load image.");
        }
        imageMap.put("background", new Image(Utils.getImageSize("resources/textures/map_objects/struct_grass.png"), backgroundImage));

        int settingsImage = nvgCreateImage(vg, "resources/textures/settings.png", NVG_IMAGE_REPEATX | NVG_IMAGE_REPEATY);
        if (settingsImage == 0) {
            throw new Exception("Could not load image.");
        }
        imageMap.put("settings", new Image(Utils.getImageSize("resources/textures/settings.png"), settingsImage));

        for(String item : items) {
            if(item == null || item.isEmpty()) {
                continue;
            }
            addImageToMap(item);
        }

        imgPaint = NVGPaint.calloc();
    }

    private void addImageToMap(String item) throws Exception {
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


    public void startRender(Window window) {
        nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

        glfwGetCursorPos(window.getWindowHandle(), posx, posy);
    }

    public void endRender(Window window) {
        nvgEndFrame(vg);

        // Restore state
        window.restoreState();
    }

    public void renderHeath(Window window, int health, int maxHealth) {
        float x, y, width, height;
        x = 0;
        y = window.getHeight() - 50;
        width = window.getWidth() / 5f;
        height = 40;
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, 5);
        NVGColor color = NVGColor.create();

        nvgFillColor(vg, rgba(128, 128, 128, 150, color));
        nvgFill(vg);

        nvgStrokeColor(vg, rgba(0, 0, 0, 256, color));
        nvgStrokeWidth(vg, 2.0f);
        nvgStroke(vg);

        float healthWidth = (health / (float) maxHealth) * (width - 4);
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 2, y + 2, healthWidth, height - 4, 5);
        nvgFillColor(vg, rgba(255, 0, 0, 200, color));
        nvgFill(vg);

        nvgFontSize(vg, 24.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER);
        nvgFillColor(vg, rgba(255, 255, 255, 255, color));
        String healthText = "Health: " + health + " / " + maxHealth;
        nvgText(vg, x + width / 2, y + height / 2, healthText);


    }
    public void renderStamina(Window window, int stamina, int maxStamina) {
        float x, y, width, height;
        x = 0;
        y = window.getHeight() - 50 - 60;
        width = window.getWidth() / 5f;
        height = 40;
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, 5);
        NVGColor color = NVGColor.create();

        nvgFillColor(vg, rgba(128, 128, 128, 150, color));
        nvgFill(vg);

        nvgStrokeColor(vg, rgba(0, 0, 0, 256, color));
        nvgStrokeWidth(vg, 2.0f);
        nvgStroke(vg);

        float staminaWidth = (stamina / (float) maxStamina) * (width - 4);
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 2, y + 2, staminaWidth, height - 4, 5);
        nvgFillColor(vg, rgba(0, 0, 156, 200, color));
        nvgFill(vg);

        nvgFontSize(vg, 24.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER);
        nvgFillColor(vg, rgba(255, 255, 255, 255, color));
        String healthText = "Stamina: " + stamina + " / " + maxStamina;
        nvgText(vg, x + width / 2, y + height / 2, healthText);
    }

    public void renderSettingsButton(Window window, MouseInput mouseInput) throws Exception {
        float width;
        float height;
        float x;
        float y;

        width = 80;
        height = 80;
        x = window.getWidth() - 90;
        y = window.getHeight() - 90;

        Image image = imageMap.get("settings");
        if(image == null) {
            throw new Exception("Image not found in map: " + "settings");
        }

        // Draw transparent rectangle with only border
/*        nvgBeginPath(vg);
        nvgRect(vg, x, y, width, height);
        nvgStrokeColor(vg, rgba(0x20, 0x20, 0x20, 200, NVGColor.create()));
        nvgStrokeWidth(vg, 4.0f);
        nvgStroke(vg);*/

        // Draw the settings image centered and scaled to fit inside the button

        boolean hover = isHoveringButton(x, y, width, height);
        if(!mouseInput.isLeftButtonPressed()) {
            canOpenSettingsInGame = true;
        }
        if(canOpenSettingsInGame && hover && mouseInput.isLeftButtonPressed()) {
            hudResult.openSettingsInGame = true;
            canCloseSettingsInGame = false;
        }
        float paddingFactor = hover ? 0.05f : 0.1f;
        float padding = width * paddingFactor;
        float imgW = width - 2 * padding;
        float imgH = height - 2 * padding;
        float imgX = x + padding;
        float imgY = y + padding;
        nvgBeginPath(vg);
        nvgRect(vg, imgX, imgY, imgW, imgH);
        nvgImagePattern(vg, imgX, imgY, imgW, imgH, 0.0f, image.textureId, 1.0f, imgPaint);
        nvgFillPaint(vg, imgPaint);
        nvgFill(vg);

    }

    public void renderSettingsMenu(Window window, MouseInput mouseInput) {
        float width;
        float height;
        float x;
        float y;

        width = window.getWidth() / 3f;
        height = window.getHeight() / 2f;
        x = window.getWidth() / 2f - width / 2f;
        y = window.getHeight() / 2f - height / 2f;

        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, width, height, 10);
        nvgFillColor(vg, rgba(0x20, 0x20, 0x20, 200, NVGColor.create()));
        nvgFill(vg);

        nvgStrokeColor(vg, rgba(0x50, 0x50, 0x50, 255, NVGColor.create()));
        nvgStrokeWidth(vg, 4.0f);
        nvgStroke(vg);

        nvgFontSize(vg, 30.0f);
        nvgFontFace(vg, FONT_NAME);
        nvgTextAlign(vg, NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER);
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, NVGColor.create()));
        nvgText(vg, x + width / 2f, y + 40, "Settings Menu");
        if(!mouseInput.isLeftButtonPressed()) {
            canCloseSettingsInGame = true;
        }
        if(canCloseSettingsInGame && !isHoveringButton(x, y, width, height) && mouseInput.isLeftButtonPressed()) {
            hudResult.openSettingsInGame = false;
            canOpenSettingsInGame = false;
        }

    }

    /**
     * Renders the player's inventory as a horizontal grid at the bottom center of the screen.
     * Each cell displays an item icon and its quantity.
     * @param window the application window
     * @param inventoryItems array of item texture paths in the inventory (max 10 items)
     */
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
        }


    }

    /**
     * Renders a single inventory cell with an item icon and quantity.
     * @param x the x position of the cell
     * @param y the y position of the cell
     * @param width the width of the cell
     * @param height the height of the cell
     * @param item the item texture path to display
     * @param quantity the quantity of the item
     * @param backgroundColor the background color of the cell
     * @throws Exception if image loading fails
     */
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

        // Draw the item image centered and scaled to fit inside the cell with padding
        float padding = width * 0.15f; // 15% padding
        float imgW = width - 2 * padding;
        float imgH = height - 2 * padding;
        float imgX = x + padding;
        float imgY = y + padding;
        nvgBeginPath(vg);
        nvgRect(vg, imgX, imgY, imgW, imgH);
        nvgImagePattern(vg, imgX, imgY, imgW, imgH, 0.0f, image.textureId, 1.0f, imgPaint);
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

        nvgBeginPath(vg);
        // Background image
        nvgRect(vg, 0,0, window.getWidth(), window.getHeight());
        nvgImagePattern(vg, 0, 0, imageMap.get("background").sizeInPixels[0], imageMap.get("background").sizeInPixels[1], 0.0f, imageMap.get("background").textureId, 1.0f, imgPaint);
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
    /**
     * Checks if the mouse is hovering over a button.
     * @param x position x of button
     * @param y position y of button
     * @param width width of button
     * @param height height of button
     * @return true if hovering, false otherwise
     */

    public boolean isHoveringButton(float x, float y, float width, float height) {
        return (posx != null && posy != null) &&
                (posx.get(0) > x && posx.get(0) < x + width && posy.get(0) > y && posy.get(0) < y + height);
    }


    /**
     * Converts RGBA values to NVGColor.
     * @param r red value (0-255)
     * @param g green value (0-255)
     * @param b blue value (0-255)
     * @param a alpha value (0-255)
     * @param colour NVGColor object to store the result
     * @return the NVGColor object with the specified RGBA values
     */

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
        public boolean openSettingsInGame;
        public HUDResults() {
            startGame = false;
            loadGame = false;
            openSettings = false;
            exitGame = false;
            openSettingsInGame = false;
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
