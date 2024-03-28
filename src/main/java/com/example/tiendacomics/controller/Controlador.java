package com.example.tiendacomics.controller;

import com.example.tiendacomics.AppTienda;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Controlador {
    protected double anchoPestana;
    protected double altoPestana;

    public Controlador(){
        altoPestana = 700;
        anchoPestana = 1100;
    }

    void cerrarSesion(ActionEvent event, double anchoPestana, double altoPestana) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/LoginGui.fxml"));
        Scene escena = new Scene(fxmlLoader.load(), anchoPestana, altoPestana);
        cambiarPestana(escena,event,"Login");
    }

    void pestanaArticulos(ActionEvent event, double anchoPestana, double altoPestana) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/ArticulosGui.fxml"));
        Scene escena = new Scene(fxmlLoader.load(), anchoPestana, altoPestana);
        cambiarPestana(escena,event,"Articulos");
    }

    void pestanaHistorial(ActionEvent event, double anchoPestana, double altoPestana) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/HistorialGui.fxml"));
        Scene escena = new Scene(fxmlLoader.load(), anchoPestana, altoPestana);
        cambiarPestana(escena,event,"Historial");
    }

    void pestanaVentas(ActionEvent event, double anchoPestana, double altoPestana) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/VentasGui.fxml"));
        Scene escena = new Scene(fxmlLoader.load(), anchoPestana, altoPestana);
        cambiarPestana(escena,event,"Ventas");
    }

    public void cambiarPestana(Scene escena, ActionEvent event, String titulo){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(escena);
        stage.setTitle(titulo);
    }
}
