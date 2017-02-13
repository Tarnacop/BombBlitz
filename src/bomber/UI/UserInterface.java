package bomber.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import bomber.game.Block;
import bomber.game.Game;
import bomber.game.Map;
import bomber.game.Maps;
import bomber.game.Response;

public class UserInterface extends Application{

	private static String appName;
	
	public UserInterface(String appName){
		
		UserInterface.appName = appName;
		launch();
	}
	
	public UserInterface(){
		//for JavaFX
	}

	@Override
	public void start(Stage primaryStage){

		primaryStage.setTitle(UserInterface.appName);
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                
            	List<Map> maps = Maps.getMaps();
            	System.out.println("Got maps");
            	Map map1 = maps.get(0);
            	System.out.println("Got map");
        		HashMap<Response, Integer> keymap = new HashMap<Response, Integer>();
        		keymap.put(Response.PLACE_BOMB, GLFW_KEY_SPACE);
        		keymap.put(Response.UP_MOVE, GLFW_KEY_UP);
        		keymap.put(Response.DOWN_MOVE, GLFW_KEY_DOWN);
        		keymap.put(Response.LEFT_MOVE, GLFW_KEY_LEFT);
        		keymap.put(Response.RIGHT_MOVE, GLFW_KEY_RIGHT);
        		
            	beginGame(map1, "player1", keymap);
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
	}

	public void beginGame(Map map, String playerName, HashMap<Response, Integer> keymap) {
		
		Game game = new Game(map, playerName, keymap);
	}
}
