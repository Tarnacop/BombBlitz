package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import bomber.renderer.interfaces.RendererInterface;

public class Renderer implements RendererInterface {

	@Override
	public void init() throws Exception {
		
	} // END OF init METHOD

	@Override
	public void clear() {
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	} // END OF clear METHOD

} // END OF Renderer CLASS
