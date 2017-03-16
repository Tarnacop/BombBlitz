package bomber.UI;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import bomber.game.SettingsParser;
import bomber.networking.ClientNetInterface;
import bomber.networking.ClientServerAI;
import bomber.networking.ClientServerLobbyRoom;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ClientServerRoom;
import bomber.networking.ClientThread;

public class UserInterface extends Application implements ClientNetInterface{

	private final String appName = "Bomb Blitz v1";
	private SimpleStringProperty playerName;
	private Stage currentStage;
	private BorderPane serverMenu;
	private BorderPane singleMenu;
	private Parent mainScene, serverScene, singleScene;
	private TextField nameText, ipText;
	private Button nameBtn;
	private Button connectBtn;
	private Stack<Parent> previousScenes;
	private HashMap<Response, Integer> controls;
	private Map map;
	private SimpleStringProperty mapName;
	private SimpleIntegerProperty aiNumber;
	private TextField portNum;
	private AIDifficulty aiDiff;
	private List<Map> maps;
	private Label enterLabel;
	private VBox connectPane;
	private ClientThread client;
	private HBox roomsBox;
	private FlowPane playersBox;
	private boolean expectingConnection;
	private Font font;
	private Label roomCreationLabel;
	private UserInterface ui;
	
	private Parent roomScene;
	private String css;
	private Parent creditsScene;
	private BorderPane creditsMenu;
	private BorderPane roomMenu;
	private Button createRoomBtn;
	private BorderPane mainMenu;
	private SimpleStringProperty currentNameText;
	private double windowHeight;
	private double windowWidth;
	private BorderPane connectMenu;
	private Parent connectScene;
	private TextField roomNameField;
	private SimpleIntegerProperty roomNumber;
	private boolean expectingRoomCreation;
	private boolean expectingRoomJoin;
	private FlowPane playersBox2;
	private int humanPlayers;
	private int aiPlayers;
	private Pane mapCanvas;
	private Pane onlineMapCanvas;
	private ChoiceBox<String> aiDifficultyChoice;
	private ChoiceBox<String> aiOnlineDifficultyChoice;
	private VBox readyPane;
	private Button readyButton;
	private Rectangle readyTorch;
	private Slider musicSlider;
	private Slider soundSlider;
	private CheckBox muteMusicBtn;
	private CheckBox muteSoundBtn;
	private double versionNumber;
	private boolean musicMuted;
	private boolean soundMuted;
	private CheckBox fullScreenBtn;
	private double screenHeight;
	private double screenWidth;
	private double btnHeight;
	private double btnWidth;
	private double bigBtnHeight;
	private double bigBtnWidth;
	private double boxHeight;
	private double boxWidth;
	private double canvasHeight;
	private double canvasWidth;
	
	public UserInterface(){

		this.versionNumber = 1.2;
		
		SettingsParser.init();

		this.ui = this;
		this.font = Font.loadFont(UserInterface.class.getResource("../../resources/minecraft.ttf").toExternalForm(), 20);
		this.css = UserInterface.class.getResource("../../resources/stylesheet.css").toExternalForm(); 
		this.playerName = new SimpleStringProperty(SettingsParser.getPlayerName());
		this.aiNumber = new SimpleIntegerProperty(1);
		new SimpleIntegerProperty(1);
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
		this.controls.put(Response.PAUSE_GAME, GLFW_KEY_P);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.screenHeight = screenSize.height;
		this.screenWidth = screenSize.width;
		this.windowHeight = screenHeight * 0.8;
		this.windowWidth = screenWidth * 0.8;
		
		this.btnHeight = windowHeight/20;
		this.btnWidth = windowWidth/8;
		this.bigBtnHeight = windowHeight/15;
		this.bigBtnWidth = windowWidth/6;
		
		this.boxHeight = windowHeight/4;
		this.boxWidth = windowWidth/6;
		
		this.canvasHeight = windowHeight/3; 
		this.canvasWidth = windowWidth/3;
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
		previousScenes = new Stack<Parent>();
		initScenes();
		
        primaryStage.setScene(new Scene(mainScene));
        primaryStage.setOnCloseRequest(e -> disconnect());
        primaryStage.show();
	}
	
	private void initScenes(){
		
		mainMenu = new BorderPane(); 
		creditsMenu = new BorderPane();
        roomMenu = new BorderPane();
        connectMenu = new BorderPane();
        serverMenu = new BorderPane();
        singleMenu = new BorderPane();
        
        connectScene = createScene(connectMenu);
        serverScene = createScene(serverMenu);
        singleScene = createScene(singleMenu);
        mainScene = createScene(mainMenu);
		creditsScene = createScene(creditsMenu);
		roomScene = createScene(roomMenu);
		
		initMainScene();
		initCreditsScene();
		initSingleScene();
		initConnectScene();
		initServerScene();
		initRoomScene();
	}
	
