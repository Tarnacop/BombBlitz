package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

/**
 * The class containing a Screen and all its information
 * 
 * @author Alexandru Blinda
 * 
 */
public class Screen {

	private final String title;
	private int width;
	private int height;
	private long screenID;
	private boolean resized;
	private boolean vSync;
	private boolean fullScreen;
	private GLFWVidMode vidmode;

	/**
	 * Create a Screen with the given title, width, height and vsync
	 * 
	 * @param title
	 *            The title of the screen
	 * @param width
	 *            The width of the screen
	 * @param height
	 *            The height of the screen
	 * @param vSync
	 *            Use vsync for rendering
	 * @param fullScreen
	 *            Is the Screen fullScreen or window
	 */
	public Screen(String title, int width, int height, boolean vSync, boolean fullScreen) {

		this.width = width;
		this.height = height;
		this.title = title;
		this.vSync = vSync;
		this.fullScreen = fullScreen;
	}

	/**
	 * Get the state of the keyboard key with the given key code
	 * 
	 * @param keyCode
	 *            The given key Code
	 * @return The state
	 */
	public int getKeyState(int keyCode) {

		return glfwGetKey(this.screenID, keyCode);
	}

	/**
	 * Initialise the screen
	 */
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

		if (fullScreen) {

			screenID = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), MemoryUtil.NULL);
		} else {

			screenID = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		}
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

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.

		glfwSetKeyCallback(screenID, new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {

				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {

					glfwSetWindowShouldClose(window, true);

				}
			}

		});
		// Get the resolution of the primary monitor

		if (!fullScreen) {

			vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Centre the Screen
			glfwSetWindowPos(screenID, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		}
		// Bind the screen with the openGL context current
		glfwMakeContextCurrent(screenID);

		// If vsync is set to enabled, enable it
		if (isVsyncOn()) {

			// 60 FPS
			glfwSwapInterval(1);
		}

		// Make the screen visible
		glfwShowWindow(screenID);

		GL.createCapabilities();

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Support for transparencies
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glfwSetInputMode(this.screenID, GLFW_STICKY_KEYS, GLFW_TRUE);

		System.out.println("Initialized screen");
	}

	/**
	 * Set the clear colour using the red, green, blue and alpha values provided
	 * 
	 * @param red
	 *            The given red value
	 * @param green
	 *            The given green value
	 * @param blue
	 *            The given blue value
	 * @param alpha
	 *            The given alpha value
	 */
	public void setClearColour(float red, float green, float blue, float alpha) {

		glClearColor(red, green, blue, alpha);
	}

	/**
	 * Check if the screen should close
	 * 
	 * @return A boolean saying whether the screen should close or not
	 */
	public boolean screenShouldClose() {

		return glfwWindowShouldClose(screenID);
	}

	/**
	 * Get the title of the screen
	 * 
	 * @return The title of the screen
	 */
	public String getTitle() {

		return title;
	}

	/**
	 * Get the width of the screen
	 * 
	 * @return The width of the screen
	 */
	public int getWidth() {

		return width;
	}

	/**
	 * Get the height of the screen
	 * 
	 * @return The height of the screen
	 */
	public int getHeight() {

		return height;
	}

	/**
	 * Check whether the screen was resized or not
	 * 
	 * @return A boolean saying whether the screen was resized or not
	 */
	public boolean isResized() {

		return resized;
	}

	/**
	 * Check whether the screen uses vsync or not
	 * 
	 * @return A boolean saying whether the screen uses vsync or not
	 */
	public boolean isVsyncOn() {

		return vSync;
	}

	/**
	 * Set whether the screen was resized or not
	 * 
	 * @param resized
	 *            A boolean saying whether the screen was resized or not
	 */
	public void setResized(boolean resized) {

		this.resized = resized;
	}

	/**
	 * Set whether the screen uses vsync or not
	 * 
	 * @param vSync
	 *            A boolean saying whether the screen uses vsync or not
	 */
	public void setVsyncOn(boolean vSync) {

		this.vSync = vSync;
	}

	/**
	 * Set the viewport of the screen starting from originX, originY with the
	 * width and height
	 * 
	 * @param originX
	 *            The given originX
	 * @param originY
	 *            The given originY
	 * @param width
	 *            The given width
	 * @param height
	 *            The given height
	 */
	public void setViewport(int originX, int originY, int width, int height) {

		glViewport(originX, originY, width, height);
	}

	/**
	 * Get the id of the screen
	 * 
	 * @return The id of the screen
	 */
	public long getScreenID() {

		return screenID;
	}

	/**
	 * Close the screen
	 */
	public void close() {

		glfwSetWindowShouldClose(screenID, true);
	}

	/**
	 * Update the screen
	 */
	public void update() {

		glfwSwapBuffers(screenID);
		glfwPollEvents();
	}

}