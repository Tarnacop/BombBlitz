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
	
	public TestGame(GameState state) {
		
		this.state = state;
		renderer = new Renderer();
	} // END OF CONSTRUCTOR

	@Override
	public void init(ScreenInterface screen) throws Exception {

		renderer.init(screen);

		// Coords of a triangle
		/*
		float[] positions = new float[] { 
				// V0
				0f, 100f, 
				// V1
				0f, 0f, 
				// V2
				100f, 0f, 
				// V3
				100f, 100f };
		int[] indices = new int[] { 0, 1, 3, 3, 1, 2, };
		float[] colours = new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, };
		meshConstructor = new Mesh(positions, colours, indices);
		*/
		// GameEntity entity = new GameEntity(meshConstructor);
		// gameEntities = new GameEntity[] { entity };
	} // END OF init METHOD

	// private boolean sign = true;

	private float angle = 0f;
	@Override
	public void update(float interval) {


		/*
		for (GameEntity gameEntity : gameEntities) {
			
			//float x = gameEntity.getPosition().x;
			//float y = gameEntity.getPosition().y;
			angle += 1f;
			if(angle > 360f) angle = 0f;
			//x += 1f;
			
			//gameEntity.setPosition(x, y);
			gameEntity.setRotation(angle);
		}*/
		
		state.getPlayers().get(0).getPos().translate(1, 0);

	} // END OF update METHOD

	@Override
	public void render(ScreenInterface screen) {

		renderer.render(screen, state);
	}

	@Override
	public void dispose() {

		renderer.dispose();
		/*for (GameEntity gameEntity : gameEntities) {
			gameEntity.getMesh().dispose();
		}*/
	} // END OF dispose METHOD
} // END OF TestGame CLASS