	private void initRoomScene() {
		
        BorderPane roomBox = new BorderPane();
		
        Button backBtn = createButton("Leave Room", 300, 50);
        backBtn.setOnAction(e -> leaveRoom());
        
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(20);

		onlineMapCanvas = new Pane();
		onlineMapCanvas.setMinHeight(300);
		onlineMapCanvas.setMinWidth(300);
        centerBox.getChildren().addAll(createAiDifficultySelector(true), createMapSelector(onlineMapCanvas, true));
        
        HBox backBtnPane = new HBox();
        backBtnPane.getChildren().add(backBtn);
        backBtnPane.setPadding(new Insets(20, 10, 20, 10));
        
        Label playersTitle = createLabel("Online Players:", false, true);

		playersBox2 = new FlowPane(Orientation.VERTICAL);
		playersBox2.setVgap(20);
		playersBox2.setHgap(20);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setMinHeight(200);
		scrollPane.setMinWidth(300);
		scrollPane.setContent(playersBox2);
		
		VBox playersListPane = new VBox();
		playersListPane.getStyleClass().add("wideclearbox");
		playersListPane.setSpacing(10);
		playersListPane.setPrefWidth(200);
		playersListPane.getChildren().addAll(playersTitle, scrollPane);
		playersListPane.setAlignment(Pos.TOP_LEFT);
		playersListPane.minHeightProperty().bind(playersTitle.minHeightProperty().add(playersBox2.minHeightProperty().add(70)));

		readyTorch = new Rectangle();
		readyTorch.setWidth(90);
		readyTorch.setHeight(120);
		readyTorch.getStyleClass().add("wideclearbox");
		readyTorch.setFill(new ImagePattern(new Image("resources/images/darktorch.png")));
		
		VBox readyBox = new VBox();
		readyBox.getStyleClass().add("namebox");
		readyBox.setSpacing(10);
		readyBox.setAlignment(Pos.CENTER);
		readyBox.setMinWidth(300);
		readyBox.setMinHeight(300);
		readyBox.setPrefHeight(300);
		readyButton = createButton("Not Ready", 250, 50);
		readyButton.setOnAction(e -> ready());
		readyBox.getChildren().addAll(createLabel("Click to\ntoggle Ready:", false, false), readyTorch, readyButton);
		
		readyPane = new VBox();
		readyPane.getStyleClass().add("menubox");
		readyPane.setSpacing(30);
		readyPane.setMinWidth(320);
		readyBox.setMinHeight(300);
		readyBox.setPrefHeight(300);
		readyPane.getChildren().add(createLabel("Game will begin when all\nplayers click ready!", false, false));
		
		HBox playersReadyBox = new HBox();
		playersReadyBox.setAlignment(Pos.CENTER);
		playersReadyBox.setSpacing(20);
		playersReadyBox.setPadding(new Insets(10, 10, 10, 10));
		playersReadyBox.getChildren().addAll(playersListPane, readyPane, readyBox);
		
		VBox centerPane = new VBox();
		centerPane.setSpacing(10);
		centerPane.getChildren().addAll(centerBox, playersReadyBox);
		
        roomBox.setCenter(centerPane);
        roomBox.setTop(backBtnPane);
        
        setBackgroundPane(roomMenu, roomBox);
	}

	private void initServerScene() {
		
		Button disconnectBtn = createBackButton("Disconnect", true);

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
		torch1.setFill(new ImagePattern(new Image("resources/images/torch.png")));
		
		Rectangle torch2 = new Rectangle();
		torch2.setWidth(60);
		torch2.setHeight(80);
		torch2.setFill(new ImagePattern(new Image("resources/images/torch.png")));
        
		HBox createRoomPane = new HBox();
		createRoomPane.setAlignment(Pos.CENTER);
		createRoomPane.setSpacing(20);
		createRoomPane.getStyleClass().add("wideclearbox");
		createRoomPane.maxWidthProperty().bind(roomNumPane.widthProperty().add(roomNameBox.widthProperty().add(roomDisplay.widthProperty())));
		createRoomPane.getChildren().addAll(torch1, roomNumPane, roomNameBox, roomDisplay, torch2);
		
		Label roomsTitle = createLabel("Rooms:				( Join or create a room to play a match! )", false, true);

		roomsBox = new HBox();
		roomsBox.setSpacing(40);
		
		ScrollPane scrollRooms = new ScrollPane();
		scrollRooms.setMinHeight(250);
		scrollRooms.setVbarPolicy(ScrollBarPolicy.NEVER);
		scrollRooms.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollRooms.setFitToHeight(true);
		scrollRooms.setFitToWidth(true);
		scrollRooms.setContent(roomsBox);
		
		VBox roomsListPane = new VBox();
		roomsListPane.setSpacing(10);
		roomsListPane.setAlignment(Pos.TOP_LEFT);
		roomsListPane.minHeightProperty().bind(roomsTitle.minHeightProperty().add(scrollRooms.minHeightProperty()));
		roomsListPane.getChildren().addAll(roomsTitle, scrollRooms);

		Label playersTitle = createLabel("Online Players:", false, true);

		playersBox = new FlowPane();
		playersBox.setVgap(20);
		playersBox.setHgap(20);
		playersBox.setMinHeight(100);
		
		ScrollPane scrollPlayers = new ScrollPane();
		scrollPlayers.setContent(playersBox);
		
		VBox playersListPane = new VBox();
		playersListPane.setSpacing(10);
		playersListPane.getChildren().addAll(playersTitle, scrollPlayers);
		playersListPane.setAlignment(Pos.TOP_LEFT);
		playersListPane.minHeightProperty().bind(playersTitle.minHeightProperty().add(playersBox.minHeightProperty().add(70)));
		
		VBox roomsPlayersPane = new VBox();
		roomsPlayersPane.setSpacing(40);
		roomsPlayersPane.setAlignment(Pos.CENTER);
		roomsPlayersPane.getChildren().addAll(roomsListPane, playersListPane);
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
		if(roomNumber.get() > 2)roomNumber.set(roomNumber.get()-1);
	}

