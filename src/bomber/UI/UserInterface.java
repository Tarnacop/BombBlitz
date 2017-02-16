package bomber.UI;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import bomber.game.Block;
import bomber.game.Game;
import bomber.game.Map;
import bomber.game.Maps;
import bomber.game.Response;
import bomber.networking.ClientNetInterface;
import bomber.networking.ClientServerLobbyRoom;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ClientThread;

public class UserInterface extends Application implements ClientNetInterface{

	private final String appName = "Bomb Blitz v1";
	private SimpleStringProperty playerName;
	private Stage currentStage;
	private BorderPane mainMenu;
	private VBox settingsMenu, keyMenu, multiMenu, serverMenu, singleMenu;
	private Scene mainScene, settingsScene, keyScene, multiScene, serverScene, singleScene;
	private TextField nameText, ipText;
	private Button nameBtn, singleBtn, multiBtn, settingsBtn, controlsBtn, audioBtn, graphicsBtn, startBtn;
	private Button backBtn1, backBtn2, backBtn3, backBtn4;
	private Button rightMapToggle, leftMapToggle, upAiToggle, downAiToggle;
	private Button connectBtn;
	private Stack<Scene> previousScenes;
	private HashMap<Response, Integer> controls;
	private Map map;
	private Label displayName;
	private Label displayMap;
	private SimpleStringProperty mapName;
	private Label currentNameLabel;
	private Label currentMapLabel;
	private TextField portNum;
	private HBox namePane;
	private VBox currentName;
	private VBox nameSetter;
	
	private final String font = "Arial";
	private VBox singleButtonPane;
	private Pane imagePane;
	
	public UserInterface(){
		//for JavaFX
		this.playerName = new SimpleStringProperty("Player 1");
		Maps maps = new Maps();
		this.map = maps.getMaps().get(1);
		this.mapName = new SimpleStringProperty(this.map.getName());
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
		this.currentStage.setMinHeight(600);
		this.currentStage.setMinWidth(800);
		primaryStage.setTitle(this.appName);
		
        mainMenu = new BorderPane(); 
        
        namePane = new HBox();
        
        settingsMenu = new VBox();
        keyMenu = new VBox();
        multiMenu = new VBox();
        serverMenu = new VBox();
        singleMenu = new VBox();
        
        mainScene = new Scene(mainMenu, 750, 500);
        settingsScene = new Scene(settingsMenu, 300, 250);
        keyScene = new Scene(keyMenu, 300, 250);
        multiScene = new Scene(multiMenu, 300, 250);
        serverScene = new Scene(serverMenu, 300, 250);
        singleScene = new Scene(singleMenu, 300, 250);
        
        previousScenes = new Stack<Scene>();
        
        nameText = new TextField("Enter Name");
        ipText = new TextField("Enter IP Address");
        portNum = new TextField("Enter Port Number");
        
        connectBtn = new Button("Connect");
        connectBtn.setOnAction(e -> connect(ipText.getText(), Integer.parseInt(portNum.getText())));
        
        currentNameLabel = new Label("Current Name:");
        currentNameLabel.setFont(Font.font(font, FontWeight.BOLD, 20));
        displayName = new Label();
        displayName.setFont(Font.font(font, FontWeight.BOLD, FontPosture.ITALIC, 20));
        displayName.textProperty().bind(this.playerName);
        
        nameBtn = new Button("Set Name");
        nameBtn.setPrefWidth(200);
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
        singleBtn.setPrefHeight(Integer.MAX_VALUE);
        singleBtn.setOnAction(e -> advance(mainScene, singleScene));
        
        multiBtn = new Button("Multi Player");
        multiBtn.setPrefHeight(Integer.MAX_VALUE);
        multiBtn.setOnAction(e -> advance(mainScene, multiScene));
        
        settingsBtn = new Button("Settings");
        settingsBtn.setPrefWidth(Integer.MAX_VALUE);
        settingsBtn.setOnAction(e -> advance(mainScene, settingsScene));
        
        controlsBtn = new Button("Control Options");
        controlsBtn.setOnAction(e -> advance(settingsScene, keyScene));
        
        audioBtn = new Button("Audio Options");
        
        graphicsBtn = new Button("Graphics Options");
        
        currentMapLabel = new Label("Current Map:");
        displayMap = new Label();
        displayMap.textProperty().bind(this.mapName);
        
        rightMapToggle = new Button("->");
        
        leftMapToggle = new Button("<-");
        
        upAiToggle = new Button("^");
        
        downAiToggle = new Button("v");
        
        currentName = new VBox();
        currentName.setSpacing(10);
        currentName.getChildren().addAll(currentNameLabel, displayName);
        
        nameSetter = new VBox();
        nameSetter.setSpacing(0);
        nameSetter.getChildren().addAll(nameText, nameBtn);
        
        namePane.setSpacing(100);
        namePane.getChildren().addAll(currentName, nameSetter);
        namePane.setAlignment(Pos.CENTER);
        
        singleButtonPane = new VBox();
        singleButtonPane.setAlignment(Pos.CENTER);
        singleButtonPane.getChildren().add(singleBtn);
        
        
        Image mainImage = new Image("resources/images/titlescreen.png");
        ImageView mainImageView = new ImageView(mainImage);
        imagePane = new Pane();
        imagePane.getChildren().add(mainImageView); 
        imagePane.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        mainImageView.fitWidthProperty().bind(imagePane.widthProperty()); 
        mainImageView.fitHeightProperty().bind(imagePane.heightProperty());
        
        mainMenu.setCenter(imagePane);
        mainMenu.setTop(namePane);
        mainMenu.setLeft(singleButtonPane);
        mainMenu.setRight(multiBtn);
        mainMenu.setBottom(settingsBtn);
        
        //addElements(mainMenu, currentNameLabel, displayName, nameText, nameBtn, singleBtn, multiBtn, settingsBtn);
        addElements(settingsMenu, controlsBtn, audioBtn, graphicsBtn, backBtn1);
        addElements(keyMenu, backBtn2);
        addElements(singleMenu, leftMapToggle, currentMapLabel, displayMap, rightMapToggle, upAiToggle, downAiToggle, startBtn, backBtn3);
        addElements(multiMenu, ipText, portNum, connectBtn, backBtn4);
        
        primaryStage.setScene(mainScene);
        primaryStage.show();
        
	}
	
