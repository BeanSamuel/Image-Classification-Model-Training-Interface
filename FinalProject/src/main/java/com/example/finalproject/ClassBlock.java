package com.example.finalproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassBlock extends Pane{
    private static final AtomicInteger count = new AtomicInteger(1);
    private static final String BASE_DIRECTORY = "D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\TrainingFile\\";
    private final FileChooser fileChooser = new FileChooser();
    private final HBox imageBox = new HBox(10);
    private final ScrollPane imageScrollPane = new ScrollPane(imageBox);
    private File directory;
    private StringProperty textFieldValue = new SimpleStringProperty();
    private String className;


    public ClassBlock(int classnum) {

        className = "Class" + classnum;
        directory = new File(BASE_DIRECTORY + className);
        directory.mkdir();

        TextField textField = createTextField(className);
        monitorTextField(textField);
        Button uploadButton = createUploadButton(className);
        VBox vbox = createVBox(textField, uploadButton);

        this.getChildren().addAll(getbackground(400, 120,Color.WHITE,15,15), vbox);
        this.setOnMouseClicked(event -> {
            if (event.getTarget() == this) {
                this.requestFocus();
            }
        });
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        imageScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);  // Always show horizontal scroll bar.
        imageScrollPane.setPrefSize(280,40);
        imageScrollPane.setFitToWidth(true);  // Let the ScrollPane fit its width.
        imageScrollPane.setStyle("-fx-background: white; -fx-padding: 0; -fx-hbar-policy: never; -fx-vbar-policy: never; -fx-control-inner-background: white;");
        imageScrollPane.getStylesheets().clear();
        imageScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double deltaY = event.getDeltaY();
            double width = imageScrollPane.getContent().getBoundsInLocal().getWidth();
            double x = imageScrollPane.getHvalue();
            imageScrollPane.setHvalue(x + -deltaY / width);
        });
    }
    public void monitorTextField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            textFieldValue.set(newValue);
        });
    }
    public String getTextFieldValue() {
        return textFieldValue.get();
    }
    private TextField createTextField(String className) {
        TextField textField = new TextField(className);
        textField.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 15px; -fx-border-color: transparent;");
        textField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String newValue = textField.getText();
                this.className = newValue;
                if (!newValue.isEmpty()) {
                    File newDirectory = new File(BASE_DIRECTORY + newValue);

                    if (!newDirectory.exists()) {
                        boolean success = directory.renameTo(newDirectory);

                        if (success) {
                            directory = newDirectory;
                        } else {
                            System.err.println("Failed to rename directory to " + newValue);
                        }
                    } else {
                        System.err.println("Directory with name " + newValue + " already exists");
                    }
                }

                textFieldValue.set(newValue);
                textField.getParent().requestFocus();
            }
        });
        return textField;
    }
    private Button createUploadButton(String className) {
        Button uploadButton = new Button();
        uploadButton.setPrefSize(55, 40);
        uploadButton.setStyle("-fx-background-color: #e8f0fe; -fx-border-color: transparent; -fx-background-radius: 5;");
        uploadButton.setGraphic(createIconAndText());
        uploadButton.setOnAction(event -> uploadImages(className));
        return uploadButton;
    }
    private VBox createIconAndText() {
        ImageView uploadIcon = new ImageView(new Image("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\com\\example\\upload.png"));
        uploadIcon.setFitWidth(20);
        uploadIcon.setFitHeight(20);
        Label uploadLabel = new Label("Upload");
        uploadLabel.setFont(new Font("Arial", 10));
        uploadLabel.setStyle("-fx-text-fill: #277ed5;");
        VBox iconAndText = new VBox(5, uploadIcon, uploadLabel);
        iconAndText.setAlignment(Pos.CENTER);
        return iconAndText;
    }
    private VBox createVBox(TextField textField, Button uploadButton) {
        HBox hBox = new HBox(30, uploadButton, imageScrollPane);  // Spacing between Button and ScrollPane.
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(0, 0, 0, 15));

        HBox textFieldWrapper = new HBox(textField);
        textFieldWrapper.setPadding(new Insets(0, 0, 5, 5));

        HBox hintWrapper = new HBox(new Label("Add Image Samples:"));
        hintWrapper.setAlignment(Pos.CENTER_LEFT);
        hintWrapper.setPadding(new Insets(2,0,2,15));

        VBox vbox = new VBox(textFieldWrapper, new Separator(), hintWrapper, hBox);
        vbox.setAlignment(Pos.CENTER_LEFT);
        return vbox;
    }
    private void uploadImages(String className) {
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        if (selectedFiles != null && directory != null) {
            for (File file : selectedFiles) {
                try {
                    Path destinationPath = Paths.get(BASE_DIRECTORY + this.className + "\\", file.getName());
                    Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            updatePreviewImage();
        }
    }
    private void updatePreviewImage() {
        File[] imageFiles = directory.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif"));
        if (imageFiles != null && imageFiles.length > 0) {
            Arrays.sort(imageFiles, Comparator.comparingLong(File::lastModified));

            imageBox.getChildren().clear();  // Clear previous images

            // Limit to first 10 images
            for (int i = 0; i < Math.min(imageFiles.length, 10); i++) {
                File imageFile = imageFiles[i];
                Image image = new Image(imageFile.toURI().toString());

                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(40);
                imageView.setFitWidth(55);
                imageView.setPreserveRatio(true);

                imageBox.getChildren().add(imageView);  // Add the new ImageView to the imageBox.
            }
        }
    }
    private Rectangle getbackground(double width, double height,Color color, int archeight, int arcwidth) {
        Rectangle rect = new Rectangle(width, height);
        rect.setFill(color);
        rect.setArcHeight(archeight);
        rect.setArcWidth(arcwidth);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(5);
        rect.setEffect(dropShadow);

        return rect;
    }
}
