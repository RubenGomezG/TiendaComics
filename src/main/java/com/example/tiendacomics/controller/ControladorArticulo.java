package com.example.tiendacomics.controller;

import com.example.tiendacomics.AppTienda;
import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.DAO;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.service.ServicioArticulos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControladorArticulo extends Controlador implements Initializable {
    @FXML
    private TextField nombreArticulo; //buscador de articulo en tabla
    @FXML
    private VBox tabla; //tabla de todos los articulos
    @FXML
    private Label usuarioActivo;
    
    @FXML
    void modificarArticulo(ActionEvent ignoredEvent) {
        popupPestanaArticulos();
    }

    @FXML
    void quitarArticuloBBDD(ActionEvent ignoredEvent) {
        popupPestanaArticulos();
    }

    @FXML
    void anadirArticuloBBDD(ActionEvent ignoredEvent){
        popupPestanaArticulos();
    }

    private static void popupPestanaArticulos() {
        try {
            // Crear popup
            Stage nuevaVentana = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(AppTienda.class.getResource("views/AnadirGui.fxml"));
            Parent root = fxmlLoader.load();
            Scene nuevaEscena = new Scene(root);

            nuevaVentana.setScene(nuevaEscena);
            nuevaVentana.setTitle("Gestionar Articulos");
            nuevaVentana.show();
        } catch (IOException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUsuarioActivo();
        cargarEstado();
        buscarUnArticulo(nombreArticulo.getText());
    }

    private void guardarEstado() {
        FileWriter salida = null;
        try {
            salida = new FileWriter("src/main/resources/com/example/tiendacomics/log/logarticulo.txt");
            salida.write(nombreArticulo.getText());
        } catch (IOException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } finally {
            if (salida != null) {
                try {
                    salida.close();
                } catch (IOException e) {
                    Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }
    
    private void cargarEstado() {
        BufferedReader entrada = null;
        try {
            entrada = new BufferedReader(new FileReader("src/main/resources/com/example/tiendacomics/log/logarticulo.txt"));
            String linea = entrada.readLine();
            String nombreArt = "";
            
            while (linea != null) {
                // Dividir la línea en dos strings utilizando un separador
                String[] strings = linea.split(";");

                // Asignar los strings a variables separadas
                nombreArt = strings[0];
                linea = entrada.readLine();
            }
            nombreArticulo.setText(nombreArt);
            
        } catch (IOException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        } finally {
            if (entrada != null) {
                try {
                    entrada.close();
                } catch (NullPointerException | IOException e) {
                    Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }
    
    @FXML
    void buscarArticulo(ActionEvent ignoredEvent) {
        String nombre = nombreArticulo.getText();
        buscarUnArticulo(nombre);
    }
    
    @FXML
    void cerrarSesion(ActionEvent event) throws IOException {
        super.cerrarSesion(event, anchoPestana, altoPestana);
        guardarEstado();
    }

    @FXML
    void pestanaArticulos(ActionEvent event) throws IOException {
        super.pestanaArticulos(event, anchoPestana, altoPestana);
        guardarEstado();
    }

    @FXML
    void pestanaHistorial(ActionEvent event) throws IOException {
        super.pestanaHistorial(event, anchoPestana, altoPestana);
        guardarEstado();
    }

    @FXML
    void pestanaVentas(ActionEvent event) throws IOException {
        super.pestanaVentas(event, anchoPestana, altoPestana);
        guardarEstado();
    }

    /**
     * Genera una nueva fila (ya rellenada con los datos de la BD)
     * en la tabla
     */
    public  HBox generarNuevaFila(String codigo, String nombre, String precio, String stock, String desc, String numVol, String colecc, String tipo, String talla){
        HBox articuloEnTablaClone = new HBox();
    
        Label codigoTabla = new Label("CodigoORIGINAL");
        codigoTabla.setAlignment(Pos.CENTER);
        codigoTabla.setPrefHeight(46.0);
        codigoTabla.setPrefWidth(90.0);
        codigoTabla.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        codigoTabla.setText(codigo);
    
        Label nombreTabla = new Label("NombreORIGINAL");
        nombreTabla.setAlignment(Pos.CENTER);
        nombreTabla.setContentDisplay(ContentDisplay.CENTER);
        nombreTabla.setMaxWidth(1.7976931348623157E308);
        nombreTabla.setPrefHeight(46.0);
        nombreTabla.setPrefWidth(155.0);
        nombreTabla.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        nombreTabla.setText(nombre);
    
        Label precioTabla = new Label("PrecioORIGINAL");
        precioTabla.setAlignment(Pos.CENTER);
        precioTabla.setPrefHeight(46.0);
        precioTabla.setPrefWidth(102.0);
        precioTabla.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        precioTabla.setText(precio);
    
        Label stockTabla = new Label("StockORIGINAL");
        stockTabla.setAlignment(Pos.CENTER);
        stockTabla.setPrefHeight(46.0);
        stockTabla.setPrefWidth(102.0);
        stockTabla.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        stockTabla.setText(stock);
    
        Label descripcionTabla = new Label("DescripciónORIGINAL");
        descripcionTabla.setAlignment(Pos.CENTER);
        descripcionTabla.setContentDisplay(ContentDisplay.CENTER);
        descripcionTabla.setLayoutX(284.0);
        descripcionTabla.setMaxWidth(1.7976931348623157E308);
        descripcionTabla.setPrefHeight(46.0);
        descripcionTabla.setPrefWidth(118.0);
        descripcionTabla.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        descripcionTabla.setText(desc);
    
        Label numVolTabla = new Label("Num.VolORIGINAL");
        numVolTabla.setAlignment(Pos.CENTER);
        numVolTabla.setContentDisplay(ContentDisplay.CENTER);
        numVolTabla.setLayoutX(477.0);
        numVolTabla.setMaxWidth(1.7976931348623157E308);
        numVolTabla.setPrefHeight(46.0);
        numVolTabla.setPrefWidth(102.0);
        numVolTabla.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        numVolTabla.setText(numVol);
    
        Label coleccionTabla = new Label("ColeccionORIGINAL");
        coleccionTabla.setAlignment(Pos.CENTER);
        coleccionTabla.setContentDisplay(ContentDisplay.CENTER);
        coleccionTabla.setLayoutX(588.0);
        coleccionTabla.setMaxWidth(1.7976931348623157E308);
        coleccionTabla.setPrefHeight(46.0);
        coleccionTabla.setPrefWidth(102.0);
        coleccionTabla.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        coleccionTabla.setText(colecc);
    
        Label tipoTabla = new Label("TipoORIGINAL");
        tipoTabla.setAlignment(Pos.CENTER);
        tipoTabla.setContentDisplay(ContentDisplay.CENTER);
        tipoTabla.setLayoutX(661.0);
        tipoTabla.setMaxWidth(1.7976931348623157E308);
        tipoTabla.setPrefHeight(46.0);
        tipoTabla.setPrefWidth(102.0);
        tipoTabla.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        tipoTabla.setText(tipo);
    
        Label tallaTabla = new Label("TallaORIGINAL");
        tallaTabla.setAlignment(Pos.CENTER);
        tallaTabla.setContentDisplay(ContentDisplay.CENTER);
        tallaTabla.setLayoutX(734.0);
        tallaTabla.setMaxWidth(1.7976931348623157E308);
        tallaTabla.setPrefHeight(46.0);
        tallaTabla.setPrefWidth(102.0);
        tallaTabla.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        tallaTabla.setText(talla);
    
        articuloEnTablaClone.getChildren().addAll(codigoTabla,nombreTabla,precioTabla,stockTabla,descripcionTabla,numVolTabla,coleccionTabla,tipoTabla,tallaTabla);
        return articuloEnTablaClone;
    }

    @FXML
    public void barraBuscador(KeyEvent ignoredEvent)  {
        tabla.getChildren().clear(); //para refrescar la tabla cada vez que añades una letra
        buscarUnArticulo(nombreArticulo.getText());
    }
    
    public void buscarUnArticulo(String texto){
        ArrayList<Articulo> articulos = ServicioArticulos.getConsulta(texto);

        for (Articulo articulo : articulos) {
            String codigo =  (String.valueOf(articulo.getCodArticulo()));
            String nombre =  (String.valueOf(articulo.getNombre()));
            String precio =  (String.valueOf(articulo.getPrecio()));
            String stock =  (String.valueOf(articulo.getStock()));
            String desc =  (String.valueOf(articulo.getDescripcion()));
            String numVol = "";
            
            if(! (articulo.getNumVolArticulo() == 0)){
                numVol = (String.valueOf(articulo.getNumVolArticulo()));
            }
            String colecc =  (String.valueOf(articulo.getColeccionArticulo()));
            String tipo =  (String.valueOf(articulo.getTipoMerchArticulo()));
            String talla =  (String.valueOf(articulo.getTallaMerchArticulo()));
            
            HBox fila = generarNuevaFila(codigo, nombre, precio, stock, desc, numVol, colecc, tipo, talla);
            tabla.getChildren().add(fila);
        }
    }

    public void setUsuarioActivo() {
        this.usuarioActivo.setText(DAO.USUARIO);
    }
    
    @FXML
    void abrirCaja(ActionEvent ignoredEvent) {
        //disabled
    }
}
