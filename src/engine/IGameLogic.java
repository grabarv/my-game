package engine;

public interface IGameLogic {

    void init(Window window) throws Exception;
    
    void input(Window window, MouseInput mouseInput);

    boolean update(float interval, MouseInput mouseInput, Window window);
    
    void render(Window window, MouseInput mouseInput);
    
    void cleanup();
}