	private void incrementRoomNum() {
		if(roomNumber.get() < 4)roomNumber.set(roomNumber.get()+1);
	}

	private void initConnectScene(){
		
		enterLabel = createLabel("Enter Server Details:", false, false);
        
        HBox ipBox = new HBox();
        
        ipText = createTextField("IP Address");
        ipText.setMinWidth(200);
        portNum = createTextField("Port Number");
        portNum.setMinWidth(200);
        
        ipBox.setSpacing(10);
        ipBox.setPrefSize(200, 50);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.getChildren().addAll(ipText, createLabel("/", false, false), portNum);
        
        connectBtn = createButton("Connect", 300, 75);
        connectBtn.setOnAction(e -> connect());
        
        Button backBtn = createBackButton("Cancel", false);

        VBox connectBox = new VBox();
        connectBox.setSpacing(20);
        connectBox.setAlignment(Pos.CENTER);
        connectBox.maxWidthProperty().bind(ipBox.widthProperty().add(40));
        connectBox.getStyleClass().add("connectbox");
        connectBox.getChildren().addAll(enterLabel, ipBox, connectBtn);
        
        connectPane = new VBox();
        connectPane.setAlignment(Pos.CENTER);
        connectPane.setSpacing(40);
        connectPane.getChildren().addAll(connectBox, backBtn);
        
        setBackgroundPane(connectMenu, connectPane);
	}
	
	private void initSingleScene() {
		
		BorderPane singleBox = new BorderPane();
		
        Button startBtn = createButton("Start Game", 300, 75);
        
        HBox startBtnPane = new HBox();
        startBtnPane.setPadding(new Insets(0, 0, 40, 0));
        startBtnPane.setAlignment(Pos.CENTER);
        startBtnPane.getChildren().add(startBtn);
        startBtn.setOnAction(e -> beginGame(this.map, this.playerName.getValue(), this.controls, this.aiNumber.get()));
        
        Button backBtn = createBackButton("Back", false);
        
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(20);

		mapCanvas = new Pane();
		mapCanvas.setMinHeight(300);
		mapCanvas.setMinWidth(300);
        centerBox.getChildren().addAll(createAiDifficultySelector(false), createMapSelector(mapCanvas, false));
        
        HBox backBtnPane = new HBox();
        backBtnPane.getChildren().add(backBtn);
        backBtnPane.setPadding(new Insets(20, 10, 20, 10));
        
        singleBox.setCenter(centerBox);
        singleBox.setTop(backBtnPane);
        singleBox.setBottom(startBtnPane);
        
        setBackgroundPane(singleMenu, singleBox);
	}

