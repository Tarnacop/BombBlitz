package bomber.UI;

import static bomber.AI.AIDifficulty.EASY;
import static bomber.AI.AIDifficulty.EXTREME;
import static bomber.AI.AIDifficulty.HARD;
import static bomber.AI.AIDifficulty.MEDIUM;
import static bomber.game.Constants.*;
import static bomber.game.Response.DOWN_MOVE;
import static bomber.game.Response.LEFT_MOVE;
import static bomber.game.Response.PAUSE_GAME;
import static bomber.game.Response.PLACE_BOMB;
import static bomber.game.Response.RIGHT_MOVE;
import static bomber.game.Response.UP_MOVE;
import static javafx.geometry.Orientation.VERTICAL;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.geometry.Pos.TOP_LEFT;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

/**
 *
 * @author Owen
 * @version 1.3
 * @since 2017-03-20
 *
 *        UserInterface class for "Bomb Blitz" Game Application (2017 Year 2
 *        Team Project, Team B1). Contains all methods for creating the JavaFX
 *        Menu for interfacing between users of the software and all other
 *        subsystems. Implements the ClientNetInterface to allow interface with
 *        the networking subsystem.
 */
public class UserInterface extends Application implements ClientNetInterface {

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
	private FlowPane serverPlayersBox;
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
	private FlowPane roomPlayersBox;
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
	private boolean musicMuted;
	private boolean soundMuted;
	private CheckBox fullScreenBtn;
	private double screenHeight;
	private double screenWidth;
	private double boxHeight;
	private double boxWidth;
	private Font smallFont;
	private CheckBox rememberServerBtn;
	private boolean gotServer;
	protected boolean gameEnded;
	private Button leaveRoomBtn;
	private OnlineGame onlineGame;
	private List<ClientServerPlayer> connectedPlayers;
	private BorderPane tutorialMenu;
	private Parent tutorialScene;
	private CheckBox wasdBtn;
	private ImageView currentTutorial;
	private Image tutorialImage;
	private Image wasdTutorialImage;
	private boolean wasd;

	/**
	 * Constructor to create a new UserInterface object and initialise fields.
	 */
	public UserInterface() {

		// Assign this object to a variable so it can be passed into any
		// Runnable.
		this.ui = this;

		// Initialise the SettingsParser object.
		SettingsParser.init();

		// Resource management.
		this.font = Font.loadFont(UserInterface.class.getResource(FONT_PATH)
				.toExternalForm(), FONT_SIZE);
		this.smallFont = Font.loadFont(
				UserInterface.class.getResource(FONT_PATH).toExternalForm(),
				SMALL_FONT_SIZE);
		this.css = UserInterface.class.getResource(CSS_PATH).toExternalForm();

		// Initialise property fields for display.
		this.playerName = new SimpleStringProperty(
				SettingsParser.getPlayerName());
		this.aiNumber = new SimpleIntegerProperty(1);
		this.roomNumber = new SimpleIntegerProperty(4);
		this.roomCreationLabel = createLabel(
				"Create and join a room\nwith these settings", false, true);

		// Default AI Difficulty.
		this.aiDiff = MEDIUM;

		// Initialise maps data from the Maps object.
		Maps maps = new Maps();
		this.maps = maps.getMaps();
		this.map = this.maps.get(0);
		this.mapName = new SimpleStringProperty(this.map.getName());

		// Initialise the control scheme data.
		this.controls = new HashMap<Response, Integer>();
		this.controls.put(PLACE_BOMB, GLFW_KEY_SPACE);
		this.controls.put(UP_MOVE, GLFW_KEY_UP);
		this.controls.put(DOWN_MOVE, GLFW_KEY_DOWN);
		this.controls.put(LEFT_MOVE, GLFW_KEY_LEFT);
		this.controls.put(RIGHT_MOVE, GLFW_KEY_RIGHT);
		this.controls.put(PAUSE_GAME, GLFW_KEY_P);

		// Get the monitor size.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.screenHeight = screenSize.height;
		this.screenWidth = screenSize.width;
		this.windowHeight = screenHeight * SCREEN_HEIGHT_SCALAR;
		this.windowWidth = screenWidth * SCREEN_WIDTH_SCALAR;
		this.boxHeight = windowHeight / MENU_BOX_HEIGHT_SCALAR;
		this.boxWidth = windowWidth / MENU_BOX_WIDTH_SCALAR;

		// Initialise the audio manager
		AudioManager.init();
	}

	/**
	 * Begin this application (invoked the JavaFX 'launch()' method so this menu
	 * can be started from another class).
	 */
	public static void begin() {
		launch();
	}

	/**
	 * 'start' method for the JavaFX Application.
	 * 
	 * @param primaryStage
	 *            the primary stage
	 */
	@Override
	public void start(Stage primaryStage) {

		// Set the minimum width and height.
		currentStage = primaryStage;
		currentStage.setMinHeight(windowHeight);
		currentStage.setMinWidth(windowWidth);

		// Set the title and create the previous scenes stack.
		primaryStage.setTitle(GAME_NAME);
		previousScenes = new Stack<Parent>();

		// Initialise all the scenes in the menu and show to the user.
		initScenes();
		primaryStage.setScene(new Scene(mainScene));
		primaryStage.show();

		primaryStage.getIcons().add(new Image(BOMB_PATH));

		// If the stage is closed, disconnect from the server just in case.
		primaryStage.setOnCloseRequest(e -> disconnect());

	}

	/**
	 * Initialise the different scenes for the game menu.
	 */
	private void initScenes() {

		// Create the BorderPanes.
		mainMenu = new BorderPane();
		tutorialMenu = new BorderPane();
		creditsMenu = new BorderPane();
		roomMenu = new BorderPane();
		connectMenu = new BorderPane();
		serverMenu = new BorderPane();
		singleMenu = new BorderPane();

		// Apply background and styling.
		connectScene = createScene(connectMenu);
		serverScene = createScene(serverMenu);
		singleScene = createScene(singleMenu);
		mainScene = createScene(mainMenu);
		tutorialScene = createScene(tutorialMenu);
		creditsScene = createScene(creditsMenu);
		roomScene = createScene(roomMenu);

		// Initialise each individual scene.
		initMainScene();
		initTutorialScene();
		initCreditsScene();
		initSingleScene();
		initConnectScene();
		initServerScene();
		initRoomScene();
	}

