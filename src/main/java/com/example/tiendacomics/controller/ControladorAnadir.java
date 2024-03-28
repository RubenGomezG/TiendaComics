package com.example.tiendacomics.controller;

import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.ArticuloDAO;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Juego;
import com.example.tiendacomics.model.Merchandising;
import com.example.tiendacomics.model.Tomo;
import com.example.tiendacomics.service.ServicioAnadir;
import com.example.tiendacomics.service.ServicioArticulos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class ControladorAnadir {
    @FXML
    private Button botonAceptarAnadirArticulo;
    @FXML
    private Button botonBorrarArticulo;
    @FXML
    private Button botonCancelarAnadirArticulo;
    @FXML
    private Button botonModificarArticulo;
    @FXML
    private TextField coleccion;
    @FXML
    private TextField descripcion;
    @FXML
    private MenuButton tipoArticulo;
    @FXML
    private TextField codArticulo;
    @FXML
    private TextField nomArticulo;
    @FXML
    private TextField numVolumen;
    @FXML
    private TextField precioArticulo;
    @FXML
    private TextField stockArticulo;
    @FXML
    private MenuButton talla;
    @FXML
    private MenuButton tipoMerch;


    @FXML
    void cancelarAnadirArticulo(ActionEvent ignoredEvent) {
        cerrarVentanaAnadir(botonCancelarAnadirArticulo);
    }


    @FXML
    void confirmarAnadirArticulo(ActionEvent ignoredEvent) {
        Articulo articulo = extraerArticulo();
        if (articulo != null) {
            ServicioAnadir.anadirArticulo(articulo);
            cerrarVentanaAnadir(botonAceptarAnadirArticulo);
        } else {
            alertaCamposVacios();
        }
    }

    private static void alertaCamposVacios() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error al realizar la operación");
        alert.setContentText("Todos los campos deben estar completos.");
        alert.showAndWait();
        alert.close();
    }

    @FXML
    void confirmarModificar(ActionEvent ignored) {
        Articulo articulo = extraerArticulo();
        if (articulo != null){
            ServicioAnadir.modificarArticulo(articulo);
            cerrarVentanaAnadir(botonModificarArticulo);
        } else {
            alertaCamposVacios();
        }
    }

    private Articulo extraerArticulo() {
        String desc = null;
        Articulo articulo = null;
        String nombre = "";
        double precio = 0;
        int stock = 0;
        int codigo = 0;

        if (! descripcion.getText().isEmpty()) {
            desc = descripcion.getText();
        }
        if (! nomArticulo.getText().isEmpty()) {
            nombre = nomArticulo.getText();
        }
        try {
            codigo = Integer.parseInt(codArticulo.getText());
            precio = Double.parseDouble(precioArticulo.getText());
            stock = Integer.parseInt(stockArticulo.getText());
        } catch (NullPointerException np){
            Logger.generarLineaLogError(np.getClass().getSimpleName(), np.getMessage());
            Logger.generarLineaLogInfo( "(ControladorAnadir) extraerArticulo()", "El campo o campos no pueden estar vacíos");
        }
        catch (NumberFormatException num){
            Logger.generarLineaLogError(num.getClass().getSimpleName(), num.getMessage());
            Logger.generarLineaLogInfo( "(ControladorAnadir) extraerArticulo()", "Debes escribir un número");
        }
        Articulo.TIPO tipoArt;
        Merchandising.TALLA tallaTemp = null;
        Merchandising.TIPO_MERCH merchType = null;
        
        String tipo1 = "";
        String tipo2 = "";
        String tipo3 = "";
        try{
            tipo1 = tipoArticulo.getText();
            tipo2 = tipoMerch.getText();
            tipo3 = talla.getText();
        } catch (NullPointerException np){
            Logger.generarLineaLogError(np.getClass().getSimpleName(), np.getMessage());
            Logger.generarLineaLogInfo( "(ControladorAnadir) extraerArticulo()", "Debes elegir una de las opciones");
        }
        
        switch (tipo1){

            case "tomo": tipoArt = Articulo.TIPO.TOMO;
                         int numVol = Integer.parseInt(numVolumen.getText());
                         articulo = new Tomo(codigo,nombre,precio,stock,tipoArt,numVol,desc);
            break;

            case "juego": tipoArt = Articulo.TIPO.JUEGO;
                          articulo = new Juego(codigo,nombre,precio,stock,tipoArt,desc);
            break;

            case "merch": tipoArt = Articulo.TIPO.MERCHANDISING;
                          String colec = coleccion.getText();
                          switch (tipo2){
                              case "ropa" -> {
                                  merchType = Merchandising.TIPO_MERCH.ROPA;
                                  switch (tipo3) {
                                      case "XS" -> tallaTemp = Merchandising.TALLA.XS;
                                      case "S" -> tallaTemp = Merchandising.TALLA.S;
                                      case "M" -> tallaTemp = Merchandising.TALLA.M;
                                      case "L" -> tallaTemp = Merchandising.TALLA.L;
                                      case "XL" -> tallaTemp = Merchandising.TALLA.XL;
                                      case "XXL" -> tallaTemp = Merchandising.TALLA.XXL;
                                      case "otro" -> tallaTemp = Merchandising.TALLA.OTRA;
                                  }
                              }
                              case "figura" -> merchType = Merchandising.TIPO_MERCH.FIGURA;
                              case "accesorio" -> merchType = Merchandising.TIPO_MERCH.ACCESORIO;
                              case "poster" -> merchType = Merchandising.TIPO_MERCH.POSTER;
                              case "otro" -> merchType = Merchandising.TIPO_MERCH.OTROS;
                          }
                          articulo = new Merchandising(codigo,nombre,precio,stock,tipoArt,merchType,tallaTemp,colec);
            break;
        }
        return articulo;
    }

    @FXML
    void confirmarBorrarArticulo(ActionEvent ignored) {
        int codigo = 0;
        desactivarTodo();
        botonAceptarAnadirArticulo.setDisable(true);
        botonModificarArticulo.setDisable(true);

        try{
            codigo = Integer.parseInt(codArticulo.getText());
        } catch (NumberFormatException e){
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        
        Articulo articulo = ServicioArticulos.consultarArticulo(codigo);
        ServicioAnadir.borrarArticulo(articulo);
        cerrarVentanaAnadir(botonBorrarArticulo);
    }



    public void modificarOQuitar(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quieres Modificar Artículo?");
        alert.setHeaderText("Modificar o borrar");
        alert.setContentText("Si quieres modificar, dale a OK, si no, a cancel.");
        alert.showAndWait();
        
        ButtonType boton = alert.getResult();
        
        if (boton == ButtonType.OK){
            activarGeneral();
            botonModificarArticulo.setDisable(false);
            botonBorrarArticulo.setDisable(true);
            botonAceptarAnadirArticulo.setDisable(true);
        }
        else if (boton == ButtonType.CANCEL){
            botonBorrarArticulo.setDisable(false);
            botonModificarArticulo.setDisable(true);
            botonAceptarAnadirArticulo.setDisable(true);
            if (!nomArticulo.isDisable()){
                desactivarTodo();
            }
        }
        alert.close();
    }
    @FXML
    void consultarCodigo(ActionEvent ignoredEvent){
        Articulo articulo = null;
        articulo = ArticuloDAO.consultarArticulo(Integer.parseInt(codArticulo.getText()));
        if (articulo !=null){
            modificarOQuitar();
        }
        else {
            activarGeneral();
            botonBorrarArticulo.setDisable(true);
            botonModificarArticulo.setDisable(true);
            botonAceptarAnadirArticulo.setDisable(false);
        }
    }

    public void cerrarVentanaAnadir(Button boton) {
        Stage ventanaActual = (Stage) boton.getScene().getWindow();
        ventanaActual.close();
    }

    @FXML
    void elegirTomo(ActionEvent ignoredEvent) {
        tipoArticulo.setText("tomo");
        desactivarMerch();
        numVolumen.setDisable(false);
        descripcion.setDisable(false);
    }
    
    @FXML
    void elegirJuego(ActionEvent ignoredEvent) {
        tipoArticulo.setText("juego");
        desactivarTomo();
        desactivarMerch();
        descripcion.setDisable(false);
    }

    public void desactivarMerch() {
        if (!coleccion.isDisable()){
            coleccion.setDisable(true);
        }
        if (!tipoMerch.isDisable()){
            tipoMerch.setDisable(true);
        }
        if (!talla.isDisable()){
            talla.setDisable(true);
        }
    }
    
    public void desactivarJuego() {
        desactivarTomo();
        if (!descripcion.isDisable()){
            descripcion.setDisable(true);
        }

    }
    
    public void desactivarTomo() {
        if (!numVolumen.isDisable()){
            numVolumen.setDisable(true);
        }
    }

    public void desactivarGeneral(){
        if (!nomArticulo.isDisable()){
            nomArticulo.setDisable(true);
        }
        if (!precioArticulo.isDisable()){
            precioArticulo.setDisable(true);
        }
        if (!stockArticulo.isDisable()){
            stockArticulo.setDisable(true);
        }
    }
    
    public void activarGeneral(){
        if (nomArticulo.isDisable()){
            nomArticulo.setDisable(false);
        }
        if (precioArticulo.isDisable()){
            precioArticulo.setDisable(false);
        }
        if (stockArticulo.isDisable()){
            stockArticulo.setDisable(false);
        }
        if (tipoArticulo.isDisable()){
            tipoArticulo.setDisable(false);
        }
    }

    public void desactivarTodo(){
        desactivarGeneral();
        desactivarTomo();
        desactivarJuego();
        desactivarMerch();
    }

    @FXML
    void elegirMerch(ActionEvent ignoredEvent) {
        tipoArticulo.setText("merch");
        desactivarJuego();
        coleccion.setDisable(false);
        tipoMerch.setDisable(false);
        if (tipoMerch.getText().equalsIgnoreCase("ropa")){
            talla.setDisable(false);
        }
    }

    @FXML
    void elegirOtros(ActionEvent ignoredEvent) {
        tipoMerch.setText("otros");
    }

    @FXML
    void elegirPoster(ActionEvent ignoredEvent) {
        tipoMerch.setText("poster");
    }

    @FXML
    void elegirRopa(ActionEvent ignoredEvent) {
        tipoMerch.setText("ropa");
        talla.setDisable(false);
    }

    @FXML
    void elegirAccesorio(ActionEvent ignoredEvent) {
        tipoMerch.setText("accesorio");
    }

    @FXML
    void elegirFigura(ActionEvent ignoredEvent) {
        tipoMerch.setText("figura");
    }

    @FXML
    void elegirXs(ActionEvent ignoredEvent) {
        talla.setText("XS");
    }
    
    @FXML
    void elegirS(ActionEvent ignoredEvent) {
        talla.setText("S");
    }
    
    @FXML
    void elegirM(ActionEvent ignoredEvent) {
        talla.setText("M");
    }
    
    @FXML
    void elegirL(ActionEvent ignoredEvent) {
        talla.setText("L");
    }
    
    @FXML
    void elegirXl(ActionEvent ignoredEvent) {
        talla.setText("XL");
    }
    
    @FXML
    void elegirXxl(ActionEvent ignoredEvent) {
        talla.setText("XXL");
    }
    
    @FXML
    void elegirOtra(ActionEvent ignoredEvent) {
        talla.setText("otro");
    }
    
}
