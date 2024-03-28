module com.example.tiendacomics {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;

    opens com.example.tiendacomics to javafx.fxml;
    exports com.example.tiendacomics;
    exports com.example.tiendacomics.controller;
    opens com.example.tiendacomics.controller to javafx.fxml;
}