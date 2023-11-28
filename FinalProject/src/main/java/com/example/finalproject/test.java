package com.example.finalproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class test extends Application {

    private Rectangle background(double x, double y) {
        Rectangle rect = new Rectangle(x, y);
        rect.setFill(Color.rgb(255, 255, 255));
        rect.setArcHeight(10);
        rect.setArcWidth(10);
        return rect;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Folder Uploader");

        // 建立DirectoryChooser物件
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("選擇資料夾");

        // 創建UI元素
        Rectangle bg = background(300, 350);
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(20));

        Text text = new Text("Preview");
        text.setFont(Font.font(null, FontWeight.BOLD, 18));
        text.setFill(Color.BLACK);

        Button uploadButton = new Button("上傳資料夾");
        uploadButton.setFont(Font.font(null, FontWeight.SEMI_BOLD, 14));
        uploadButton.setOnAction(event -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                // 在此處加入上傳資料夾的程式碼
                System.out.println("已選擇資料夾: " + selectedDirectory.getAbsolutePath());
            }
        });

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(50);
        progressIndicator.setProgress(0);

        Slider slider = new Slider();
        slider.setPrefWidth(200);
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(0);

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setProgress(newValue.doubleValue() / 100);
            progressIndicator.setProgress(newValue.doubleValue() / 100);
        });

        layout.getChildren().addAll(text, uploadButton, progressBar, progressIndicator, slider);

        StackPane stackPane = new StackPane(bg, layout);
        Scene scene = new Scene(stackPane, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
