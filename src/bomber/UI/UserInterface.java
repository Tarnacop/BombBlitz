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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import bomber.game.Block;
import bomber.game.Game;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Maps;
import bomber.game.OnlineGame;
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
	private Button backBtn5;
	private Button upBtn;
	private Button downBtn;
	private Button leftBtn;
	private Button rightBtn;
	private Button spaceBtn;
	private Label upLabel;
	private Label downLabel;
	private Label rightLabel;
	private Label leftLabel;
	private Label keyLabel;
	private HBox upPane;
	private Label bombLabel;
	private Button bombBtn;
	private HBox downPane;
	private HBox rightPane;
	private HBox leftPane;
	private HBox bombPane;
	private boolean connected;
	private boolean connectionBroke;
	private boolean expectingConnection;
	private Scene currentScene;
	private Font font;
	
	private UserInterface ui;
	
	private boolean createdRoom;
	private Scene roomScene;
	private Button backButtonRooms;
	private String css;
	
	public UserInterface(){
		//for JavaFX
		
		this.ui = this;
		this.font = Font.loadFont(UserInterface.class.getResource("minecraft.ttf").toExternalForm(), 20);
		this.css = this.getClass().getResource("resources/stylesheet.css").toExternalForm(); 
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
		this.currentStage.setMinWidth(1000);
		primaryStage.setTitle(this.appName);
		
        mainMenu = new BorderPane(); 
        
        namePane = new HBox();
        
        settingsMenu = new VBox();
        
        keyMenu = new VBox();
        
        multiMenu = new BorderPane();
        
        loadMenu = new VBox();
        serverMenu = new VBox();
        
        singleMenu = new BorderPane();
        
        mainScene = createScene(mainMenu);
       
        settingsScene = createScene(settingsMenu);
        
        keyScene = createScene(keyMenu);
        multiScene = createScene(multiMenu);

        serverScene = createScene(serverMenu);
        singleScene = createScene(singleMenu);
        
        previousScenes = new Stack<Scene>();
        
        nameText = new TextField("Enter Name");
        
        enterLabel = new Label("Enter Server Details:");
        enterLabel.setFont(font);
        
        ipBox = new HBox();
        
        ipText = new TextField("IP Address");
        slashLabel = new Label("/");
        slashLabel.setFont(font);
        portNum = new TextField("Port Number");
        
        ipBox.setSpacing(10);
        ipBox.setPrefSize(200, 50);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.getChildren().addAll(ipText, slashLabel, portNum);
        
        connectBtn = new Button("Connect");
        connectBtn.setAlignment(Pos.CENTER);
        connectBtn.setOnAction(e -> connect(multiScene, ipText.getText(), Integer.parseInt(portNum.getText())));
        
        currentNameLabel = new Label("Current Name:");
        currentNameLabel.setFont(font);
        
        displayName = new Label();
        displayName.setFont(font);
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
        backBtn4.setOnAction(e -> disconnect());
        
        backBtn5 = new Button("Back");
        backBtn5.setAlignment(Pos.CENTER);
        backBtn5.setOnAction(e -> previous());
        
        singleBtn = createButton("Single Player", 200, 50, mainScene, singleScene);
        multiBtn = createButton("Multiplayer", 200, 50, mainScene, multiScene);
        
        settingsBtn = new Button("Settings");
        settingsBtn.setPrefWidth(200);
        settingsBtn.setOnAction(e -> advance(mainScene, keyScene));//TODO
        
        controlsBtn = new Button("Control Options");
        controlsBtn.setOnAction(e -> advance(mainScene, keyScene));
        
        audioBtn = new Button("Audio Options");
        
        graphicsBtn = new Button("Graphics Options");
        
        currentMapLabel = new Label("Current Map:");
        currentMapLabel.setFont(font);
        
        displayMap = new Label();
        displayMap.setFont(font);
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
        displayAi.setFont(font);
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
        
        Image mainImage = new Image("bomber/UI/resources/images/background.png");
        ImageView mainImageView = new ImageView(mainImage);
        imagePane = new Pane();
        imagePane.getChildren().add(mainImageView); 
        imagePane.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        mainImageView.fitWidthProperty().bind(imagePane.widthProperty()); 
        mainImageView.fitHeightProperty().bind(imagePane.heightProperty());
        
        Image logoImage = new Image("bomber/UI/resources/images/logo.png");
        ImageView logoImageView = new ImageView(logoImage);
        HBox logoPane = new HBox();
        logoPane.setSpacing(20);
        logoPane.setAlignment(Pos.CENTER);
        VBox menuBox = new VBox();
        menuBox.setSpacing(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(singleBtn, multiBtn);
        logoPane.getChildren().addAll(logoImageView, menuBox);
        //logoImageView.fitWidthProperty().bind(imagePane.widthProperty().divide(3)); 
        //logoImageView.fitHeightProperty().bind(imagePane.heightProperty().divide(3));
//        mainMenu.setStyle("-fx-background-image: url(\"resources/images/menu_background.png\");"
//        		+ "-fx-background-repeat:no-repeat;"
//        		+ "-fx-background-size:cover;"
//        		+ "-fx-background-position:center;"
//        		+ "-fx-overflow:hidden;");
        StackPane background = new StackPane();
        background.setAlignment(Pos.CENTER);
        background.getChildren().add(imagePane);
        background.getChildren().add(logoPane);
        mainMenu.setCenter(background);
        //mainMenu.setTop(namePane);
        //mainMenu.setLeft(singleButtonPane);
        //mainMenu.setRight(multiBtn);
        //TODO mainMenu.setBottom(settingsBtn);
        
        //addElements(settingsMenu, controlsBtn, audioBtn, graphicsBtn, backBtn1);
        
        upBtn = new Button("" + this.controls.get(Response.UP_MOVE));
        upBtn.setPrefWidth(200);
        upBtn.setOnAction(e -> setUp());
        
        downBtn = new Button("" + this.controls.get(Response.DOWN_MOVE));
        downBtn.setPrefWidth(200);
        downBtn.setOnAction(e -> setDown());
        
        leftBtn = new Button("" + this.controls.get(Response.LEFT_MOVE));
        leftBtn.setPrefWidth(200);
        leftBtn.setOnAction(e -> setLeft());
        
        rightBtn = new Button("" + this.controls.get(Response.RIGHT_MOVE));
        rightBtn.setPrefWidth(200);
        rightBtn.setOnAction(e -> setRight());
        
        bombBtn = new Button("" + this.controls.get(Response.PLACE_BOMB));
        bombBtn.setPrefWidth(200);
        bombBtn.setOnAction(e -> setNextKey(this.bombBtn, Response.PLACE_BOMB));
        
        keyLabel = new Label("Key Bindings");
        keyLabel.setFont(font);
        
        upLabel = new Label("UP");
        upLabel.setPrefWidth(200);
        upPane = new HBox();
        upPane.setAlignment(Pos.CENTER);
        upPane.getChildren().addAll(upLabel, upBtn);
        upPane.setSpacing(20);
        
        downLabel = new Label("DOWN");
        downLabel.setPrefWidth(200);
        downPane = new HBox();
        downPane.setAlignment(Pos.CENTER);
        downPane.getChildren().addAll(downLabel, downBtn);
        downPane.setSpacing(20);
        
        rightLabel = new Label("RIGHT");
        rightLabel.setPrefWidth(200);
        rightPane = new HBox();
        rightPane.setAlignment(Pos.CENTER);
        rightPane.getChildren().addAll(rightLabel, rightBtn);
        rightPane.setSpacing(20);
        
        leftLabel = new Label("LEFT");
        leftLabel.setPrefWidth(200);
        leftPane = new HBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.getChildren().addAll(leftLabel, leftBtn);
        leftPane.setSpacing(20);
        
        bombLabel = new Label("BOMB");
        bombLabel.setPrefWidth(200);
        bombPane = new HBox();
        bombPane.setAlignment(Pos.CENTER);
        bombPane.getChildren().addAll(bombLabel, bombBtn);
        bombPane.setSpacing(20);
        
        keyMenu.setSpacing(20);
        keyMenu.setAlignment(Pos.CENTER);
        keyMenu.getChildren().addAll(upPane, downPane, rightPane, leftPane, bombPane, backBtn5);
        
        mapBox = new VBox();
        mapBox.setAlignment(Pos.CENTER);
        mapBox.getChildren().addAll(currentMapLabel, displayMap);
        
        backBox1 = new HBox();
        backBox1.setAlignment(Pos.CENTER_LEFT);
        backBox1.getChildren().addAll(backBtn3);
        
        backBox2 = new HBox();
        backBox2.setAlignment(Pos.CENTER_LEFT);
        backBox2.getChildren().add(backBtn4);
        
        aiBox = new VBox();
        aiBox.setAlignment(Pos.CENTER);
        aiBox.setSpacing(20);
        aiBox.getChildren().addAll(upAiToggle, displayAi, downAiToggle);
        
        aiLabel = new Label("Number of\nAI Players");
        aiLabel.setFont(font);
        
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
        roomsTitle.setFont(font);
        
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
        playersTitle.setFont(font);
        
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
        
        VBox roomMenu = new VBox();
        Button createRoomBtn = new Button("New\nRoom");
        createRoomBtn.setOnAction(e -> createRoom());
        
        Button addAi = new Button("Add Ai");
        addAi.setOnAction(e -> incrementAi());
        
        Button removeAi = new Button("Remove Ai");
        removeAi.setOnAction(e -> decrementAi());
        
        Button startGame = new Button("Start Game");
        startGame.setOnAction(e -> beginOnlineGame());
        
        roomScene = new Scene(roomMenu, 1000, 600);
        
        backButtonRooms = new Button("Back");
        backButtonRooms.setOnAction(e -> previous());
        roomMenu.getChildren().addAll(backButtonRooms, addAi, removeAi, startGame);
        
        serverMenu.getChildren().addAll(disconnectBtn, createRoomBtn, roomsPlayersPane);
        
        connectPane = new VBox();
        connectPane.setAlignment(Pos.CENTER);
        connectPane.setSpacing(20);
        connectPane.getChildren().addAll(enterLabel, ipBox, connectBtn);
        
        multiMenu.setTop(backBox2);
        multiMenu.setCenter(connectPane);
        
        Button backButtonCredits = createBackButton();
        
        String credits = "Credits:\n"
        		+ "\nGraphics - Alexandru Blindu"
        		+ "\nPhysics - Alexandru Rosu"
        		+ "\nAI - Jokubas Liutkus"
        		+ "\nUI/Game Logic - Owen Jenkins"
        		+ "\nNetworking - Qiyang Li"
        		+ "\n"
        		+ "\n\nFonts:\nCredit to 'PurePixel'"
        		+ "\n\nImages:\nMain Menu Background - From the TRaK2 Texture set\nby Georges 'TRaK' Grondin.";
        
        Label creditsLabel = createLabel(credits);
        VBox creditsBox = verticalMenu(
        		creditsLabel, backButtonCredits);
        BorderPane creditsMenu = createBackgroundPane(creditsBox);
        
        Scene creditsScene = createScene(creditsMenu);
        
        Button creditsBtn = createButton("Credits", 200, 50, mainScene, creditsScene);
        
        menuBox.getChildren().add(creditsBtn);
        
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(e -> disconnect());
        primaryStage.show();
        
        
	}
	
	private BorderPane createBackgroundPane(Node content){
		Image mainImage = new Image("bomber/UI/resources/images/background.png");
        ImageView mainImageView = new ImageView(mainImage);
        imagePane = new Pane();
        imagePane.getChildren().add(mainImageView); 
        imagePane.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        mainImageView.fitWidthProperty().bind(imagePane.widthProperty()); 
        mainImageView.fitHeightProperty().bind(imagePane.heightProperty());
        StackPane background = new StackPane();
        background.setAlignment(Pos.CENTER);
        background.getChildren().add(imagePane);
        background.getChildren().add(content);
        BorderPane backgroundPane = new BorderPane();
        backgroundPane.setCenter(background);
        return backgroundPane;
	}
	
	private Scene createScene(Parent menu){
		Scene scene = new Scene(menu, 1000, 600);
		scene.getStylesheets().add(css);
		return scene;
	}
	
	private VBox verticalMenu(Node... elements){
		VBox menu = new VBox();
		menu.setSpacing(20);
		menu.setAlignment(Pos.CENTER);
		menu.getChildren().addAll(elements);
		return menu;
	}
	
	private Label createLabel(String text){
		Label label = new Label(text);
		label.setFont(font);
		label.setTextFill(Color.WHITE);
		label.setAlignment(Pos.CENTER);
		return label;
	}

	private Button createBackButton(){
		Button button = new Button("Back");
		button.setFont(font);
		button.setPrefWidth(200);
		button.setPrefHeight(50);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add(".button");
		button.setOnAction(e -> previous());
		return button;
	}
	
	private Button createButton(String label, double width, double height, Scene currentScene, Scene nextScene){
		Button button = new Button(label);
		button.setFont(font);
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add(".button");
		button.setOnAction(e -> advance(currentScene, nextScene));
		return button;
	}
	
	private void beginOnlineGame() {
		
		try {
			this.client.readyToPlay(true);
			for(int x = 0; x < this.aiNumber.get(); x++){
				this.client.addAI();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createRoom() {
	
		try {
			this.client.createRoom("Room 1", (byte) 4, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			advance(currentScene, roomScene);
		}
	}

	private Object setRight() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object setLeft() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object setDown() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object setUp() {
		// TODO Auto-generated method stub
		return null;
	}

	private void setNextKey(Button btn, Response response) {

		btn.setText("PRESS KEY");
		this.keyScene.setOnKeyPressed(e -> addControl(btn, e, response));
	}

	private void addControl(Button btn, KeyEvent e, Response response) {
		
		btn.setText(e.getText());
		System.out.println(e.getCode());
	}

	private void disconnect() {
		if (this.client != null) {
			try {

				this.client.disconnect();
			} catch (Exception e) {
			} finally {
				this.client.exit();
				this.client = null;
			}
		}
		this.expectingConnection = false;

		if(this.previousScenes.size() != 0){
			previous();
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
		ipText.setText("10.20.171.10");
		//portNum.setText("Enter Port Number");
		portNum.setText("1234");
	}
	
	private void connect(Scene thisScene, String hostName, int port) {
		
		System.out.println("Attempting connection to: " + hostName + ", port = " + port);
		this.connectionBroke = false;
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
		}finally{
			
			this.expectingConnection = true;
				
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
			//roomName.setFont(Font.font(font, FontWeight.BOLD, 40));
			Button joinButton = new Button("JOIN");
			joinButton.setPrefHeight(50);
			joinButton.setOnAction(e -> joinRoom(room.getID()));
			HBox roomPane = new HBox();
			roomPane.setSpacing(15);
			roomPane.setPrefHeight(50);
			roomPane.setAlignment(Pos.CENTER);
			Label numPlayers = new Label("" + room.getPlayerNumber());
			Label slash = new Label("/");
			Label maxPlayers = new Label("" + room.getMaxPlayer());
			slash.setAlignment(Pos.CENTER);
			//slash.setFont(Font.font(font, FontWeight.BOLD, 50));
			numPlayers.setAlignment(Pos.CENTER);
			//numPlayers.setFont(Font.font(font, FontWeight.BOLD, 50));
			maxPlayers.setAlignment(Pos.CENTER);
			//maxPlayers.setFont(Font.font(font, FontWeight.BOLD, 50));
			roomPane.getChildren().addAll(joinButton, numPlayers, slash, maxPlayers);
			roomContainer.getChildren().addAll(roomName, roomPane);
			
			this.roomsBox.getChildren().add(roomContainer);
		}
	}

	private void joinRoom(int id) {
		
		try {
			this.client.joinRoom(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			advance(currentScene, roomScene);
		}
	}

	private void displayPlayers() {
		
		List<ClientServerPlayer> connectedPlayers = this.client.getPlayerList();
		
		this.playersBox.getChildren().clear();
		
		for(ClientServerPlayer player : connectedPlayers){
			Label playerLabel = new Label("Connected (" + player.getID() + "): " + player.getName());
			//playerLabel.setFont(Font.font(font, FontPosture.ITALIC, 15));
			this.playersBox.getChildren().add(playerLabel);
		}
	}

	private void previous() {
		try {
		if(this.client != null && this.client.isInRoom()){
			
				this.client.leaveRoom();
			
		}} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		System.out.println("Moving from " + this.currentScene);
		this.currentStage.setScene(this.previousScenes.pop());
		System.out.println("Moved back to " + this.currentScene);
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
		
		}
	}
	
	private void advance(Scene thisScene, Scene nextScene) {
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		
		this.previousScenes.push(thisScene);
		this.currentStage.setScene(nextScene);
		this.currentScene = nextScene;
		
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

		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				disconnect();
			}
			   
		});
	}

	@Override
	public void connectionAccepted() {
		
		System.out.println("ADDED CONNECTION EVENT");
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				if(expectingConnection){
					advance(multiScene, serverScene);
					displayPlayers();
					displayRooms();
				}
				System.out.println("CONNECTION ACCEPTED");
			}
			   
		});
	}

	@Override
	public void connectionRejected() {
		
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				
				disconnect();
			}
			   
		});
	}

	@Override
	public void alreadyConnected() {
		
	}

	@Override
	public void notConnected() {
		
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				connectionBroke = true;
				disconnect();
			}
			   
		});
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
		System.out.println("Adding display rooms event to queue");
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				System.out.println("Displaying Rooms");
				displayRooms();
			}
			   
		});
	}

	@Override
	public void roomAccepted() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				System.out.println("Room creation accepted");
				createdRoom = true;
			}
			   
		});
	}

	@Override
	public void roomRejected() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				System.out.println("Room creation rejected");
				createdRoom = false;
			}
			   
		});
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
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				int mapID = client.getMapID();
				GameState gameState = client.getGameState();
				Platform.setImplicitExit(false);
				OnlineGame game = new OnlineGame(ui, client, gameState, maps.get(1), playerName.get(), controls);
			}
			   
		});
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
