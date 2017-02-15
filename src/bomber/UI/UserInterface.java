package bomber.UI;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import bomber.game.Block;
import bomber.game.Game;
import bomber.game.Map;
import bomber.game.Maps;
import bomber.game.Response;

public class UserInterface extends Application{

	private String appName;
	private SimpleStringProperty playerName;
	private Stage currentStage;
	private VBox mainMenu, settingsMenu, keyMenu, multiMenu, singleMenu;
	private Scene mainScene, settingsScene, keyScene, multiScene, singleScene;
	private TextField nameText;
	private Button nameBtn, singleBtn, multiBtn, settingsBtn, controlsBtn, audioBtn, graphicsBtn, startBtn;
	private Button backBtn1, backBtn2, backBtn3, backBtn4;
	private Button rightMapToggle, leftMapToggle, upAiToggle, downAiToggle;
	private Stack<Scene> previousScenes;
	private HashMap<Response, Integer> controls;
	private Map map;
	private Label displayName;
	private Label displayMap;
	private SimpleStringProperty mapName;
	
	public UserInterface(){
		//for JavaFX
		this.playerName = new SimpleStringProperty("Current Name:\nPlayer 1");
		Maps maps = new Maps();
		this.map = maps.getMaps().get(0);
		this.mapName = new SimpleStringProperty("Current Map:\n" + this.map.getName());
		this.controls = new HashMap<Response, Integer>();
		this.controls.put(Response.PLACE_BOMB, GLFW_KEY_SPACE);
		this.controls.put(Response.UP_MOVE, GLFW_KEY_UP);
		this.controls.put(Response.DOWN_MOVE, GLFW_KEY_DOWN);
		this.controls.put(Response.LEFT_MOVE, GLFW_KEY_LEFT);
		this.controls.put(Response.RIGHT_MOVE, GLFW_KEY_RIGHT);
		
	}
	
	public static void begin(){
		launch();
	}

	@Override
	public void start(Stage primaryStage){

		this.currentStage = primaryStage;
		primaryStage.setTitle(this.appName);
		
        mainMenu = new VBox(); 
        settingsMenu = new VBox();
        keyMenu = new VBox();
        multiMenu = new VBox();
        singleMenu = new VBox();
        
        mainScene = new Scene(mainMenu, 300, 250);
        settingsScene = new Scene(settingsMenu, 300, 250);
        keyScene = new Scene(keyMenu, 300, 250);
        multiScene = new Scene(multiMenu, 300, 250);
        singleScene = new Scene(singleMenu, 300, 250);
        
        previousScenes = new Stack<Scene>();
        
        nameText = new TextField("Enter Name");
        
        displayName = new Label();
        displayName.textProperty().bind(this.playerName);
        
        nameBtn = new Button("Set Name");
        nameBtn.setOnAction(e -> setName(nameText.getText()));
        
        //button to start the game
        startBtn = new Button("Start Game");
        startBtn.setOnAction(e -> beginGame(this.map, this.playerName.getValue(), this.controls));
        
        //back button
        backBtn1 = new Button("Back");
        backBtn1.setOnAction(e -> previous());
        
        backBtn2 = new Button("Back");
        backBtn2.setOnAction(e -> previous());
        
        backBtn3 = new Button("Back");
        backBtn3.setOnAction(e -> previous());
        
        backBtn4 = new Button("Back");
        backBtn4.setOnAction(e -> previous());
        
        singleBtn = new Button("Single Player");
        singleBtn.setOnAction(e -> advance(mainScene, singleScene));
        
        multiBtn = new Button("Multi Player");
        multiBtn.setOnAction(e -> advance(mainScene, multiScene));
        
        settingsBtn = new Button("Settings");
        settingsBtn.setOnAction(e -> advance(mainScene, settingsScene));
        
        controlsBtn = new Button("Control Options");
        controlsBtn.setOnAction(e -> advance(settingsScene, keyScene));
        
        audioBtn = new Button("Audio Options");
        
        graphicsBtn = new Button("Graphics Options");
        
        displayMap = new Label();
        displayMap.textProperty().bind(this.mapName);
        
        rightMapToggle = new Button("->");
        
        leftMapToggle = new Button("<-");
        
        upAiToggle = new Button("^");
        
        downAiToggle = new Button("v");
        
        addElements(mainMenu, displayName, nameText, nameBtn, singleBtn, multiBtn, settingsBtn);
        addElements(settingsMenu, controlsBtn, audioBtn, graphicsBtn, backBtn1);
        addElements(keyMenu, backBtn2);
        addElements(singleMenu, leftMapToggle, displayMap, rightMapToggle, upAiToggle, downAiToggle, startBtn, backBtn3);
        addElements(multiMenu, backBtn4);
        
        primaryStage.setScene(mainScene);
        primaryStage.show();
	}
	
	private void addElements(Pane pane, Node... elems){
		
		for(Node node : elems){
			pane.getChildren().add(node);
		}
	}

	private void previous() {
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		this.currentStage.setScene(this.previousScenes.pop());
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}
	
	private void advance(Scene thisScene, Scene nextScene) {
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		this.currentStage.setScene(nextScene);
		this.previousScenes.push(thisScene);
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}

	public void setName(String string){
		
		System.out.println("Set name to " + string);
		this.playerName.set("Current Name:\n" + string);
	}
	
	public void setControls(){
		
		this.controls = null;
	}
	
	public void beginGame(Map map, String playerName, HashMap<Response, Integer> controls) {
		
		Block[][] masterMap = this.map.getGridMap();
		int columnLength = masterMap[0].length;
		Block[][] arrayCopy = new Block[masterMap.length][columnLength];
		
		for(int x = 0; x < masterMap.length; x++){
			
			arrayCopy[x] = Arrays.copyOf(masterMap[x], columnLength);
		}
		
		Map mapCopy = new Map(map.getName(), arrayCopy);
		
		Game game = new Game(mapCopy, playerName, controls);
	}
}
