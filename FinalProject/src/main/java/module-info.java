module com.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    opens com.example.finalproject to javafx.fxml;
    exports com.example.finalproject;
}