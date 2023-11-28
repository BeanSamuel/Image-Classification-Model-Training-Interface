package com.example.finalproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;


public class Main extends Application {
    public static void deleteDirectoryContents(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDirectoryContents(file);
                file.delete();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1080, 720);
        scene.setFill(Color.rgb(232,234,237));

        root.setTop(new TitlePane("Image Classification", primaryStage,root));

        root.setCenter(new WorkingPane().getWorkingPane(primaryStage));

        primaryStage.setOnCloseRequest(event -> {

            ArrayList<File> dellist = new ArrayList<>();

            dellist.add(new File("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\TrainingFile"));
            dellist.add(new File("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\PredictFile"));

            for(File i:dellist){
                deleteDirectoryContents(i);
            }
            System.exit(0);

        });

        primaryStage.setTitle("Image Classification");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
