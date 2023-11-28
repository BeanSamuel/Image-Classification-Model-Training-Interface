package com.example.finalproject;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PreviewBox extends StackPane {
    private ProgressBar progressBar;
    private String image_path;
    private double initialHeight = 180.0;
    private final DoubleProperty bgHeight = new SimpleDoubleProperty(initialHeight);
    private VBox vbox=new VBox();
    private Map<String, ProgressBar> progressBarMap;
    public WatchService watchService;
    public Thread watchThread;
    private List<String> progressBarNames;


    private static final String MODEL_FILE_PATH = "D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\com\\example\\model\\model.h5";
    private static final String DESTINATION_PATH = "D:\\model.h5";
    private static final String PREDICT_FILE_PATH = "D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\PredictFile\\";
    private Rectangle createBackground(double width, double height, Color color, int arcHeight, int arcWidth) {
        Rectangle rect = new Rectangle(width, height);
        rect.heightProperty().bind(bgHeight);
        rect.setFill(color);
        rect.setArcHeight(arcHeight);
        rect.setArcWidth(arcWidth);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        dropShadow.setOffsetX(2);
        dropShadow.setOffsetY(2);
        dropShadow.setRadius(5);
        rect.setEffect(dropShadow);

        return rect;
    }
    private HBox getTextFeildWrapper(){

        Text text = new Text("Preview");
        text.setFont(Font.font(null, FontWeight.BOLD, 18));
        text.setFill(Color.BLACK);

        Button btw = new Button("Export");
        btw.setFont(Font.font(null, FontWeight.BOLD, 12));
        btw.setStyle("-fx-background-color: #f1f3f4; -fx-border-color: transparent;");
        btw.setMinWidth(100);
        btw.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(btw.getScene().getWindow());

            if (selectedDirectory != null) {
                Path source = Paths.get(MODEL_FILE_PATH);
                Path destination = Paths.get(selectedDirectory.getPath(), source.getFileName().toString());

                try {
                    Files.copy(source, destination);
//                    System.out.println("File copied successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        HBox hbox = new HBox(text, btw);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);

        return hbox;

    }
    private ImageView getImageView(){

        ImageView imageView = new ImageView();
        imageView.setFitHeight(0);
        imageView.setFitWidth(150);
        return imageView;

    }
    private HashMap getProgressBarMap(){
        HashMap hashmap = new HashMap<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\TrainingFile"))) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    String folderName = path.getFileName().toString();
                    ProgressBar folderProgressBar = new ProgressBar();
                    folderProgressBar.setProgress(0);

                    hashmap.put(folderName, folderProgressBar);
                    this.progressBarNames.add(folderName);

                    Label folderLabel = new Label(folderName);

                    Region padding = new Region();
                    padding.setPrefWidth(10);  // Set the padding width

                    HBox hbox = new HBox(padding, folderLabel, folderProgressBar);  // Create a HBox with padding
                    hbox.setSpacing(10);  // Set space between items
                    vbox.getChildren().add(hbox);  // Add the HBox to vbox
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return hashmap;
    }
    public Button getUploadButton(String text, ImageView imageView, Stage primaryStage, Rectangle background, VBox vboxContainer){

        Button btw = new Button(text);
        btw.setFont(Font.font(null, FontWeight.SEMI_BOLD, 12));
        btw.setStyle("-fx-background-color: #f1f3f4; -fx-border-color: transparent;");
        btw.setMinWidth(75);

        btw.setOnAction(event -> {
            // 刪除PredictFile資料夾中的舊檔案
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(PREDICT_FILE_PATH))) {
                for (Path path : directoryStream) {
                    if (!Files.isDirectory(path)) {
                        Files.deleteIfExists(path);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setFitHeight(150);
                imageView.setImage(image);

                Path sourcePath = selectedFile.toPath();
                Path destinationPath = Path.of(PREDICT_FILE_PATH + selectedFile.getName());

                try {
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    image_path = selectedFile.getName();

                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(background.widthProperty(), background.getWidth()),
                                    new KeyValue(bgHeight, background.getHeight())
                            ),
                            new KeyFrame(Duration.millis(200),
                                    new KeyValue(background.widthProperty(), 200),
                                    new KeyValue(bgHeight, vboxContainer.getHeight() - 340)
                            )
                    );
                    timeline.play();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        return btw;
    }
    private Button getRunButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font(null, FontWeight.SEMI_BOLD, 12));
        btn.setStyle("-fx-background-color: #f1f3f4; -fx-border-color: transparent;");
        btn.setMinWidth(75);

        btn.setOnAction(event -> {
            Thread pythonThread = new Thread(() -> {
                try {

                    Path sourceModelPath = Path.of(MODEL_FILE_PATH);
                    Path destinationModelPath = Path.of(DESTINATION_PATH);
                    Files.copy(sourceModelPath, destinationModelPath, StandardCopyOption.REPLACE_EXISTING);

                    String scriptPath = "D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\com\\example\\model\\Predict.py";
                    String pythonPath = "C:/Users/SamuelChao/AppData/Local/Programs/Python/Python311/python.exe";

                    List<String> command = new ArrayList<>();
                    command.add(pythonPath);
                    command.add(scriptPath);
                    command.add("--image_path");
                    command.add(image_path);
                    command.add("--class_names");
                    for(String i:progressBarNames){
                        command.add(i);
                    }
                    ProcessBuilder pb = new ProcessBuilder(command);
                    Process process = pb.start();

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String outputString;
                    while ((outputString = stdInput.readLine()) != null) {
                        System.out.println(outputString);
                        for(String i:progressBarNames){
                            if (outputString.contains(i)) {
                                String[] parts = outputString.split(": ");
                                String className = parts[0];
                                double progressValue = Double.parseDouble(parts[1]);
                                ProgressBar progressBar = progressBarMap.get(className);
                                if (progressBar != null) {
                                    Platform.runLater(() -> progressBar.setProgress(progressValue));
                                }
                            }
                        }

                    }

                    // Read any errors from the attempted command
                    while ((outputString = stdError.readLine()) != null) {
                        System.out.println(outputString);
                    }

                    Files.deleteIfExists(destinationModelPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            pythonThread.start();
        });

        return btn;
    }


    public PreviewBox(Stage primaryStage) {
        this.progressBarNames = new ArrayList<>();

        Rectangle background = createBackground(200, initialHeight, Color.WHITE, 10, 10);
        HBox textFieldWrapper = getTextFeildWrapper();
        ImageView imageView = getImageView();
        progressBarMap = getProgressBarMap();

        VBox vboxContainer = new VBox();
        vboxContainer.setAlignment(Pos.CENTER);
        vboxContainer.setSpacing(10);

        watchFolderChanges();

        Button uploadbutton = getUploadButton("Upload",imageView, primaryStage, background, vboxContainer);
        Button runPythonButton = getRunButton("Predict");
        HBox buttonFieldWrapper = new HBox(uploadbutton, runPythonButton);
        buttonFieldWrapper.setSpacing(10);
        buttonFieldWrapper.setAlignment(Pos.CENTER);

        vboxContainer.getChildren().addAll(textFieldWrapper, imageView, buttonFieldWrapper, vbox);

        this.getChildren().addAll(background,vboxContainer);



    }

    private void watchFolderChanges() {
        String parentFolder = "D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\data\\TrainingFile";
        Path parentFolderPath = Paths.get(parentFolder);

        try {
            watchService = FileSystems.getDefault().newWatchService();
            parentFolderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

            watchThread = new Thread(() -> {
                Path oldFolderName = null;

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path folderName = ev.context();

                        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            oldFolderName = folderName;
                        } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            final Path finalOldFolderName = oldFolderName;

                            Platform.runLater(() -> {
                                String newFolderNameStr = folderName.toString();
                                String oldFolderNameStr = (finalOldFolderName != null) ? finalOldFolderName.toString() : null;

                                if (oldFolderNameStr != null && progressBarMap.containsKey(oldFolderNameStr)) {
                                    ProgressBar oldProgressBar = progressBarMap.get(oldFolderNameStr);
                                    int index = -1;

                                    for (int i = 0; i < vbox.getChildren().size(); i++) {
                                        if (((HBox) vbox.getChildren().get(i)).getChildren().contains(oldProgressBar)) {
                                            index = i;
                                            break;
                                        }
                                    }

                                    if (index >= 0) {
                                        HBox oldHBox = (HBox) vbox.getChildren().get(index);
                                        Label oldLabel = (Label) oldHBox.getChildren().get(1); // This line has been modified
                                        oldLabel.setText(newFolderNameStr);

                                        progressBarMap.put(newFolderNameStr, progressBarMap.remove(oldFolderNameStr));
                                    }
                                    int oldNameIndex = progressBarNames.indexOf(oldFolderNameStr);
                                    if (oldNameIndex != -1) {
                                        this.progressBarNames.set(oldNameIndex, newFolderNameStr);
                                    }

                                } else {
                                    ProgressBar folderProgressBar = new ProgressBar();
                                    folderProgressBar.setProgress(0);
                                    progressBarMap.put(newFolderNameStr, folderProgressBar);

                                    Label folderLabel = new Label(newFolderNameStr);
                                    Region padding = new Region();
                                    padding.setPrefWidth(10);  // Set the padding width
                                    HBox hbox = new HBox(padding, folderLabel, folderProgressBar);  // Create a HBox
                                    hbox.setSpacing(10);  // Set space between items
                                    vbox.getChildren().add(hbox);  // Add the HBox to vbox
                                    this.progressBarNames.add(newFolderNameStr);
                                }
                            });

                            oldFolderName = null;
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            });

            watchThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
