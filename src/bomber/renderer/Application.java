package bomber.renderer;


import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

public class Application {
	
	private int width;
	private int height;
	private String title;
	private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err); 
	
	public Application(int width, int height, String title) {
		
		this.width = width;
		this.height = height;
		this.title = title;
		long windowID;
		
		// Init
		glfwSetErrorCallback(errorCallback);
		
		if(!glfwInit()) {
			
			throw new IllegalStateException("GLFW error occured!");
		}
		
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		windowID = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		
		if(windowID == MemoryUtil.NULL) {
			
			glfwTerminate();
			throw new RuntimeException("Failed to create the GLFW window!");
		}
		
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(windowID, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
		
		glfwShowWindow(windowID);
		
		glfwMakeContextCurrent(windowID);
		
		GL.createCapabilities();
		
		// Update part
		while(!glfwWindowShouldClose(windowID)) {
			
			glfwPollEvents();
			
			glClear(GL_COLOR_BUFFER_BIT);
			
			glBegin(GL_QUADS);
				
				glColor4f(1, 0, 0, 0);
				glVertex2f(-0.5f, 0.5f);
				
				glColor4f(0, 1, 0, 0);
				glVertex2f(0.5f, 0.5f);
				
				glColor4f(0, 0, 1, 0);
				glVertex2f(0.5f, -0.5f);
				
				glColor4f(1, 1, 1, 0);
				glVertex2f(-0.5f, -0.5f);
			
			glEnd();
			
			glfwSwapBuffers(windowID);
		}
		
		// Dispose part
		glfwDestroyWindow(windowID);
		glfwTerminate();
		errorCallback.free();
	} // END OF CONSTRUCTOR
} // END OF Application CLASS
