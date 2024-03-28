package com.example.tiendacomics.model;

public class Juego extends Articulo{
    private String descripcion;

    public Juego(String nombre, double precio, int stock, TIPO tipo, String descripcion) {
        super(nombre, precio, stock, tipo);
        this.descripcion = descripcion;
    }
    
    public Juego(int codArticulo, String nombre, double precio, int stock, String descripcion) {
        super(codArticulo, nombre, precio, stock);
        this.descripcion = descripcion;
    }

    public Juego(int codArticulo, String nombre, double precio, int stock, TIPO tipo, String descripcion) {
        super(codArticulo,nombre,precio,stock,tipo);
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
