package bomber.renderer.tests;

import bomber.renderer.Renderer;
import bomber.renderer.interfaces.GameLogicInterface;
import bomber.renderer.interfaces.ScreenInterface;

public class TestGame implements GameLogicInterface {

	private float colour = 0f;

	private final Renderer renderer;

	public TestGame() {

		renderer = new Renderer();
	} // END OF CONSTRUCTOR

	@Override
	public void init() throws Exception {

		renderer.init();
	} // END OF init METHOD

	private boolean sign = true;

	@Override
	public void update(float interval) {

		/*
		if (sign == true)
			colour = colour + 0.01f;
		else
			colour = colour - 0.01f;
		if (colour > 1f) {

			colour = 1f;
			sign = false;
		}
		if (colour < 0f) {

			colour = 0f;
			sign = true;
		}
		 */
	} // END OF update METHOD

	@Override
	public void render(ScreenInterface screen) {
		
		/*
		if (screen.isResized()) {
			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
		}
		
		screen.setClearColour(colour, colour, colour, 0.0f);*/
		renderer.render(screen);
	}
	
	@Override
	public void dispose() {
		
		renderer.dispose();
	} // END OF dispose METHOD
} // END OF TestGame CLASS
