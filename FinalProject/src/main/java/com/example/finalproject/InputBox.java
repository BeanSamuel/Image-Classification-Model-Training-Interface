package com.example.finalproject;

import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class InputBox extends StackPane{
    private double vboxHeight = 600;
    private int classnum = 2;
    private VBox vbox;
    public void traverseVBox(VBox vbox) {
        for (var node : vbox.getChildren()) {
            if (node instanceof Pane) {
                Pane pane = (Pane) node;
                for (var child : pane.getChildren()) {
                    if (child instanceof Rectangle) {
                        Rectangle rectangle = (Rectangle) child;
                        rectangle.setHeight((vboxHeight-15*(classnum-1))/classnum);
                    }
                }
            }
        }
    }
    public InputBox(Stage primaryStage){
        vbox = new VBox(new ClassBlock(1), new ClassBlock(2));
        vbox.setSpacing(15);
        Button btn = new Button("Add New Class");
        vbox.getChildren().add(btn);
        VBox.setMargin(btn, new Insets(10));
        vbox.setAlignment(Pos.CENTER);

        btn.setOnAction(event -> {
            classnum+=1;
            if(classnum<5) {
                vbox.getChildren().add(classnum - 1, new ClassBlock(classnum));
            }
//            } else{
//                vbox.getChildren().add(classnum-1,new ClassBlock().newclass(primarystage));
//                traverseVBox(vbox);
//                btn.setDisable(true);
//            }
            if(classnum>=4){
                btn.setDisable(true);
            }

        });

        this.getChildren().add(vbox);
    }
    public ArrayList<String> getClassnames(){
        ArrayList<String> classnames = new ArrayList<>();
        for (var node : vbox.getChildren()) {
            if (node instanceof ClassBlock) {
                ClassBlock classBlock = (ClassBlock) node;
                classnames.add(classBlock.getTextFieldValue());
            }
        }
        for(String i:classnames){
            System.out.println(i);
        }
        return classnames;
    }
}
