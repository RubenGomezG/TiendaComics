package com.example.tiendacomics.dao;


import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TicketDAO extends DAO{

    public static void anadirTicket(Ticket ticket) {
        PreparedStatement sentencia = null;
        int codTicket = codUltimoTicket() + 1;
        
        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String sql = "INSERT INTO tickets(cod_ticket, fecha_hora_ticket) VALUES(?,now());";
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1,codTicket);
            sentencia.executeUpdate();

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            try{
                conexion.rollback();
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(TicketDAO) anadirTicket(Ticket)", "Rollback aplicado");
            }
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
    }
    public static void modificarTicket(Ticket ticket) {
        PreparedStatement sentencia = null;
        ArrayList<Articulo> articulos = ticket.getListaCompra();
        int codigo = ticket.getNumTicket();
    
        conectar();
        try {
            String sql = "UPDATE tickets SET cod_ticket = ?, fecha_hora_ticket = now() WHERE cod_ticket = ?";
            sentencia = conexion.prepareStatement(sql);
    
            sentencia.setInt(1, codigo);
            sentencia.setInt(2, codigo);
            sentencia.executeUpdate();
    
            for (Articulo articulo : articulos) {
                int codArt = articulo.getCodArticulo();
                Venta venta = VentaDAO.consultarVentaArticulo(codigo,codArt);
                conectar();
                int cantidad = venta.getCantidad();
                String sqlVenta = "UPDATE ventas SET cod_ticket = ?, cod_articulo = ?, cantidad = ?, fecha_hora_venta = now() WHERE cod_ticket = ? AND cod_articulo = ?";
                sentencia = conexion.prepareStatement(sqlVenta);
                sentencia.setInt(1,codigo);
                sentencia.setInt(2,codArt);
                sentencia.setInt(3,cantidad);
                sentencia.setInt(4,codigo);
                sentencia.setInt(5,codArt);
                sentencia.executeUpdate();
            }

        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
            }
            catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
            }
            desconectar();
        }
}
    public static ArrayList<Articulo> consultaTicket(int codTicket) {
        ArrayList<Articulo> articulosTicket = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String sql = "SELECT a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(t.descripcion, j.descripcion), '') AS 'descripcion', t.num_volumen, m.coleccion, m.tipo, m.talla FROM articulos a INNER JOIN ventas v ON  v.cod_articulo = a.cod_articulo INNER JOIN tickets ON v.cod_ticket = tickets.cod_ticket LEFT JOIN tomos t ON a.cod_articulo = t.cod_tomo LEFT JOIN juegos_mesa j ON a.cod_articulo = j.cod_juego LEFT JOIN merchandising m ON a.cod_articulo = m.cod_merch WHERE tickets.cod_ticket = ?";
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1,codTicket);

            resultado = sentencia.executeQuery();
            while (resultado.next()) {
                Articulo articulo = ArticuloDAO.getArticulo(resultado);
                articulosTicket.add(articulo);
            }

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(TicketDAO) consultaTicket(int)", "Rollback aplicado");
            }
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null){
                    resultado.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
        return articulosTicket;
    }

    public static int codUltimoTicket() { // devuelve el codigo del ticket abierto actualmente
        Statement sentencia = null;
        ResultSet resultado = null;
        conectar();
        int ultimo = 0;
        try {
            String sql = "SELECT max(cod_ticket) from tickets;";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);
            if (resultado.next()) {
                ultimo = resultado.getInt(1);
            }
        }
        catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null){
                    resultado.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
        return ultimo;
    }
    
    public static void eliminarTicket(Ticket ticket){
        int codTicket = ticket.getNumTicket();
        PreparedStatement sentencia = null;
        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String consultarCantidad = "DELETE FROM tickets WHERE cod_ticket = ?;";
            sentencia = conexion.prepareStatement(consultarCantidad);
            sentencia.setInt(1, codTicket);
            sentencia.execute();

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(TicketDAO) eliminarTicket(Ticket)", "Rollback aplicado");
            }
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
    }

    public static void anadirArticuloUltimoTicket(Articulo articulo){
        Ticket ticket =  consultarTicketBuscado(codUltimoTicket());
        Venta venta = new Venta(ticket, articulo, 1);
        VentaDAO.anadirVenta(venta);
    }

    public static Ticket consultarTicketBuscado(int codTicket){
        Statement sentencia = null;
        ResultSet resultado = null;
        conectar();

        Articulo articulo;
        Ticket ticket = null;
        ArrayList<Articulo> articulos = new ArrayList<>();

        try {
            String sql = "SELECT a.cod_articulo, a.nombre, a.precio, a.stock FROM articulos a INNER JOIN ventas v ON  v.cod_articulo = a.cod_articulo INNER JOIN tickets t ON v.cod_ticket = t.cod_ticket WHERE v.cod_ticket =" + codTicket + ";";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);

            while (resultado.next()) {
                int codArt = resultado.getInt("a.cod_articulo");
                String nombreArt = resultado.getString("a.nombre");
                double precioArt = resultado.getInt("a.precio");
                int stockArt = resultado.getInt("a.stock");

                articulo = new Articulo(codArt,nombreArt,precioArt,stockArt);
                articulos.add(articulo);
            }
            ticket = new Ticket(codTicket, articulos);
            
        } catch (NullPointerException | SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null) {
                    resultado.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
        return ticket;
    }
    /**
     * Otra utilidad más que en algún momento utilizamos y ahora ya no, no merece la pena borrar, puede ser útil en un futuro.
     * El método busca un artículo concreto dentro de un ticket
     * @param cantidad cantidad de articulos en ticket
     * @param codTicket código del ticket a buscar
     * @param nombreArt nombre del artículo a buscar
     * @param precioArt precio del artículo a buscar
     */
    public static Articulo buscarArticuloEnTicket(String nombreArt, double precioArt, int cantidad, int codTicket){
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        
        conectar();
        Articulo articulo = null;
        try {
            String sql =  "SELECT a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(tom.descripcion, j.descripcion), '') AS 'descripcion', tom.num_volumen, m.coleccion, m.tipo, m.talla FROM articulos a INNER JOIN ventas v ON  v.cod_articulo = a.cod_articulo INNER JOIN tickets tic ON v.cod_ticket = tic.cod_ticket LEFT JOIN tomos tom ON a.cod_articulo = tom.cod_tomo LEFT JOIN juegos_mesa j ON a.cod_articulo = j.cod_juego LEFT JOIN merchandising m ON a.cod_articulo = m.cod_merch WHERE a.nombre=? AND a.precio=? AND v.cantidad=? AND v.cod_ticket=?;";
            sentencia = conexion.prepareStatement(sql);
            
            sentencia.setString(1, nombreArt);
            sentencia.setDouble(2, precioArt);
            sentencia.setInt(3, cantidad);
            sentencia.setInt(4, codTicket);
            
            sentencia.executeQuery();

            while (resultado.next()) {
                int codArticulo = resultado.getInt("a.cod_articulo");
                String nombreArticulo = resultado.getString("a.nombre");
                double precioArticulo = resultado.getDouble("a.precio");
                int stockArticulo  = resultado.getInt("a.stock");
                String descArticulo  = resultado.getString("descripcion");
                int numVolArticuloInt  = resultado.getInt("tom.num_volumen");
                String coleccionArticulo  = resultado.getString("m.coleccion");
                String tipoMerchArticulo  = resultado.getString("m.tipo");
                String tallaMerchArticulo  = resultado.getString("m.talla");

                articulo = new Articulo(codArticulo, nombreArticulo, precioArticulo, stockArticulo, descArticulo, numVolArticuloInt, coleccionArticulo, tipoMerchArticulo, tallaMerchArticulo);
            }

        }
        catch (NullPointerException | SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null) {
                    resultado.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
        return articulo;
    }

    public static boolean existeTicketBuscado(int codTicket){
        Statement sentencia = null;
        ResultSet resultado = null;
        conectar();

        try {
            String sql = "SELECT * FROM tickets WHERE cod_ticket =" + codTicket + ";";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);
            if (resultado.next()) {
                return true;
            }
        } catch (NullPointerException | SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null) {
                    resultado.close();
                }
            } catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
        return false;
    }


}
