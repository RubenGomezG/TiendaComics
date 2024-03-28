package com.example.tiendacomics.service;

import com.example.tiendacomics.dao.TicketDAO;
import com.example.tiendacomics.dao.VentaDAO;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Ticket;
import com.example.tiendacomics.model.Venta;

import java.util.ArrayList;

public class ServicioTicket {
    /**
     * A este metodo se le llamará siempre que pulsemo el botón de comprar,
     * pues se cerrará el ticket activo y se creará uno nuevo vacío en su lugar
     */
    public static void anadirTicketBD(Ticket ticket) {
        TicketDAO.anadirTicket(ticket);
    }

    /**
     * incrementa la cantidad de un solo articulo dentro de un ticket
     * (Update en BD)
     */
    public static void sumarCantDeArticuloEnVentaBD(int codTicket, int codArticulo) {
        Venta venta = VentaDAO.consultarVentaArticulo(codTicket, codArticulo);
        VentaDAO.sumarCantDeArticulo(venta);
    }

    public static ArrayList<Articulo> consultarArticulosUltimoTicket() {
        return TicketDAO.consultaTicket(TicketDAO.codUltimoTicket());
    }

    public static int ultimoTicket() {
        return TicketDAO.codUltimoTicket();
    }

    public static void modificarTicket(Ticket ticket){
        TicketDAO.modificarTicket(ticket);
    }

    public static void crearVentaNueva(Venta venta) {
        VentaDAO.anadirVenta(venta);
    }

    public static Ticket consultarTicket(int codigo){
        return TicketDAO.consultarTicketBuscado(codigo);
    }
    
    public static void eliminarVentasTicket(int codTicket){
        VentaDAO.eliminarVentasTicket(codTicket);
    }

    public static void eliminarVenta(int codTicket, int codArticulo){
        VentaDAO.eliminarVenta(codTicket,codArticulo);
    }
    
    public static int consultarCantidadArticuloEnTicket(int codTicket, int codArt) {
        return VentaDAO.consultarCantidadArticuloEnTicket(codTicket, codArt);
    }
    
    public static void eliminarTicket(Ticket ticket){
        TicketDAO.eliminarTicket(ticket);
    }
}
