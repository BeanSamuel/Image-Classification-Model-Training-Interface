package com.example.finalproject;

import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;



public class WorkingPane {
    public Pane getWorkingPane(Stage primaryStage){

        StackPane stackpane = new StackPane();
        HBox hbox = new HBox(new InputBox(primaryStage), new TrainingBox().getTrainingBox(), new PreviewBox(primaryStage));

        hbox.setPrefSize(1080,700);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(40);
        stackpane.getChildren().add(hbox);

        return stackpane;
    }

}
