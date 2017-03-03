package bomber.UI;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import bomber.AI.AIDifficulty;
import bomber.audio.AudioManager;
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
	private BorderPane keyMenu;
	private BorderPane serverMenu;
	private BorderPane multiMenu;
	private BorderPane singleMenu;
	private Scene mainScene, keyScene, multiScene, serverScene, singleScene;
	private TextField nameText, ipText;
	private Button nameBtn, settingsBtn, controlsBtn, startBtn;
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
	private AIDifficulty aiDiff;
	private VBox mapBox;
	private VBox aiBox;
	private Label displayAi;
	private HBox aiPane;
	private Label aiLabel;
	private HBox centerBox;
	private List<Map> maps;
	private HBox ipBox;
	private Label enterLabel;
	private Label slashLabel;
	private HBox backBox1;
	private HBox backBox2;
	private VBox connectPane;
	private ClientThread client;
	private Button disconnectBtn;
	private Label roomsTitle;
	private Label playersTitle;
	private VBox roomsListPane;
	private VBox roomsPlayersPane;
	private VBox playersListPane;
	private FlowPane roomsBox;
	private FlowPane playersBox;
	private Button backBtn5;
	private Button upBtn;
	private Button downBtn;
	private Button leftBtn;
	private Button rightBtn;
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
	private boolean expectingConnection;
	private Scene currentScene;
	private Font font;
	private Label roomCreationLabel;
	private UserInterface ui;
	
	private Scene roomScene;
	private Button backButtonRooms;
	private String css;
	private Scene creditsScene;
	private BorderPane creditsMenu;
	private VBox roomMenu;
	private Button createRoomBtn;
	private Button addAi;
	private ButtonBase removeAi;
	private Button startGame;
	private BorderPane mainMenu;
	private SimpleStringProperty currentNameText;
	private Pane mapCanvas;
	private int windowHeight;
	private int windowWidth;
	private HBox mapPane;
	private VBox aiContainer;
	private Label aiExplanation;
	private VBox aiDiffBox;
	private BorderPane connectMenu;
	private Scene connectScene;
	private TextField roomNameField;
	private SimpleIntegerProperty roomNumber;
	private boolean expectingRoomCreation;
	private boolean expectingRoomJoin;
	
	public UserInterface(){
		//for JavaFX
		
		this.ui = this;
		this.font = Font.loadFont(UserInterface.class.getResource("minecraft.ttf").toExternalForm(), 20);
		this.css = this.getClass().getResource("resources/stylesheet.css").toExternalForm(); 
		this.playerName = new SimpleStringProperty("Player 1");
		this.aiNumber = new SimpleIntegerProperty(1);
		this.roomNumber = new SimpleIntegerProperty(4);
		this.roomCreationLabel = createLabel("Create and join a room\nwith these settings", false, true);
		this.aiDiff = AIDifficulty.EASY;
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
		this.windowHeight = 950;
		this.windowWidth = 1100;
	}
	
	public static void begin(){
        launch();
	}

	@Override
	public void start(Stage primaryStage){
		
		currentStage = primaryStage;
		currentStage.setMinHeight(windowHeight);
		currentStage.setMinWidth(windowWidth);
		primaryStage.setTitle(this.appName);
		previousScenes = new Stack<Scene>();
		initScenes();
		
        backBtn2 = new Button("Back");
        backBtn2.setOnAction(e -> previous());
        
        backBtn3 = new Button("Back");
        backBtn3.setOnAction(e -> previous());
        
        backBtn5 = new Button("Back");
        backBtn5.setAlignment(Pos.CENTER);
        backBtn5.setOnAction(e -> previous());
        
        settingsBtn = new Button("Settings");
        settingsBtn.setPrefWidth(200);
        settingsBtn.setOnAction(e -> advance(mainScene, keyScene));
        
        controlsBtn = new Button("Control Options");
        controlsBtn.setOnAction(e -> advance(mainScene, keyScene));
        
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
        
//        keyMenu.setSpacing(20);
//        keyMenu.setAlignment(Pos.CENTER);
//        keyMenu.getChildren().addAll(upPane, downPane, rightPane, leftPane, bombPane, backBtn5);
//        
        backBox1 = new HBox();
        backBox1.setAlignment(Pos.CENTER_LEFT);
        backBox1.getChildren().addAll(backBtn3);
        
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(e -> disconnect());
        primaryStage.show();
	}
	
	private void initScenes(){
		
		mainMenu = new BorderPane(); 
		creditsMenu = new BorderPane();
        keyMenu = new BorderPane();
        connectMenu = new BorderPane();
        serverMenu = new BorderPane();
        singleMenu = new BorderPane();
        
        //settingsScene = createScene(settingsMenu);
        //keyScene = createScene(keyMenu);
        connectScene = createScene(connectMenu);
        serverScene = createScene(serverMenu);
        singleScene = createScene(singleMenu);
        mainScene = createScene(mainMenu);
		creditsScene = createScene(creditsMenu);
		
		initMainScene();
		initCreditsScene();
		initSingleScene();
		initConnectScene();
		initServerScene();
		initRoomScene();
	}
	
	private void initRoomScene() {
		roomMenu = new VBox();
        
        addAi = new Button("Add Ai");
        addAi.setOnAction(e -> incrementAi());
        
        removeAi = new Button("Remove Ai");
        removeAi.setOnAction(e -> decrementAi());
        
        startGame = new Button("Start Game");
        startGame.setOnAction(e -> beginOnlineGame());
        
        roomScene = new Scene(roomMenu, 1000, 600);
        
        backButtonRooms = createBackButton("Leave Room", true);
        backButtonRooms.setOnAction(e -> previous());
        roomMenu.getChildren().addAll(backButtonRooms, addAi, removeAi, startGame);
        
	}

	private void initServerScene() {
		
		disconnectBtn = createBackButton("Disconnect", true);

		HBox backBox = new HBox();
		backBox.setAlignment(Pos.CENTER_LEFT);
		backBox.setPadding(new Insets(20, 10, 20, 10));
		backBox.getChildren().add(disconnectBtn);
		
		createRoomBtn = createButton("Create New Room", 250, 50);
		createRoomBtn.setOnAction(e -> createRoom());

        Button upRoomNumToggle = new Button();
        upRoomNumToggle.setPrefWidth(30);
        upRoomNumToggle.getStyleClass().add("aitoggleup");
        upRoomNumToggle.setOnAction(e -> incrementRoomNum());
        
        Label displayRoomNum = createBoundLabel(this.roomNumber, false, false);
        displayRoomNum.getStyleClass().add("textfield");
        displayRoomNum.setPrefWidth(30);
        
        Button downRoomNumToggle = new Button();
        downRoomNumToggle.setPrefWidth(30);
        downRoomNumToggle.getStyleClass().add("aitoggledown");
        downRoomNumToggle.setOnAction(e -> decrementRoomNum());
        
        VBox roomNumBox = new VBox();
        roomNumBox.setAlignment(Pos.CENTER);
        roomNumBox.getStyleClass().add("nopadbox");
        roomNumBox.maxHeightProperty().bind(upRoomNumToggle.heightProperty().add(displayRoomNum.heightProperty().add(downRoomNumToggle.heightProperty())));
        roomNumBox.getChildren().addAll(upRoomNumToggle, displayRoomNum, downRoomNumToggle);
        
        Label roomNumLabel = createLabel("Room\nCapacity", false, false);
        
        HBox roomNumPane = new HBox();
        roomNumPane.setAlignment(Pos.CENTER);
        roomNumPane.getStyleClass().add("namebox");
        roomNumPane.setSpacing(20);
        roomNumPane.setMinWidth(250);
        roomNumPane.getChildren().addAll(roomNumBox, roomNumLabel);
        
        roomNameField = createTextField("Enter Name");
        
        VBox roomNameBox = new VBox();
        roomNameBox.setSpacing(10);
        roomNameBox.getStyleClass().add("namebox");
        roomNameBox.setAlignment(Pos.CENTER);
        roomNameBox.setMinWidth(200);
        roomNameBox.getChildren().addAll(createLabel("Room Name:", false, false), roomNameField);
        
        VBox roomDisplay = new VBox();
        roomDisplay.setSpacing(20);
        roomDisplay.setAlignment(Pos.CENTER);
        roomDisplay.setMinWidth(300);
        roomDisplay.getChildren().addAll(this.roomCreationLabel, createRoomBtn);
        
        Rectangle torch1 = new Rectangle();
		torch1.setWidth(60);
		torch1.setHeight(80);
		torch1.setFill(new ImagePattern(new Image("bomber/UI/resources/images/torch.png")));
		
		Rectangle torch2 = new Rectangle();
		torch2.setWidth(60);
		torch2.setHeight(80);
		torch2.setFill(new ImagePattern(new Image("bomber/UI/resources/images/torch.png")));
        
		HBox createRoomPane = new HBox();
		createRoomPane.setAlignment(Pos.CENTER);
		createRoomPane.setSpacing(20);
		createRoomPane.getStyleClass().add("wideclearbox");
		createRoomPane.maxWidthProperty().bind(roomNumPane.widthProperty().add(roomNameBox.widthProperty().add(roomDisplay.widthProperty())));
		createRoomPane.getChildren().addAll(torch1, roomNumPane, roomNameBox, roomDisplay, torch2);
		
		roomsTitle = createLabel("Rooms:				( Join or create a room to play a match! )", false, true);

		roomsBox = new FlowPane();
		roomsBox.setVgap(20);
		roomsBox.setHgap(40);
		roomsBox.setMinHeight(100);
		
		roomsListPane = new VBox();
		roomsListPane.setSpacing(10);
		roomsListPane.setAlignment(Pos.TOP_LEFT);
		roomsListPane.minHeightProperty().bind(roomsTitle.minHeightProperty().add(roomsBox.minHeightProperty().add(200)));
		roomsListPane.getChildren().addAll(roomsTitle, roomsBox);

		playersTitle = createLabel("Online Players:", false, true);

		playersBox = new FlowPane(Orientation.VERTICAL);
		playersBox.setVgap(20);
		playersBox.setHgap(20);
		playersBox.setMinHeight(100);
		playersListPane = new VBox();
		playersListPane.setSpacing(10);
		playersListPane.getChildren().addAll(playersTitle, playersBox);
		playersListPane.setAlignment(Pos.TOP_LEFT);
		playersListPane.minHeightProperty().bind(playersTitle.minHeightProperty().add(playersBox.minHeightProperty().add(70)));
		
		roomsPlayersPane = new VBox();
		roomsPlayersPane.setSpacing(40);
		roomsPlayersPane.setAlignment(Pos.CENTER);
		roomsPlayersPane.getChildren().addAll(roomsListPane, playersListPane);
		roomsBox.maxWidthProperty().bind(roomsPlayersPane.widthProperty());
		BorderPane serverPane = new BorderPane();
		VBox serverBox = new VBox();
		serverBox.setSpacing(20);
		serverBox.setAlignment(Pos.CENTER);
		roomsPlayersPane.getStyleClass().add("wideclearbox");
		roomsPlayersPane.minHeightProperty().bind(roomsListPane.minHeightProperty().add(playersListPane.minHeightProperty().add(50)));
		serverBox.minHeightProperty().bind(roomsPlayersPane.minHeightProperty());
		serverBox.getChildren().addAll(createRoomPane, roomsPlayersPane);
		serverPane.setTop(backBox);
		serverPane.setPadding(new Insets(0, 20, 20, 20));
		serverPane.setCenter(serverBox);
		setBackgroundPane(serverMenu, serverPane);
	}

	private void decrementRoomNum() {
		if(this.roomNumber.get() > 2)roomNumber.set(this.roomNumber.get()-1);
	}

	private void incrementRoomNum() {
		if(this.roomNumber.get() < 4)roomNumber.set(this.roomNumber.get()+1);
	}

	private void initConnectScene(){
		
		enterLabel = createLabel("Enter Server Details:", false, false);
        
        ipBox = new HBox();
        
        ipText = createTextField("IP Address");
        ipText.setMinWidth(200);
        slashLabel = createLabel("/", false, false);
        portNum = createTextField("Port Number");
        portNum.setMinWidth(200);
        
        ipBox.setSpacing(10);
        ipBox.setPrefSize(200, 50);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.getChildren().addAll(ipText, slashLabel, portNum);
        
        connectBtn = createButton("Connect", 300, 75);
        connectBtn.setOnAction(e -> connect());
        
        backBtn4 = createBackButton("Cancel", true);

        VBox connectBox = new VBox();
        connectBox.setSpacing(20);
        connectBox.setAlignment(Pos.CENTER);
        connectBox.maxWidthProperty().bind(ipBox.widthProperty().add(40));
        connectBox.getStyleClass().add("connectbox");
        connectBox.getChildren().addAll(enterLabel, ipBox, connectBtn);
        
        connectPane = new VBox();
        connectPane.setAlignment(Pos.CENTER);
        connectPane.setSpacing(40);
        connectPane.getChildren().addAll(connectBox, backBtn4);
        
        setBackgroundPane(connectMenu, connectPane);
	}
	
	private void initSingleScene() {
		
		mapCanvas = new Pane();
		mapCanvas.setMinHeight(300);
		mapCanvas.setMinWidth(300);
		mapCanvas.widthProperty().addListener(e -> drawMap(mapCanvas));
		mapCanvas.heightProperty().addListener(e -> drawMap(mapCanvas));
		
		VBox mapContainer = new VBox();
		mapContainer.getStyleClass().add("mapbox");
		mapContainer.setAlignment(Pos.CENTER);
		Label mapNameLabel = createBoundLabel(this.mapName, false, true);
		mapNameLabel.getStyleClass().add("maplabel");
		Image keyImage = new Image("bomber/UI/resources/images/key.png");
        ImageView mapKey = new ImageView(keyImage);
        
        VBox currentPane = new VBox();
        currentPane.setAlignment(Pos.CENTER);
        currentPane.setSpacing(10);
        currentPane.getChildren().addAll(createLabel("Current Map:", false, true), mapNameLabel);
        
        HBox keyPane = new HBox();
        keyPane.setAlignment(Pos.CENTER);
        keyPane.setSpacing(40);
        keyPane.setPadding(new Insets(20));
        keyPane.getChildren().addAll(currentPane, mapKey);
        
		mapContainer.getChildren().addAll(keyPane, mapCanvas);
		
		BorderPane singleBox = new BorderPane();
		
		//button to start the game
        startBtn = createButton("Start Game", 300, 75);
        HBox startBtnPane = new HBox();
        startBtnPane.setPadding(new Insets(20, 0, 20, 0));
        startBtnPane.setAlignment(Pos.CENTER);
        startBtnPane.getChildren().add(startBtn);
        startBtn.setOnAction(e -> beginGame(this.map, this.playerName.getValue(), this.controls, this.aiNumber.get()));
        
        //back button
        backBtn3 = createBackButton("Back", false);
        
        rightMapToggle = new Button();
        rightMapToggle.getStyleClass().add("maptoggleright");
        rightMapToggle.setPrefWidth(90);
        
        rightMapToggle.setOnAction(e -> incremenetMap());
        
        leftMapToggle = new Button();
        leftMapToggle.getStyleClass().add("maptoggleleft");
        
        leftMapToggle.setPrefWidth(90);
        leftMapToggle.setOnAction(e -> decrementMap());
        
        upAiToggle = new Button();
        upAiToggle.setPrefWidth(30);
        upAiToggle.getStyleClass().add("aitoggleup");
        upAiToggle.setOnAction(e -> incrementAi());
        
        displayAi = createBoundLabel(this.aiNumber, false, false);
        displayAi.getStyleClass().add("textfield");
        displayAi.setPrefWidth(30);
        
        downAiToggle = new Button();
        downAiToggle.setPrefWidth(30);
        downAiToggle.getStyleClass().add("aitoggledown");
        downAiToggle.setOnAction(e -> decrementAi());
        
        aiBox = new VBox();
        aiBox.setAlignment(Pos.CENTER);
        aiBox.getStyleClass().add("nopadbox");
        aiBox.maxHeightProperty().bind(upAiToggle.heightProperty().add(displayAi.heightProperty().add(downAiToggle.heightProperty())));
        aiBox.getChildren().addAll(upAiToggle, displayAi, downAiToggle);
        
        aiLabel = createLabel("Number of\nAI Players", false, false);
        
        aiPane = new HBox();
        aiPane.setAlignment(Pos.CENTER);
        aiPane.getStyleClass().add("namebox");
        aiPane.setSpacing(20);
        aiPane.getChildren().addAll(aiBox, aiLabel);
        aiExplanation = createLabel("AI players will\nseek to\ndestroy you.", true, true);
        aiExplanation.setAlignment(Pos.CENTER);
        aiExplanation.setPrefWidth(200);
        
        ChoiceBox<String> aiDifficultyChoice = new ChoiceBox<>();
        aiDifficultyChoice.setTooltip(new Tooltip("Change AI Difficulty"));
        aiDifficultyChoice.setPrefHeight(50);
        aiDifficultyChoice.setPrefWidth(200);
        aiDifficultyChoice.getStyleClass().add("textfield");
        aiDifficultyChoice.getItems().addAll("Easy", "Medium", "Hard", "Extreme");
        aiDifficultyChoice.getSelectionModel().select(1);
        aiDifficultyChoice.getSelectionModel().selectedItemProperty().addListener(new
                ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ob,
					String oldValue, String newValue) {
				switch(newValue){
				case "Easy": 
					aiDiff = AIDifficulty.EASY; 
					aiExplanation.setText("AI players will\nmove randomly.");
					break;
				case "Medium": 
					aiDiff = AIDifficulty.MEDIUM;
					aiExplanation.setText("AI players will\nseek to\ndestroy you.");
					break;
				case "Hard": 
					aiDiff = AIDifficulty.HARD; 
					aiExplanation.setText("AI players will\nbe tough\nto beat!");
					break;
				case "Extreme": 
					aiDiff = AIDifficulty.EXTREME;
					aiExplanation.setText("AI players will\ncollaborate\nto bring\nyou down!");
				}
				
				System.out.println("Set difficulty to " + newValue);
			}
        });
        
        
        aiDiffBox = new VBox();
        aiDiffBox.setAlignment(Pos.CENTER);
        aiDiffBox.getStyleClass().add("namebox");
        aiDiffBox.setSpacing(20);
        aiDiffBox.getChildren().addAll(createLabel("AI Difficulty:", false, false), aiDifficultyChoice, aiExplanation);
        
        aiContainer = new VBox();
        aiContainer.setAlignment(Pos.CENTER);
        aiContainer.getStyleClass().add("box");
        aiContainer.setSpacing(20);
        aiContainer.getChildren().addAll(aiPane, aiDiffBox);
        
        mapPane = new HBox();
        mapPane.setAlignment(Pos.CENTER);
        mapPane.getChildren().addAll(leftMapToggle, mapContainer, rightMapToggle);
        
        rightMapToggle.prefHeightProperty().bind(mapPane.heightProperty());
        leftMapToggle.prefHeightProperty().bind(mapPane.heightProperty());
        
        mapPane.maxHeightProperty().bind(aiBox.heightProperty());
        
        VBox mapPad = new VBox();
		mapPad.setAlignment(Pos.CENTER);
		mapPad.getChildren().add(mapPane);
        
		VBox aiPad = new VBox();
		aiPad.setAlignment(Pos.CENTER);
		aiPad.getChildren().add(aiContainer);
		
        centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(20);
        centerBox.getChildren().addAll(aiPad, mapPad);
        
        HBox backBtnPane = new HBox();
        backBtnPane.getChildren().add(backBtn3);
        backBtnPane.setPadding(new Insets(20, 10, 20, 10));
        
        singleBox.setCenter(centerBox);
        singleBox.setTop(backBtnPane);
        singleBox.setBottom(startBtnPane);
        
        setBackgroundPane(singleMenu, singleBox);
	}

	private void drawMap(Pane mapCanvas) {

		int xpadding = 250;
		int ypadding = 50;
		
		mapCanvas.getChildren().clear();
		Rectangle torch1 = new Rectangle();
		torch1.setWidth(60);
		torch1.setHeight(80);
		torch1.setFill(new ImagePattern(new Image("bomber/UI/resources/images/torch.png")));
		torch1.setX(40);
		torch1.setY(30);
		
		Rectangle torch2 = new Rectangle();
		torch2.setWidth(60);
		torch2.setHeight(80);
		torch2.setFill(new ImagePattern(new Image("bomber/UI/resources/images/torch.png")));
		torch2.setX(mapCanvas.getWidth()-xpadding+150);
		torch2.setY(30);
		
		Rectangle torch3 = new Rectangle();
		torch3.setWidth(60);
		torch3.setHeight(80);
		torch3.setFill(new ImagePattern(new Image("bomber/UI/resources/images/torch.png")));
		torch3.setX(mapCanvas.getWidth()-xpadding+150);
		torch3.setY(170);
		
		Rectangle torch4 = new Rectangle();
		torch4.setWidth(60);
		torch4.setHeight(80);
		torch4.setFill(new ImagePattern(new Image("bomber/UI/resources/images/torch.png")));
		torch4.setX(40);
		torch4.setY(170);
		
		mapCanvas.getChildren().addAll(torch1, torch2, torch3, torch4);
		
		double canvasWidth = mapCanvas.getWidth() - xpadding;
		double canvasHeight = mapCanvas.getHeight() - ypadding;
		
		Block[][] gridMap = this.map.getGridMap();
		double xscalar = canvasWidth/gridMap.length;
		double yscalar = canvasHeight/gridMap[0].length;
		
		for(int x = 0; x < gridMap.length; x++){
			for(int y = 0; y < gridMap[0].length; y++){
				
				String image = "";
				switch(gridMap[x][y]){
				case BLANK: image = "blank"; break;
				case SOLID: image = "solid"; break;
				case SOFT: image = "soft"; break;
				default:
					break;
				}
				Rectangle rect = new Rectangle(xscalar, yscalar);
				rect.setFill(new ImagePattern(new Image("bomber/UI/resources/images/" + image + ".png")));
					    	    
				rect.setStroke(Color.BLACK);
				rect.setX((xpadding/2) + xscalar*x);
				rect.setY((ypadding/2) + yscalar*y);
				mapCanvas.getChildren().add(rect);
			}
		}
		
		for(int x = 0; x < this.map.getSpawnPoints().size(); x++){
			Point pos = this.map.getSpawnPoints().get(x);
			Rectangle rect = new Rectangle(10, 10);
			if(x == 0){
				rect.setFill(new ImagePattern(new Image("bomber/UI/resources/images/playerspawnpoint.png")));
			}
			else{
				rect.setFill(new ImagePattern(new Image("bomber/UI/resources/images/spawnpoint.png")));
			}
			rect.setStroke(Color.BLACK);
			rect.setX((xpadding/2) + 5 + xscalar*(pos.x/64));
			rect.setY((ypadding/2) + 5 + yscalar*(pos.y/64));
			mapCanvas.getChildren().add(rect);
		}
	}

	private void initCreditsScene() {
		
		String credits = "Credits:\n"
				+ "\nGraphics - Alexandru Blindu"
				+ "\nPhysics/Audio - Alexandru Rosu"
				+ "\nAI - Jokubas Liutkus"
				+ "\nUI/Game Logic - Owen Jenkins"
				+ "\nNetworking - Qiyang Li"
				+ "\n\nFonts:\nCredit to 'PurePixel'"
				+ "\n\nImages:\nMain Menu Background - From the TRaK2 Texture set\nby Georges 'TRaK' Grondin."
				+ "\n\nButtons: Credit to 'Buch', http://opengameart.org/users/buch";

		Label creditsLabel = createLabel(credits, false, true);
		creditsLabel.getStyleClass().add("creditsbox");
		VBox creditsBox = new VBox();
		creditsBox.setAlignment(Pos.CENTER);
		creditsBox.setSpacing(20);
		creditsBox.getChildren().addAll(creditsLabel, createBackButton("Back", false));

		setBackgroundPane(creditsMenu, creditsBox);
	}

	private void initMainScene() {
		
		this.currentNameText = new SimpleStringProperty("Current Name:");
		nameText = createTextField("Enter Name");
		
        nameBtn = createButton("Set Name", 200, 50);
        nameBtn.setOnAction(e -> setName(nameText.getText()));
        
        Button singlePlayerBtn = createSceneButton("Single Player", 200, 50, mainScene, singleScene);
        Button multiPlayerBtn = createSceneButton("Multiplayer", 200, 50, mainScene, connectScene);
        Button creditsBtn = createSceneButton("Credits", 200, 50, mainScene, creditsScene);
        
        Image logoImage = new Image("bomber/UI/resources/images/logo.png");
        ImageView logoImageView = new ImageView(logoImage);
        
        HBox logoPane = new HBox();
        logoPane.setSpacing(20);
        logoPane.setAlignment(Pos.CENTER);

        VBox menuBox = new VBox();
        menuBox.setSpacing(30);
        menuBox.setAlignment(Pos.CENTER);
        logoPane.getChildren().addAll(logoImageView, menuBox);
        
        VBox namePane = new VBox();
        namePane.setAlignment(Pos.CENTER);
        namePane.setSpacing(5);
        namePane.getStyleClass().add("namebox");
        namePane.getChildren().addAll(createBoundLabel(this.currentNameText, false, false), createBoundLabel(this.playerName, false, false),
        		nameText, nameBtn);
        
        menuBox.getChildren().addAll(namePane, singlePlayerBtn, multiPlayerBtn, creditsBtn);
        setBackgroundPane(mainMenu, logoPane);
	}

	private void setBackgroundPane(BorderPane pane, Node content){
		Image mainImage = new Image("bomber/UI/resources/images/background.png");
        ImageView mainImageView = new ImageView(mainImage);
        Pane imagePane = new Pane();
        imagePane.getChildren().add(mainImageView); 
        imagePane.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        mainImageView.fitWidthProperty().bind(imagePane.widthProperty()); 
        mainImageView.fitHeightProperty().bind(imagePane.heightProperty());
        StackPane background = new StackPane();
        background.setAlignment(Pos.CENTER);
        background.getChildren().add(imagePane);
        background.getChildren().add(content);
        pane.setCenter(background);
	}
	
	private Scene createScene(Parent menu){
		Scene scene = new Scene(menu, windowWidth, windowHeight);
		scene.getStylesheets().add(css);
		return scene;
	}
	
	private Label createLabel(String text, boolean shaded, boolean white){
		Label label = new Label(text);
		label.setFont(font);
		if(white)label.setTextFill(Color.WHITE);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		if(shaded){
			label.getStyleClass().add("shaded");
		}
		return label;
	}
	
	private TextField createTextField(String defaultText){
		TextField text = new TextField();
		text.setPromptText(defaultText);
		text.getStyleClass().add("textfield");
		text.setFont(font);
		text.setAlignment(Pos.CENTER);
		return text;
	}
	
	private Label createBoundLabel(SimpleIntegerProperty property, boolean shaded, boolean white) {
		Label label = new Label();
		label.setFont(font);
		if(white)label.setTextFill(Color.WHITE);
		label.setAlignment(Pos.CENTER);
		label.textProperty().bind(property.asString());
		if(shaded){
			label.getStyleClass().add("shaded");
		}
		return label;
	}
	
	private Label createBoundLabel(SimpleStringProperty property, boolean shaded, boolean white) {
		Label label = new Label();
		label.setFont(font);
		if(white)label.setTextFill(Color.WHITE);
		label.setAlignment(Pos.CENTER);
		label.textProperty().bind(property);
		if(shaded){
			label.getStyleClass().add("shaded");
		}
		return label;
	}

	private Button createButton(String label, double width, double height) {
		Button button = new Button(label);
		button.setFont(font);
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add("menubutton");
		return button;
	}
	
	private Button createBackButton(String label, boolean online){
		Button button = new Button(label);
		button.setFont(font);
		button.setPrefWidth(200);
		button.setPrefHeight(50);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add("menubutton");
		button.setOnAction(online?e -> disconnect():e -> previous());
		return button;
	}
	
	private Button createSceneButton(String label, double width, double height, Scene currentScene, Scene nextScene){
		Button button = new Button(label);
		button.setFont(font);
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add("menubutton");
		button.setOnAction(e -> advance(currentScene, nextScene));
		return button;
	}
	
	public void beep(){
		AudioManager.playMenuItemSelected();
	}
	
	private void beginOnlineGame() {
		beep();
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
		beep();
		String text = this.roomNameField.getText().trim();
		if(text.length() < 1){
			this.roomCreationLabel.setText("Create and join a room\nwith these settings\n( Name too short! )");
		}
		else if(text.length() > 11){
			this.roomCreationLabel.setText("Create and join a room\nwith these settings\n( Name too long! )");
		}
			else{
		try {
			this.client.createRoom(text, (byte) this.roomNumber.get(), 1);
			this.expectingRoomCreation = true;
			blankButton(createRoomBtn, "Generating...");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		beep();
		if (this.client != null && !this.client.isInRoom()) {
			try {

				this.client.disconnect();
			} catch (Exception e) {
			} finally {
				this.client.exit();
				this.client = null;
			}
		}
		else if(this.client != null && !this.client.isInRoom()){
			try {
				this.client.leaveRoom();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.expectingConnection = false;
		if(previousScenes.size() != 0){
			previous();
		}
	}
	
	private void decrementMap() {
		beep();
		int index = this.maps.indexOf(this.map);
		if(index > 0){
			this.map = this.maps.get(index-1);
		}
		else{
			this.map = this.maps.get(this.maps.size()-1);
		}
		this.mapName.set(this.map.getName());
		drawMap(mapCanvas);
	}

	private void incremenetMap() {
		beep();
		int index = this.maps.indexOf(this.map);
		if(index < (this.maps.size()-1)){
			this.map = this.maps.get(index+1);
		}
		else{
			this.map = this.maps.get(0);
		}
		this.mapName.set(this.map.getName());
		drawMap(mapCanvas);
	}

	private void decrementAi() {
		beep();
		if(this.aiNumber.get() > 1)aiNumber.set(this.aiNumber.get()-1);
	}

	private void incrementAi() {
		beep();
		if(this.aiNumber.get() < 3)aiNumber.set(this.aiNumber.get()+1);
	}
	
	private void connect() {
		beep();
		enterLabel.setText("Enter Server Details:");
		String host = ipText.getText();
		try{
			int port = Integer.parseInt(portNum.getText());
		
		System.out.println("Attempting connection to: " + host + ", port = " + port);
		this.client = null;
			client = new ClientThread(host, port);

			client.addNetListener(this);

			Thread networkThread = new Thread(client);
			networkThread.start();

			client.connect(this.playerName.get());
			
			blankButton(connectBtn, "Connecting...");
			this.expectingConnection = true;
		} 
		catch (NumberFormatException e1) {
			enterLabel.setText("Enter Server Details:\n( Invalid Port Number! )");
			resetButton(connectBtn, "Connect", e -> connect());
		}
		catch(IOException e2){
			enterLabel.setText("Enter Server Details:\n( Couldn't open a connection! )");
			resetButton(connectBtn, "Connect", e -> connect());
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
			roomContainer.setSpacing(5);
			roomContainer.setAlignment(Pos.CENTER);
			roomContainer.getStyleClass().add("namebox");
			Label roomID = createLabel("Room " + room.getID() +":", false, false);
			Label roomName = createLabel("\"" + room.getName() + "\"", false, false);
			roomName.setAlignment(Pos.CENTER);
			HBox roomPane = new HBox();
			roomPane.setSpacing(15);
			roomPane.setPrefHeight(50);
			roomPane.setAlignment(Pos.CENTER);
			Label numPlayers = createLabel(room.getPlayerNumber() + "/" + room.getMaxPlayer(), true, true);
			Label playersLabel = createLabel("", true, true);
			playersLabel.setMinWidth(160);
			int[] playerids = room.getPlayerID();
			for(ClientServerPlayer player : this.client.getPlayerList()){
				if(player.getID() == playerids[0]){
					playersLabel.setText(playersLabel.getText() + " + " + player.getName());
				}
			}
			for(int x = 1; x < playerids.length; x++){
				for(ClientServerPlayer player : this.client.getPlayerList()){
					if(player.getID() == playerids[x]){
						playersLabel.setText(playersLabel.getText() + "\n + " + player.getName());
					}
				}
			}
			if(room.getPlayerNumber() < room.getMaxPlayer()){
				Button joinButton = createButton("Join", 100, 50);
				joinButton.setOnAction(e -> joinRoom(room.getID(), joinButton));
				roomPane.getChildren().addAll(joinButton, numPlayers);
			}
			else{
				Label fullLabel = createLabel("FULL", false, false);
				fullLabel.setPrefWidth(100);
				fullLabel.setPrefHeight(50);
				fullLabel.getStyleClass().add("textfield");
				roomPane.getChildren().addAll(fullLabel, numPlayers);
			}
			roomContainer.getChildren().addAll(roomID, roomName, playersLabel, roomPane);
			
			this.roomsBox.getChildren().add(roomContainer);
		}
	}

	private void joinRoom(int id, Button button) {
		
		beep();
		try {
			this.client.joinRoom(id);
			blankButton(button, ". . .");
			this.expectingRoomJoin = true;
			
			this.expectingRoomCreation = true;
			blankButton(createRoomBtn, "Please wait...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void displayPlayers() {
		
		List<ClientServerPlayer> connectedPlayers = this.client.getPlayerList();
		
		this.playersBox.getChildren().clear();
		
		for(ClientServerPlayer player : connectedPlayers){
			Label playerLabel = createLabel("- P" + player.getID() + ":   " + player.getName(), true, true);
			this.playersBox.getChildren().add(playerLabel);
		}
	}

	private void previous() {
		beep();
		try {
		if(this.client != null && this.client.isInRoom()){
			
				this.client.leaveRoom();
			
		}} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		System.out.println("am at " + this.currentScene);
		this.currentStage.setScene(this.previousScenes.pop());
		System.out.println("now at " + this.currentScene);
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
		
		this.roomCreationLabel.setText("Create and join a room\nwith these settings");
		enterLabel.setText("Enter Server Details:");
		this.currentNameText.set("Current Name:");
		
		resetButton(connectBtn, "Connect", e -> connect());
		resetButton(createRoomBtn, "Create New Room", e -> createRoom());
		}
	}
	
	private void advance(Scene thisScene, Scene nextScene) {
		
		beep();
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		
		this.previousScenes.push(thisScene);
		System.out.println("Added " + thisScene);
		System.out.println(this.previousScenes);
		this.currentStage.setScene(nextScene);
		this.currentScene = nextScene;
		
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}

	public void setName(String string){
		
		beep();
		string = string.trim();
		if(string.length() > 0 && string.length() < 12){
			this.playerName.set(string);
			this.currentNameText.set("Current Name:");
		}
		else if(string.length() > 0){
			this.currentNameText.set("Current Name:\n ( Too long! )");
		}
		else{
			this.currentNameText.set("Current Name:\n( Too short! )");
		}
	}

	public void beginGame(Map map, String playerName, HashMap<Response, Integer> controls, int aiNum) {
		
		beep();
		Block[][] masterMap = this.map.getGridMap();
		int columnLength = masterMap[0].length;
		Block[][] arrayCopy = new Block[masterMap.length][columnLength];
		
		for(int x = 0; x < masterMap.length; x++){
			
			arrayCopy[x] = Arrays.copyOf(masterMap[x], columnLength);
		}
		
		Map mapCopy = new Map(map.getName(), arrayCopy, map.getSpawnPoints());

		Platform.setImplicitExit(false);
		new Game(this, mapCopy, playerName, controls, aiNum, aiDiff);
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
				System.out.println("Expecting Connection: " + expectingConnection);
				if(expectingConnection){
					
					advance(connectScene, serverScene);
					try {
						client.updateRoomList();
						client.updatePlayerList();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					displayPlayers();
					displayRooms();
					expectingConnection = false;
					System.out.println("CONNECTION ACCEPTED");
				}
			}
			   
		});
	}

	@Override
	public void connectionRejected() {
		
		System.out.println("CALLED CONNECTION REJECTED");
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				enterLabel.setText("Enter Server Details:\n( A player with your name is already\nconncted to the server!\nChange and try again! )");
				resetButton(connectBtn, "Connect", e -> connect());
				expectingConnection = false;
			}
			   
		});
	}

	@Override
	public void alreadyConnected() {
		
	}

	@Override
	public void notConnected() {
		
		System.out.println("CALLED NOT CONNECTED");
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				enterLabel.setText("Enter Server Details:\n( Couldn't connect to server!\nMake sure server is running. )");
				resetButton(connectBtn, "Connect", e -> connect());
				expectingConnection = false;
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
				if(expectingRoomCreation){
					advance(currentScene, roomScene);
					roomCreationLabel.setText("Create and join a room\nwith these settings");
					resetButton(createRoomBtn, "Create New Room", e -> createRoom());
					expectingRoomCreation = false;
				}
				else if(expectingRoomJoin){
					advance(currentScene, roomScene);
					resetButton(createRoomBtn, "Create New Room", e -> createRoom());
					displayRooms();
					expectingRoomJoin = false;
				}
			}
			   
		});
	}

	private void blankButton(Button button, String blankText){
		button.setOnAction(null);
		button.setText(blankText);
		button.getStyleClass().clear();
		button.getStyleClass().add("textfield");
	}
	
	private void resetButton(Button button, String resetText, EventHandler<ActionEvent> handler){
		button.setOnAction(handler);
		button.setText(resetText);
		button.getStyleClass().clear();
		button.getStyleClass().add("menubutton");
	}
	
	@Override
	public void roomRejected() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				System.out.println("Room creation/join rejected");
				if(expectingRoomCreation){
					roomCreationLabel.setText("Create and join a room\nwith these settings\n( Room creation failed! )");
					resetButton(createRoomBtn, "Create New Room", e -> createRoom());
					expectingRoomCreation = false;
				}
				if(expectingRoomJoin){
					displayRooms();
					expectingRoomJoin = false;
				}
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
				GameState gameState = client.getGameState();
				Platform.setImplicitExit(false);
				new OnlineGame(ui, client, gameState, playerName.get(), controls);
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

	@Override
	public void connectionAttemptTimeout() {
		System.out.println("CALLED CONNECTION TIMEOUT");
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				enterLabel.setText("Enter Server Details:\n( Timed out trying to connect!\nMake sure server is running. )");
				resetButton(connectBtn, "Connect", e -> connect());
				expectingConnection = false;
			}
			   
		});
	}

	@Override
	public void roomReceived() {
		// TODO Auto-generated method stub
		
	}
}
