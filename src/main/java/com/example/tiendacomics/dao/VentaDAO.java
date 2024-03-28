package com.example.tiendacomics.dao;

import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Ticket;
import com.example.tiendacomics.model.Venta;
import java.sql.*;
import java.util.ArrayList;

public class VentaDAO extends DAO{

    public static void anadirVenta(Venta venta) {
        PreparedStatement sentencia = null;
        int cantidad  = venta.getCantidad();
        int codTicket = venta.getTicket().getNumTicket();
        int codArticulo = venta.getArticulo().getCodArticulo();

        conectar();
        try {
            String sql = "INSERT INTO ventas (cod_ticket, cod_articulo, fecha_hora_venta, cantidad) VALUES (?, ?, default, ?)";
            sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, codTicket);
            sentencia.setInt(2, codArticulo);
            sentencia.setInt(3, cantidad);
            try{
                sentencia.executeUpdate();
            }
            catch(SQLIntegrityConstraintViolationException excep){
                Logger.generarLineaLogError(excep.getClass().getSimpleName(), excep.getMessage());
            }

        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
    
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

    /**
     * Más utilidades sin usar. El método consulta la cantidad de artículos de una ocurrencia de la relación entre un ticket y un artículo concretos
     * @param venta La relación entre ticket y artículo
     * @return int
     */
    public int consultarCantArticuEnTicket(Venta venta) { //transaccion con rollback incluido en metodo
        int codArticulo = venta.getArticulo().getCodArticulo();
        int codTicket = venta.getTicket().getNumTicket();
        int cantidad = 0;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String consultarCantidad = "SELECT cantidad FROM ventas WHERE cod_articulo = ? and cod_ticket = ?;";
            sentencia = conexion.prepareStatement(consultarCantidad);

            sentencia.setInt(1, codArticulo);
            sentencia.setInt(2, codTicket);

            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                cantidad = resultado.getInt("cantidad");
            }

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(VentaDAO) consultarCantArticuEnTicket(Venta)", "Rollback aplicado");
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
        return cantidad;
    }

    public static void sumarCantDeArticulo(Venta venta) { //transaccion con rollback incluido en metodo
        int codArticulo = venta.getArticulo().getCodArticulo();
        int codTicket = venta.getTicket().getNumTicket();
        int cantidadArt = venta.getCantidad();
        PreparedStatement sentencia = null;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String updateCantidad = "UPDATE ventas SET cantidad = ? WHERE cod_articulo = ? and cod_ticket = ?;";
            sentencia = conexion.prepareStatement(updateCantidad);

            cantidadArt++;
            sentencia.setInt(1, cantidadArt);
            sentencia.setInt(2, codArticulo);
            sentencia.setInt(3, codTicket);

            sentencia.executeUpdate();

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            e.printStackTrace();
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(VentaDAO) sumarCantDeArticulo(Venta)", "Rollback aplicado");
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
    

    public static Venta consultarVentaArticulo(int codTicket, int codArt){
        Statement sentencia = null;
        ResultSet resultado = null;
        conectar();

        int cantidadArticulo = 0;
        Articulo articulo = null;
        Venta venta = null;
        ArrayList<Articulo> articulos = new ArrayList<>();

        try {
            String sql = "SELECT a.cod_articulo, a.nombre, a.precio, a.stock, t.cod_ticket, v.cantidad FROM articulos a INNER JOIN ventas v ON  v.cod_articulo = a.cod_articulo INNER JOIN tickets t ON v.cod_ticket = t.cod_ticket WHERE v.cod_ticket =" + codTicket +  " AND a.cod_articulo = " + codArt +";";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);

            while (resultado.next()) {
                int codArticulo = resultado.getInt("a.cod_articulo");
                String nombreArticulo = resultado.getString("a.nombre");
                double precioArticulo = resultado.getDouble("a.precio");
                int stockArticulo  = resultado.getInt("a.stock");
                cantidadArticulo  = resultado.getInt("v.cantidad");

                articulo = new Articulo(codArticulo,nombreArticulo,precioArticulo,stockArticulo);
                articulos.add(articulo);
            }
            Ticket ticket = new Ticket(codTicket, articulos);
            venta = new Venta(ticket, articulo, cantidadArticulo);
            
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
        return venta;
    }

    public static int consultarCantidadArticuloEnTicket(int codTicket, int codArticulo){
        Statement sentencia = null;
        ResultSet resultado = null;
        int cantidad = 0;
        conectar();
        try {
            String sql = "SELECT v.cantidad FROM articulos a INNER JOIN ventas v ON  v.cod_articulo = a.cod_articulo INNER JOIN tickets t ON v.cod_ticket = t.cod_ticket WHERE v.cod_ticket =" + codTicket + " AND a.cod_articulo = " + codArticulo +";";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);

            while(resultado.next()){ //si la consulta devuelve algo
                cantidad = resultado.getInt("v.cantidad");
            }
            
        } catch (NullPointerException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
                if (resultado != null) {
                    resultado.close();
                }
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
            }
            desconectar();
        }
        return cantidad;
    }
    
    public static void eliminarVentasTicket(int codTicket){
        PreparedStatement sentencia = null;
    
        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez
        
            String updateCantidad = "DELETE FROM ventas where cod_ticket = ?;";
            sentencia = conexion.prepareStatement(updateCantidad);
            sentencia.setInt(1, codTicket);
        
            sentencia.executeUpdate();
        
            conexion.commit(); //para hacer transaccion a la vez
        }
        catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(VentaDAO) eliminarVentasTicket(int)", "Rollback aplicado");
            }
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
    public static void eliminarVenta(int codTicket,int codArticulo){
        PreparedStatement sentencia = null;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String updateCantidad = "DELETE FROM ventas where cod_ticket = ? AND cod_articulo = ?;";
            sentencia = conexion.prepareStatement(updateCantidad);
            sentencia.setInt(1, codTicket);
            sentencia.setInt(2, codArticulo);

            sentencia.executeUpdate();

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(VentaDAO) eliminarVentasTicket(int)", "Rollback aplicado");
            }
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
            }
            desconectar();
        }
    }
}
