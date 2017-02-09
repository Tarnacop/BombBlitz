package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Optional;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import bomber.game.KeyboardState;
import bomber.game.Movement;
import bomber.game.Player;
import bomber.game.Response;

public class Screen {

	private final String title;
	private int width;
	private int height;
	private long screenID;
	private boolean resized;
	private boolean vSync;
	private GLFWVidMode vidmode;
	private HashMap<Response, Integer> controlScheme;
	private Player player;

	public Screen(int width, int height, String title, boolean vSync, HashMap<Response, Integer> controls, Player player) {

		this.player = player;
		this.controlScheme = controls;
		this.width = width;
		this.height = height;
		this.title = title;
		this.vSync = vSync;
	} // END OF CONSTRUCTOR

	public void init() {

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Set the Screen
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
													// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be
													// resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

		// Create the screen
		screenID = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (screenID == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup resize callback
		glfwSetWindowSizeCallback(screenID, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long screenHandler, int width, int height) {
				Screen.this.width = width;
				Screen.this.height = height;
				Screen.this.setResized(true);
			}
		});

		// Get the resolution of the primary monitor
		vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Center the Screen
		glfwSetWindowPos(screenID, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		
		// Bind the screen with the openGL context current
		glfwMakeContextCurrent(screenID);
		
		// If vsync is set to enabled, enable it
		if(isVsyncOn()) {
			
			// 30 FPS
			glfwSwapInterval(2);
		}

		// Make the screen visible
		glfwShowWindow(screenID);
		
		GL.createCapabilities();
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glfwSetInputMode(this.screenID, GLFW_STICKY_KEYS, GLFW_TRUE);

		
	} // END OF init METHOD

	public boolean input(boolean bombPressed){
		
		System.out.println("polling the keyboard");
		
		KeyboardState keyState = this.player.getKeyState();
		
		int state = GLFW_RELEASE;
			
			//System.out.println("Checking again...");
			
			//check for bomb
			if(getKey(Response.PLACE_BOMB).isPresent()){
				state = glfwGetKey(this.screenID, getKey(Response.PLACE_BOMB).get());
			}
			if(state == GLFW_PRESS && !bombPressed){
			    keyState.setBomb(true);
			    bombPressed = true;
			    state = GLFW_RELEASE;
			}else if(state == GLFW_RELEASE && bombPressed){
				bombPressed = false;
			}
			
			//Check for up
			
			if(getKey(Response.UP_MOVE).isPresent()){
				state = glfwGetKey(this.screenID, getKey(Response.UP_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.UP);
			    state = GLFW_RELEASE;
			}
			
			//check for down
			if(getKey(Response.DOWN_MOVE).isPresent()){
				state = glfwGetKey(this.screenID, getKey(Response.DOWN_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.DOWN);
			    state = GLFW_RELEASE;
			}
			
			//check for left
			if(getKey(Response.LEFT_MOVE).isPresent()){
				state = glfwGetKey(this.screenID, getKey(Response.LEFT_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.LEFT);
			    state = GLFW_RELEASE;
			}
			
			//check for right
			if(getKey(Response.RIGHT_MOVE).isPresent()){
				state = glfwGetKey(this.screenID, getKey(Response.RIGHT_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.RIGHT);
			    state = GLFW_RELEASE;
			}
			
			return bombPressed;
	}
	
	private Optional<Integer> getKey(Response r){
		if(this.controlScheme.containsKey(r)){
			return Optional.of(this.controlScheme.get(r));
		}
		return Optional.empty();
	}
	
	public void setClearColour(float red, float green, float blue, float alpha) {
		
		glClearColor(red, green, blue, alpha);
	} // END OF setClearColour

	public boolean screenShouldClose() {
		
		return glfwWindowShouldClose(screenID);
	} // END OF screenShouldClose METHOD

	public String getTitle() {
		
		return title;
	} // END OF getTitle METHOD

	public int getWidth() {
		
		return width;
	} // END OF getWidth METHOD

	public int getHeight() {
		
		return height;
	} // END OF getHeight METHOD

	public boolean isResized() {
		
		return resized;
	} // END OF isResized METHOD

	public boolean isVsyncOn() {

		return vSync;
	} // END OF isVsyncOn METHOD

	public void setResized(boolean resized) {
		
		this.resized = resized;
	} // END OF setResized METHOD

	public void setVsyncOn(boolean vSync) {
		
		this.vSync = vSync;
	} // END OF setVsyncOn METHOD
	
	public void setViewport(int originX, int originY, int width, int height) {
		
		glViewport(originX, originY, width, height);
	} // END OF setViewport METHOD
	
	public long getScreenID() {
		
		return screenID;
	} // END OF getScreenID METHOD
	
	public void close() {
		
		glfwSetWindowShouldClose(screenID, true);
	} // END OF close METHOD
	
	public void update() {
		
        glfwSwapBuffers(screenID);
        glfwPollEvents();
    } // END OF update METHOD

} // END OF Screen CLASS
