package bomber.UI;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public abstract class UIScene {

	private BorderPane menu;
	public UIScene(){
		
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
}
