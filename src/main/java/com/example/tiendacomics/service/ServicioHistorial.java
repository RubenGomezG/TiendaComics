package com.example.tiendacomics.service;

import com.example.tiendacomics.dao.TicketDAO;
import com.example.tiendacomics.dao.VentaDAO;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Ticket;

import java.util.ArrayList;


public class ServicioHistorial {
    public static ArrayList<Articulo> articulosTicket(int codTicket){
        return TicketDAO.consultaTicket(codTicket);
    }

    public static boolean existeTicketBuscado(int codTicket){
        return TicketDAO.existeTicketBuscado(codTicket);
    }

    public static ArrayList<Articulo> consultaTicket(int codticket){
        return TicketDAO.consultaTicket(codticket);
    }
    
    public static Ticket consultarTicketBuscado(int codTicket){
        return TicketDAO.consultarTicketBuscado(codTicket);
    }
    
    public static int consultarCantidadArticuloEnTicket(int codTicket, int codArticulo){
        return VentaDAO.consultarCantidadArticuloEnTicket(codTicket,codArticulo);
    }
}
