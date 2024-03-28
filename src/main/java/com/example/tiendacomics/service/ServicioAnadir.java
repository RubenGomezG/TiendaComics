package com.example.tiendacomics.service;

import com.example.tiendacomics.dao.ArticuloDAO;
import com.example.tiendacomics.model.Articulo;


public class ServicioAnadir {
    public static void anadirArticulo(Articulo articulo) {
        ArticuloDAO.anadirArticulo(articulo);
    }

    public static void modificarArticulo(Articulo articulo) {
        ArticuloDAO.modificarArticulo(articulo);
    }

    public static void borrarArticulo(Articulo articulo) {
        ArticuloDAO.borrarArticulo(articulo);
    }
}
