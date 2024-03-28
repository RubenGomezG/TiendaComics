package com.example.tiendacomics.service;

import com.example.tiendacomics.dao.DAO;
import javafx.scene.control.TextField;

public class ServicioLogin {
    public static boolean validarContraseña(TextField contrasena){
        return contrasena.getText().equalsIgnoreCase(DAO.USUARIO);
    }

    public static boolean validarUsuario(TextField usuario) {
        return usuario.getText().equalsIgnoreCase(DAO.USUARIO);
    }
}