	private VBox createAiDifficultySelector(boolean online){
		
		Button upAiToggle = new Button();
        upAiToggle.setPrefWidth(30);
        upAiToggle.getStyleClass().add("aitoggleup");
        upAiToggle.setOnAction(online?e -> incrementOnlineAi():e -> incrementAi());
        
        Label displayAi = createBoundLabel(this.aiNumber, false, false);
        displayAi.getStyleClass().add("textfield");
        displayAi.setPrefWidth(30);
        
        Button downAiToggle = new Button();
        downAiToggle.setPrefWidth(30);
        downAiToggle.getStyleClass().add("aitoggledown");
        downAiToggle.setOnAction(online?e -> decrementOnlineAi():e -> decrementAi());
        
        VBox aiBox = new VBox();
        aiBox.setAlignment(Pos.CENTER);
        aiBox.getStyleClass().add("nopadbox");
        aiBox.maxHeightProperty().bind(upAiToggle.heightProperty().add(displayAi.heightProperty().add(downAiToggle.heightProperty())));
        aiBox.getChildren().addAll(upAiToggle, displayAi, downAiToggle);
        
        Label aiLabel = createLabel("Number of\nAI Players", false, false);
        
        HBox aiPane = new HBox();
        aiPane.setAlignment(Pos.CENTER);
        aiPane.getStyleClass().add("namebox");
        aiPane.setSpacing(20);
        aiPane.getChildren().addAll(aiBox, aiLabel);
        Label aiExplanation = createLabel("AI players will\nseek to\ndestroy you.", true, true);
        aiExplanation.setAlignment(Pos.CENTER);
        aiExplanation.setPrefWidth(200);
        if(online){
        	aiOnlineDifficultyChoice = new ChoiceBox<>();
            aiOnlineDifficultyChoice.setTooltip(new Tooltip("Change AI Difficulty"));
            aiOnlineDifficultyChoice.setPrefHeight(50);
            aiOnlineDifficultyChoice.setPrefWidth(200);
            aiOnlineDifficultyChoice.getStyleClass().add("textfield");
            aiOnlineDifficultyChoice.getItems().addAll("Easy", "Medium", "Hard", "Extreme");
            aiOnlineDifficultyChoice.getSelectionModel().select(1);
            aiOnlineDifficultyChoice.getSelectionModel().selectedItemProperty().addListener(new
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

            VBox aiDiffBox = new VBox();
            aiDiffBox.setAlignment(Pos.CENTER);
            aiDiffBox.getStyleClass().add("namebox");
            aiDiffBox.setSpacing(20);
            aiDiffBox.getChildren().addAll(createLabel("AI Difficulty:", false, false), aiOnlineDifficultyChoice, aiExplanation);
            aiDiffBox.setMinHeight(300);
            
            VBox aiContainer = new VBox();
            aiContainer.setAlignment(Pos.CENTER);
            aiContainer.getStyleClass().add("menubox");
            aiContainer.setSpacing(20);
            aiContainer.getChildren().addAll(aiPane, aiDiffBox);
            
    		VBox aiPad = new VBox();
    		aiPad.setAlignment(Pos.CENTER);
    		aiPad.getChildren().add(aiContainer);
    		
    		return aiPad;
        }
        else{
        aiDifficultyChoice = new ChoiceBox<>();
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
			}
        });

        VBox aiDiffBox = new VBox();
        aiDiffBox.setAlignment(Pos.CENTER);
        aiDiffBox.getStyleClass().add("namebox");
        aiDiffBox.setSpacing(20);
        aiDiffBox.getChildren().addAll(createLabel("AI Difficulty:", false, false), aiDifficultyChoice, aiExplanation);
        
        VBox aiContainer = new VBox();
        aiContainer.setAlignment(Pos.CENTER);
        aiContainer.getStyleClass().add("menubox");
        aiContainer.setSpacing(20);
        aiContainer.getChildren().addAll(aiPane, aiDiffBox);
        
		VBox aiPad = new VBox();
		aiPad.setAlignment(Pos.CENTER);
		aiPad.getChildren().add(aiContainer);
		
