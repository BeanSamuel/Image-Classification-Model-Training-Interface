package com.example.finalproject;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.*;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrainingBox {
    private Spinner<Integer> epochSpinner = new Spinner<>();
    private Spinner<Integer> batchSizeSpinner = new Spinner<>();
    private Spinner<Double> learningRateSpinner = new Spinner<>();
    private ProgressBar trainingProgress = new ProgressBar(0);
    private Process pythonProcess;

    private double initialHeight = 180.0;
    private DoubleProperty bgWidth = new SimpleDoubleProperty(200);

    private Rectangle background() {
        Rectangle rect = new Rectangle(bgWidth.get(), initialHeight);
        rect.setFill(Color.WHITE);
        rect.setArcHeight(10);
        rect.setArcWidth(10);
        rect.widthProperty().bind(bgWidth);
        return rect;
    }

    private HBox doubleparameter(String label, double min, double max, double initialValue, double delta, Spinner<Double> spinner) {
        DecimalFormat df = new DecimalFormat("#.######");
        HBox hbox = new HBox();
        Label label1 = new Label(label);
        spinner.setEditable(true);

        spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, delta));


        // Add a listener to update the text field to the specified format when the value changes
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            spinner.getEditor().setText(df.format(newValue));
        });

        // Set initial value in the text field
        spinner.getEditor().setText(df.format(initialValue));
        spinner.getEditor().setStyle("-fx-pref-width: 90px;");
        hbox.getChildren().addAll(label1, spinner);
        return hbox;
    }

    private HBox intparameter(String label, int min, int max, int initialValue, int delta, Spinner<Integer> spinner) {
        HBox hbox = new HBox();
        Label label2 = new Label(label);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue, delta));

        spinner.setEditable(true);
        spinner.getEditor().setStyle("-fx-pref-width: 60px;");

        hbox.getChildren().addAll(label2, spinner);
        return hbox;
    }

    private void closeWindowEvent(WindowEvent event) {
        if (pythonProcess != null) {
            pythonProcess.destroy();
        }
    }

    public Pane getTrainingBox() {
        StackPane stackpane = new StackPane();
        stackpane.getStylesheets().add(getClass().getResource("/com/example/TrainingBox_Styles.css").toExternalForm());

        Rectangle bg = background();
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        DoubleProperty bgHeight = new SimpleDoubleProperty(initialHeight);
        bg.heightProperty().bind(bgHeight);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.maxWidthProperty().bind(bgWidth);

        Text text = new Text("Training");
        text.setFont(Font.font(null, FontWeight.BOLD, 18));
        text.setFill(Color.rgb(0, 0, 0));
        HBox textFieldWrapper = new HBox(text);
        textFieldWrapper.setPadding(new Insets(0, 0, 0, 5));

        TitledPane collapsiblePane = new TitledPane();
        collapsiblePane.setExpanded(false);
        collapsiblePane.setContentDisplay(ContentDisplay.LEFT);
        collapsiblePane.getStyleClass().add("custom-collapsible-pane");
        collapsiblePane.setText("Advanced");
        collapsiblePane.prefWidthProperty().bind(bg.widthProperty());


        VBox paneContent = new VBox(10);
        paneContent.setStyle("-fx-background-color: white;"); // Set the background color of paneContent
        HBox hbox1 = intparameter("Epoch:              ", 1, 100, 50, 1, epochSpinner);
        HBox hbox2 = intparameter("Batch Size:       ", 16, 512, 32, 16, batchSizeSpinner);
        HBox hbox3 = doubleparameter("lr:             ", 0.00001, 2.0, 0.001, 0.00001, learningRateSpinner);
        paneContent.getChildren().addAll(hbox1, hbox2, hbox3);
        collapsiblePane.setContent(paneContent);

        Button trainingButton = new Button("Train Model");
        trainingButton.setFont(Font.font(null, FontWeight.BOLD, 14));
        trainingButton.setStyle("-fx-background-color: #f1f3f4; -fx-border-color: transparent;");
        trainingButton.setPrefSize(170, 20);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(trainingButton);

        trainingProgress.setPrefWidth(260); // Set preferred width for the progress bar

        HBox progressWrapper = new HBox(trainingProgress); // Create HBox to wrap the progress bar
        progressWrapper.setAlignment(Pos.CENTER); // Set alignment of the wrapper to center

        layout.getChildren().addAll(textFieldWrapper, buttonBox, progressWrapper,new Separator(), collapsiblePane);

        collapsiblePane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            double targetHeight;
            if (newValue) {
                targetHeight = layout.getHeight() - 400;
            } else {
                targetHeight = initialHeight;  // use the initialHeight instead of a calculated value
            }

            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(400),
                            new KeyValue(bgHeight, targetHeight, Interpolator.EASE_BOTH)
                    )
            );
            timeline.playFromStart();
        });

        trainingButton.setOnAction(event -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String scriptPath = "D:\\NCU\\大一下\\計算機實習\\FInalProject\\FinalProject\\src\\main\\resources\\com\\example\\model\\CNN.py";
                    String pythonPath = "C:/Users/SamuelChao/AppData/Local/Programs/Python/Python311/python.exe";

                    List<String> command = new ArrayList<>();
                    command.add(pythonPath);
                    command.add(scriptPath);
                    command.add("--epochs");
                    command.add(String.valueOf(epochSpinner.getValue()));
                    command.add("--batch_size");
                    command.add(String.valueOf(batchSizeSpinner.getValue()));
                    command.add("--learning_rate");
                    command.add(String.valueOf(learningRateSpinner.getValue()));

                    ProcessBuilder pb = new ProcessBuilder(command);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    Pattern epochPattern = Pattern.compile("Epoch (\\d+)/(\\d+)");
                    String line = "";
                    Platform.runLater(() -> {
                        trainingProgress.setProgress(0);
                        trainingProgress.setVisible(true);
                    });
                    while ((line = bfr.readLine()) != null) {
                        System.out.println(line);
                        Matcher matcher = epochPattern.matcher(line);
                        if (matcher.find()) {
                            int currentEpoch = Integer.parseInt(matcher.group(1));
                            double progress = (double) currentEpoch / epochSpinner.getValue();
                            Platform.runLater(() -> {
                                trainingProgress.setProgress(progress);
                            });
                        }
                    }

                    // Add animation to decrease the width
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(
                            new KeyFrame(Duration.millis(400),
                                    new KeyValue(bgWidth, 200, Interpolator.EASE_BOTH)
                            )
                    );
                    timeline.playFromStart();

                    return null;
                }
            };
            new Thread(task).start();
        });



        stackpane.getChildren().addAll(bg, layout);

        stackpane.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    newScene.windowProperty().addListener(new ChangeListener<Window>() {
                        @Override
                        public void changed(ObservableValue<? extends Window> observableValue, Window oldWindow, Window newWindow) {
                            if (newWindow != null) {
                                newWindow.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, TrainingBox.this::closeWindowEvent);
                            }
                        }
                    });
                }
            }
        });

        return stackpane;
    }
}
