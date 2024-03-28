package com.example.tiendacomics;


import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.ArticuloDAO;
import com.example.tiendacomics.dao.DAO;
import com.example.tiendacomics.model.Articulo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class AppTienda extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/LoginGui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700); //ancho, alto
        
       // ABRIR APLICACIÓN EN PANTALLA COMPLETA:
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
        
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        ArrayList<Articulo> articulos = ArticuloDAO.mostrarTodosArticulos();
        if (articulos.isEmpty()){
            DAO.crearBase();
        }
    
        Logger.eliminarLoggerAnterior(); //cada vez que lancemos la aplicación se nos creará un log nuevo, borrando el anterior
    }

    public static void main(String[] args) {
        launch();
    }
}