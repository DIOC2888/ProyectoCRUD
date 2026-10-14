module org.example.proyectocrud {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.proyectocrud to javafx.fxml;
    exports org.example.proyectocrud;
}