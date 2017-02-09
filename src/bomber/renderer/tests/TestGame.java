package bomber.renderer.tests;

import bomber.game.GameState;
import bomber.renderer.Renderer;
import bomber.renderer.interfaces.GameLogicInterface;
import bomber.renderer.interfaces.ScreenInterface;
import bomber.renderer.shaders.Mesh;
import bomber.renderer.utils.GameEntity;

public class TestGame implements GameLogicInterface {

	// private float colour = 0f;

	private final Renderer renderer;
	private Mesh meshConstructor;

	// private GameEntity[] gameEntities;
	private GameState state;

	public TestGame() {

		// For the moemnt
		state = new GameState(null, null, null);
		renderer = new Renderer();
	} // END OF CONSTRUCTOR

	@Override
	public void init(ScreenInterface screen) throws Exception {

		renderer.init(screen);

		// Coords of a triangle
		float[] colours = new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, };
		meshConstructor = new Mesh(0, 0, 64, 64, colours);
		
		// Add the players / blocks / bombs etc.
		// GameEntity entity = new GameEntity(meshConstructor);
		// gameEntities = new GameEntity[] { entity };
	} // END OF init METHOD

	// private boolean sign = true;

	@Override
	public void update(float interval) {

		
		// Update everything you need to update
		// For example physics.
		
		/*
		for (GameEntity gameEntity : gameEntities) {
			
			float x = gameEntity.getPosition().x;
			float y = gameEntity.getPosition().y;
			x += 1f;
			y += 1f;
			
			gameEntity.setPosition(x, y);
		}*/

	} // END OF update METHOD

	@Override
	public void render(ScreenInterface screen) {

		// Render the state by passing it to the renderer
		renderer.render(screen, state);
	}

	@Override
	public void dispose() {

		renderer.dispose();
		
		// TODO - dispose the get Mesh
		
		/*for (GameEntity gameEntity : gameEntities) {
			gameEntity.getMesh().dispose();
		}*/
	} // END OF dispose METHOD
} // END OF TestGame CLASS