	/**
	 * Initialise the main menu scene.
	 */
	private void initMainScene() {

		// 'currentNameText' - the label above the name setter, used to display
		// error info when players try
		// and set their names (ie if it's too short, or too long).
		currentNameText = new SimpleStringProperty("Current Name:");

		// 'nameText' - textfield for name input
		nameText = createTextField("Enter Name");

		// 'nameBtn' - button to set the name text.
		nameBtn = createButton("Set Name", MENU_BUTTON_WIDTH,
				MENU_BUTTON_HEIGHT);
		nameBtn.setOnAction(e -> setName(nameText.getText()));

		// Create the buttons to advance the scenes.
		Button singlePlayerBtn = createSceneButton("Single Player",
				MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, mainScene, singleScene);
		Button multiPlayerBtn = createSceneButton("Multiplayer",
				MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, mainScene, connectScene);
		Button tutorialBtn = createSceneButton("Tutorial", MENU_BUTTON_WIDTH,
				MENU_BUTTON_HEIGHT, mainScene, tutorialScene);
		Button creditsBtn = createSceneButton("Credits", MENU_BUTTON_WIDTH,
				MENU_BUTTON_HEIGHT, mainScene, creditsScene);
		
		// Create menu button to exit the game
		Button exitBtn = createButton("Exit", MENU_BUTTON_WIDTH,
				MENU_BUTTON_HEIGHT);
		exitBtn.setOnAction(e -> System.exit(0));

		// Create logo image for the front screen.
		Image logoImage = new Image(LOGO_PATH);
		ImageView logoImageView = new ImageView(logoImage);

		// Create box to hold the menu selection.
		VBox menuVBox = new VBox();
		menuVBox.setSpacing(LARGE_PAD);
		menuVBox.setAlignment(CENTER);

		// Create box to hold the logo and the menu selection.
		HBox logoHBox = new HBox();
		logoHBox.setSpacing(MEDIUM_PAD);
		logoHBox.setAlignment(CENTER);
		logoHBox.getChildren().addAll(logoImageView, menuVBox);

		// 'musicSlider' - slider to control music volume
		musicSlider = new Slider();
		musicSlider.setMin(0);
		musicSlider.setMax(100);
		musicSlider.setValue(SettingsParser.getMusicVolume());
		musicSlider.setShowTickLabels(false);
		musicSlider.setShowTickMarks(false);
		musicSlider.setOnMouseReleased(e -> setMusic(musicMuted ? 0
				: (float) musicSlider.getValue()));

		// 'soundSlider' - slider to control sound effects volume
		soundSlider = new Slider();
		soundSlider.setMin(0);
		soundSlider.setMax(100);
		soundSlider.setValue(SettingsParser.getEffectsVolume());
		soundSlider.setShowTickLabels(false);
		soundSlider.setShowTickMarks(false);
		soundSlider.setOnDragDone(e -> setSound(soundMuted ? 0
				: (float) soundSlider.getValue()));

		// Set the volumes from the settings extracted from the SettingsParser.
		AudioManager.setMusicVolume(SettingsParser.getMusicVolume());
		AudioManager.setEffectsVolume(SettingsParser.getEffectsVolume());

		// 'muteMusicBtn' - checkbox for muting the music volume.
		muteMusicBtn = new CheckBox();
		muteMusicBtn.setOnAction(e -> setMusic(0));
		if (SettingsParser.getMusicVolume() == 0) {
			musicMuted = true;
			muteMusicBtn.setSelected(true);
		} else {
			musicMuted = false;
			muteMusicBtn.setSelected(false);
		}

		// 'muteSoundBtn' - checkbox for muting the sound effect volume.
		muteSoundBtn = new CheckBox();
		muteSoundBtn.setOnAction(e -> setSound(0));
		if (SettingsParser.getEffectsVolume() == 0) {
			soundMuted = true;
			muteSoundBtn.setSelected(true);
		} else {
			soundMuted = false;
			muteSoundBtn.setSelected(false);
		}

		// Create box to hold the settings options.
		VBox settingsVBox = new VBox();
		settingsVBox.setAlignment(CENTER);
		settingsVBox.setSpacing(TINY_PAD);
		settingsVBox.getStyleClass().add("namebox");

		// Create boxes to hold the info and mute buttons for the music and
		// sound effects volume.
		HBox musicHBox = new HBox();
		musicHBox.setAlignment(CENTER);
		musicHBox.getChildren().addAll(
				createLabel("Music (Mute ", false, false), muteMusicBtn,
				createLabel(")", false, false));
		HBox soundHBox = new HBox();
		soundHBox.setAlignment(CENTER);
		soundHBox.getChildren().addAll(
				createLabel("Sounds (Mute ", false, false), muteSoundBtn,
				createLabel(")", false, false));

		VBox audioVBox = new VBox();
		audioVBox.setAlignment(CENTER);
		audioVBox.setSpacing(TINY_PAD);
		audioVBox.setPadding(new Insets(SMALL_PAD, 0, 0, 0));
		audioVBox.getChildren().addAll(musicHBox, musicSlider, soundHBox,
				soundSlider);

		// 'fullScreenBtn' - checkbox to set the game to fullscreen.
		fullScreenBtn = new CheckBox();
		fullScreenBtn.setOnAction(e -> fullScreen());

		// Create box to contain the fullscreen label and checkbox.
		HBox fullScreenHBox = new HBox();
		fullScreenHBox.setAlignment(CENTER);
		fullScreenHBox.setSpacing(TINY_PAD);
		fullScreenHBox.getChildren().addAll(
				createLabel("Fullscreen ", false, false), fullScreenBtn);

		// 'fullScreenBtn' - checkbox to set the game to fullscreen.
		wasdBtn = new CheckBox();
		wasdBtn.setOnAction(e -> toggleWasd());

		// Create box to contain the fullscreen label and checkbox.
		HBox wasdHBox = new HBox();
		wasdHBox.setAlignment(CENTER);
		wasdHBox.setSpacing(TINY_PAD);
		wasdHBox.getChildren().addAll(
		createLabel("WASD controls ", false, false), wasdBtn);

		// Add settings options to the settings container.
		settingsVBox.getChildren().addAll(
				createBoundLabel(this.currentNameText, false, false),
				createBoundLabel(this.playerName, false, false), nameText,
				nameBtn, audioVBox, fullScreenHBox, wasdHBox);

		// Add settings container and other menu buttons to the menu container.
		menuVBox.getChildren().addAll(settingsVBox, singlePlayerBtn,
				multiPlayerBtn, tutorialBtn, creditsBtn, exitBtn);

		// Set the background of the scene.
		setBackgroundPane(mainMenu, logoHBox);
	}
	
	/**
	 * Initialise the tutorial menu.
	 */
	private void initTutorialScene() {

		// Create story image.
		Image storyImage = new Image(STORY_PATH);
		ImageView storyImageView = new ImageView(storyImage);
		storyImageView.getStyleClass().add("creditsbox");	
		
		tutorialImage = new Image(TUTORIAL_PATH);
		wasdTutorialImage = new Image(WASD_TUTORIAL_PATH);
		
		//'currentTutorial' - Tutorial ImageView which can be changed later.
		currentTutorial = new ImageView(tutorialImage);
		
		//Create a container to hold the story image and tutorial image.
		HBox tutorialHBox = new HBox();
		tutorialHBox.setAlignment(CENTER);
		tutorialHBox.setSpacing(HUGE_PAD);
		tutorialHBox.getStyleClass().add("creditsbox");
		tutorialHBox.getChildren().addAll( 
		storyImageView, currentTutorial);
		
		//Create a container to hold the tutorial container and back button.
		VBox infoVBox = new VBox();
		infoVBox.setAlignment(CENTER);
		infoVBox.setSpacing(SMALL_PAD);
		infoVBox.setPadding(new Insets(0, LARGE_PAD, 0, LARGE_PAD));
		infoVBox.getChildren().addAll(tutorialHBox, createBackButton("Back", false));

		//Set the background of the menu.
		setBackgroundPane(tutorialMenu, infoVBox);
	}
	
	/**
	 * Initialise the credits menu.
	 */
	private void initCreditsScene() {

		Label creditsLabel = createLabel(CREDITS, false, true);
		creditsLabel.getStyleClass().add("creditsbox");
		VBox creditsBox = new VBox();
		creditsBox.setAlignment(CENTER);
		creditsBox.setSpacing(MEDIUM_PAD);
		creditsBox.getChildren().addAll(
		createLabel("Version: " + VERSION_NUMBER, false, true),
		creditsLabel, createBackButton("Back", false));

		setBackgroundPane(creditsMenu, creditsBox);
	}

