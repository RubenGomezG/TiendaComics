package com.example.tiendacomics.model;

public class Articulo {
    public enum TIPO {
        TOMO, MERCHANDISING, JUEGO
    }
    
    private int codArticulo;
    private String nombre;
    private double precio;
    private int stock;
    private TIPO tipo;
    
    String descArticulo;
    int numVolArticulo;
    String coleccionArticulo;
    String tipoMerchArticulo;
    String tallaMerchArticulo;

    public Articulo(String nombre, double precio, int stock, TIPO tipo) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.tipo = tipo;
    }
    
    public Articulo(int codArticulo, String nombre, double precio, int stock, TIPO tipo) {
        this.codArticulo = codArticulo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.tipo = tipo;
    }

    public Articulo(int codArticulo, String nombre, double precio, int stock) {
        this.codArticulo = codArticulo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public Articulo(int codArticulo, String nombre, double precio, int stock, String descArticulo,
                    int numVolArticulo, String coleccionArticulo, String tipoMerchArticulo, String tallaMerchArticulo){
        this.codArticulo = codArticulo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descArticulo = descArticulo;
        this.numVolArticulo = numVolArticulo;
        this.coleccionArticulo = coleccionArticulo;
        this.tipoMerchArticulo = tipoMerchArticulo;
        this.tallaMerchArticulo = tallaMerchArticulo;

    }
    
    public int getCodArticulo() {
        return codArticulo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public TIPO getTipo() {
        return tipo;
    }
    
    public void setTipo(TIPO tipo) {
        this.tipo = tipo;
    }
    
    public String getDescripcion(){
        return this.descArticulo;
    }

    public int getNumVolArticulo(){
        return this.numVolArticulo;
    }

    public String getColeccionArticulo(){
        return coleccionArticulo;
    }

    public String getTipoMerchArticulo(){
        return tipoMerchArticulo;
    }

    public String getTallaMerchArticulo(){
        return tallaMerchArticulo;
    }

    public String getInfoExtraArticulo(){
        String infoExtra = "";

        if(this instanceof Tomo){
            infoExtra += numVolArticulo;
        }
        
        if(this instanceof Merchandising){
            if (tipoMerchArticulo.equals("Ropa")){
                infoExtra += tallaMerchArticulo;
            }
        }
        
        return infoExtra;
    }
    
    @Override
    public String toString() {
        return "Articulo{" +
                "codArticulo=" + codArticulo +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", tipo=" + tipo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Articulo articulo = (Articulo) o;
        return codArticulo == articulo.codArticulo && Double.compare(articulo.precio, precio) == 0 && stock == articulo.stock && nombre.equals(articulo.nombre) && tipo == articulo.tipo;
    }
    
}