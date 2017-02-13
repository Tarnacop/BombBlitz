package bomber.renderer.interfaces;

import bomber.renderer.Screen;

public interface GameInterface {

	
	public void init(Screen screen);
	
	public void update(float interval);
	
	public void render(Screen screen);
	
	public void input(Screen screen);
	
	public void dispose();
} // END OF GameInterface
