module com.example.laborator7gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.socialnetwork to javafx.fxml;
    exports com.example.socialnetwork;

    exports com.example.socialnetwork.Domain;
    opens com.example.socialnetwork.Domain to javafx.fxml;
    exports com.example.socialnetwork.Controller;
    opens com.example.socialnetwork.Controller to javafx.fxml;
    exports com.example.socialnetwork.Controller.alert;
    opens com.example.socialnetwork.Controller.alert to javafx.fxml;

}