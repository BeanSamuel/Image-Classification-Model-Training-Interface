package com.example.finalproject;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.Glyph;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.File;

public class TitlePane extends Pane {
    private Rectangle getbackground(int width, int height, Color color, int archeight, int arcwidth){

        Rectangle rect = new Rectangle(width, height);
        rect.setFill(color);
        rect.setArcHeight(archeight);
        rect.setArcWidth(arcwidth);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(5);
        rect.setEffect(dropShadow);

        return rect;
    }
    private Text gettitle(int width, int height,String text){

        Text t = new Text(width,height,text);
        t.setFont(Font.font("Arial Black", FontWeight.MEDIUM, 20));
        t.setFill(Color.rgb(25, 103, 210));

        return t;
    }
    private Glyph geticonbutton(String iconname, int fontsize, Stage primaryStage,BorderPane root){

        Glyph icon = Glyph.create(iconname);
        icon.setFontSize(fontsize);

        icon.setOnMouseClicked(event -> {

            Popup popup = new Popup();
            VBox popupContent = new VBox();

            popupContent.setStyle("-fx-background-color: white; -fx-padding: 10px;");
            popupContent.getChildren().add(new Text("Restart the Project"));

            Button yesButton = new Button("Yes");
            Button noButton = new Button("No");

            HBox buttonBox = new HBox(yesButton, noButton);
            buttonBox.setSpacing(10);
            buttonBox.setAlignment(Pos.CENTER);
            popupContent.getChildren().add(buttonBox);
            popup.getContent().add(popupContent);
            popup.setAutoHide(true);

            yesButton.setOnAction(e -> {
                primaryStage.close();
                Main.deleteDirectoryContents(new File("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\TrainingFile"));
                Main.deleteDirectoryContents(new File("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\PredictFile"));
                root.setCenter(new WorkingPane().getWorkingPane(primaryStage));
                primaryStage.show();
                popup.hide();
            });

            noButton.setOnAction(e -> popup.hide());
            double buttonX = icon.localToScreen(icon.getBoundsInLocal()).getMinX()-22;
            double buttonY = icon.localToScreen(icon.getBoundsInLocal()).getMinY()+3;
            popup.show(icon, buttonX, buttonY + icon.getHeight());

        });

        return icon;

    }

    public TitlePane(String text, Stage primaryStage,BorderPane root) {

        this.setPrefSize(1080, 50);

        Rectangle background = getbackground(310,50,Color.rgb(255,255,255),10,10);

        Text title = gettitle(115,25,text);

        Glyph hamburger = geticonbutton("FontAwesome|BARS",22, primaryStage,root);

        HBox hbox = new HBox(hamburger, title);
        hbox.setSpacing(20);
        hbox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(new StackPane(background, hbox));
    }
}