	private void resetFields(){
		
		nameText.setText("Enter Name");
		ipText.setText("Enter IP Address");
		portNum.setText("Enter Port Number");
	}
	
	private void connect(String hostName, int port) {
		
		System.out.println("Attempting connection to: " + hostName + ", port = " + port);
		
		ClientThread client = null;
		try {
			client = new ClientThread(hostName, port);
		} catch (SocketException e1) {
			// Can't resolve hostname or port, or can't create socket
			e1.printStackTrace();
		}

		client.addNetListener(this);
		
		Thread networkThread = new Thread(client);
		networkThread.start();

		try {
			// connect to a lobby
			client.connect("nickname");
			// update my player list
			client.updatePlayerList();
			// get my player list
			List<ClientServerPlayer> playerList = client.getPlayerList();
			client.updateRoomList();
			// simple room to display in lobby
			List<ClientServerLobbyRoom> roomList = client.getRoomList();
			// TODO complex room desc
			client.createRoom("room name", (byte) 4, 0);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		
		resetFields();
	}

	public void setName(String string){
		
		this.playerName.set(string);
	}
	
	public void beginGame(Map map, String playerName, HashMap<Response, Integer> controls) {
		
		Block[][] masterMap = this.map.getGridMap();
		int columnLength = masterMap[0].length;
		Block[][] arrayCopy = new Block[masterMap.length][columnLength];
		
		for(int x = 0; x < masterMap.length; x++){
			
			arrayCopy[x] = Arrays.copyOf(masterMap[x], columnLength);
		}
		
		Map mapCopy = new Map(map.getName(), arrayCopy);
		
		Game game = new Game(this, mapCopy, playerName, controls);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionAccepted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionRejected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alreadyConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerListReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void roomListReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void roomAccepted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void roomRejected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notInRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alreadyInRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void haveLeftRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameStateReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEnded() {
		// TODO Auto-generated method stub
		
	}

	public void hide() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				currentStage.hide();
			}
			   
		});
	}
	
	public void show() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				currentStage.show();
			}
			   
		});
	}
}
