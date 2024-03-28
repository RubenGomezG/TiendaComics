package com.example.tiendacomics.service;

import com.example.tiendacomics.dao.ArticuloDAO;
import com.example.tiendacomics.model.Articulo;

import java.util.ArrayList;

public class ServicioArticulos {
    public static ArrayList<Articulo> getConsulta(String nombre){
        return ArticuloDAO.consultarArticulos(nombre);
    }

    public static Articulo consultarArticulo(int codigo) {
        return ArticuloDAO.consultarArticulo(codigo);
    }

}
