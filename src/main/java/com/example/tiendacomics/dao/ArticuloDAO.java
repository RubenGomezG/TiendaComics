package com.example.tiendacomics.dao;

import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Juego;
import com.example.tiendacomics.model.Merchandising;
import com.example.tiendacomics.model.Tomo;


import java.sql.*;
import java.util.ArrayList;

public class ArticuloDAO extends DAO {

    public static void anadirArticulo(Articulo articulo) {
        PreparedStatement sentencia = null;
        String nombre = articulo.getNombre();
        double precio = articulo.getPrecio();
        int stock = articulo.getStock();
        int codigo = articulo.getCodArticulo();
        String sqlSubtipo;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String sql = "INSERT INTO articulos (cod_articulo,nombre,precio,stock) VALUES (?,?,?,?)";
            sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, codigo);
            sentencia.setString(2, nombre);
            sentencia.setDouble(3, precio);
            sentencia.setInt(4, stock);

            sentencia.executeUpdate();

            if (articulo instanceof Tomo) {
                int numVol = ((Tomo) articulo).getNumVolumen();
                if (articulo.getDescripcion() == null) {

                    sqlSubtipo = "INSERT INTO tomos (cod_tomo,num_volumen) VALUES (?,?)";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setInt(2, numVol);
                } else {
                    String descripcion = articulo.getDescripcion();
                    sqlSubtipo = "INSERT INTO tomos (cod_tomo,num_volumen,descripcion) VALUES (?,?,?)";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setInt(2, numVol);
                    sentencia.setString(3, descripcion);
                }
            } else if (articulo instanceof Juego) {
                if (articulo.getDescripcion() == null) {
                    sqlSubtipo = "INSERT INTO juegos_mesa (cod_juego) VALUES (?)";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                } else {
                    String descripcion =  articulo.getDescripcion();
                    sqlSubtipo = "INSERT INTO juegos_mesa (cod_juego,descripcion) VALUES (?,?)";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setString(2, descripcion);
                }
            } else if (articulo instanceof Merchandising) {
                String tipoMerch = String.valueOf(((Merchandising) articulo).getTipoMerch());
                String coleccion = ((Merchandising) articulo).getColeccion();

                if (((Merchandising) articulo).getTalla() == null) {

                    sqlSubtipo = "INSERT INTO merchandising (cod_merch,tipo,coleccion) VALUES (?,?,?)";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setString(2, tipoMerch);
                    sentencia.setString(3, coleccion);
                } else {
                    String talla = String.valueOf(((Merchandising) articulo).getTalla());
                    sqlSubtipo = "INSERT INTO merchandising (cod_merch,tipo,coleccion,talla) VALUES (?,?,?,?)";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setString(2, tipoMerch);
                    sentencia.setString(3, coleccion);
                    sentencia.setString(4, talla);
                }
            }
            sentencia.executeUpdate();
            
            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try {
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(ArticuloDAO) anadirArticulo(Articulo)", "Rollback aplicado");
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

    public static ArrayList<Articulo> consultarArticulos(String nombreABuscar) {
        Statement sentencia = null;
        ResultSet resultado = null;
        conectar();

        ArrayList<Articulo> articulos = new ArrayList<>();
        if (nombreABuscar.equals("")){
            return articulos;
        }
        
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String sql = "SELECT a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(t.descripcion, j.descripcion), '') AS 'descripcion', t.num_volumen, m.coleccion, m.tipo, m.talla FROM articulos a LEFT JOIN tomos t ON a.cod_articulo = t.cod_tomo LEFT JOIN juegos_mesa j ON a.cod_articulo = j.cod_juego LEFT JOIN merchandising m on a.cod_articulo = m.cod_merch WHERE a.nombre LIKE '"+nombreABuscar+"%';";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);

            while (resultado.next()) {
                Articulo articulo = getArticulo(resultado);
                articulos.add(articulo);
            }

            conexion.commit(); //para hacer transaccion a la vez
        } catch (NullPointerException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try {
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(ArticuloDAO) consultarArticulo(String)", "Rollback aplicado");
            }
        } finally {
            try {
                if (sentencia != null) {
                    // Cerramos la conexión y la sentencia
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
        return articulos;
    }

    public static Articulo getArticulo(ResultSet resultado) throws SQLException {
        int codArticulo = resultado.getInt("a.cod_articulo");
        String nombreArticulo = resultado.getString("a.nombre");
        double precioArticulo = resultado.getDouble("a.precio");
        int stockArticulo  = resultado.getInt("a.stock");
        String descArticulo  = resultado.getString("descripcion");
        int numVolArticuloInt  = resultado.getInt("t.num_volumen");
        String coleccionArticulo  = resultado.getString("m.coleccion");
        if (coleccionArticulo == null){
            coleccionArticulo = "";
        }
        String tipoMerchArticulo  = resultado.getString("m.tipo");
        if (tipoMerchArticulo == null){
            tipoMerchArticulo = "";
        }
        String tallaMerchArticulo  = resultado.getString("m.talla");
        if (tallaMerchArticulo == null){
            tallaMerchArticulo = "";
        }
        Articulo articulo = new Articulo(codArticulo, nombreArticulo, precioArticulo, stockArticulo, descArticulo, numVolArticuloInt, coleccionArticulo, tipoMerchArticulo, tallaMerchArticulo);
        return articulo;
    }

    /**
     * Consulta para mostrar todos los articulos de todos los tipos
     * cuando aún no has buscado nada en el buscador
     * (muestra tod0 ordenado en base a la PK)
     * SELECT a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(t.descripcion, j.descripcion), null), t.num_volumen, m.coleccion, m.tipo, m.talla FROM articulos a LEFT JOIN tomos t ON t.cod_tomo = a.cod_articulo LEFT JOIN juegos_mesa j ON j.cod_juego = a.cod_articulo LEFT JOIN merchandising m on m.cod_merch = a.cod_articulo;
     * @return articulos - El conjunto de articulos existentes en la BD (ArrayList)
     *
     */
    public static ArrayList<Articulo> mostrarTodosArticulos(){
        ArrayList<Articulo> todosArticulos = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        int numVolArticuloInt = 0;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String sql = "SELECT a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(t.descripcion, j.descripcion), null) as 'descripcion', t.num_volumen, m.coleccion, m.tipo, m.talla FROM articulos a LEFT JOIN tomos t ON t.cod_tomo = a.cod_articulo LEFT JOIN juegos_mesa j ON j.cod_juego = a.cod_articulo LEFT JOIN merchandising m on m.cod_merch = a.cod_articulo;";
            sentencia = conexion.prepareStatement(sql);

            resultado = sentencia.executeQuery(sql);

            while (resultado.next()) {
                int codArticulo = resultado.getInt("a.cod_articulo");
                String nombreArticulo = resultado.getString("a.nombre");
                double precioArticulo = resultado.getDouble("a.precio");
                int stockArticulo  = resultado.getInt("a.stock");
                String descArticulo  = resultado.getString("descripcion");
                String numVolArticuloStr  = resultado.getString("t.num_volumen");
                String coleccionArticulo  = resultado.getString("m.coleccion");
                String tipoMerchArticulo  = resultado.getString("m.tipo");
                String tallaMerchArticulo  = resultado.getString("m.talla");

                if(numVolArticuloStr != null){
                    numVolArticuloInt =  Integer.parseInt(numVolArticuloStr);
                }

                Articulo articulo = new Articulo(codArticulo, nombreArticulo, precioArticulo, stockArticulo, descArticulo, numVolArticuloInt, coleccionArticulo, tipoMerchArticulo, tallaMerchArticulo);
                todosArticulos.add(articulo);
            }
            
        } catch (SQLSyntaxErrorException syntax){
            Logger.generarLineaLogError(syntax.getClass().getSimpleName(), syntax.getMessage());
            return new ArrayList<>();
        } catch (NullPointerException e){
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(ArticuloDAO) mostrarTodosArticulos()", "Rollback aplicado");
            }
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
        return todosArticulos;
    }

    public static Articulo consultarArticulo(int codigo){
        Statement sentencia = null;
        ResultSet resultado = null;
        conectar();

        Articulo articulo = null;

        try {
            String sql = "SELECT a.cod_articulo, a.nombre, a.precio, a.stock, ifnull(ifnull(t.descripcion, j.descripcion), '') AS 'descripcion', t.num_volumen, m.coleccion, m.tipo, m.talla FROM articulos a LEFT JOIN tomos t ON a.cod_articulo = t.cod_tomo LEFT JOIN juegos_mesa j ON a.cod_articulo = j.cod_juego LEFT JOIN merchandising m on a.cod_articulo = m.cod_merch WHERE a.cod_articulo = '"+codigo+"';";
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(sql);

            while (resultado.next()) {
                articulo = getArticulo(resultado);
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
        return articulo;
    }


    public static void borrarArticulo(Articulo articulo) {
        PreparedStatement sentencia = null;
        int codigo = articulo.getCodArticulo();
        
        conectar();
        try {
            String sql = "DELETE FROM articulos WHERE cod_articulo = ?";
            sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, codigo);
            sentencia.executeUpdate();
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

    public static void modificarArticulo(Articulo articulo) {
        PreparedStatement sentencia = null;
        String nombre = articulo.getNombre();
        double precio = articulo.getPrecio();
        int stock = articulo.getStock();
        int codigo = articulo.getCodArticulo();

        String sqlSubtipo;

        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez

            String sql = "UPDATE articulos SET cod_articulo = ?, nombre = ?, precio = ?,stock = ? WHERE cod_articulo = ?";
            sentencia = conexion.prepareStatement(sql);

            sentencia.setInt(1, codigo);
            sentencia.setString(2, nombre);
            sentencia.setDouble(3, precio);
            sentencia.setInt(4, stock);
            sentencia.setInt(5,codigo);

            sentencia.executeUpdate();

            if (articulo instanceof Tomo) {
                int numVol = ((Tomo) articulo).getNumVolumen();
                if (articulo.getDescripcion() == null) {

                    sqlSubtipo = "UPDATE tomos SET cod_tomo = ?, num_volumen = ? WHERE cod_tomo = ?";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setInt(2, numVol);
                    sentencia.setInt(3,codigo);
                } else {
                    String descripcion = articulo.getDescripcion();
                    sqlSubtipo = "UPDATE tomos SET cod_tomo = ?, num_volumen = ?, descripcion = ? WHERE cod_tomo = ?";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setInt(2, numVol);
                    sentencia.setString(3, descripcion);
                    sentencia.setInt(4,codigo);
                }
            } else if (articulo instanceof Juego) {
                if (articulo.getDescripcion() == null) {
                    sqlSubtipo = "UPDATE juegos_mesa SET cod_juego = ? WHERE cod_juego = ?";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setInt(2,codigo);
                } else {
                    String descripcion =  articulo.getDescripcion();
                    sqlSubtipo = "UPDATE juegos_mesa SET cod_juego = ?, descripcion = ? WHERE cod_juego = ?";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setString(2, descripcion);
                    sentencia.setInt(3,codigo);
                }
            } else if (articulo instanceof Merchandising) {
                String tipoMerch = String.valueOf(((Merchandising) articulo).getTipoMerch());
                String coleccion = ((Merchandising) articulo).getColeccion();

                if (((Merchandising) articulo).getTalla() == null) {

                    sqlSubtipo = "UPDATE merchandising SET cod_merch = ?, tipo = ?, coleccion = ? WHERE cod_merch = ?";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setString(2, tipoMerch);
                    sentencia.setString(3, coleccion);
                    sentencia.setInt(4,codigo);
                } else {
                    String talla = String.valueOf(((Merchandising) articulo).getTalla());
                    sqlSubtipo = "UPDATE merchandising SET cod_merch = ?, tipo = ?, coleccion = ?, talla = ? WHERE cod_merch = ?";
                    sentencia = conexion.prepareStatement(sqlSubtipo);
                    sentencia.setInt(1, codigo);
                    sentencia.setString(2, tipoMerch);
                    sentencia.setString(3, coleccion);
                    sentencia.setString(4, talla);
                    sentencia.setInt(5,codigo);
                }
            }
            sentencia.executeUpdate();
            
            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try {
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(ArticuloDAO) modificarArticulo(Articulo)", "Rollback aplicado");
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
}
