package com.example.tiendacomics.model;

import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.TicketDAO;
import com.example.tiendacomics.service.ServicioTicket;
import java.util.ArrayList;
import java.util.Date;

public class Ticket {
    private ArrayList<Articulo> listaCompra;
    private double importe;
    private int numTicket;
    private Date fechaHoraTicket;

    public Ticket(){
        this.numTicket += 1;
        this.fechaHoraTicket = Venta.getFechaHoraVenta();
        this.importe = 0;
        this.listaCompra = new ArrayList<>();
    }

    public Ticket(int codTicket, ArrayList<Articulo> articulos){
        this.numTicket = codTicket;
        this.fechaHoraTicket = Venta.getFechaHoraVenta();
        this.listaCompra = articulos;
        calcularImporte();
    }

    public int getNumTicket(){
        return this.numTicket;
    }
    
    public double getImporte(){
        calcularImporte();
        return this.importe;
    }
    
    public ArrayList<Articulo> getListaCompra(){
        return this.listaCompra;
    }
    
    private void setListaCompra(ArrayList<Articulo> listaCompra) {
        this.listaCompra = listaCompra;
    }

    public void setNumTicket(int numTicket){
        this.numTicket = numTicket;
    }
    
    public void calcularImporte(){
        double importe = 0.00;
        setListaCompra(TicketDAO.consultaTicket(getNumTicket()));
        try {
            for (Articulo articulo : this.listaCompra) {
                int codTicket = this.numTicket;
                int codArt = articulo.getCodArticulo();
                int cantidad = ServicioTicket.consultarCantidadArticuloEnTicket(codTicket, codArt);
                importe += articulo.getPrecio() * cantidad;
            }
        }catch(NumberFormatException e){
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            Logger.generarLineaLogDebug( "(Ticket) calcularImporte()", "No se puede calcular el importe porque no hay ningún artículo en el ticket");
        }
        this.importe = importe;
    }
    
    public void anadirArticulo(Articulo articulo){
        listaCompra.add(articulo);
    }
    
    public void anadirTodosATicket(ArrayList<Articulo> articulos){
        listaCompra.addAll(articulos);
    }

    public Date getFechaHoraTicket() {
        return fechaHoraTicket;
    }

    public void setFechaHoraTicket(Date fechaHoraTicket) {
        this.fechaHoraTicket = fechaHoraTicket;
    }
}