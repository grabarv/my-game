package engine;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 120;
    public static final int TARGET_UPS = 30;

    private final Window window;
    private final Timer timer;
    private final IGameLogic gameLogic;
    private final MouseInput mouseInput;

    private String windowTitle;

    // Frame/Update counters
    private int fps, ups;
    private double fpsTimer;

    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, opts, gameLogic);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this.windowTitle = windowTitle;
        window = new Window(windowTitle, width, height, vSync, opts);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
        fps = 0;
        ups = 0;
        fpsTimer = timer.getTime();
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                running = update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }

            // Show FPS/UPS once per second
            printFpsUps();
        }
    }

    private void printFpsUps() {
        double currentTime = timer.getTime();
        if (currentTime - fpsTimer >= 1.0) {
            if (window.getWindowOptions().showFps) {
                window.setWindowTitle(windowTitle + " - " + fps + " FPS / " + ups + " UPS");
            }
            fps = 0;
            ups = 0;
            fpsTimer = currentTime;
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;

        // Sleep first (coarse-grained)
        while (timer.getTime() < endTime - 0.002) { // sleep until ~2ms before deadline
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }

        // Busy wait (fine-grained)
        while (timer.getTime() < endTime) {
            Thread.onSpinWait(); // Java 9+, lets CPU spin efficiently
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected boolean update(float interval) {
        ups++;
        return gameLogic.update(interval, mouseInput, window);

    }

    protected void render() {
        gameLogic.render(window, mouseInput);
        window.update();
        fps++;
    }
}
