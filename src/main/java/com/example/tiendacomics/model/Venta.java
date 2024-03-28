package com.example.tiendacomics.model;


import java.time.Instant;
import java.util.Date;


public class Venta {
    private Ticket ticket;
    private Articulo articulo;
    private int cantidad;
    private static Date fechaHoraVenta; //default now()

    public Venta(Ticket ticket, Articulo articulo, int cantidad) {
        this.ticket = ticket;
        this.articulo = articulo;
        this.cantidad = cantidad;
        fechaHoraVenta = Date.from(Instant.now());
    }

    public int getCantidad(){
        return this.cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public static Date getFechaHoraVenta() {
        return fechaHoraVenta;
    }

    public  void setFechaHoraVenta(Date fechaHoraVenta) {
        Venta.fechaHoraVenta = fechaHoraVenta;
    }
}
