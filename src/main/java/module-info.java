module org.example.proyectocrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.proyectocrud to javafx.fxml;
    exports org.example.proyectocrud;
    opens org.example.proyectocrud.controller to javafx.fxml;
    exports org.example.proyectocrud.controller;
    opens org.example.proyectocrud.model to javafx.base;
    exports org.example.proyectocrud.model;
    exports org.example.proyectocrud.database;
}