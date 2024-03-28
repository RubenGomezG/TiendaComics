package com.example.tiendacomics.dao;

import com.example.tiendacomics.Logger.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public abstract class DAO{
    public static final String URL = "jdbc:mysql://localhost:3306/tiendacomics?useSSL=false";
    public static final String USUARIO = "root";
    public static final String CONTRASENA = "root";
    public static Connection conexion = null;

    public static void crearBase(){
        BufferedReader entrada = null;
        Statement sentencia = null;
        String nombreArchivo = "src/main/resources/com/example/tiendacomics/sql/ScriptTiendaComics.sql";
        conectar();
        
        try {
            entrada = new BufferedReader(new FileReader(nombreArchivo));
            String linea = entrada.readLine();
            conexion.setAutoCommit(false);
            while (linea != null) {
                linea = entrada.readLine();
                try{
                    sentencia = conexion.createStatement();
                    sentencia.execute(linea);
                } catch (SQLException e){
                    Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
                }
            }
            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(DAO) crearBase()", "Rollback aplicado");
            }
        } catch (IOException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } finally {
            try {
                if (entrada != null) {
                    try {
                        entrada.close();
                    } catch (NullPointerException | IOException e) {
                        Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
                    }
                }
                if (sentencia != null) {
                    sentencia.close();
                }
            }
            catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
    }
    
    public static void conectar() {
        // Configuramos la conexión con la base de datos
        try {
            // Cargamos el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecemos la conexión con la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            
        } catch (SQLException | ClassNotFoundException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public static void desconectar() {
        try {
            conexion.close();
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
    }
    /**
     * Método que crea una tabla en la base de datos. Está sin usar porque de primeras creábamos las tablas desde aquí, en vez de leer el Script.
     * Si en un futuro quisieramos añadir funcionalidades desde la aplicación podría ser útil y trabajar 2 veces es tontería
     * @param tabla El nombre de la tabla a crear
     */
    public static void createTable(String tabla) {
        Statement sentencia = null;
        String sql = "";
        conectar();

        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez
            switch (tabla) {
                case "articulos" ->
                        sql = "CREATE TABLE articulos (cod_articulo INT PRIMARY KEY auto_increment,nombre VARCHAR(400) NOT NULL ,precio DECIMAL(5,2) NOT NULL ,stock INTEGER NOT NULL);";
                case "ventas" ->
                        sql = "CREATE TABLE ventas (cod_venta INT PRIMARY KEY auto_increment,fecha_hora_venta datetime NOT NULL DEFAULT now());";
                case "tomos" ->
                        sql = "CREATE TABLE tomos(cod_tomo INTEGER PRIMARY KEY AUTO_INCREMENT,num_volumen INTEGER NOT NULL,descripcion VARCHAR(300) DEFAULT NULL, CONSTRAINT tomo_articulo FOREIGN KEY(cod_tomo) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE);";
                case "merchandising" ->
                        sql = "CREATE TABLE merchandising (cod_merch INTEGER PRIMARY KEY AUTO_INCREMENT,tipo ENUM ('Ropa', 'Accesorio', 'Poster', 'Figura', 'otro') NOT NULL,coleccion VARCHAR(20),talla VARCHAR(10), CONSTRAINT merch_articulo FOREIGN KEY(cod_merch) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE);";
                case "juegos_mesa" ->
                        sql = "CREATE TABLE juegos_mesa(cod_juego INTEGER PRIMARY KEY AUTO_INCREMENT, descripcion VARCHAR(300), CONSTRAINT juego_articulo FOREIGN KEY(cod_juego) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE);";
                case "ticket" ->
                        sql = "CREATE TABLE ticket(cod_venta INTEGER NOT NULL, cod_articulo INTEGER NOT NULL, fecha_hora_venta DATETIME NOT NULL DEFAULT NOW(), cantidad INTEGER NOT NULL DEFAULT 1, CONSTRAINT ticket_pk PRIMARY KEY(cod_venta,cod_articulo), CONSTRAINT articulo_fk FOREIGN KEY(cod_articulo) REFERENCES articulos(cod_articulo) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT venta_fk FOREIGN KEY(cod_venta) REFERENCES ventas(cod_venta) ON UPDATE CASCADE ON DELETE CASCADE);";
                default -> System.out.println("Introduce un nombre válido");
            }
            sentencia = conexion.createStatement();
            sentencia.execute(sql);

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(DAO) crearTable(String)", "Rollback aplicado");
            }
        } finally {
            try {
                if (sentencia != null) {
                    sentencia.close();
                }
            }
            catch (SQLException e) {
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            desconectar();
        }
    }

    /**
     * Método que borra una tabla en la base de datos. Está sin usar porque de primeras creábamos las tablas desde aquí, en vez de leer el Script.
     * Si en un futuro quisieramos añadir funcionalidades desde la aplicación podría ser útil y trabajar 2 veces es tontería
     * @param tabla El nombre de la tabla a borrar
     */
    public static void deleteTable(String tabla) {
        Statement sentencia = null;
        String sql = "";
        conectar();
        try {
            conexion.setAutoCommit(false); //para hacer transaccion a la vez
            switch (tabla) {
                case "articulos" ->
                        sql = "DROP TABLE articulos IF EXISTS;";
                case "ventas" ->
                        sql = "DROP TABLE ventas IF EXISTS;";
                case "tomos" ->
                        sql = "DROP TABLE tomos IF EXISTS;";
                case "merchandising" ->
                        sql = "DROP TABLE merchandising IF EXISTS;";
                case "juegos" ->
                        sql = "DROP TABLE juegos_mesa IF EXISTS;";
                case "ticket" ->
                        sql = "DROP TABLE tickets IF EXISTS;";
                default -> System.out.println("Introduce un nombre válido");
            }
            sentencia = conexion.createStatement();
            sentencia.execute(sql);

            conexion.commit(); //para hacer transaccion a la vez
        } catch (SQLException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            //para hacer transaccion a la vez:
            try{
                conexion.rollback(); //si al ejecutar da error, hacemos rollback
            } catch (SQLException e1) {
                Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                Logger.generarLineaLogDebug( "(DAO) deleteTable(String)", "Rollback aplicado");
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
