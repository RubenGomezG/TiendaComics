package com.example.tiendacomics.model;

public class Merchandising extends Articulo{
    public enum TIPO_MERCH{
        ROPA,ACCESORIO,POSTER,FIGURA,OTROS
    }

    public enum TALLA{
        XS,S,M,L,XL,XXL,OTRA
    }

    private TIPO_MERCH tipoMerch;
    private TALLA talla;
    private String coleccion;
    
    public Merchandising(int codArticulo,String nombre, double precio, int stock, TIPO tipo, TIPO_MERCH tipoMerch, TALLA talla, String coleccion) {
        super(codArticulo,nombre, precio, stock, tipo);
        this.tipoMerch = tipoMerch;
        this.talla = talla;
        this.coleccion = coleccion;
    }
    
    public TIPO_MERCH getTipoMerch() {
        return tipoMerch;
    }

    public void setTipoMerch(TIPO_MERCH tipoMerch) {
        this.tipoMerch = tipoMerch;
    }

    public TALLA getTalla() {
        return talla;
    }

    public void setTalla(TALLA talla) {
        this.talla = talla;
    }

    public String getColeccion() {
        return coleccion;
    }

    public void setColeccion(String coleccion) {
        this.coleccion = coleccion;
    }

}
