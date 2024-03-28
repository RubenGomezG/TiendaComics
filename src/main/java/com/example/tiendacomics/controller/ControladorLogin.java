package com.example.tiendacomics.controller;

import com.example.tiendacomics.AppTienda;
import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.DAO;
import com.example.tiendacomics.service.ServicioLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ControladorLogin extends Controlador {
    @FXML
    private TextField contrasena;
    @FXML
    private TextField usuario;
    @FXML
    private Button botonIniciarSesion;

    @FXML
    void introducirUsuario(ActionEvent ignoredEvent) {
        if (usuario.getText().equalsIgnoreCase(DAO.USUARIO)) {
            contrasena.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Usuario incorrecto");
            alert.setContentText("Pista: Luis Golden aprueba este usuario");
            alert.showAndWait();
            alert.close();
        }
    }
    
    @FXML
    void introducirContrasena(ActionEvent ignoredEvent) {
        if (ServicioLogin.validarContraseña(contrasena)) {
            if (ServicioLogin.validarUsuario(usuario)) {
                botonIniciarSesion.setDisable(false);
            }
        } else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Contraseña incorrecta");
            alert.setContentText("Pista: Luis Golden también aprueba esta contraseña");
            alert.showAndWait();
            alert.close();
        }
    }
    
    @FXML
    void conectarUsuario(ActionEvent event) {
        if (ServicioLogin.validarUsuario(usuario) && ServicioLogin.validarContraseña(contrasena)){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/VentasGui.fxml"));
                Scene escena = new Scene(fxmlLoader.load(), anchoPestana, altoPestana); //ancho, alto
                cambiarPestana(escena, event, "Ventas");
            } catch (IOException ioException) {
                Logger.generarLineaLogError(ioException.getClass().getSimpleName(), ioException.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Usuario o contraseña incorrectos");
            alert.setContentText("No cambies el usuario o contraseña después de que el botón esté habilitado, gracias!");
            alert.showAndWait();
            alert.close();
        }
    }
}
