package com.example.tiendacomics.model;

public class Tomo extends Articulo{
    private int numVolumen;
    private static String descripcion = null;

    public Tomo(String nombre, double precio, int stock, TIPO tipo, int numVolumen) {
        super(nombre, precio, stock, tipo);
        this.numVolumen = numVolumen;
    }

    public Tomo(String nombre, double precio, int stock, TIPO tipo, int numVolumen, String descripcion) {
        super(nombre, precio, stock, tipo);
        this.numVolumen = numVolumen;
        Tomo.descripcion = descripcion;
    }

    public Tomo(int codArticulo, String nombre, double precio, int stock, TIPO tipo, int numVolumen, String descripcion) {
        super(codArticulo,nombre,precio,stock,tipo);
        this.numVolumen = numVolumen;
        Tomo.descripcion = descripcion;
    }
    
    public Tomo(int codArticulo, String nombre, double precio, int stock, int numVolumen, String descripcion) {
        super(codArticulo,nombre,precio,stock);
        this.numVolumen = numVolumen;
        Tomo.descripcion = descripcion;
    }

    public int getNumVolumen() {
        return numVolumen;
    }

    public void setNumVolumen(int numVolumen) {
        this.numVolumen = numVolumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        Tomo.descripcion = descripcion;
    }

}