		return aiPad;
        
        }
	}
	
	private VBox createMapSelector(Pane mapCanvas, boolean online){
		
		VBox mapContainer = new VBox();
		mapContainer.getStyleClass().add("mapbox");
		mapContainer.setAlignment(Pos.CENTER);
		Label mapNameLabel = createBoundLabel(this.mapName, false, true);
		mapNameLabel.getStyleClass().add("maplabel");
		Image keyImage;
		if(online){
			keyImage = new Image("/resources/images/onlinekey.png");
		}
		else{
			keyImage = new Image("/resources/images/key.png");
		}
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
		
        Button rightMapToggle = new Button();
        rightMapToggle.getStyleClass().add("maptoggleright");
        rightMapToggle.setPrefWidth(90);
        
        rightMapToggle.setOnAction(online?e -> incrementOnlineMap():e -> incrementMap());
        
        Button leftMapToggle = new Button();
        leftMapToggle.getStyleClass().add("maptoggleleft");
        
        leftMapToggle.setPrefWidth(90);
        leftMapToggle.setOnAction(online?e -> decrementOnlineMap():e -> decrementMap());
		
        HBox mapPane = new HBox();
        mapPane.setAlignment(Pos.CENTER);
        mapPane.getChildren().addAll(leftMapToggle, mapContainer, rightMapToggle);
        
        rightMapToggle.maxHeightProperty().bind(mapPane.heightProperty());
        leftMapToggle.maxHeightProperty().bind(mapPane.heightProperty());
        
        //mapPane.maxHeightProperty().bind(aiBox.heightProperty());
        
        VBox mapPad = new VBox();
		mapPad.setAlignment(Pos.CENTER);
		mapPad.getChildren().add(mapPane);
		drawMap(mapCanvas, online);
		return mapPad;
	}
	
	private void drawMap(Pane mapCanvas, boolean online) {

		int height = 300;
		int width;
		if(online){
			width = 565;
		}else{
			width = 535;
		}
		//System.out.println(mapCanvas + "size " + mapCanvas.getWidth() + ", " + mapCanvas.getHeight());
		int xpadding = 250;
		int ypadding = 50;
		
		mapCanvas.getChildren().clear();
		Rectangle torch1 = new Rectangle();
		torch1.setWidth(60);
		torch1.setHeight(80);
		torch1.setFill(new ImagePattern(new Image("resources/images/torch.png")));
		torch1.setX(40);
		torch1.setY(30);
		
		Rectangle torch2 = new Rectangle();
		torch2.setWidth(60);
		torch2.setHeight(80);
		torch2.setFill(new ImagePattern(new Image("resources/images/torch.png")));
		torch2.setX(width-xpadding+150);
		torch2.setY(30);
		
		Rectangle torch3 = new Rectangle();
		torch3.setWidth(60);
		torch3.setHeight(80);
		torch3.setFill(new ImagePattern(new Image("resources/images/torch.png")));
		torch3.setX(width-xpadding+150);
		torch3.setY(170);
		
		Rectangle torch4 = new Rectangle();
		torch4.setWidth(60);
		torch4.setHeight(80);
		torch4.setFill(new ImagePattern(new Image("resources/images/torch.png")));
		torch4.setX(40);
		torch4.setY(170);
		
		mapCanvas.getChildren().addAll(torch1, torch2, torch3, torch4);
		
		double canvasWidth = width - xpadding;
		double canvasHeight = height - ypadding;
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
				rect.setFill(new ImagePattern(new Image("resources/images/" + image + ".png")));
					    	    
				rect.setStroke(Color.BLACK);
				rect.setX((xpadding/2) + xscalar*x);
				rect.setY((ypadding/2) + yscalar*y);
				mapCanvas.getChildren().add(rect);
			}
		}
		
		for(int x = 0; x < map.getSpawnPoints().size(); x++){
			Point pos = map.getSpawnPoints().get(x);
			Rectangle rect = new Rectangle(10, 10);
			if(x == 0){
				rect.setFill(new ImagePattern(new Image("resources/images/playerspawnpoint.png")));
			}
			else{
				if(online){
					rect.setFill(new ImagePattern(new Image("resources/images/playerspawnpoint.png")));
				}
				else{
					rect.setFill(new ImagePattern(new Image("resources/images/spawnpoint.png")));
				}
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
		creditsBox.getChildren().addAll(createLabel("Version: " + this.versionNumber, false, true), creditsLabel, createBackButton("Back", false));

		setBackgroundPane(creditsMenu, creditsBox);
	}

	private void initMainScene() {
		
		this.currentNameText = new SimpleStringProperty("Current Name:");
		nameText = createTextField("Enter Name");
		
        nameBtn = createButton("Set Name", btnWidth, btnHeight);
        nameBtn.setOnAction(e -> setName(nameText.getText()));
        
        Button singlePlayerBtn = createSceneButton("Single Player", 200, 50, mainScene, singleScene);
        Button multiPlayerBtn = createSceneButton("Multiplayer", 200, 50, mainScene, connectScene);
        Button creditsBtn = createSceneButton("Credits", 200, 50, mainScene, creditsScene);
        
        Image logoImage = new Image("resources/images/logo.png");
        ImageView logoImageView = new ImageView(logoImage);
        
        HBox logoPane = new HBox();
        logoPane.setSpacing(20);
        logoPane.setAlignment(Pos.CENTER);

        VBox menuBox = new VBox();
        menuBox.setSpacing(30);
        menuBox.setAlignment(Pos.CENTER);
        logoPane.getChildren().addAll(logoImageView, menuBox);
        
        musicSlider = new Slider();
        musicSlider.setMin(0);
        musicSlider.setMax(100);
        musicSlider.setValue(SettingsParser.getMusicVolume());
        musicSlider.setShowTickLabels(false);
        musicSlider.setShowTickMarks(false);
        musicSlider.setOnMouseReleased(e -> setMusic(musicMuted?0:(float)musicSlider.getValue()));
        
        soundSlider = new Slider();
        soundSlider.setMin(0);
        soundSlider.setMax(100);
        soundSlider.setValue(SettingsParser.getEffectsVolume());
        soundSlider.setShowTickLabels(false);
        soundSlider.setShowTickMarks(false);
        soundSlider.setOnDragDone(e -> setSound(soundMuted?0:(float)soundSlider.getValue()));
        
        muteMusicBtn = new CheckBox();
        muteMusicBtn.setOnAction(e -> setMusic(0));
        
        AudioManager.setMusicVolume(SettingsParser.getMusicVolume());
        AudioManager.setEffectsVolume(SettingsParser.getEffectsVolume());
        
        if(SettingsParser.getMusicVolume() == 0){
        	musicMuted = true;
        	muteMusicBtn.setSelected(true);
        }else{
        	musicMuted = false;
        	muteMusicBtn.setSelected(false);
        }
        muteSoundBtn = new CheckBox();
        muteSoundBtn.setOnAction(e -> setSound(0));
        
        if(SettingsParser.getEffectsVolume() == 0){
        	soundMuted = true;
        	muteSoundBtn.setSelected(true);
        }else{
        	soundMuted = false;
        	muteSoundBtn.setSelected(false);
        }
        
        VBox namePane = new VBox();
        namePane.setAlignment(Pos.CENTER);
        namePane.setSpacing(5);
        namePane.getStyleClass().add("namebox");
        
        HBox musicLabelBox = new HBox();
        musicLabelBox.setAlignment(Pos.CENTER);
        musicLabelBox.getChildren().addAll(createLabel("Music (Mute ", false, false), muteMusicBtn, createLabel(")", false, false));
        HBox soundLabelBox = new HBox();
        soundLabelBox.setAlignment(Pos.CENTER);
        soundLabelBox.getChildren().addAll(createLabel("Sounds (Mute ", false, false), muteSoundBtn, createLabel(")", false, false));
        
        VBox audioPane = new VBox();
        audioPane.setAlignment(Pos.CENTER);
        audioPane.setSpacing(5);
        audioPane.setPadding(new Insets(10, 0, 0, 0));
        audioPane.getChildren().addAll(musicLabelBox, musicSlider, soundLabelBox, soundSlider);
        
        fullScreenBtn = new CheckBox();
        fullScreenBtn.setOnAction(e -> fullScreen());
        
        HBox fullScreenBox = new HBox();
        fullScreenBox.setAlignment(Pos.CENTER);
        fullScreenBox.setSpacing(5);
        fullScreenBox.getChildren().addAll(createLabel("Fullscreen ", false, false), fullScreenBtn);
        
        namePane.getChildren().addAll(createBoundLabel(this.currentNameText, false, false), createBoundLabel(this.playerName, false, false),
        		nameText, nameBtn, audioPane, fullScreenBox);
        
        
        Button exitBtn = createButton("Exit", 200, 50);
        exitBtn.setOnAction(e -> System.exit(0));
        
        menuBox.getChildren().addAll(namePane, singlePlayerBtn, multiPlayerBtn, creditsBtn, exitBtn);
        setBackgroundPane(mainMenu, logoPane);
	}

	private void fullScreen() {
		if(fullScreenBtn.isSelected()){
			this.currentStage.setFullScreen(true);
		}
		else{
			this.currentStage.setFullScreen(false);
		}
	}

	private void setMusic(float volume) {
		if(muteMusicBtn.isSelected()){
			musicMuted = true;
			SettingsParser.setMusicVolume(volume);
			AudioManager.setMusicVolume(volume);
			System.out.println("SET AND STORED MUSIC " + volume);
		}else{
			musicMuted = false;
			SettingsParser.setMusicVolume((float)musicSlider.getValue());
			AudioManager.setMusicVolume((float)musicSlider.getValue());
			System.out.println("SET AND STORED MUSIC " + (float)musicSlider.getValue());
		}
		SettingsParser.storeSettings();
	}

	private void setSound(float volume) {
		
		if(muteSoundBtn.isSelected()){
			soundMuted = true;
			SettingsParser.setEffectsVolume(volume);
			AudioManager.setEffectsVolume(volume);
			System.out.println("SET AND STORED SOUND " + volume);
		}else{
			soundMuted = false;
			SettingsParser.setEffectsVolume((float)soundSlider.getValue());
			AudioManager.setEffectsVolume((float)soundSlider.getValue());
			System.out.println("SET AND STORED SOUND " + (float)soundSlider.getValue());
		}
		SettingsParser.storeSettings();
	}
	
	private void setBackgroundPane(BorderPane pane, Node content){
		Image mainImage = new Image("resources/images/background.png");
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
	
	private Parent createScene(Parent menu){
		BorderPane scene = new BorderPane();
		scene.setCenter(menu);
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
	
	private Button createSceneButton(String label, double width, double height, Parent currentScene, Parent nextScene){
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
	
	private void ready() {
		beep();
		readyButton.setText("Ready to Start");
		readyButton.setOnAction(e -> notReady());
		readyTorch.setFill(new ImagePattern(new Image("resources/images/torch.png")));
		
		try {
			this.client.readyToPlay(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void notReady() {
		beep();
		readyButton.setText("Not Ready");
		readyButton.setOnAction(e -> ready());
		readyTorch.setFill(new ImagePattern(new Image("resources/images/darktorch.png")));
		
		try {
			this.client.readyToPlay(false);
		} catch (IOException e) {
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

	private void leaveRoom(){
		if(this.client != null && this.client.isInRoom()){
			try {
				this.client.leaveRoom();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.expectingRoomCreation = false;
			this.expectingRoomJoin = false;
			previous();
		}
	}
	
	private void disconnect() {
		beep();
		
		if (this.client != null) {
			try {
				this.client.disconnect();
			} catch (Exception e) {
			}
		}
		this.expectingConnection = false;
	}
	
	private void decrementOnlineMap() {
		beep();
		try{
		int index = this.client.getMapID();
		//System.out.println("INDEX: " + index);
		if(index > 0){
			this.client.setRoomMapID(index-1);
			this.map = this.maps.get(index-1);
		}
		else{
			this.client.setRoomMapID(this.maps.size()-1);
			this.map = this.maps.get(this.maps.size()-1);
		}
		this.mapName.set(this.map.getName());
		drawMap(onlineMapCanvas, true);
		}catch(IOException e){
			e.printStackTrace();
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
		drawMap(mapCanvas, false);
	}

	private void incrementOnlineMap() {
		beep();
		try{
		int index = this.client.getMapID();
		//System.out.println("INDEX: " + index);
		if(index < (this.maps.size()-1)){
			this.client.setRoomMapID(index+1);
			this.map = this.maps.get(index+1);
		}
		else{
			this.client.setRoomMapID(0);
			this.map = this.maps.get(0);
		}
		this.mapName.set(this.map.getName());
		drawMap(onlineMapCanvas, true);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void incrementMap() {
		beep();
		int index = this.maps.indexOf(this.map);
		if(index < (this.maps.size()-1)){
			this.map = this.maps.get(index+1);
		}
		else{
			this.map = this.maps.get(0);
		}
		this.mapName.set(this.map.getName());
		drawMap(mapCanvas, false);
	}

	private void decrementOnlineAi(){
		if(this.client != null){
			this.humanPlayers = this.client.getRoom().getHumanPlayerNumber();
			this.aiPlayers = this.client.getRoom().getAIPlayerNumber();
		}
		//System.out.println("humans: "  + this.humanPlayers);
		if(this.aiNumber.get() > 0){
				
				try {
					this.client.removeAI();
				} catch (IOException e) {
					e.printStackTrace();
				}
				aiNumber.set(this.aiPlayers-1);
			}
		}
	
	
	private void decrementAi() {
		beep();
		if(this.aiNumber.get() > 1)aiNumber.set(this.aiNumber.get()-1);
	}

	private void incrementOnlineAi(){
		if(this.client != null){
			this.humanPlayers = this.client.getRoom().getHumanPlayerNumber();
			this.aiPlayers = this.client.getRoom().getAIPlayerNumber();
		}
		//System.out.println("humans: "  + this.humanPlayers);
		
			if(this.aiNumber.get() < (4 - this.humanPlayers)){
			try {
				this.client.addAI();
				for(ClientServerAI ai : client.getRoom().getAIPlayerList()){
					try {
						client.setAIDifficulty(ai.getID(), aiDiff);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			aiNumber.set(this.aiPlayers+1);}
			
	}
	
	private void incrementAi() {
		beep();
		if(this.aiNumber.get() < 3)aiNumber.set(this.aiNumber.get()+1);
	}
	
	private void connect() {
		beep();
		enterLabel.setText("Enter Server Details:");
		String host = ipText.getText().trim();
		this.client = null;
		try{
			int port = Integer.parseInt(portNum.getText());
		
			//System.out.println("Attempting connection to: " + host + ", port = " + port);
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
	}

	private void displayRooms() {
		
		List<ClientServerLobbyRoom> rooms = this.client.getRoomList();
		
		this.roomsBox.getChildren().clear();
		
		for(ClientServerLobbyRoom room : rooms){
			
			VBox roomContainer = new VBox();
			roomContainer.setMinHeight(100);
			roomContainer.setMinWidth(210);
			roomContainer.setSpacing(5);
			roomContainer.setAlignment(Pos.CENTER);
			roomContainer.getStyleClass().add("namebox");
			Label roomID = createLabel("Room " + room.getID() +":", false, false);
			Label roomName = createLabel(room.getName(), false, true);
			roomName.setAlignment(Pos.CENTER);
			roomName.getStyleClass().add("maplabel");
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
			e.printStackTrace();
		}
	}

	private void displayPlayers() {
		
		List<ClientServerPlayer> connectedPlayers = this.client.getPlayerList();
		
		this.playersBox.getChildren().clear();
		this.playersBox2.getChildren().clear();
		
		for(ClientServerPlayer player : connectedPlayers){
			this.playersBox.getChildren().add(createLabel("- Player ID [" + player.getID() + "] Name:   " + player.getName(), true, true));
			this.playersBox2.getChildren().add(createLabel("- P" + player.getID() + ":   " + player.getName(), true, true));
		}
		
		if(this.client.isInRoom()){
			ClientServerRoom room = this.client.getRoom();
			aiNumber.set(room.getAIPlayerNumber());
			this.map = this.maps.get(room.getMapID());
			this.mapName.set(this.map.getName());
			readyPane.getChildren().clear();
			readyPane.getChildren().add(createLabel("Game will begin when all\nplayers click ready!", false, false));
			for(ClientServerPlayer player : room.getHumanPlayerList()){
				System.out.println(player.getName());
				Rectangle torch = new Rectangle();
				torch.setWidth(40);
				torch.setHeight(40);
				if(player.isReadyToPlay()){
					torch.setFill(new ImagePattern(new Image("resources/images/torch.png")));
				}else{
					torch.setFill(new ImagePattern(new Image("resources/images/darktorch.png")));
				}
				HBox playerBox = new HBox();
				playerBox.setSpacing(20);
				playerBox.setAlignment(Pos.CENTER);
				Label playerName = createLabel(player.getName(), true, true);
				playerName.setPrefHeight(20);
				playerName.setPrefWidth(180);
				playerBox.getChildren().addAll(playerName, torch);
				if(!player.getName().equals(this.playerName.get())){
					readyPane.getChildren().add(playerBox);
				}
			}
			List<ClientServerAI> ais = room.getAIPlayerList();
			if(ais.size() > 0){
				AIDifficulty diff = room.getAIPlayerList().get(0).getDifficulty();
				System.out.println("GOT DIFF: " + diff);
				int index = 1;
				switch(diff){
				case EASY: index = 0;aiDiff = AIDifficulty.EASY;break;
				case MEDIUM: index = 1;aiDiff = AIDifficulty.MEDIUM;break;
				case HARD: index = 2;aiDiff = AIDifficulty.HARD;break;
				case EXTREME: index = 3;aiDiff = AIDifficulty.EXTREME;break;
				}
				aiOnlineDifficultyChoice.getSelectionModel().select(index);
			}
				try {
					if(room.getAIPlayerNumber() + room.getHumanPlayerNumber() > room.getMaxPlayer()){
						this.client.removeAI();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			drawMap(onlineMapCanvas, true);
		}
	}

	private void previous() {
		beep();
		
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		//System.out.println("am at " + this.currentScene);
		Parent lastScene = this.previousScenes.pop();
		this.currentStage.getScene().setRoot(lastScene);
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
		
		this.roomCreationLabel.setText("Create and join a room\nwith these settings");
		enterLabel.setText("Enter Server Details:");
		this.currentNameText.set("Current Name:");
		
		resetButton(connectBtn, "Connect", e -> connect());
		resetButton(createRoomBtn, "Create New Room", e -> createRoom());
		
		this.aiNumber.set(1);
		this.humanPlayers = 1;
		if(this.currentStage.isFullScreen()){
			fullScreenBtn.setSelected(true);
		}
		else{
			fullScreenBtn.setSelected(false);
		}
		}
	
	
	private void advance(Parent thisScene, Parent nextScene) {
		
		beep();
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();
		
		
		this.previousScenes.push(thisScene);
		System.out.println("Added " + thisScene);
		System.out.println(this.previousScenes);
		this.currentStage.getScene().setRoot(nextScene);
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}

	public void setName(String string){
		
		beep();
		string = string.trim();
		if(string.length() > 0 && string.length() < 12){
			this.playerName.set(string);
			SettingsParser.setPlayerName(string);
			SettingsParser.storeSettings();
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
		System.out.println("Ai difficulty: " + this.aiDiff);
		
		float musicVolume = 50;
		if(muteMusicBtn.isSelected()){
			musicVolume = 0;
		}else{
			musicVolume = (float) musicSlider.getValue();
		}
		
		float soundVolume = 50;
		if(muteSoundBtn.isSelected()){
			soundVolume = 0;
		}else{
			soundVolume = (float) soundSlider.getValue();
		}
		
		new Game(this, mapCopy, playerName, controls, aiNum, this.aiDiff, musicVolume, soundVolume, this.currentStage.isFullScreen());
	}

	@Override
	public void disconnected() {

		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				client.exit();
				client = null;
				previous();
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
		//System.out.println("Adding display rooms event to queue");
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				//System.out.println("Displaying Rooms");
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
					mapName.set(". . .");
					Label label = createLabel("Loading...", false, true);
					label.setMinHeight(300);
					label.setMinWidth(565);
					onlineMapCanvas.getChildren().clear();
					onlineMapCanvas.getChildren().add(label);
					advance(serverScene, roomScene);
					roomCreationLabel.setText("Create and join a room\nwith these settings");
					resetButton(createRoomBtn, "Create New Room", e -> createRoom());
					expectingRoomCreation = false;
					aiOnlineDifficultyChoice.getSelectionModel().selectedItemProperty().addListener(new
			                ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> ob,
								String oldValue, String newValue) {
							for(ClientServerAI ai : client.getRoom().getAIPlayerList()){
								try {
									client.setAIDifficulty(ai.getID(), aiDiff);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							System.out.println("SET ALL AI TO: " + aiDiff);
						}
			        });
				}
				else if(expectingRoomJoin){
					advance(serverScene, roomScene);
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
					roomCreationLabel.setText("Create and join a room\nwith these settings\n( Something went wrong! )");
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
	}

	@Override
	public void alreadyInRoom() {
		
	}

	@Override
	public void haveLeftRoom() {
	}

	@Override
	public void gameStarted() {
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				GameState gameState = client.getGameState();
				Platform.setImplicitExit(false);
				new OnlineGame(ui, client, gameState, playerName.get(), controls, currentStage.isFullScreen());
			}
			   
		});
	}

	@Override
	public void gameStateReceived() {
	}

	@Override
	public void gameEnded() {
		
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
	}
}
