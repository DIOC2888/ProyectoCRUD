package org.example.proyectocrud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionProductoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GestionProductoApplication.class.getResource("/org/example/proyectocrud/GestionProducto.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestion de productos");
        stage.setScene(scene);
        stage.show();
    }
}
