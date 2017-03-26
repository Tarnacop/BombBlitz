package bomber.UI;

import static bomber.AI.AIDifficulty.EASY;
import static bomber.AI.AIDifficulty.EXTREME;
import static bomber.AI.AIDifficulty.HARD;
import static bomber.AI.AIDifficulty.MEDIUM;
import static bomber.game.Constants.BACKGROUND_PATH;
import static bomber.game.Constants.BIG_PAD;
import static bomber.game.Constants.BOMB_PATH;
import static bomber.game.Constants.CREDITS;
import static bomber.game.Constants.CSS_PATH;
import static bomber.game.Constants.FONT_PATH;
import static bomber.game.Constants.FONT_SIZE;
import static bomber.game.Constants.GAME_NAME;
import static bomber.game.Constants.HUGE_MENU_BUTTON_WIDTH;
import static bomber.game.Constants.HUGE_PAD;
import static bomber.game.Constants.KEY_PATH;
import static bomber.game.Constants.LARGE_MENU_BUTTON_HEIGHT;
import static bomber.game.Constants.LARGE_MENU_BUTTON_WIDTH;
import static bomber.game.Constants.LARGE_PAD;
import static bomber.game.Constants.LITTLE_PAD;
import static bomber.game.Constants.LOGO_PATH;
import static bomber.game.Constants.MAP_CANVAS_HEIGHT;
import static bomber.game.Constants.MAP_CANVAS_WIDTH;
import static bomber.game.Constants.MAP_TOGGLE_WIDTH;
import static bomber.game.Constants.MAP_X_PADDING;
import static bomber.game.Constants.MAP_Y_PADDING;
import static bomber.game.Constants.MASSIVE_PAD;
import static bomber.game.Constants.MEDIUM_PAD;
import static bomber.game.Constants.MENU_BOX_HEIGHT_SCALAR;
import static bomber.game.Constants.MENU_BOX_WIDTH_SCALAR;
import static bomber.game.Constants.MENU_BUTTON_HEIGHT;
import static bomber.game.Constants.MENU_BUTTON_WIDTH;
import static bomber.game.Constants.ONLINE_KEY_PATH;
import static bomber.game.Constants.ONLINE_MAP_CANVAS_WIDTH;
import static bomber.game.Constants.PANE_WIDTH;
import static bomber.game.Constants.PLAYER_SPAWN_PATH;
import static bomber.game.Constants.SCREEN_HEIGHT_SCALAR;
import static bomber.game.Constants.SCREEN_WIDTH_SCALAR;
import static bomber.game.Constants.SMALL_FONT_SIZE;
import static bomber.game.Constants.SMALL_LABEL_HEIGHT;
import static bomber.game.Constants.SMALL_LABEL_WIDTH;
import static bomber.game.Constants.SMALL_PAD;
import static bomber.game.Constants.SMALL_TORCH_HEIGHT;
import static bomber.game.Constants.SMALL_TORCH_WIDTH;
import static bomber.game.Constants.SPAWN_PATH;
import static bomber.game.Constants.STORY_PATH;
import static bomber.game.Constants.THICK_PANE_WIDTH;
import static bomber.game.Constants.THIN_PANE_HEIGHT;
import static bomber.game.Constants.THIN_PANE_WIDTH;
import static bomber.game.Constants.TINY_PAD;
import static bomber.game.Constants.TINY_PANE_HEIGHT;
import static bomber.game.Constants.TINY_TORCH_SIZE;
import static bomber.game.Constants.TOGGLE_WIDTH;
import static bomber.game.Constants.TORCH_HEIGHT;
import static bomber.game.Constants.TORCH_PATH;
import static bomber.game.Constants.TORCH_WIDTH;
import static bomber.game.Constants.TUTORIAL_PATH;
import static bomber.game.Constants.UNLIT_TORCH_PATH;
import static bomber.game.Constants.VERSION_NUMBER;
import static bomber.game.Constants.WASD_TUTORIAL_PATH;
import static bomber.game.Response.DOWN_MOVE;
import static bomber.game.Response.LEFT_MOVE;
import static bomber.game.Response.MUTE_GAME;
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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
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
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
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
	private FlowPane roomPlayersFlowPane;
	private int humanPlayers;
	private int aiPlayers;
	private Pane mapCanvas;
	private Pane onlineMapCanvas;
	private ChoiceBox<String> aiDifficultyChoice;
	private ChoiceBox<String> onlineAiDifficultyChoice;
	private VBox readyPlayersVBox;
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
		this.font = Font.loadFont(getClass().getResource(FONT_PATH)
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
		this.controls.put(MUTE_GAME, GLFW_KEY_M);

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
		soundSlider.setOnMouseReleased(e -> setSound(soundMuted ? 0
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

		// Initialise the two alternate tutorial images.
		// 'tutorialImage' the arrow keys tutorial image
		// 'wasdTutorialImage' the WASD keys tutorial image
		tutorialImage = new Image(TUTORIAL_PATH);
		wasdTutorialImage = new Image(WASD_TUTORIAL_PATH);

		// 'currentTutorial' - Tutorial ImageView which can be changed later.
		currentTutorial = new ImageView(tutorialImage);

		// Create a container to hold the story image and tutorial image.
		HBox tutorialHBox = new HBox();
		tutorialHBox.setAlignment(CENTER);
		tutorialHBox.setSpacing(HUGE_PAD);
		tutorialHBox.getStyleClass().add("creditsbox");
		tutorialHBox.getChildren().addAll(storyImageView, currentTutorial);

		// Create a container to hold the tutorial container and back button.
		VBox infoVBox = new VBox();
		infoVBox.setAlignment(CENTER);
		infoVBox.setSpacing(SMALL_PAD);
		infoVBox.setPadding(new Insets(0, LARGE_PAD, 0, LARGE_PAD));
		infoVBox.getChildren().addAll(tutorialHBox,
				createBackButton("Back", false));

		// Set the background of the menu.
		setBackgroundPane(tutorialMenu, infoVBox);
	}

	/**
	 * Initialise the credits menu.
	 */
	private void initCreditsScene() {

		// Create the label to hold the game credits.
		Label creditsLabel = createLabel(CREDITS, false, true);
		creditsLabel.getStyleClass().add("creditsbox");

		// Create a container to hold the credits and the back button
		VBox creditsVBox = new VBox();
		creditsVBox.setAlignment(CENTER);
		creditsVBox.setSpacing(MEDIUM_PAD);
		creditsVBox.getChildren().addAll(
				createLabel("Version: " + VERSION_NUMBER, false, true),
				creditsLabel, createBackButton("Back", false));

		// Set the background of the menu.
		setBackgroundPane(creditsMenu, creditsVBox);
	}

	/**
	 * Initialise the Scene within an online multiplayer room.
	 */
	private void initRoomScene() {

		// Create container to hold the room elements
		BorderPane roomBorderPane = new BorderPane();

		// Create button to leave the current room.
		Button leaveRoomBtn = createButton("Leave Room",
				LARGE_MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
		leaveRoomBtn.setOnAction(e -> leaveRoom());

		// Create a container to hold the central elements.
		HBox centerHBox = new HBox();
		centerHBox.setAlignment(CENTER);
		centerHBox.setSpacing(MEDIUM_PAD);

		// 'onlineMapCanvas' - the map canvas used for online games.
		onlineMapCanvas = new Pane();
		onlineMapCanvas.setMinHeight(MAP_CANVAS_HEIGHT);
		onlineMapCanvas.setMinWidth(MAP_CANVAS_WIDTH);

		// 'onlineAiDifficultyChoice' - the choicebox for changing the online AI
		// difficulty.
		onlineAiDifficultyChoice = new ChoiceBox<>();
		centerHBox.getChildren().addAll(
				createAiDifficultySelector(onlineAiDifficultyChoice, true),
				createMapSelector(onlineMapCanvas, true));

		// Create a container to hold the back button.
		HBox backBtnHBox = new HBox();
		backBtnHBox.getChildren().add(leaveRoomBtn);
		backBtnHBox.setPadding(new Insets(MEDIUM_PAD, SMALL_PAD, MEDIUM_PAD,
				SMALL_PAD));

		Label playersTitle = createLabel("Online Players:", false, true);

		// Create the flowpane to contain the list of online players.
		roomPlayersFlowPane = new FlowPane(VERTICAL);
		roomPlayersFlowPane.setVgap(TINY_PAD);
		roomPlayersFlowPane.setHgap(MEDIUM_PAD);

		// Create a scrollpane to contain the flowpane in case it gets too big.
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setMaxHeight(boxHeight - MASSIVE_PAD);
		scrollPane.setMinWidth(boxWidth + MASSIVE_PAD);
		scrollPane.setVbarPolicy(AS_NEEDED);
		scrollPane.setContent(roomPlayersFlowPane);

		// Create a container for the list of players with a title at the top.
		VBox playersListVBox = new VBox();
		playersListVBox.getStyleClass().add("wideclearbox");
		playersListVBox.setSpacing(SMALL_PAD);
		playersListVBox.setMinWidth(boxWidth + MASSIVE_PAD);
		playersListVBox.getChildren().addAll(playersTitle, scrollPane);

		// 'readyTorch' - the torch turned on when the player has toggled ready
		// to on.
		readyTorch = new Rectangle();
		readyTorch.setWidth(TORCH_WIDTH);
		readyTorch.setHeight(TORCH_HEIGHT);
		readyTorch.getStyleClass().add("mapbox");
		readyTorch.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));

		// Create a container for the ready torch and ready button toggle.
		VBox readyVBox = new VBox();
		readyVBox.getStyleClass().add("namebox");
		readyVBox.setSpacing(MEDIUM_PAD);
		readyVBox.setAlignment(CENTER);
		readyVBox.setMinWidth(boxHeight);
		readyVBox.setMinHeight(boxHeight);
		readyVBox.setMaxHeight(boxHeight);
		readyButton = createButton("Not Ready", LARGE_MENU_BUTTON_WIDTH,
				LARGE_MENU_BUTTON_HEIGHT);
		readyButton.setOnAction(e -> ready());

		// Create a container for the ready torch.
		HBox torchBox = new HBox();
		torchBox.setSpacing(SMALL_PAD);
		torchBox.setAlignment(CENTER);
		torchBox.getChildren().addAll(
				createLabel("Click to\ntoggle Ready:", false, false),
				readyTorch);
		readyVBox.getChildren().addAll(torchBox, readyButton);

		// 'readyPlayersVBox' - container for the players in the room and their
		// ready torches.
		readyPlayersVBox = new VBox();
		readyPlayersVBox.getStyleClass().add("menubox");
		readyPlayersVBox.setSpacing(LITTLE_PAD);
		readyPlayersVBox.setAlignment(CENTER);
		readyPlayersVBox.setMinWidth(boxWidth + MEDIUM_PAD);
		readyPlayersVBox.setMinHeight(boxHeight);
		readyPlayersVBox.setMaxHeight(boxHeight);
		readyPlayersVBox.getChildren().add(
				createLabel("Game will begin when all\nplayers click ready!",
						false, false));

		// Create container for the lower level of room elements.
		HBox bottomHBox = new HBox();
		bottomHBox.setAlignment(CENTER);
		bottomHBox.setSpacing(LARGE_PAD);
		bottomHBox.setPadding(new Insets(SMALL_PAD));
		bottomHBox.getChildren().addAll(playersListVBox, readyPlayersVBox,
				readyVBox);

		// Add all the room elements to a new container.
		VBox centerVBox = new VBox();
		centerVBox.setAlignment(CENTER);
		centerVBox.setSpacing(SMALL_PAD);
		centerVBox.getChildren().addAll(centerHBox, bottomHBox);

		// Set the room elements into the borderpane.
		roomBorderPane.setCenter(centerVBox);
		roomBorderPane.setTop(backBtnHBox);

		// Set the background of the menu.
		setBackgroundPane(roomMenu, roomBorderPane);
	}

	/**
	 * Initialise the scene when a player has connected to a server.
	 */
	private void initServerScene() {

		// Create the button to disconnect from the server.
		Button disconnectBtn = createBackButton("Disconnect", true);

		// Create a container to hold the disconnect button.
		HBox disconnectHBox = new HBox();
		disconnectHBox.setAlignment(CENTER_LEFT);
		disconnectHBox.setPadding(new Insets(MEDIUM_PAD, SMALL_PAD, MEDIUM_PAD,
				SMALL_PAD));
		disconnectHBox.getChildren().add(disconnectBtn);

		// 'createRoomBtn' - the button used to create a new room.
		createRoomBtn = createButton("Create New Room",
				LARGE_MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);
		createRoomBtn.setOnAction(e -> createRoom());

		// create toggles and a label to change the capacity of a room to be
		// created and display it.
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

		// Create a container to hold the toggle and a label.
		VBox roomNumToggle = new VBox();
		roomNumToggle.setAlignment(CENTER);
		roomNumToggle.getStyleClass().add("nopadbox");
		roomNumToggle.maxHeightProperty().bind(
				upRoomNumToggle.heightProperty().add(
						displayRoomNum.heightProperty().add(
								downRoomNumToggle.heightProperty())));
		roomNumToggle.getChildren().addAll(upRoomNumToggle, displayRoomNum,
				downRoomNumToggle);

		Label roomNumLabel = createLabel("Room\nCapacity", false, false);

		HBox roomNumHBox = new HBox();
		roomNumHBox.setAlignment(CENTER);
		roomNumHBox.getStyleClass().add("namebox");
		roomNumHBox.setSpacing(MEDIUM_PAD);
		roomNumHBox.setMinWidth(PANE_WIDTH);
		roomNumHBox.getChildren().addAll(roomNumToggle, roomNumLabel);

		// 'roomNameField' - the text field to enter a name for the room to be
		// created.
		roomNameField = createTextField("Enter Name");

		// Create a container to hold the room name setter.
		VBox roomNameVBox = new VBox();
		roomNameVBox.setSpacing(SMALL_PAD);
		roomNameVBox.getStyleClass().add("namebox");
		roomNameVBox.setAlignment(CENTER);
		roomNameVBox.setMinWidth(THIN_PANE_WIDTH);
		roomNameVBox.getChildren().addAll(
				createLabel("Room Name:", false, false), roomNameField);

		// Create a container to hold the room creation label and button
		VBox roomDisplayVBox = new VBox();
		roomDisplayVBox.setSpacing(MEDIUM_PAD);
		roomDisplayVBox.setAlignment(CENTER);
		roomDisplayVBox.setMinWidth(THICK_PANE_WIDTH);
		roomDisplayVBox.getChildren().addAll(this.roomCreationLabel,
				createRoomBtn);

		// Add some torches for decoration.
		Rectangle torch1 = new Rectangle();
		torch1.setWidth(SMALL_TORCH_WIDTH);
		torch1.setHeight(SMALL_TORCH_HEIGHT);
		torch1.setFill(new ImagePattern(new Image(TORCH_PATH)));

		Rectangle torch2 = new Rectangle();
		torch2.setWidth(SMALL_TORCH_WIDTH);
		torch2.setHeight(SMALL_TORCH_HEIGHT);
		torch2.setFill(new ImagePattern(new Image(TORCH_PATH)));

		// Create a container to hold all the room creation elements.
		HBox createRoomHBox = new HBox();
		createRoomHBox.setAlignment(CENTER);
		createRoomHBox.setSpacing(MEDIUM_PAD);
		createRoomHBox.getStyleClass().add("wideclearbox");
		createRoomHBox.maxWidthProperty().bind(
				roomNumHBox.widthProperty().add(
						roomNameVBox.widthProperty().add(
								roomDisplayVBox.widthProperty())));
		createRoomHBox.getChildren().addAll(torch1, roomNumHBox, roomNameVBox,
				roomDisplayVBox, torch2);

		// create label to display over the rooms
		Label roomsTitle = createLabel(
				"Rooms:											( Join or create a room to play a match! )",
				false, true);

		// 'roomsBox' - container holding all the room displays on the server.
		roomsBox = new HBox();
		roomsBox.setSpacing(BIG_PAD);

		// Create a scrollpane to hold the roomsBox in case it gets too big.
		ScrollPane scrollRooms = new ScrollPane();
		scrollRooms.setMinHeight(boxHeight + LARGE_PAD);
		scrollRooms.setVbarPolicy(NEVER);
		scrollRooms.setHbarPolicy(AS_NEEDED);
		scrollRooms.setFitToHeight(true);
		scrollRooms.setFitToWidth(true);
		scrollRooms.setContent(roomsBox);

		// Create a container for the scrollpane with a title.
		VBox roomsListVBox = new VBox();
		roomsListVBox.setSpacing(SMALL_PAD);
		roomsListVBox.setAlignment(TOP_LEFT);
		roomsListVBox.minHeightProperty().bind(
				roomsTitle.minHeightProperty().add(
						scrollRooms.minHeightProperty()));
		roomsListVBox.getChildren().addAll(roomsTitle, scrollRooms);

		// Create a label for the online players.
		Label playersTitle = createLabel("Online Players:", false, true);

		// 'serverPlayersBox' - container to hold all the player displays
		// currently on the server.
		serverPlayersBox = new FlowPane();
		serverPlayersBox.setVgap(SMALL_PAD);
		serverPlayersBox.setHgap(SMALL_PAD);
		serverPlayersBox.setMinHeight(THIN_PANE_HEIGHT);

		// Create a scrollpane to hold the serverPlayersBox in case it gets too
		// big.
		ScrollPane scrollPlayers = new ScrollPane();
		scrollPlayers.setVbarPolicy(AS_NEEDED);
		scrollPlayers.setContent(serverPlayersBox);

		// Create a container to hold the scrollpane with a title.
		VBox playersListVBox = new VBox();
		playersListVBox.setSpacing(SMALL_PAD);
		playersListVBox.getChildren().addAll(playersTitle, scrollPlayers);
		playersListVBox.minHeightProperty().bind(
				playersTitle.minHeightProperty().add(
						serverPlayersBox.minHeightProperty().add(HUGE_PAD)));

		// Create a container to hold all the rooms and player info on the
		// server.
		VBox roomsPlayersVBox = new VBox();
		roomsPlayersVBox.setSpacing(BIG_PAD);
		roomsPlayersVBox.setAlignment(CENTER);
		roomsPlayersVBox.getChildren().addAll(roomsListVBox, playersListVBox);

		// Create a BorderPane containing a VBox to make the padding look nice.
		BorderPane serverBorderPane = new BorderPane();
		VBox serverVBox = new VBox();
		serverVBox.setSpacing(MEDIUM_PAD);
		serverVBox.setAlignment(CENTER);
		roomsPlayersVBox.getStyleClass().add("wideclearbox");
		roomsPlayersVBox.minHeightProperty().bind(
				roomsListVBox.minHeightProperty().add(
						playersListVBox.minHeightProperty().add(50)));
		serverVBox.minHeightProperty().bind(
				roomsPlayersVBox.minHeightProperty());
		serverVBox.getChildren().addAll(createRoomHBox, roomsPlayersVBox);
		serverBorderPane.setTop(disconnectHBox);
		serverBorderPane.setPadding(new Insets(0, MEDIUM_PAD, MEDIUM_PAD,
				MEDIUM_PAD));
		serverBorderPane.setCenter(serverVBox);

		// Set the background of the menu.
		setBackgroundPane(serverMenu, serverBorderPane);
	}

	/**
	 * Initialise the menu for the user connecting to a server.
	 */
	private void initConnectScene() {

		// 'enterLabel' - label displaying information about connecting to the
		// server.
		enterLabel = createLabel("Enter Server Details:", false, false);

		// 'ipText' - textfield to get the ip address.
		ipText = createTextField("IP Address");
		ipText.setMinWidth(200);

		// 'portNum' - textfield to get the port number.
		portNum = createTextField("Port Number");
		portNum.setMinWidth(200);

		// check if the last user wanted us to remember the server details, if
		// so add them here.
		gotServer = (!SettingsParser.getServerPort().equals(""))
				&& (!SettingsParser.getServerIp().equals(""));
		if (gotServer) {
			ipText.setText(SettingsParser.getServerIp());
			portNum.setText(SettingsParser.getServerPort());
		}

		// Create a container to hold the ip and port number fields.
		HBox serverHBox = new HBox();
		serverHBox.setSpacing(SMALL_PAD);
		serverHBox.setPrefSize(THIN_PANE_WIDTH, TINY_PANE_HEIGHT);
		serverHBox.setAlignment(CENTER);
		serverHBox.getChildren().addAll(ipText, createLabel(":", false, false),
				portNum);

		// 'connectBtn' - button to attempt a connection to the server.
		connectBtn = createButton("Connect", HUGE_MENU_BUTTON_WIDTH,
				LARGE_MENU_BUTTON_HEIGHT);
		connectBtn.setOnAction(e -> connect());

		// Create a button to cancel connection and go back.
		Button backBtn = createBackButton("Cancel", false);

		// 'rememberServerBtn' - checkbox for remembering server details.
		rememberServerBtn = new CheckBox();
		rememberServerBtn.setSelected(gotServer);

		// Create container to hold the checkbox and label.
		HBox rememberServerHBox = new HBox();
		rememberServerHBox.setSpacing(SMALL_PAD);
		rememberServerHBox.setAlignment(CENTER);
		rememberServerHBox.getChildren().addAll(
				createLabel("Remember Details", false, false),
				rememberServerBtn);

		// Create container to hold all the connection menu elements.
		VBox connectVBox = new VBox();
		connectVBox.setSpacing(MEDIUM_PAD);
		connectVBox.setAlignment(CENTER);
		connectVBox.maxWidthProperty().bind(
				serverHBox.widthProperty().add(BIG_PAD));
		connectVBox.getStyleClass().add("connectbox");
		connectVBox.getChildren().addAll(enterLabel, serverHBox,
				rememberServerHBox, connectBtn);

		// Create container to hold all the elements and the back button.
		VBox connectPane = new VBox();
		connectPane.setAlignment(CENTER);
		connectPane.setSpacing(BIG_PAD);
		connectPane.getChildren().addAll(connectVBox, backBtn);

		// Set the menu background.
		setBackgroundPane(connectMenu, connectPane);
	}

	/**
	 * Initialise the menu for single player mode.
	 */
	private void initSingleScene() {

		// Create a borderpane to hold the single player elements.
		BorderPane singleBox = new BorderPane();

		// Create button to start the game.
		Button startBtn = createButton("Start Game",
				LARGE_MENU_BUTTON_WIDTH * 1.5, LARGE_MENU_BUTTON_HEIGHT * 1.5);
		startBtn.setOnAction(e -> beginGame(this.map,
				this.playerName.getValue(), this.controls, this.aiNumber.get()));

		// Create a container to hold the start button.
		HBox startBtnHBox = new HBox();
		startBtnHBox.setPadding(new Insets(0, 0, SMALL_PAD, 0));
		startBtnHBox.setAlignment(CENTER);
		startBtnHBox.getChildren().add(startBtn);

		// Create a back button.
		Button backBtn = createBackButton("Back", false);

		// Create a container to hold the central elements.
		HBox centerHBox = new HBox();
		centerHBox.setAlignment(CENTER);
		centerHBox.setSpacing(MEDIUM_PAD);

		// 'mapCanvas' - the canvas to draw the offline map on.
		mapCanvas = new Pane();
		mapCanvas.setMinHeight(MAP_CANVAS_HEIGHT);
		mapCanvas.setMinWidth(MAP_CANVAS_WIDTH);

		// 'aiDifficultyChoice' - the offline choicebox for ai difficulty
		// selection.
		aiDifficultyChoice = new ChoiceBox<>();
		centerHBox.getChildren().addAll(
				createAiDifficultySelector(aiDifficultyChoice, false),
				createMapSelector(mapCanvas, false));

		// Create container to hold the central elements and start button.
		VBox centerVBox = new VBox();
		centerVBox.setSpacing(HUGE_PAD);
		centerVBox.setAlignment(CENTER);
		centerVBox.getChildren().addAll(centerHBox, startBtnHBox);

		// Create container to hold the back button.
		HBox bacnBtnHBox = new HBox();
		bacnBtnHBox.getChildren().add(backBtn);
		bacnBtnHBox.setPadding(new Insets(SMALL_PAD));

		// Set the elements in the single player menu borderpane.
		singleBox.setCenter(centerVBox);
		singleBox.setTop(bacnBtnHBox);

		// Set the menu background.
		setBackgroundPane(singleMenu, singleBox);
	}

	/**
	 * Decrement the capacity of an online room to be created (range 2-4).
	 */
	private void decrementRoomNum() {
		if (roomNumber.get() > 2)
			roomNumber.set(roomNumber.get() - 1);
	}

	/**
	 * Increment the capacity of an online room to be created (range 2-4).
	 */
	private void incrementRoomNum() {
		if (roomNumber.get() < 4)
			roomNumber.set(roomNumber.get() + 1);
	}

	/**
	 * Initialise a choice box to become an AI Difficulty selector in its own
	 * VBox.
	 * 
	 * @param selector
	 *            the choice box
	 * @param online
	 *            whether it needs to interact with the client
	 * @return the VBox containing the AI Difficulty Selector elements
	 */
	private VBox createAiDifficultySelector(ChoiceBox<String> selector,
			boolean online) {

		// Create a toggle for increasing and decreasing the number of AI, with
		// a display label
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

		// Create a container to hold the toggle.
		VBox aiToggle = new VBox();
		aiToggle.setAlignment(CENTER);
		aiToggle.getStyleClass().add("nopadbox");
		aiToggle.maxHeightProperty().bind(
				upAiToggle.heightProperty().add(
						displayAi.heightProperty().add(
								downAiToggle.heightProperty())));
		aiToggle.getChildren().addAll(upAiToggle, displayAi, downAiToggle);

		// Create a label for the toggle.
		Label aiLabel = createLabel("Number of\nAI Players", false, false);

		// Create a container to hold the AI number elements.
		HBox aiNumberHBox = new HBox();
		aiNumberHBox.setAlignment(CENTER);
		aiNumberHBox.getStyleClass().add("namebox");
		aiNumberHBox.setSpacing(MEDIUM_PAD);
		aiNumberHBox.getChildren().addAll(aiToggle, aiLabel);

		// Create a label with information about the level of AI difficulty.
		Label aiExplanation = createLabel(
				"AI players will seek\nto destroy you.", true, true);
		aiExplanation.setFont(smallFont);
		aiExplanation.setAlignment(CENTER);
		double width = boxWidth * 0.7;
		aiExplanation.setPrefWidth(width);

		// Add the relevant options to the selector
		selector.setTooltip(new Tooltip("Change AI Difficulty"));
		selector.setPrefHeight(BIG_PAD + SMALL_PAD);
		selector.setPrefWidth(width);
		selector.getStyleClass().add("textfield");
		selector.getItems().addAll("Easy", "Medium", "Hard", "Extreme");
		selector.getSelectionModel().select(1);
		selector.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> ob,
							String oldValue, String newValue) {
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

		// Create a container for the selector and the explanation
		VBox aiDiffVBox = new VBox();
		aiDiffVBox.setAlignment(CENTER);
		aiDiffVBox.getStyleClass().add("namebox");
		aiDiffVBox.setSpacing(MEDIUM_PAD);
		aiDiffVBox.getChildren().addAll(
				createLabel("AI Difficulty:", false, false), selector,
				aiExplanation);
		aiDiffVBox.setMinHeight(boxHeight / 2);

		// Create a container for all the elements.
		VBox aiContainer = new VBox();
		aiContainer.setAlignment(CENTER);
		aiContainer.getStyleClass().add("menubox");
		aiContainer.setSpacing(MEDIUM_PAD);
		aiContainer.getChildren().addAll(aiNumberHBox, aiDiffVBox);

		// Create a container to pad the elements within.
		VBox aiDifficultySelector = new VBox();
		aiDifficultySelector.setAlignment(CENTER);
		aiDifficultySelector.getChildren().add(aiContainer);

		return aiDifficultySelector;
	}

	/**
	 * Initialise a map canvas to become a map selector.
	 * 
	 * @param mapCanvas
	 *            the canvas to use
	 * @param online
	 *            whether this will need to interact with the client
	 * @return the VBox containing all the map selection elements
	 */
	private VBox createMapSelector(Pane mapCanvas, boolean online) {

		// Create a container to hold the key and canvas.
		VBox mapContainer = new VBox();
		mapContainer.getStyleClass().add("mapbox");
		mapContainer.setAlignment(CENTER);

		// Create the map name label.
		Label mapNameLabel = createBoundLabel(this.mapName, false, true);
		mapNameLabel.getStyleClass().add("maplabel");

		// Create the key image.
		Image keyImage;
		keyImage = new Image(online ? ONLINE_KEY_PATH : KEY_PATH);
		ImageView mapKey = new ImageView(keyImage);

		// Create a container for the current map name.
		VBox currentPane = new VBox();
		currentPane.setAlignment(CENTER);
		currentPane.setSpacing(SMALL_PAD);
		currentPane.getChildren().addAll(
				createLabel("Current Map:", false, true), mapNameLabel);

		// Create a container for the current map name info and the key.
		HBox keyPane = new HBox();
		keyPane.setAlignment(CENTER);
		keyPane.setSpacing(BIG_PAD);
		keyPane.setPadding(new Insets(MEDIUM_PAD));
		keyPane.getChildren().addAll(currentPane, mapKey);

		mapContainer.getChildren().addAll(keyPane, mapCanvas);

		// Create the toggles for the map
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

		// Create a container for the toggles and the map info display.
		HBox mapPane = new HBox();
		mapPane.setAlignment(CENTER);
		mapPane.getChildren().addAll(leftMapToggle, mapContainer,
				rightMapToggle);

		rightMapToggle.maxHeightProperty().bind(mapPane.heightProperty());
		leftMapToggle.maxHeightProperty().bind(mapPane.heightProperty());

		// Create a container to pad the map elements inside.
		VBox mapPad = new VBox();
		mapPad.setAlignment(CENTER);
		mapPad.getChildren().add(mapPane);
		drawMap(mapCanvas, online);

		return mapPad;
	}

	/**
	 * Draw the current map onto a map canvas.
	 * 
	 * @param mapCanvas
	 *            the map canvas to draw on.
	 * @param online
	 *            if we have to interact with the client or not
	 */
	private void drawMap(Pane mapCanvas, boolean online) {

		double height = MAP_CANVAS_HEIGHT;
		double width = online ? ONLINE_MAP_CANVAS_WIDTH : MAP_CANVAS_WIDTH;

		mapCanvas.getChildren().clear();

		// Create the torches for decoration.
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

		// For every block in the gridmap, draw a rectangle on the canvas with
		// the appropriate texture.
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
				rect.setFill(new ImagePattern(new Image("/images/" + image
						+ ".png")));

				rect.setStroke(Color.BLACK);
				rect.setX((MAP_X_PADDING / 2) + xscalar * x);
				rect.setY((MAP_Y_PADDING / 2) + yscalar * y);
				mapCanvas.getChildren().add(rect);
			}
		}

		// Draw each of the spawn points. If offline, draw all except the first
		// as AI spawn points.
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

	/**
	 * Toggle the stage to fullscreen or not.
	 */
	private void fullScreen() {
		if (fullScreenBtn.isSelected()) {
			this.currentStage.setFullScreen(true);
		} else {
			this.currentStage.setFullScreen(false);
		}
	}

	/**
	 * Toggle WASD controls.
	 */
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

	/**
	 * Set the music volume.
	 * 
	 * @param volume
	 *            the new volume
	 */
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

	/**
	 * Set the sound effects volume.
	 * 
	 * @param volume
	 *            the new volume
	 */
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

	/**
	 * Set the background image of the content to the menu background image.
	 * 
	 * @param pane
	 *            the background of the menu
	 * @param content
	 *            the content to be displayed over the background
	 */
	private void setBackgroundPane(BorderPane pane, Node content) {

		// Create the image for the background.
		Image mainImage = new Image(BACKGROUND_PATH);
		ImageView mainImageView = new ImageView(mainImage);
		Pane imagePane = new Pane();
		imagePane.getChildren().add(mainImageView);
		imagePane.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		mainImageView.fitWidthProperty().bind(imagePane.widthProperty());
		mainImageView.fitHeightProperty().bind(imagePane.heightProperty());

		// Stack the content on the background.
		StackPane background = new StackPane();
		background.setAlignment(CENTER);
		background.getChildren().add(imagePane);
		background.getChildren().add(content);

		// Set the stacked elements onto the borderpane.
		pane.setCenter(background);
	}

	/**
	 * Create a scene parent (allows for switching scenes when fullscreen).
	 * 
	 * @param menu
	 *            the menu to be the child of this parent
	 * @return the parent
	 */
	private Parent createScene(Parent menu) {
		BorderPane scene = new BorderPane();
		scene.setCenter(menu);

		// Add our stylesheet as well
		scene.getStylesheets().add(css);

		return scene;
	}

	/**
	 * Create a label.
	 * 
	 * @param text
	 *            the label text
	 * @param shaded
	 *            true if the label should be shaded
	 * @param white
	 *            true if the text should be white, otherwise it will be black
	 * @return the new label
	 */
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

	/**
	 * Create a text field.
	 * 
	 * @param defaultText
	 *            the prompt text to display
	 * @return the new text field
	 */
	private TextField createTextField(String defaultText) {
		TextField text = new TextField();
		text.setPromptText(defaultText);
		text.getStyleClass().add("textfield");
		text.setFont(font);
		text.setAlignment(CENTER);
		return text;
	}

	/**
	 * Create a bound label.
	 * 
	 * @param property
	 *            the property to bind to.
	 * @param shaded
	 *            if true, label will be shaded
	 * @param white
	 *            if true, text will be white, otherwise it will be black
	 * @return the new bound label
	 */
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

	/**
	 * Create a bound label.
	 * 
	 * @param property
	 *            the property to bind to
	 * @param shaded
	 *            if true, label will be shaded
	 * @param white
	 *            if true, text will be white, otherwise it will be black
	 * @return the new bound label
	 */
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

	/**
	 * Create a button.
	 * 
	 * @param label
	 *            the button text
	 * @param width
	 *            the width of the button
	 * @param height
	 *            the height of the button
	 * @return the new button
	 */
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

	/**
	 * Create a back button.
	 * 
	 * @param label
	 *            the button text
	 * @param online
	 *            true if the button needs to disconnect too
	 * @return the new back button
	 */
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

	/**
	 * Create a scene traversal button.
	 * 
	 * @param label
	 *            the button text
	 * @param width
	 *            the width of the button
	 * @param height
	 *            the height of the button
	 * @param currentScene
	 *            the current scene
	 * @param nextScene
	 *            the scene to advance to
	 * @return the new button
	 */
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

	/**
	 * Make a button click noise.
	 */
	public void beep() {
		AudioManager.playMenuItemSelected();
	}

	/**
	 * Set the player as ready to start an online game.
	 */
	private void ready() {
		beep();

		// Set the ready torch as lit and button to show the player is ready.
		readyButton.setText("Ready to Start");
		readyButton.setOnAction(e -> notReady());
		readyTorch.setFill(new ImagePattern(new Image(TORCH_PATH)));

		try {
			this.client.readyToPlay(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the player as not ready to start a game.
	 */
	private void notReady() {
		beep();

		// Set the ready torch to unlit and button to show player is not ready.
		readyButton.setText("Not Ready");
		readyButton.setOnAction(e -> ready());
		readyTorch.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));

		try {
			this.client.readyToPlay(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a room on the server.
	 */
	private void createRoom() {
		beep();

		// Get the name of the room.
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

	/**
	 * Leave a room on the server.
	 */
	private void leaveRoom() {
		if (this.client != null && this.client.isInRoom()) {
			try {
				this.client.leaveRoom();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Reset some fields.
			this.expectingRoomCreation = false;
			this.expectingRoomJoin = false;
			readyButton.setText("Not Ready");
			readyButton.setOnAction(e -> ready());
			readyTorch.setFill(new ImagePattern(new Image(UNLIT_TORCH_PATH)));
			previous();
		}
	}

	/**
	 * Disconnect from the server.
	 */
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

	/**
	 * Decrement the index of the online map.
	 */
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

	/**
	 * Decrement the index of the offline map.
	 */
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

	/**
	 * Increment the index of the online map.
	 */
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

	/**
	 * Increment the index of the offline map.
	 */
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

	/**
	 * Decrement the number of online AI.
	 */
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

	/**
	 * Decrement the number of offline AI.
	 */
	private void decrementAi() {
		beep();
		if (this.aiNumber.get() > 1)
			aiNumber.set(this.aiNumber.get() - 1);
	}

	/**
	 * Increment the number of the online AI.
	 */
	private void incrementOnlineAi() {
		if (this.client != null) {
			this.humanPlayers = this.client.getRoom().getHumanPlayerNumber();
			this.aiPlayers = this.client.getRoom().getAIPlayerNumber();
		}

		if (this.aiNumber.get() < (this.client.getRoom().getMaxPlayer() - this.humanPlayers)) {
			try {
				this.client.addAI();
			} catch (IOException e) {
				e.printStackTrace();
			}
			aiNumber.set(this.aiPlayers + 1);
		}

	}

	/**
	 * Increment the number of the offline AI.
	 */
	private void incrementAi() {
		beep();
		if (this.aiNumber.get() < 3)
			aiNumber.set(this.aiNumber.get() + 1);
	}

	/**
	 * Connect to a server.
	 */
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

	/**
	 * Display the rooms.
	 */
	private void displayRooms() {

		List<ClientServerLobbyRoom> rooms = this.client.getRoomList();

		this.roomsBox.getChildren().clear();

		for (ClientServerLobbyRoom room : rooms) {

			// Create a room display from its constituent elements.
			VBox roomContainer = new VBox();
			roomContainer.setMaxHeight(boxHeight * 0.7);
			roomContainer.setMinWidth(boxWidth/1.5);
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
			playersLabel.setMinWidth(boxHeight / 2.2);

			// Construct a label showing all the players in the room.
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

			// Show either a button to allow players to join the room, or a
			// label saying "IN GAME" or "FULL".
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

	/**
	 * Join a room.
	 * 
	 * @param id
	 *            the room ID
	 * @param button
	 *            the button used to join the room
	 */
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

	/**
	 * Display the players on the server.
	 */
	private void displayPlayers() {

		this.connectedPlayers = this.client.getPlayerList();

		this.serverPlayersBox.getChildren().clear();
		this.roomPlayersFlowPane.getChildren().clear();

		// For each player, add them to the two panes displaying online players.
		for (ClientServerPlayer player : this.connectedPlayers) {
			this.serverPlayersBox.getChildren().add(
					createLabel("- Player: " + player.getName(), true, true));
			this.roomPlayersFlowPane.getChildren().add(
					createLabel("- Player: " + player.getName(), true, true));
		}

		// If the client is in a room, perform some updating.
		if (this.client.isInRoom()) {
			ClientServerRoom room = this.client.getRoom();

			// Update the number of online AI
			this.aiNumber.set(room.getAIPlayerNumber());
			this.map = this.maps.get(room.getMapID());
			this.mapName.set(this.map.getName());
			this.readyPlayersVBox.getChildren().clear();
			this.readyPlayersVBox.getChildren().add(
					createLabel(
							"Game will begin when all\nplayers click ready!",
							false, false));

			// For each player, update their ready status and ready display on
			// the list.
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
					this.readyPlayersVBox.getChildren().add(playerBox);
				}
			}

			// Make sure all the AIs in the room are the same difficulty.
			List<ClientServerAI> ais = room.getAIPlayerList();
			for (ClientServerAI ai : ais) {
				try {
					if (!this.client.isInGame()) {
						this.client.setAIDifficulty(ai.getID(), this.aiDiff);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Update the difficulty selector with the AI difficulty we got from
			// the server.
			if (ais.size() > 0) {
				AIDifficulty diff = room.getAIPlayerList().get(0)
						.getDifficulty();
				int index = 1;
				switch (diff) {
				case EASY:
					index = 0;
					this.aiDiff = EASY;
					break;
				case MEDIUM:
					index = 1;
					this.aiDiff = MEDIUM;
					break;
				case HARD:
					index = 2;
					this.aiDiff = HARD;
					break;
				case EXTREME:
					index = 3;
					this.aiDiff = EXTREME;
					break;
				}
				this.onlineAiDifficultyChoice.getSelectionModel().select(index);
			}

			// Check there aren't too many AIs in the room using the max player
			// number.
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

	/**
	 * Go back to the previous scene on the scene stack.
	 */
	private void previous() {
		beep();

		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();

		Parent lastScene = this.previousScenes.pop();
		this.currentStage.getScene().setRoot(lastScene);
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);

		// Reset some fields just in case.
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

	/**
	 * Advance to the next scene.
	 * 
	 * @param thisScene
	 *            the current scene
	 * @param nextScene
	 *            the scene to advance to
	 */
	private void advance(Parent thisScene, Parent nextScene) {

		beep();
		double x = this.currentStage.getWidth();
		double y = this.currentStage.getHeight();

		this.previousScenes.push(thisScene);
		this.currentStage.getScene().setRoot(nextScene);
		this.currentStage.setWidth(x);
		this.currentStage.setHeight(y);
	}

	/**
	 * Set the player name.
	 * 
	 * @param string
	 *            the new player name
	 */
	private void setName(String string) {

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

	/**
	 * Begin an offline game.
	 * 
	 * @param map
	 *            the selected map
	 * @param playerName
	 *            the current player name
	 * @param controls
	 *            the current control scheme
	 * @param aiNum
	 *            the number of AI
	 */
	public void beginGame(Map map, String playerName,
			HashMap<Response, Integer> controls, int aiNum) {

		beep();
		Block[][] masterMap = this.map.getGridMap();
		int columnLength = masterMap[0].length;
		Block[][] arrayCopy = new Block[masterMap.length][columnLength];

		for (int x = 0; x < masterMap.length; x++) {

			arrayCopy[x] = Arrays.copyOf(masterMap[x], columnLength);
		}

		// Make a copy of the map to use.
		Map mapCopy = new Map(map.getName(), arrayCopy, map.getSpawnPoints());

		// Don't exit when the UI is hidden.
		Platform.setImplicitExit(false);

		// Get the volume to use for music and sound effects.
		float musicVolume = 50;
		musicVolume = this.muteMusicBtn.isSelected() ? 0
				: (float) this.musicSlider.getValue();

		float soundVolume = 50;
		soundVolume = this.muteSoundBtn.isSelected() ? 0
				: (float) this.soundSlider.getValue();

		// Start the game.
		new Game(this, mapCopy, playerName, controls, aiNum, this.aiDiff,
				musicVolume, soundVolume, this.currentStage.isFullScreen(),
				(int) this.currentStage.getWidth(),
				(int) this.currentStage.getHeight(), this.wasd);
	}

	/**
	 * Called if we get disconnected from the server.
	 */
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

	/**
	 * Called if our connection is accepted.
	 */
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

	/**
	 * Called if our connection is rejected (someone already has our name).
	 */
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

	/**
	 * Called if we are already connected.
	 */
	@Override
	public void alreadyConnected() {

	}

	/**
	 * Called if the client couldn't find the server.
	 */
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

	/**
	 * Called if the player list is received from the server.
	 */
	@Override
	public void playerListReceived() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				displayPlayers();
			}

		});
	}

	/**
	 * Called if the room list is received from the server.
	 */
	@Override
	public void roomListReceived() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				displayRooms();
			}

		});
	}

	/**
	 * Called if our request to creat a room was accepted.
	 */
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

				// Now that we are in a room, make sure our AI Difficulty
				// selector updates the server too.
				onlineAiDifficultyChoice.getSelectionModel()
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
										if (!client.isInGame()) {
											client.setAIDifficulty(ai.getID(),
													aiDiff);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
			}

		});
	}

	/**
	 * Blank a button (make it unresponsive).
	 * 
	 * @param button
	 *            the button to blank
	 * @param blankText
	 *            the new text for it to display
	 */
	private void blankButton(Button button, String blankText) {
		button.setOnAction(null);
		button.setText(blankText);
		button.getStyleClass().clear();
		button.getStyleClass().add("textfield");
	}

	/**
	 * Reset a button to new functionality and label.
	 * 
	 * @param button
	 *            the button to reset
	 * @param resetText
	 *            the new text to display
	 * @param handler
	 *            the action for it to do
	 */
	private void resetButton(Button button, String resetText,
			EventHandler<ActionEvent> handler) {
		button.setOnAction(handler);
		button.setText(resetText);
		button.getStyleClass().clear();
		button.getStyleClass().add("menubutton");
	}

	/**
	 * Called if our room creation request was rejected.
	 */
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

	/**
	 * Called if we tried to do something we need to be in a room to do, but
	 * we're not in a room.
	 */
	@Override
	public void notInRoom() {
	}

	/**
	 * Called if we try to join or create a room, but we're already in a room.
	 */
	@Override
	public void alreadyInRoom() {

	}

	/**
	 * Called if we tried to do something we need to be in a room to do, but
	 * we've left the room.
	 */
	@Override
	public void haveLeftRoom() {
	}

	/**
	 * Called if the game starts.
	 */
	@Override
	public void gameStarted() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				GameState gameState = client.getGameState();

				// Don't quit when the UI is hidden.
				Platform.setImplicitExit(false);

				// Get the volumes for music and sound effects.
				float musicVolume = 50;
				musicVolume = muteMusicBtn.isSelected() ? 0
						: (float) musicSlider.getValue();

				float soundVolume = 50;
				soundVolume = muteSoundBtn.isSelected() ? 0
						: (float) soundSlider.getValue();

				// Start an online game.
				onlineGame = new OnlineGame(ui, client, gameState, playerName
						.get(), musicVolume, soundVolume, connectedPlayers,
						controls, currentStage.isFullScreen(),
						(int) currentStage.getWidth(), (int) currentStage
								.getHeight());
			}

		});
	}

	/**
	 * Called when a new gameState object is received.
	 */
	@Override
	public void gameStateReceived() {
	}

	/**
	 * Called when the current online game ends.
	 */
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

	/**
	 * Hide the UI.
	 */
	public void hide() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				currentStage.hide();
			}

		});
	}

	/**
	 * Show the UI.
	 * 
	 * @param fullScreen
	 *            true if the UI should be fullscreen
	 * @param muted
	 *            true if the UI should be muted
	 * @param online
	 *            true if we're online
	 * @param gameFinished
	 *            true if the game has finished
	 */
	public void show(boolean fullScreen, boolean muted, boolean online,
			boolean gameFinished) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				muteMusicBtn.setSelected(muted);
				muteSoundBtn.setSelected(muted);
				currentStage.setFullScreen(fullScreen);
				currentStage.show();
				Platform.setImplicitExit(true);
				if (online && !gameFinished)
					leaveRoom();
			}

		});
	}

	/**
	 * Called if our connection attempt timed out.
	 */
	@Override
	public void connectionAttemptTimeout() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				enterLabel
						.setText("Enter Server Details:\n( Timed out trying to connect!\nMake sure server is running. )");
				resetButton(connectBtn, "Connect", e -> connect());
				client.exit();
				client = null;
				expectingConnection = false;
			}

		});
	}

	/**
	 * Called when we receive new information about the room.
	 */
	@Override
	public void roomReceived() {
	}
}