	/**
	 * Initialise the Scene within an online multiplayer room.
	 */
	private void initRoomScene() {

		BorderPane roomBox = new BorderPane();

		leaveRoomBtn = createButton("Leave Room", LARGE_MENU_BUTTON_WIDTH,
				MENU_BUTTON_HEIGHT);
		leaveRoomBtn.setOnAction(e -> leaveRoom());

		HBox centerBox = new HBox();
		centerBox.setAlignment(CENTER);
		centerBox.setSpacing(MEDIUM_PAD);

		onlineMapCanvas = new Pane();
		onlineMapCanvas.setMinHeight(MAP_CANVAS_HEIGHT);
		onlineMapCanvas.setMinWidth(MAP_CANVAS_WIDTH);
		aiOnlineDifficultyChoice = new ChoiceBox<>();
		centerBox.getChildren().addAll(
				createAiDifficultySelector(aiOnlineDifficultyChoice, true),
				createMapSelector(onlineMapCanvas, true));

		HBox backBtnPane = new HBox();
		backBtnPane.getChildren().add(leaveRoomBtn);
		backBtnPane.setPadding(new Insets(MEDIUM_PAD, SMALL_PAD, MEDIUM_PAD,
				SMALL_PAD));

		Label playersTitle = createLabel("Online Players:", false, true);

		roomPlayersBox = new FlowPane(VERTICAL);
		roomPlayersBox.setVgap(TINY_PAD);
		roomPlayersBox.setHgap(MEDIUM_PAD);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setMaxHeight(boxHeight - MASSIVE_PAD);
		scrollPane.setMinWidth(boxWidth + MASSIVE_PAD);
		scrollPane.setVbarPolicy(AS_NEEDED);
		scrollPane.setContent(roomPlayersBox);

		VBox playersListPane = new VBox();
		playersListPane.getStyleClass().add("wideclearbox");
		playersListPane.setSpacing(SMALL_PAD);
		playersListPane.setMinWidth(boxWidth + MASSIVE_PAD);
		playersListPane.getChildren().addAll(playersTitle, scrollPane);

		readyTorch = new Rectangle();
		readyTorch.setWidth(TORCH_WIDTH);
		readyTorch.setHeight(TORCH_HEIGHT);
		readyTorch.getStyleClass().add("mapbox");
		readyTorch.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));

		VBox readyBox = new VBox();
		readyBox.getStyleClass().add("namebox");
		readyBox.setSpacing(MEDIUM_PAD);
		readyBox.setAlignment(CENTER);
		readyBox.setMinWidth(boxHeight);
		readyBox.setMinHeight(boxHeight);
		readyBox.setMaxHeight(boxHeight);
		readyButton = createButton("Not Ready", LARGE_MENU_BUTTON_WIDTH,
				LARGE_MENU_BUTTON_HEIGHT);
		readyButton.setOnAction(e -> ready());

		HBox torchBox = new HBox();
		torchBox.setSpacing(SMALL_PAD);
		torchBox.setAlignment(CENTER);
		torchBox.getChildren().addAll(
				createLabel("Click to\ntoggle Ready:", false, false),
				readyTorch);
		readyBox.getChildren().addAll(torchBox, readyButton);

		readyPane = new VBox();
		readyPane.getStyleClass().add("menubox");
		readyPane.setSpacing(LITTLE_PAD);
		readyPane.setAlignment(CENTER);
		readyPane.setMinWidth(boxWidth + MEDIUM_PAD);
		readyPane.setMinHeight(boxHeight);
		readyPane.setMaxHeight(boxHeight);
		readyPane.getChildren().add(
				createLabel("Game will begin when all\nplayers click ready!",
						false, false));

		HBox playersReadyBox = new HBox();
		playersReadyBox.setAlignment(CENTER);
		playersReadyBox.setSpacing(LARGE_PAD);
		playersReadyBox.setPadding(new Insets(SMALL_PAD));
		playersReadyBox.getChildren().addAll(playersListPane, readyPane,
				readyBox);

		VBox centerPane = new VBox();
		centerPane.setAlignment(CENTER);
		centerPane.setSpacing(SMALL_PAD);
		centerPane.getChildren().addAll(centerBox, playersReadyBox);

		roomBox.setCenter(centerPane);
		roomBox.setTop(backBtnPane);

		setBackgroundPane(roomMenu, roomBox);
	}

	private void initServerScene() {

		Button disconnectBtn = createBackButton("Disconnect", true);

		HBox backBox = new HBox();
		backBox.setAlignment(CENTER_LEFT);
		backBox.setPadding(new Insets(MEDIUM_PAD, SMALL_PAD, MEDIUM_PAD,
				SMALL_PAD));
		backBox.getChildren().add(disconnectBtn);

		createRoomBtn = createButton("Create New Room",
				LARGE_MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
		createRoomBtn.setOnAction(e -> createRoom());

		Button upRoomNumToggle = new Button();
		upRoomNumToggle.setPrefWidth(LARGE_PAD);
		upRoomNumToggle.getStyleClass().add("aitoggleup");
		upRoomNumToggle.setOnAction(e -> incrementRoomNum());

		Label displayRoomNum = createBoundLabel(this.roomNumber, false, false);
		displayRoomNum.getStyleClass().add("textfield");
		displayRoomNum.setPrefWidth(LARGE_PAD);

		Button downRoomNumToggle = new Button();
		downRoomNumToggle.setPrefWidth(LARGE_PAD);
		downRoomNumToggle.getStyleClass().add("aitoggledown");
		downRoomNumToggle.setOnAction(e -> decrementRoomNum());

		VBox roomNumBox = new VBox();
		roomNumBox.setAlignment(CENTER);
		roomNumBox.getStyleClass().add("nopadbox");
		roomNumBox.maxHeightProperty().bind(
				upRoomNumToggle.heightProperty().add(
						displayRoomNum.heightProperty().add(
								downRoomNumToggle.heightProperty())));
		roomNumBox.getChildren().addAll(upRoomNumToggle, displayRoomNum,
				downRoomNumToggle);

		Label roomNumLabel = createLabel("Room\nCapacity", false, false);

		HBox roomNumPane = new HBox();
		roomNumPane.setAlignment(CENTER);
		roomNumPane.getStyleClass().add("namebox");
		roomNumPane.setSpacing(MEDIUM_PAD);
		roomNumPane.setMinWidth(PANE_WIDTH);
		roomNumPane.getChildren().addAll(roomNumBox, roomNumLabel);

		roomNameField = createTextField("Enter Name");

		VBox roomNameBox = new VBox();
		roomNameBox.setSpacing(SMALL_PAD);
		roomNameBox.getStyleClass().add("namebox");
		roomNameBox.setAlignment(CENTER);
		roomNameBox.setMinWidth(THIN_PANE_WIDTH);
		roomNameBox.getChildren().addAll(
				createLabel("Room Name:", false, false), roomNameField);

		VBox roomDisplay = new VBox();
		roomDisplay.setSpacing(MEDIUM_PAD);
		roomDisplay.setAlignment(CENTER);
		roomDisplay.setMinWidth(THICK_PANE_WIDTH);
		roomDisplay.getChildren().addAll(this.roomCreationLabel, createRoomBtn);

		Rectangle torch1 = new Rectangle();
		torch1.setWidth(SMALL_TORCH_WIDTH);
		torch1.setHeight(SMALL_TORCH_HEIGHT);
		torch1.setFill(new ImagePattern(new Image(TORCH_PATH)));

		Rectangle torch2 = new Rectangle();
		torch2.setWidth(SMALL_TORCH_WIDTH);
		torch2.setHeight(SMALL_TORCH_HEIGHT);
		torch2.setFill(new ImagePattern(new Image(TORCH_PATH)));

		HBox createRoomPane = new HBox();
		createRoomPane.setAlignment(CENTER);
		createRoomPane.setSpacing(MEDIUM_PAD);
		createRoomPane.getStyleClass().add("wideclearbox");
		createRoomPane.maxWidthProperty().bind(
				roomNumPane.widthProperty().add(
						roomNameBox.widthProperty().add(
								roomDisplay.widthProperty())));
		createRoomPane.getChildren().addAll(torch1, roomNumPane, roomNameBox,
				roomDisplay, torch2);

		Label roomsTitle = createLabel(
				"Rooms:								( Join or create a room to play a match! )",
				false, true);

		roomsBox = new HBox();
		roomsBox.setSpacing(BIG_PAD);

		ScrollPane scrollRooms = new ScrollPane();
		scrollRooms.setMinHeight(boxHeight + LARGE_PAD);
		scrollRooms.setVbarPolicy(NEVER);
		scrollRooms.setHbarPolicy(AS_NEEDED);
		scrollRooms.setFitToHeight(true);
		scrollRooms.setFitToWidth(true);
		scrollRooms.setContent(roomsBox);

		VBox roomsListPane = new VBox();
		roomsListPane.setSpacing(SMALL_PAD);
		roomsListPane.setAlignment(TOP_LEFT);
		roomsListPane.minHeightProperty().bind(
				roomsTitle.minHeightProperty().add(
						scrollRooms.minHeightProperty()));
		roomsListPane.getChildren().addAll(roomsTitle, scrollRooms);

		Label playersTitle = createLabel("Online Players:", false, true);

		serverPlayersBox = new FlowPane();
		serverPlayersBox.setVgap(SMALL_PAD);
		serverPlayersBox.setHgap(SMALL_PAD);
		serverPlayersBox.setMinHeight(THIN_PANE_HEIGHT);

		ScrollPane scrollPlayers = new ScrollPane();
		scrollPlayers.setVbarPolicy(AS_NEEDED);
		scrollPlayers.setContent(serverPlayersBox);

		VBox playersListPane = new VBox();
		playersListPane.setSpacing(SMALL_PAD);
		playersListPane.getChildren().addAll(playersTitle, scrollPlayers);
		playersListPane.minHeightProperty().bind(
				playersTitle.minHeightProperty().add(
						serverPlayersBox.minHeightProperty().add(HUGE_PAD)));

		VBox roomsPlayersPane = new VBox();
		roomsPlayersPane.setSpacing(BIG_PAD);
		roomsPlayersPane.setAlignment(CENTER);
		roomsPlayersPane.getChildren().addAll(roomsListPane, playersListPane);
		BorderPane serverPane = new BorderPane();
		VBox serverBox = new VBox();
		serverBox.setSpacing(MEDIUM_PAD);
		serverBox.setAlignment(CENTER);
		roomsPlayersPane.getStyleClass().add("wideclearbox");
		roomsPlayersPane.minHeightProperty().bind(
				roomsListPane.minHeightProperty().add(
						playersListPane.minHeightProperty().add(50)));
		serverBox.minHeightProperty()
				.bind(roomsPlayersPane.minHeightProperty());
		serverBox.getChildren().addAll(createRoomPane, roomsPlayersPane);
		serverPane.setTop(backBox);
		serverPane.setPadding(new Insets(0, 20, 20, 20));
		serverPane.setCenter(serverBox);
		setBackgroundPane(serverMenu, serverPane);
	}

	private void decrementRoomNum() {
		if (roomNumber.get() > 2)
			roomNumber.set(roomNumber.get() - 1);
	}

	private void incrementRoomNum() {
		if (roomNumber.get() < 4)
			roomNumber.set(roomNumber.get() + 1);
	}

	private void initConnectScene() {

		enterLabel = createLabel("Enter Server Details:", false, false);

		HBox ipBox = new HBox();

		ipText = createTextField("IP Address");
		ipText.setMinWidth(200);
		portNum = createTextField("Port Number");
		portNum.setMinWidth(200);
		gotServer = (!SettingsParser.getServerPort().equals(""))
				&& (!SettingsParser.getServerIp().equals(""));
		if (gotServer) {
			ipText.setText(SettingsParser.getServerIp());
			portNum.setText(SettingsParser.getServerPort());
		}
		ipBox.setSpacing(SMALL_PAD);
		ipBox.setPrefSize(THIN_PANE_WIDTH, TINY_PANE_HEIGHT);
		ipBox.setAlignment(CENTER);
		ipBox.getChildren().addAll(ipText, createLabel(":", false, false),
				portNum);

		connectBtn = createButton("Connect", HUGE_MENU_BUTTON_WIDTH,
				LARGE_MENU_BUTTON_HEIGHT);
		connectBtn.setOnAction(e -> connect());

		Button backBtn = createBackButton("Cancel", false);

		rememberServerBtn = new CheckBox();
		rememberServerBtn.setSelected(gotServer);

		HBox rememberServerBox = new HBox();
		rememberServerBox.setSpacing(SMALL_PAD);
		rememberServerBox.setAlignment(CENTER);
		rememberServerBox.getChildren().addAll(
				createLabel("Remember Details", false, false),
				rememberServerBtn);

		VBox connectBox = new VBox();
		connectBox.setSpacing(MEDIUM_PAD);
		connectBox.setAlignment(CENTER);
		connectBox.maxWidthProperty().bind(ipBox.widthProperty().add(BIG_PAD));
		connectBox.getStyleClass().add("connectbox");
		connectBox.getChildren().addAll(enterLabel, ipBox, rememberServerBox,
				connectBtn);

		connectPane = new VBox();
		connectPane.setAlignment(CENTER);
		connectPane.setSpacing(BIG_PAD);
		connectPane.getChildren().addAll(connectBox, backBtn);

		setBackgroundPane(connectMenu, connectPane);
	}

	private void initSingleScene() {

		BorderPane singleBox = new BorderPane();

		Button startBtn = createButton("Start Game",
				LARGE_MENU_BUTTON_WIDTH * 1.5, LARGE_MENU_BUTTON_HEIGHT * 1.5);

		HBox startBtnPane = new HBox();
		startBtnPane.setPadding(new Insets(0, 0, SMALL_PAD, 0));
		startBtnPane.setAlignment(CENTER);
		startBtnPane.getChildren().add(startBtn);
		startBtn.setOnAction(e -> beginGame(this.map,
				this.playerName.getValue(), this.controls, this.aiNumber.get()));

		Button backBtn = createBackButton("Back", false);

		HBox centerBox = new HBox();
		centerBox.setAlignment(CENTER);
		centerBox.setSpacing(MEDIUM_PAD);

		mapCanvas = new Pane();
		mapCanvas.setMinHeight(MAP_CANVAS_HEIGHT);
		mapCanvas.setMinWidth(MAP_CANVAS_WIDTH);
		aiDifficultyChoice = new ChoiceBox<>();
		centerBox.getChildren().addAll(
				createAiDifficultySelector(aiDifficultyChoice, false),
				createMapSelector(mapCanvas, false));

		VBox centerPane = new VBox();
		centerPane.setSpacing(HUGE_PAD);
		centerPane.setAlignment(CENTER);
		centerPane.getChildren().addAll(centerBox, startBtnPane);

		HBox backBtnPane = new HBox();
		backBtnPane.getChildren().add(backBtn);
		backBtnPane.setPadding(new Insets(SMALL_PAD));

		singleBox.setCenter(centerPane);
		singleBox.setTop(backBtnPane);

		setBackgroundPane(singleMenu, singleBox);
	}

	private VBox createAiDifficultySelector(ChoiceBox<String> selector,
			boolean online) {

		Button upAiToggle = new Button();
		upAiToggle.setPrefWidth(TOGGLE_WIDTH);
		upAiToggle.getStyleClass().add("aitoggleup");
		upAiToggle.setOnAction(online ? e -> incrementOnlineAi()
				: e -> incrementAi());

		Label displayAi = createBoundLabel(this.aiNumber, false, false);
		displayAi.getStyleClass().add("textfield");
		displayAi.setPrefWidth(TOGGLE_WIDTH);

		Button downAiToggle = new Button();
		downAiToggle.setPrefWidth(TOGGLE_WIDTH);
		downAiToggle.getStyleClass().add("aitoggledown");
		downAiToggle.setOnAction(online ? e -> decrementOnlineAi()
				: e -> decrementAi());

		VBox aiBox = new VBox();
		aiBox.setAlignment(CENTER);
		aiBox.getStyleClass().add("nopadbox");
		aiBox.maxHeightProperty().bind(
				upAiToggle.heightProperty().add(
						displayAi.heightProperty().add(
								downAiToggle.heightProperty())));
		aiBox.getChildren().addAll(upAiToggle, displayAi, downAiToggle);

		Label aiLabel = createLabel("Number of\nAI Players", false, false);

		HBox aiPane = new HBox();
		aiPane.setAlignment(CENTER);
		aiPane.getStyleClass().add("namebox");
		aiPane.setSpacing(MEDIUM_PAD);
		aiPane.getChildren().addAll(aiBox, aiLabel);
		Label aiExplanation = createLabel(
				"AI players will seek\nto destroy you.", true, true);
		aiExplanation.setFont(smallFont);
		aiExplanation.setAlignment(CENTER);
		double width = boxWidth * 0.7;
		aiExplanation.setPrefWidth(width);
		selector.setTooltip(new Tooltip("Change AI Difficulty"));
		selector.setPrefHeight(50);
		selector.setPrefWidth(width);
		selector.getStyleClass().add("textfield");
		selector.getItems().addAll("Easy", "Medium", "Hard", "Extreme");
		selector.getSelectionModel().select(1);
		selector.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> ob,
							String oldValue, String newValue) {
						System.out.println("CHANGED: " + oldValue + " -> "
								+ newValue);
						switch (newValue) {
						case "Easy":
							aiDiff = EASY;
							aiExplanation
									.setText("AI players will move\nrandomly.");

							break;
						case "Medium":
							aiDiff = MEDIUM;
							aiExplanation
									.setText("AI players will seek\nto destroy you.");
							break;
						case "Hard":
							aiDiff = HARD;
							aiExplanation
									.setText("AI players will be tough\nto beat!");
							break;
						case "Extreme":
							aiDiff = EXTREME;
							aiExplanation
									.setText("AI players will collaborate\nto bring you down!");
						}
					}
				});

		VBox aiDiffBox = new VBox();
		aiDiffBox.setAlignment(CENTER);
		aiDiffBox.getStyleClass().add("namebox");
		aiDiffBox.setSpacing(MEDIUM_PAD);
		aiDiffBox.getChildren().addAll(
				createLabel("AI Difficulty:", false, false), selector,
				aiExplanation);
		aiDiffBox.setMinHeight(boxHeight / 2);

		VBox aiContainer = new VBox();
		aiContainer.setAlignment(CENTER);
		aiContainer.getStyleClass().add("menubox");
		aiContainer.setSpacing(MEDIUM_PAD);
		aiContainer.getChildren().addAll(aiPane, aiDiffBox);

		VBox aiPad = new VBox();
		aiPad.setAlignment(CENTER);
		aiPad.getChildren().add(aiContainer);

		return aiPad;

	}

	private VBox createMapSelector(Pane mapCanvas, boolean online) {

		VBox mapContainer = new VBox();
		mapContainer.getStyleClass().add("mapbox");
		mapContainer.setAlignment(CENTER);
		Label mapNameLabel = createBoundLabel(this.mapName, false, true);
		mapNameLabel.getStyleClass().add("maplabel");
		Image keyImage;
		keyImage = new Image(online ? ONLINE_KEY_PATH : KEY_PATH);
		ImageView mapKey = new ImageView(keyImage);

		VBox currentPane = new VBox();
		currentPane.setAlignment(CENTER);
		currentPane.setSpacing(SMALL_PAD);
		currentPane.getChildren().addAll(
				createLabel("Current Map:", false, true), mapNameLabel);

		HBox keyPane = new HBox();
		keyPane.setAlignment(CENTER);
		keyPane.setSpacing(BIG_PAD);
		keyPane.setPadding(new Insets(MEDIUM_PAD));
		keyPane.getChildren().addAll(currentPane, mapKey);

		mapContainer.getChildren().addAll(keyPane, mapCanvas);

		Button rightMapToggle = new Button();
		rightMapToggle.getStyleClass().add("maptoggleright");
		rightMapToggle.setPrefWidth(MAP_TOGGLE_WIDTH);

		rightMapToggle.setOnAction(online ? e -> incrementOnlineMap()
				: e -> incrementMap());

		Button leftMapToggle = new Button();
		leftMapToggle.getStyleClass().add("maptoggleleft");

		leftMapToggle.setPrefWidth(MAP_TOGGLE_WIDTH);
		leftMapToggle.setOnAction(online ? e -> decrementOnlineMap()
				: e -> decrementMap());

		HBox mapPane = new HBox();
		mapPane.setAlignment(CENTER);
		mapPane.getChildren().addAll(leftMapToggle, mapContainer,
				rightMapToggle);

		rightMapToggle.maxHeightProperty().bind(mapPane.heightProperty());
		leftMapToggle.maxHeightProperty().bind(mapPane.heightProperty());

		VBox mapPad = new VBox();
		mapPad.setAlignment(CENTER);
		mapPad.getChildren().add(mapPane);
		drawMap(mapCanvas, online);
		return mapPad;
	}

	private void drawMap(Pane mapCanvas, boolean online) {

		double height = MAP_CANVAS_HEIGHT;
		double width = online ? ONLINE_MAP_CANVAS_WIDTH : MAP_CANVAS_WIDTH;

		mapCanvas.getChildren().clear();
		Rectangle torch1 = new Rectangle();
		torch1.setWidth(SMALL_TORCH_WIDTH);
		torch1.setHeight(SMALL_TORCH_HEIGHT);
		torch1.setFill(new ImagePattern(new Image(TORCH_PATH)));
		torch1.setX(40);
		torch1.setY(30);

		Rectangle torch2 = new Rectangle();
		torch2.setWidth(SMALL_TORCH_WIDTH);
		torch2.setHeight(SMALL_TORCH_HEIGHT);
		torch2.setFill(new ImagePattern(new Image(TORCH_PATH)));
		torch2.setX(width - MAP_X_PADDING + 150);
		torch2.setY(30);

		Rectangle torch3 = new Rectangle();
		torch3.setWidth(SMALL_TORCH_WIDTH);
		torch3.setHeight(SMALL_TORCH_HEIGHT);
		torch3.setFill(new ImagePattern(new Image(TORCH_PATH)));
		torch3.setX(width - MAP_X_PADDING + 150);
		torch3.setY(170);

		Rectangle torch4 = new Rectangle();
		torch4.setWidth(SMALL_TORCH_WIDTH);
		torch4.setHeight(SMALL_TORCH_HEIGHT);
		torch4.setFill(new ImagePattern(new Image(TORCH_PATH)));
		torch4.setX(40);
		torch4.setY(170);

		mapCanvas.getChildren().addAll(torch1, torch2, torch3, torch4);

		double canvasWidth = width - MAP_X_PADDING;
		double canvasHeight = height - MAP_Y_PADDING;
		Block[][] gridMap = this.map.getGridMap();
		double xscalar = canvasWidth / gridMap.length;
		double yscalar = canvasHeight / gridMap[0].length;

		for (int x = 0; x < gridMap.length; x++) {
			for (int y = 0; y < gridMap[0].length; y++) {

				String image = "";
				switch (gridMap[x][y]) {
				case BLANK:
					image = "blank";
					break;
				case SOLID:
					image = "solid";
					break;
				case SOFT:
					image = "soft";
					break;
				case HOLE:
					image = "hole";
					break;
				default:
					break;
				}
				Rectangle rect = new Rectangle(xscalar, yscalar);
				rect.setFill(new ImagePattern(new Image("resources/images/"
						+ image + ".png")));

				rect.setStroke(Color.BLACK);
				rect.setX((MAP_X_PADDING / 2) + xscalar * x);
				rect.setY((MAP_Y_PADDING / 2) + yscalar * y);
				mapCanvas.getChildren().add(rect);
			}
		}

		for (int x = 0; x < map.getSpawnPoints().size(); x++) {
			Point pos = map.getSpawnPoints().get(x);
			Rectangle rect = new Rectangle(10, 10);
			if (x == 0) {
				rect.setFill(new ImagePattern(new Image(PLAYER_SPAWN_PATH)));
			} else {
				if (online) {
					rect.setFill(new ImagePattern(new Image(PLAYER_SPAWN_PATH)));
				} else {
					rect.setFill(new ImagePattern(new Image(SPAWN_PATH)));
				}
			}
			rect.setStroke(Color.BLACK);
			rect.setX((MAP_X_PADDING / 2) + 5 + xscalar * (pos.x / 64));
			rect.setY((MAP_Y_PADDING / 2) + 5 + yscalar * (pos.y / 64));
			mapCanvas.getChildren().add(rect);
		}
	}

	private void fullScreen() {
		if (fullScreenBtn.isSelected()) {
			this.currentStage.setFullScreen(true);
		} else {
			this.currentStage.setFullScreen(false);
		}
	}
	
	private void toggleWasd() {
		if (wasdBtn.isSelected()) {
			wasd = true;
			currentTutorial.setImage(wasdTutorialImage);
			controls.put(UP_MOVE, GLFW_KEY_W);
			controls.put(DOWN_MOVE, GLFW_KEY_S);
			controls.put(LEFT_MOVE, GLFW_KEY_A);
			controls.put(RIGHT_MOVE, GLFW_KEY_D);
		} else {
			wasd = false;
			currentTutorial.setImage(tutorialImage);
			controls.put(UP_MOVE, GLFW_KEY_UP);
			controls.put(DOWN_MOVE, GLFW_KEY_DOWN);
			controls.put(LEFT_MOVE, GLFW_KEY_LEFT);
			controls.put(RIGHT_MOVE, GLFW_KEY_RIGHT);
		}
	}

	private void setMusic(float volume) {
		if (muteMusicBtn.isSelected()) {
			musicMuted = true;
			SettingsParser.setMusicVolume(volume);
			AudioManager.setMusicVolume(volume);
		} else {
			musicMuted = false;
			SettingsParser.setMusicVolume((float) musicSlider.getValue());
			AudioManager.setMusicVolume((float) musicSlider.getValue());
		}
		SettingsParser.storeSettings();
	}

	private void setSound(float volume) {

		if (muteSoundBtn.isSelected()) {
			soundMuted = true;
			SettingsParser.setEffectsVolume(volume);
			AudioManager.setEffectsVolume(volume);
		} else {
			soundMuted = false;
			SettingsParser.setEffectsVolume((float) soundSlider.getValue());
			AudioManager.setEffectsVolume((float) soundSlider.getValue());
		}
		SettingsParser.storeSettings();
	}

	private void setBackgroundPane(BorderPane pane, Node content) {
		Image mainImage = new Image(BACKGROUND_PATH);
		ImageView mainImageView = new ImageView(mainImage);
		Pane imagePane = new Pane();
		imagePane.getChildren().add(mainImageView);
		imagePane.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		mainImageView.fitWidthProperty().bind(imagePane.widthProperty());
		mainImageView.fitHeightProperty().bind(imagePane.heightProperty());
		StackPane background = new StackPane();
		background.setAlignment(CENTER);
		background.getChildren().add(imagePane);
		background.getChildren().add(content);
		pane.setCenter(background);
	}

	private Parent createScene(Parent menu) {
		BorderPane scene = new BorderPane();
		scene.setCenter(menu);
		scene.getStylesheets().add(css);
		return scene;
	}

	private Label createLabel(String text, boolean shaded, boolean white) {
		Label label = new Label(text);
		label.setFont(font);
		label.setTextFill(white ? Color.WHITE : Color.BLACK);
		label.setAlignment(CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		if (shaded) {
			label.getStyleClass().add("shaded");
		}
		return label;
	}

	private TextField createTextField(String defaultText) {
		TextField text = new TextField();
		text.setPromptText(defaultText);
		text.getStyleClass().add("textfield");
		text.setFont(font);
		text.setAlignment(CENTER);
		return text;
	}

	private Label createBoundLabel(SimpleIntegerProperty property,
			boolean shaded, boolean white) {
		Label label = new Label();
		label.setFont(font);
		label.setTextFill(white ? Color.WHITE : Color.BLACK);
		label.setAlignment(CENTER);
		label.textProperty().bind(property.asString());
		if (shaded)
			label.getStyleClass().add("shaded");
		return label;
	}

	private Label createBoundLabel(SimpleStringProperty property,
			boolean shaded, boolean white) {
		Label label = new Label();
		label.setFont(font);
		label.setTextFill(white ? Color.WHITE : Color.BLACK);
		label.setAlignment(CENTER);
		label.textProperty().bind(property);
		if (shaded)
			label.getStyleClass().add("shaded");
		return label;
	}

	private Button createButton(String label, double width, double height) {
		Button button = new Button(label);
		button.setFont(font);
		button.setTextFill(Color.BLACK);
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setAlignment(CENTER);
		button.getStyleClass().add("menubutton");
		return button;
	}

	private Button createBackButton(String label, boolean online) {
		Button button = new Button(label);
		button.setFont(font);
		button.setTextFill(Color.BLACK);
		button.setPrefWidth(MENU_BUTTON_WIDTH);
		button.setPrefHeight(MENU_BUTTON_HEIGHT);
		button.setAlignment(CENTER);
		button.getStyleClass().add("menubutton");
		button.setOnAction(online ? e -> disconnect() : e -> previous());
		return button;
	}

	private Button createSceneButton(String label, double width, double height,
			Parent currentScene, Parent nextScene) {
		Button button = new Button(label);
		button.setFont(font);
		button.setTextFill(Color.BLACK);
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setAlignment(CENTER);
		button.getStyleClass().add("menubutton");
		button.setOnAction(e -> advance(currentScene, nextScene));
		return button;
	}

	public void beep() {
		AudioManager.playMenuItemSelected();
	}

	private void ready() {
		beep();
		readyButton.setText("Ready to Start");
		readyButton.setOnAction(e -> notReady());
		readyTorch.setFill(new ImagePattern(new Image(TORCH_PATH)));

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
		readyTorch.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));

		try {
			this.client.readyToPlay(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createRoom() {
		beep();
		String text = this.roomNameField.getText().trim();
		if (text.length() < 1) {
			this.roomCreationLabel
					.setText("Create and join a room\nwith these settings\n( Name too short! )");
		} else if (text.length() > 11) {
			this.roomCreationLabel
					.setText("Create and join a room\nwith these settings\n( 11 char limit! )");
		} else {
			try {
				this.client.createRoom(text, (byte) this.roomNumber.get(), 1);
				this.expectingRoomCreation = true;
				blankButton(createRoomBtn, "Generating...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void leaveRoom() {
		if (this.client != null && this.client.isInRoom()) {
			try {
				this.client.leaveRoom();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.expectingRoomCreation = false;
			this.expectingRoomJoin = false;
			readyButton.setText("Not Ready");
			readyButton.setOnAction(e -> ready());
			readyTorch.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));
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
		try {
			int index = this.client.getMapID();
			if (index > 0) {
				this.client.setRoomMapID(index - 1);
				this.map = this.maps.get(index - 1);
			} else {
				this.client.setRoomMapID(this.maps.size() - 1);
				this.map = this.maps.get(this.maps.size() - 1);
			}

			this.mapName.set(this.map.getName());
			drawMap(onlineMapCanvas, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void decrementMap() {
		beep();
		int index = this.maps.indexOf(this.map);
		if (index > 0) {
			this.map = this.maps.get(index - 1);
		} else {
			this.map = this.maps.get(this.maps.size() - 1);
		}
		this.mapName.set(this.map.getName());
		drawMap(mapCanvas, false);
	}

	private void incrementOnlineMap() {
		beep();
		try {
			int index = this.client.getMapID();
			if (index < (this.maps.size() - 1)) {
				this.client.setRoomMapID(index + 1);
				this.map = this.maps.get(index + 1);
			} else {
				this.client.setRoomMapID(0);
				this.map = this.maps.get(0);
			}
			this.mapName.set(this.map.getName());
			drawMap(onlineMapCanvas, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void incrementMap() {
		beep();
		int index = this.maps.indexOf(this.map);
		if (index < (this.maps.size() - 1)) {
			this.map = this.maps.get(index + 1);
		} else {
			this.map = this.maps.get(0);
		}
		this.mapName.set(this.map.getName());
		drawMap(mapCanvas, false);
	}

	private void decrementOnlineAi() {
		if (this.client != null) {
			this.humanPlayers = this.client.getRoom().getHumanPlayerNumber();
			this.aiPlayers = this.client.getRoom().getAIPlayerNumber();
		}
		if (this.aiNumber.get() > 0) {

			try {
				this.client.removeAI();
			} catch (IOException e) {
				e.printStackTrace();
			}
			aiNumber.set(this.aiPlayers - 1);
		}
	}

	private void decrementAi() {
		beep();
		if (this.aiNumber.get() > 1)
			aiNumber.set(this.aiNumber.get() - 1);
	}

	private void incrementOnlineAi() {
		if (this.client != null) {
			this.humanPlayers = this.client.getRoom().getHumanPlayerNumber();
			this.aiPlayers = this.client.getRoom().getAIPlayerNumber();
		}

		if (this.aiNumber.get() < (4 - this.humanPlayers)) {
			try {
				this.client.addAI();
			} catch (IOException e) {
				e.printStackTrace();
			}
			aiNumber.set(this.aiPlayers + 1);
		}

	}

	private void incrementAi() {
		beep();
		if (this.aiNumber.get() < 3)
			aiNumber.set(this.aiNumber.get() + 1);
	}

	private void connect() {
		beep();
		enterLabel.setText("Enter Server Details:");
		String host = ipText.getText().trim();
		this.client = null;
		try {
			int port = Integer.parseInt(portNum.getText());

			client = new ClientThread(host, port);

			client.addNetListener(this);

			Thread networkThread = new Thread(client);
			networkThread.start();

			client.connect(this.playerName.get());

			blankButton(connectBtn, "Connecting...");
			this.expectingConnection = true;
			if (rememberServerBtn.isSelected()) {
				SettingsParser.setServer(host, String.valueOf(port));
				SettingsParser.storeSettings();
			} else {
				SettingsParser.setServer(null, null);
				SettingsParser.storeSettings();
			}
		} catch (NumberFormatException e1) {
			enterLabel
					.setText("Enter Server Details:\n( Invalid Port Number! )");
			resetButton(connectBtn, "Connect", e -> connect());
		} catch (IOException e2) {
			enterLabel
					.setText("Enter Server Details:\n( Couldn't open a connection! )");
			resetButton(connectBtn, "Connect", e -> connect());
		}
	}

	private void displayRooms() {

		List<ClientServerLobbyRoom> rooms = this.client.getRoomList();

		this.roomsBox.getChildren().clear();

		for (ClientServerLobbyRoom room : rooms) {

			VBox roomContainer = new VBox();
			roomContainer.setMaxHeight(boxHeight * 0.7);
			roomContainer.setSpacing(TINY_PAD);
			roomContainer.setAlignment(CENTER);
			roomContainer.getStyleClass().add("namebox");
			Label roomID = createLabel("Room " + room.getID() + ":", false,
					false);
			Label roomName = createLabel(room.getName(), false, true);
			roomName.setAlignment(CENTER);
			roomName.getStyleClass().add("maplabel");
			HBox roomPane = new HBox();
			roomPane.setSpacing(LITTLE_PAD);
			roomPane.setPrefHeight(TINY_PANE_HEIGHT);
			roomPane.setAlignment(CENTER);
			Label numPlayers = createLabel(
					room.getPlayerNumber() + "/" + room.getMaxPlayer(), true,
					true);
			Label playersLabel = createLabel("", true, true);
			int[] playerids = room.getPlayerID();
			playersLabel.setMinHeight(boxHeight / 2.2);
			for (ClientServerPlayer player : this.client.getPlayerList()) {
				if (player.getID() == playerids[0]) {
					playersLabel.setText(playersLabel.getText() + " + "
							+ player.getName());
				}
			}
			for (int x = 1; x < playerids.length; x++) {
				for (ClientServerPlayer player : this.client.getPlayerList()) {
					if (player.getID() == playerids[x]) {
						playersLabel.setText(playersLabel.getText() + "\n + "
								+ player.getName());
					}
				}
			}
			if (room.isInGame()) {
				Label fullLabel = createLabel("IN GAME", false, false);
				fullLabel.setPrefWidth(SMALL_LABEL_WIDTH);
				fullLabel.setPrefHeight(SMALL_LABEL_HEIGHT);
				fullLabel.getStyleClass().add("textfield");
				roomPane.getChildren().addAll(fullLabel, numPlayers);
			} else if (room.getPlayerNumber() < room.getMaxPlayer()) {
				Button joinButton = createButton("Join", SMALL_LABEL_WIDTH,
						SMALL_LABEL_HEIGHT);
				joinButton.setOnAction(e -> joinRoom(room.getID(), joinButton));
				roomPane.getChildren().addAll(joinButton, numPlayers);
			} else {
				Label fullLabel = createLabel("FULL", false, false);
				fullLabel.setPrefWidth(SMALL_LABEL_WIDTH);
				fullLabel.setPrefHeight(SMALL_LABEL_HEIGHT);
				fullLabel.getStyleClass().add("textfield");
				roomPane.getChildren().addAll(fullLabel, numPlayers);
			}
			roomContainer.getChildren().addAll(roomID, roomName, playersLabel,
					roomPane);

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

		connectedPlayers = this.client.getPlayerList();

		this.serverPlayersBox.getChildren().clear();
		this.roomPlayersBox.getChildren().clear();

		for (ClientServerPlayer player : connectedPlayers) {
			this.serverPlayersBox.getChildren().add(
					createLabel("- Player: " + player.getName(), true, true));
			this.roomPlayersBox.getChildren().add(
					createLabel("- Player: " + player.getName(), true, true));
		}

		if (this.client.isInRoom()) {
			ClientServerRoom room = this.client.getRoom();
			aiNumber.set(room.getAIPlayerNumber());
			this.map = this.maps.get(room.getMapID());
			this.mapName.set(this.map.getName());
			readyPane.getChildren().clear();
			readyPane.getChildren().add(
					createLabel(
							"Game will begin when all\nplayers click ready!",
							false, false));
			for (ClientServerPlayer player : room.getHumanPlayerList()) {
				Rectangle torch = new Rectangle();
				torch.setWidth(TINY_TORCH_SIZE);
				torch.setHeight(TINY_TORCH_SIZE);
				torch.setFill(new ImagePattern(new Image(
						player.isReadyToPlay() ? TORCH_PATH : UNLIT_TORCH_PATH)));
				HBox playerBox = new HBox();
				playerBox.setSpacing(MEDIUM_PAD);
				playerBox.setAlignment(CENTER);
				Label playerName = createLabel(player.getName(), true, true);
				playerName.setPrefHeight(MEDIUM_PAD);
				playerName.setPrefWidth(MENU_BUTTON_WIDTH);
				playerBox.getChildren().addAll(playerName, torch);
				if (!player.getName().equals(this.playerName.get())) {
					readyPane.getChildren().add(playerBox);
				}
			}
			List<ClientServerAI> ais = room.getAIPlayerList();
			for (ClientServerAI ai : ais) {
				try {
					System.out.println("REQUESTING CHANGE AI: " + ai.getID()
							+ " TO " + aiDiff);
					client.setAIDifficulty(ai.getID(), aiDiff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ais.size() > 0) {
				AIDifficulty diff = room.getAIPlayerList().get(0)
						.getDifficulty();
				int index = 1;
				switch (diff) {
				case EASY:
					index = 0;
					aiDiff = EASY;
					break;
				case MEDIUM:
					index = 1;
					aiDiff = MEDIUM;
					break;
				case HARD:
					index = 2;
					aiDiff = HARD;
					break;
				case EXTREME:
					index = 3;
					aiDiff = EXTREME;
					break;
				}
				aiOnlineDifficultyChoice.getSelectionModel().select(index);
			}
			try {
				if (room.getAIPlayerNumber() + room.getHumanPlayerNumber() > room
						.getMaxPlayer()) {
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

		Parent lastScene = this.previousScenes.pop();
		this.currentStage.getScene().setRoot(lastScene);
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);

		this.roomCreationLabel
				.setText("Create and join a room\nwith these settings");
		enterLabel.setText("Enter Server Details:");
		this.currentNameText.set("Current Name:");

		resetButton(connectBtn, "Connect", e -> connect());
		resetButton(createRoomBtn, "Create New Room", e -> createRoom());

		this.aiNumber.set(1);
		this.humanPlayers = 1;
		if (this.currentStage.isFullScreen()) {
			fullScreenBtn.setSelected(true);
		} else {
			fullScreenBtn.setSelected(false);
		}
	}

	private void advance(Parent thisScene, Parent nextScene) {

		beep();
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();

		this.previousScenes.push(thisScene);
		this.currentStage.getScene().setRoot(nextScene);
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}

	public void setName(String string) {

		beep();
		string = string.trim();
		if (string.length() > 0 && string.length() < 12) {
			this.playerName.set(string);
			SettingsParser.setPlayerName(string);
			SettingsParser.storeSettings();
			this.currentNameText.set("Current Name:");
		} else if (string.length() > 0) {
			this.currentNameText.set("Current Name:\n( 11 char limit! )");
		} else {
			this.currentNameText.set("Current Name:\n( Too short! )");
		}
	}

	public void beginGame(Map map, String playerName,
			HashMap<Response, Integer> controls, int aiNum) {

		beep();
		Block[][] masterMap = this.map.getGridMap();
		int columnLength = masterMap[0].length;
		Block[][] arrayCopy = new Block[masterMap.length][columnLength];

		for (int x = 0; x < masterMap.length; x++) {

			arrayCopy[x] = Arrays.copyOf(masterMap[x], columnLength);
		}

		Map mapCopy = new Map(map.getName(), arrayCopy, map.getSpawnPoints());

		Platform.setImplicitExit(false);

		float musicVolume = 50;
		musicVolume = muteMusicBtn.isSelected() ? 0 : (float) musicSlider
				.getValue();

		float soundVolume = 50;
		soundVolume = muteSoundBtn.isSelected() ? 0 : (float) soundSlider
				.getValue();

		new Game(this, mapCopy, playerName, controls, aiNum, this.aiDiff,
				musicVolume, soundVolume, this.currentStage.isFullScreen(),
				(int) this.currentStage.getWidth(),
				(int) this.currentStage.getHeight(),
				this.wasd);
	}

	@Override
	public void disconnected() {

		Platform.runLater(new Runnable() {

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

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (expectingConnection) {

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
				}
			}

		});
	}

	@Override
	public void connectionRejected() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				enterLabel
						.setText("Enter Server Details:\n( A player with your name is\n already connected to the server!\nChange and try again! )");
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

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				enterLabel
						.setText("Enter Server Details:\n( Couldn't connect to server!\nMake sure server is running. )");
				resetButton(connectBtn, "Connect", e -> connect());
				expectingConnection = false;
			}

		});
	}

	@Override
	public void playerListReceived() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				displayPlayers();
			}

		});
	}

	@Override
	public void roomListReceived() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				displayRooms();
			}

		});
	}

	@Override
	public void roomAccepted() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (expectingRoomCreation) {
					mapName.set(". . .");
					Label label = createLabel("Loading...", false, true);
					label.setMinHeight(MAP_CANVAS_HEIGHT);
					label.setMinWidth(ONLINE_MAP_CANVAS_WIDTH);
					onlineMapCanvas.getChildren().clear();
					onlineMapCanvas.getChildren().add(label);
					advance(serverScene, roomScene);
					roomCreationLabel
							.setText("Create and join a room\nwith these settings");
					resetButton(createRoomBtn, "Create New Room",
							e -> createRoom());
					expectingRoomCreation = false;
				} else if (expectingRoomJoin) {
					advance(serverScene, roomScene);
					resetButton(createRoomBtn, "Create New Room",
							e -> createRoom());
					displayRooms();
					expectingRoomJoin = false;
				}

				aiOnlineDifficultyChoice.getSelectionModel()
						.selectedItemProperty()
						.addListener(new ChangeListener<String>() {
							@Override
							public void changed(
									ObservableValue<? extends String> ob,
									String oldValue, String newValue) {
								System.out.println("DETECTED CHANGE");
								System.out.println("AIS: "
										+ client.getRoom().getAIPlayerList());
								for (ClientServerAI ai : client.getRoom()
										.getAIPlayerList()) {
									try {
										client.setAIDifficulty(ai.getID(),
												aiDiff);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
				System.out.println("ADDED NEW LISTENER");
			}

		});
	}

	private void blankButton(Button button, String blankText) {
		button.setOnAction(null);
		button.setText(blankText);
		button.getStyleClass().clear();
		button.getStyleClass().add("textfield");
	}

	private void resetButton(Button button, String resetText,
			EventHandler<ActionEvent> handler) {
		button.setOnAction(handler);
		button.setText(resetText);
		button.getStyleClass().clear();
		button.getStyleClass().add("menubutton");
	}

	@Override
	public void roomRejected() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (expectingRoomCreation) {
					roomCreationLabel
							.setText("Create and join a room\nwith these settings\n( Something went wrong! )");
					resetButton(createRoomBtn, "Create New Room",
							e -> createRoom());
					expectingRoomCreation = false;
				}
				if (expectingRoomJoin) {
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
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				GameState gameState = client.getGameState();
				Platform.setImplicitExit(false);
				onlineGame = new OnlineGame(ui, client, gameState, playerName
						.get(), connectedPlayers, controls, currentStage
						.isFullScreen(), (int) currentStage.getWidth(),
						(int) currentStage.getHeight());
			}

		});
	}

	@Override
	public void gameStateReceived() {
	}

	@Override
	public void gameEnded() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				onlineGame.setGameEnded(true);
				readyButton.setText("Not Ready");
				readyButton.setOnAction(e -> ready());
				readyTorch
						.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));
			}

		});
	}

	public void hide() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				System.out.println("CLOSING MENU");
				currentStage.hide();
			}

		});
	}

	public void show(boolean fullScreen, boolean online, boolean gameFinished) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				currentStage.setFullScreen(fullScreen);
				currentStage.show();
				Platform.setImplicitExit(true);
				if (online && !gameFinished)
					leaveRoom();
			}

		});
	}

	@Override
	public void connectionAttemptTimeout() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				enterLabel
						.setText("Enter Server Details:\n( Timed out trying to connect!\nMake sure server is running. )");
				resetButton(connectBtn, "Connect", e -> connect());
				expectingConnection = false;
			}

		});
	}

	@Override
	public void roomReceived() {
	}
}
