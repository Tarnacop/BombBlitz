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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import bomber.game.Block;
import bomber.game.Game;
import bomber.game.Map;
import bomber.game.Maps;
import bomber.game.Response;

public class UserInterface extends Application{

	private String appName, playerName;
	private Stage currentStage;
	private VBox pane1, pane2;
	private Scene scene1, scene2;
	private Button startBtn;
	private Button backBtn;
	private Scene previousScene;
	
	public UserInterface(String appName){
		
		this.appName = appName;
		launch();
	}
	
	public UserInterface(){
		//for JavaFX
	}

	@Override
	public void start(Stage primaryStage){

		this.currentStage = primaryStage;
		primaryStage.setTitle(this.appName);
        startBtn = new Button();
        startBtn.setText("Next scene");
        startBtn.setOnAction(e -> advance(e, scene1));
        backBtn = new Button();
        backBtn.setText("Back");
        backBtn.setOnAction(e -> previous(e));
        pane1 = new VBox(); 
        pane2 = new VBox();
        //StackPane root = new StackPane();
        pane1.getChildren().add(startBtn);
        pane2.getChildren().add(backBtn);
        scene1 = new Scene(pane1, 300, 250);
        scene2 = new Scene(pane2, 300, 250);
        primaryStage.setScene(scene1);
        primaryStage.show();
	}

	private void previous(ActionEvent e) {
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		this.currentStage.setScene(this.previousScene);
		this.previousScene = null;
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}
	
	private void advance(ActionEvent e, Scene previousScene) {
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		this.currentStage.setScene(scene2);
		this.previousScene = previousScene;
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}

	public void setName(){
		
	}
	
	public void setControls(){
		
		HashMap<Response, Integer> keymap = new HashMap<Response, Integer>();
		keymap.put(Response.PLACE_BOMB, GLFW_KEY_SPACE);
		keymap.put(Response.UP_MOVE, GLFW_KEY_UP);
		keymap.put(Response.DOWN_MOVE, GLFW_KEY_DOWN);
		keymap.put(Response.LEFT_MOVE, GLFW_KEY_LEFT);
		keymap.put(Response.RIGHT_MOVE, GLFW_KEY_RIGHT);
		
	}
	
	public void setMap(){
		
		List<Map> maps = Maps.getMaps();
    	Map map1 = maps.get(0);
	}
	
	public void beginGame(Map map, String playerName, HashMap<Response, Integer> keymap) {
		
		Game game = new Game(map, playerName, keymap);
	}
}
