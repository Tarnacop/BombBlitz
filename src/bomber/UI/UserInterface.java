package bomber.UI;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	private VBox settingsMenu, keyMenu;
	private VBox serverMenu;
	private BorderPane multiMenu;
	private BorderPane singleMenu;
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
	private SimpleIntegerProperty aiNumber;
	private Label currentNameLabel;
	private Label currentMapLabel;
	private TextField portNum;
	private HBox namePane;
	private HBox currentName;
	private HBox nameSetter;
	
	private final String font = "Arial";
	private VBox singleButtonPane;
	private Pane imagePane;
	private VBox mapBox;
	private VBox aiBox;
	private Label displayAi;
	private HBox aiPane;
	private Label aiLabel;
	private VBox centerBox;
	private List<Map> maps;
	private HBox ipBox;
	private Label enterLabel;
	private Label slashLabel;
	private HBox backBox1;
	private HBox backBox2;
	private VBox connectPane;
	private VBox loadMenu;
	private ClientThread client;
	private Button disconnectBtn;
	private Label roomsTitle;
	private Label roomTitle;
	private Label playersTitle;
	private VBox roomsListPane;
	private VBox roomsPlayersPane;
	private VBox playersListPane;
	private FlowPane roomsBox;
	private VBox playersBox;
	
	public UserInterface(){
		//for JavaFX
		this.playerName = new SimpleStringProperty("Player 1");
		this.aiNumber = new SimpleIntegerProperty(1);
		Maps maps = new Maps();
		this.maps = maps.getMaps();
		this.map = this.maps.get(0);
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
        
        multiMenu = new BorderPane();
        
        loadMenu = new VBox();
        serverMenu = new VBox();
        //serverMenu.setStyle("-fx-background-color: black");
        
        singleMenu = new BorderPane();
        
        mainScene = new Scene(mainMenu, 800, 600);
        mainScene.setFill(Color.BLACK);
        settingsScene = new Scene(settingsMenu, 800, 600);
        
        keyScene = new Scene(keyMenu, 800, 600);
        multiScene = new Scene(multiMenu, 800, 600);
        multiScene.setFill(Color.BLACK);
        serverScene = new Scene(serverMenu, 800, 600);
        serverScene.setFill(Color.BLACK);
        singleScene = new Scene(singleMenu, 800, 600);
        
        previousScenes = new Stack<Scene>();
        
        nameText = new TextField("Enter Name");
        
        enterLabel = new Label("Enter Server Details:");
        
        ipBox = new HBox();
        
        //ipText = new TextField("IP Address");
        ipText = new TextField("10.20.170.99");
        slashLabel = new Label("/");
        slashLabel.setFont(Font.font(font, FontWeight.BOLD, 20));
        //portNum = new TextField("Port Number");
        portNum = new TextField("1234");
        
        ipBox.setSpacing(10);
        ipBox.setPrefSize(200, 50);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.getChildren().addAll(ipText, slashLabel, portNum);
        
        connectBtn = new Button("Connect");
        connectBtn.setAlignment(Pos.CENTER);
        connectBtn.setOnAction(e -> connect(multiScene, ipText.getText(), Integer.parseInt(portNum.getText())));
        
        currentNameLabel = new Label("Current Name:");
        currentNameLabel.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        displayName = new Label();
        displayName.setFont(Font.font(font, FontWeight.BOLD, FontPosture.ITALIC, 20));
        displayName.textProperty().bind(this.playerName);
        
        nameBtn = new Button("Set Name");
        nameBtn.setPrefWidth(200);
        nameBtn.setOnAction(e -> setName(nameText.getText()));
        
        //button to start the game
        startBtn = new Button("Start\nGame");
        startBtn.setPrefWidth(Integer.MAX_VALUE);
        startBtn.setOnAction(e -> beginGame(this.map, this.playerName.getValue(), this.controls, this.aiNumber.get()));
        
        //back button
        backBtn1 = new Button("Back");
        backBtn1.setOnAction(e -> previous());
        
        disconnectBtn = new Button("Disconnect");
        disconnectBtn.setOnAction(e -> disconnect());
        
        backBtn2 = new Button("Back");
        backBtn2.setOnAction(e -> previous());
        
        backBtn3 = new Button("Back");
        backBtn3.setOnAction(e -> previous());
        
        backBtn4 = new Button("Back");
        backBtn4.setOnAction(e -> previous());
        
        singleBtn = new Button("Single\nPlayer");
        singleBtn.setPrefHeight(Integer.MAX_VALUE);
        singleBtn.setOnAction(e -> advance(mainScene, singleScene));
        
        multiBtn = new Button("Multi\nPlayer");
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
        currentMapLabel.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        displayMap = new Label();
        displayMap.setFont(Font.font(font, FontWeight.BOLD, FontPosture.ITALIC, 20));
        displayMap.textProperty().bind(this.mapName);
        
        rightMapToggle = new Button("->");
        rightMapToggle.setPrefHeight(Integer.MAX_VALUE);
        rightMapToggle.setOnAction(e -> incremenetMap());
        
        leftMapToggle = new Button("<-");
        leftMapToggle.setPrefHeight(Integer.MAX_VALUE);
        leftMapToggle.setOnAction(e -> decrementMap());
        
        upAiToggle = new Button("^");
        upAiToggle.setPrefWidth(50);
        upAiToggle.setOnAction(e -> incrementAi());
        
        displayAi = new Label();
        displayAi.setFont(Font.font(font, FontWeight.BOLD, FontPosture.ITALIC, 20));
        displayAi.textProperty().bind(this.aiNumber.asString());
        
        downAiToggle = new Button("v");
        downAiToggle.setPrefWidth(50);
        downAiToggle.setOnAction(e -> decrementAi());
        
        currentName = new HBox();
        currentName.setSpacing(10);
        currentName.getChildren().addAll(currentNameLabel, displayName);
        
        nameSetter = new HBox();
        nameSetter.setSpacing(10);
        nameSetter.getChildren().addAll(nameText, nameBtn);
        
        namePane.setSpacing(50);
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
        
        addElements(settingsMenu, controlsBtn, audioBtn, graphicsBtn, backBtn1);
        addElements(keyMenu, backBtn2);
        
        mapBox = new VBox();
        mapBox.setAlignment(Pos.CENTER);
        mapBox.getChildren().addAll(currentMapLabel, displayMap);
        
        backBox1 = new HBox();
        backBox1.setAlignment(Pos.CENTER_LEFT);
        backBox1.getChildren().addAll(backBtn3);
        
        backBox2 = new HBox();
        backBox2.setAlignment(Pos.CENTER_LEFT);
        backBox2.getChildren().addAll(backBtn4);
        
        aiBox = new VBox();
        aiBox.setAlignment(Pos.CENTER);
        aiBox.setSpacing(20);
        aiBox.getChildren().addAll(upAiToggle, displayAi, downAiToggle);
        
        aiLabel = new Label("Number of\nAI Players");
        aiLabel.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        aiPane = new HBox();
        aiPane.setAlignment(Pos.CENTER);
        aiPane.setSpacing(30);
        aiPane.getChildren().addAll(aiBox, aiLabel);
        
        centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(30);
        centerBox.getChildren().addAll(mapBox, aiPane);
        
        singleMenu.setCenter(centerBox);
        singleMenu.setTop(backBox1);
        singleMenu.setLeft(leftMapToggle);
        singleMenu.setRight(rightMapToggle);
        singleMenu.setBottom(startBtn);
        
        roomsTitle = new Label("Rooms:");
        roomsTitle.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        roomsBox = new FlowPane();
        roomsBox.setVgap(10);
        roomsBox.setHgap(10);
        roomsBox.setMinHeight(100);
        //roomsBox.setStyle("-fx-background-color: aqua;"
        //		+ "-fx-border-width: 2;"
        //		+ "-fx-border-color: grey;");
        
        roomsListPane = new VBox();
        roomsListPane.setSpacing(10);
        roomsListPane.setAlignment(Pos.TOP_LEFT);
        roomsListPane.getChildren().addAll(roomsTitle, roomsBox);
        
        playersTitle = new Label("Online Players:");
        playersTitle.setFont(Font.font(font, FontWeight.BOLD, 20));
        
        playersBox = new VBox();
        playersBox.setSpacing(20);
        playersBox.setPadding(new Insets(10));
        playersBox.setMinHeight(200);
        playersBox.setStyle("-fx-background-color: plum;"
        		+ "-fx-border-width: 2;"
        		+ "-fx-border-color: grey;");
        playersBox.setAlignment(Pos.TOP_LEFT);
        
        playersListPane = new VBox();
        playersListPane.setSpacing(10);
        playersListPane.getChildren().addAll(playersTitle, playersBox);
        playersListPane.setAlignment(Pos.TOP_LEFT);
        
        roomsPlayersPane = new VBox();
        roomsPlayersPane.setSpacing(40);
        roomsPlayersPane.setPadding(new Insets(100));
        roomsPlayersPane.setAlignment(Pos.CENTER);
        roomsPlayersPane.getChildren().addAll(roomsListPane, playersListPane);
        
        serverMenu.getChildren().addAll(disconnectBtn, roomsPlayersPane);
        
        connectPane = new VBox();
        connectPane.setAlignment(Pos.CENTER);
        connectPane.setSpacing(20);
        connectPane.getChildren().addAll(enterLabel, ipBox, connectBtn);
        
        multiMenu.setTop(backBox2);
        multiMenu.setCenter(connectPane);
        
        
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(e -> disconnect());
        primaryStage.show();
        
        
	}
	
	private void disconnect() {
		if (this.client != null) {
			try {

				this.client.disconnect();
			} catch (Exception e) {
			} finally {
				this.client.exit();
				this.client = null;
				previous();
			}
		}
	}
	
	private void decrementMap() {
		int index = this.maps.indexOf(this.map);
		if(index > 0)this.map = this.maps.get(index-1);
		this.mapName.set(this.map.getName());
	}

	private void incremenetMap() {
		int index = this.maps.indexOf(this.map);
		if(index < (this.maps.size()-1))this.map = this.maps.get(index+1);
		this.mapName.set(this.map.getName());
	}

	private void decrementAi() {
		//System.out.println(this.aiNumber.get());
		if(this.aiNumber.get() > 1)aiNumber.set(this.aiNumber.get()-1);
		//System.out.println(" dec-> " + this.aiNumber.get());
	}

	private void incrementAi() {
		//System.out.println(this.aiNumber.get());
		if(this.aiNumber.get() < 3)aiNumber.set(this.aiNumber.get()+1);
		//System.out.println(" inc-> " + this.aiNumber.get());
	}

	private void resetFields(){
		
		nameText.setText("Enter Name");
		//ipText.setText("Enter IP Address");
		ipText.setText("10.20.170.99");
		//portNum.setText("Enter Port Number");
		portNum.setText("1234");
	}
	
	private void connect(Scene thisScene, String hostName, int port) {
		
		System.out.println("Attempting connection to: " + hostName + ", port = " + port);
		
		this.client = null;
		try {
			client = new ClientThread(hostName, port);

			client.addNetListener(this);

			Thread networkThread = new Thread(client);
			networkThread.start();

			client.connect(this.playerName.get());
			client.updateRoomList();
			client.updatePlayerList();
		} catch (Exception e1) {
			// Can't resolve hostname or port, or can't create socket
			e1.printStackTrace();
		}finally{

			advance(thisScene, this.serverScene);
			displayPlayers();
			displayRooms();
		}
		
//			// update my player list
//			client.updatePlayerList();
//			// get my player list
//			List<ClientServerPlayer> playerList = client.getPlayerList();
//			client.updateRoomList();
//			// simple room to display in lobby
//			List<ClientServerLobbyRoom> roomList = client.getRoomList();
//			// TODO complex room desc
//			client.createRoom("room name", (byte) 4, 0);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}

	private void displayRooms() {
		
		List<ClientServerLobbyRoom> rooms = this.client.getRoomList();
		
		this.roomsBox.getChildren().clear();
		
		for(ClientServerLobbyRoom room : rooms){
			
			VBox roomContainer = new VBox();
			roomContainer.setMinHeight(100);
			roomContainer.setMinWidth(100);
			roomContainer.setStyle("-fx-background-color: blue;"
					+ "-fx-border-width: 2;"
					+ "-fx-border-color: grey;");
			roomContainer.setAlignment(Pos.CENTER);
			Label roomName = new Label(room.getName());
			roomName.setPrefHeight(50);
			roomName.setAlignment(Pos.CENTER);
			//roomName.setPadding(new Insets(20));
			roomName.setStyle("-fx-background-color: blue;"
					+ "-fx-text-fill: white;");
			roomName.setFont(Font.font(font, FontWeight.BOLD, 40));
			Button joinButton = new Button("JOIN");
			joinButton.setPrefHeight(50);
			HBox roomPane = new HBox();
			roomPane.setSpacing(15);
			roomPane.setPrefHeight(50);
			roomPane.setAlignment(Pos.CENTER);
			Label numPlayers = new Label("" + room.getPlayerNumber());
			Label slash = new Label("/");
			Label maxPlayers = new Label("" + room.getMaxPlayer());
			slash.setAlignment(Pos.CENTER);
			slash.setFont(Font.font(font, FontWeight.BOLD, 50));
			numPlayers.setAlignment(Pos.CENTER);
			numPlayers.setFont(Font.font(font, FontWeight.BOLD, 50));
			maxPlayers.setAlignment(Pos.CENTER);
			maxPlayers.setFont(Font.font(font, FontWeight.BOLD, 50));
			roomPane.getChildren().addAll(joinButton, numPlayers, slash, maxPlayers);
			roomContainer.getChildren().addAll(roomName, roomPane);
			
			this.roomsBox.getChildren().add(roomContainer);
		}
	}

	private void displayPlayers() {
		
		List<ClientServerPlayer> connectedPlayers = this.client.getPlayerList();
		
		this.playersBox.getChildren().clear();
		
		for(ClientServerPlayer player : connectedPlayers){
			Label playerLabel = new Label("Connected (" + player.getID() + "): " + player.getName());
			playerLabel.setFont(Font.font(font, FontPosture.ITALIC, 15));
			this.playersBox.getChildren().add(playerLabel);
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
	
	public void beginGame(Map map, String playerName, HashMap<Response, Integer> controls, int aiNum) {
		
		Block[][] masterMap = this.map.getGridMap();
		int columnLength = masterMap[0].length;
		Block[][] arrayCopy = new Block[masterMap.length][columnLength];
		
		for(int x = 0; x < masterMap.length; x++){
			
			arrayCopy[x] = Arrays.copyOf(masterMap[x], columnLength);
		}
		
		Map mapCopy = new Map(map.getName(), arrayCopy, map.getSpawnPoints());

		Platform.setImplicitExit(false);
		new Game(this, mapCopy, playerName, controls, aiNum);
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
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				displayPlayers();
			}
			   
		});
	}

	@Override
	public void roomListReceived() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				displayRooms();
			}
			   
		});
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
				System.out.println("CLOSING MENU");
				currentStage.hide();
			}
			   
		});
	}
	
	public void show() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				System.out.println("OPENING MENU");
				currentStage.show();
				Platform.setImplicitExit(true);
			}
			   
		});
	}
}
