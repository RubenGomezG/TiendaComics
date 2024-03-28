package com.example.tiendacomics.controller;

import com.example.tiendacomics.AppTienda;
import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.ArticuloDAO;
import com.example.tiendacomics.dao.DAO;
import com.example.tiendacomics.dao.TicketDAO;
import com.example.tiendacomics.dao.VentaDAO;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Ticket;
import com.example.tiendacomics.model.Venta;
import com.example.tiendacomics.service.ServicioArticulos;
import com.example.tiendacomics.service.ServicioHistorial;
import com.example.tiendacomics.service.ServicioTicket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControladorTicket extends Controlador implements Initializable {
    @FXML
    private Pane contenedor;
    @FXML
    private Label articuloActual;
    @FXML
    private Label codArtActual;
    @FXML
    private Label descActual;
    
    @FXML
    private VBox ticket;
    private HBox articuloEnTicket;
    private ArrayList<Articulo> artTicketActual = new ArrayList<>();
    private Articulo articuloBuscado;

    @FXML
    private ImageView imagenArticulo;
    @FXML
    private Label importe;
    @FXML
    private TextField numeroArticulo;
    @FXML
    private Label numTicket;
    @FXML
    private Label precioArtActual;
    
    @FXML
    private Label usuarioActivo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        Al inicializar la pestaña, ponemos el nombre de usuario root ya que no estamos trabajando con distintos usuarios, cargamos del fichero
//        los datos del articulo y el ticket y ejecutamos la consulta de los mismos para rellenar el resto de la pantalla.
        setUsuarioActivo();
        cargarEstado();
        consultarArticulo();
        mostrarTicket();
        mostrarImagen();
    }

    private void guardarEstado() {
        FileWriter salida = null;
        try {
            salida = new FileWriter("src/main/resources/com/example/tiendacomics/log/logventa.txt");
            salida.write(numeroArticulo.getText() +";");
            salida.write(numTicket.getText() + ";");
        }
        catch (IOException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        finally {
            if (salida != null) {
                try {
                    salida.close();
                }
                catch (IOException e) {
                    Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    private void cargarEstado() {
        BufferedReader entrada = null;
        String numeroArt = "";
        String numeroTicket = "";
        try {
            entrada = new BufferedReader(new FileReader("src/main/resources/com/example/tiendacomics/log/logventa.txt"));
            String linea = entrada.readLine();

            while (linea != null) {
                // Dividir la línea en dos strings utilizando un separador
                String[] strings = linea.split(";");

                // Asignar los strings a variables separadas
                if (strings.length == 2) {
                    numeroArt = strings[0];
                    numeroTicket = strings[1];
                    numTicket.setText(numeroTicket);
                }

                linea = entrada.readLine();
            }
            numeroArticulo.setText(numeroArt);
            numTicket.setText(String.valueOf(ServicioTicket.ultimoTicket()));
        }
        catch (IOException e) {
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        finally {
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
    void anadirArticulo(ActionEvent ignoredEvent) {
        if (artTicketActual.size() == 0 && !numeroArticulo.getText().equals("")){ //tiene que estar el ticket vacío y tienes que haber buscado un articulo
            crearTicketNuevo();
        }
        else{
            artTicketActual = ServicioTicket.consultarArticulosUltimoTicket();
        }
        
        int codTicket = ServicioTicket.ultimoTicket();
        Venta venta = new Venta(ServicioTicket.consultarTicket(Integer.parseInt(numTicket.getText())),ServicioArticulos.consultarArticulo(Integer.parseInt(numeroArticulo.getText())),1);
        
        try {
            Articulo articuloAnadir = ServicioArticulos.consultarArticulo(Integer.parseInt(numeroArticulo.getText()));
            Ticket ticketActual = TicketDAO.consultarTicketBuscado(codTicket);
            
            if(ticketActual.getListaCompra().contains(articuloAnadir)){
                Venta venta1 = VentaDAO.consultarVentaArticulo(codTicket,articuloAnadir.getCodArticulo());
                venta1.setCantidad(venta.getCantidad()+1);
                ServicioTicket.sumarCantDeArticuloEnVentaBD(codTicket,articuloAnadir.getCodArticulo());
            }else {
                ServicioTicket.crearVentaNueva(venta);
                ticketActual.anadirArticulo(articuloAnadir);
                artTicketActual.clear();
                artTicketActual.addAll(ticketActual.getListaCompra());
            }
            ticket.getChildren().clear();
            mostrarTicket();
            setImporte(); //recalcular importe del ticket
            
        }
        catch(NumberFormatException e){
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        catch (NullPointerException e){
            if (artTicketActual.size() == 0){
                try {
                    Ticket ticket = new Ticket(Integer.parseInt(numTicket.getText()), artTicketActual);
                    ServicioTicket.eliminarTicket(ticket);
                } catch (NumberFormatException e1){
                    Logger.generarLineaLogError(e1.getClass().getSimpleName(), e1.getMessage());
                }
            }
            alertaArticulo();
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Genera una nueva fila en la lista del ticket
     * (para añadir nuevo articulo)
     */
    public HBox generarNuevaFilaTicket(String nombre, String precio, String cantidad, String infoExtra){
        articuloEnTicket = new HBox();
        articuloEnTicket.setId("articuloEnTicket");
        articuloEnTicket.setFocusTraversable(true);
        
        Label nombreTicket = new Label(); //index = 0
        nombreTicket.setId("nombreArtTicket");
        nombreTicket.setAlignment(Pos.CENTER);
        nombreTicket.setContentDisplay(ContentDisplay.CENTER);
        nombreTicket.setMaxWidth(Double.MAX_VALUE);
        nombreTicket.setMinHeight(Region.USE_PREF_SIZE);
        nombreTicket.setPrefHeight(46.0);
        nombreTicket.setPrefWidth(145.0);
        nombreTicket.setStyle("-fx-border-width: 1; -fx-border-color: black;");
        nombreTicket.setText(nombre);
        
        Label precioTicket = new Label();  //index = 1
        precioTicket.setId("precioArtTicket");
        precioTicket.setAlignment(Pos.CENTER);
        precioTicket.setMinHeight(Region.USE_PREF_SIZE);
        precioTicket.setPrefHeight(46.0);
        precioTicket.setPrefWidth(70.0);
        precioTicket.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        precioTicket.setTextAlignment(TextAlignment.CENTER);
        precioTicket.setText(precio);
        
        Label cantidadTicket = new Label();  //index = 2
        cantidadTicket.setId("cantidadArtTicket");
        cantidadTicket.setAlignment(Pos.CENTER);
        cantidadTicket.setMinHeight(Region.USE_PREF_SIZE);
        cantidadTicket.setPrefHeight(46.0);
        cantidadTicket.setPrefWidth(72.0);
        cantidadTicket.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        cantidadTicket.setText(cantidad);
        
        Label infoExtraTicket = new Label();  //index = 3
        infoExtraTicket.setId("infoExtraArtTicket");
        infoExtraTicket.setAlignment(Pos.CENTER);
        infoExtraTicket.setMinHeight(Region.USE_PREF_SIZE);
        infoExtraTicket.setPrefHeight(46.0);
        infoExtraTicket.setPrefWidth(89.0);
        infoExtraTicket.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        infoExtraTicket.setText(infoExtra);
        
        articuloEnTicket.getChildren().addAll(nombreTicket,infoExtraTicket,cantidadTicket,precioTicket);
        return articuloEnTicket;
    }
    
    /**
     * Añade tod0s los articulos de un mismo ticket al ticket,
     * todos deben insertartse según los articulos guardados
     * bajo el mismo codticket
     */
    public void mostrarTicket(){
        int codTicket = ServicioTicket.ultimoTicket();
        ArrayList<Articulo> articulosEnTicket = ServicioTicket.consultarArticulosUltimoTicket();

        for (Articulo articulo : articulosEnTicket) {
            String nombre =  (String.valueOf(articulo.getNombre()));
            String precio =  (String.valueOf(articulo.getPrecio()));
            String cantidad =  (String.valueOf(ServicioHistorial.consultarCantidadArticuloEnTicket(codTicket, articulo.getCodArticulo())));

            StringBuilder sb = new StringBuilder();
            if (articulo.getNumVolArticulo() != 0){
                sb.append(articulo.getNumVolArticulo());
            }
            if(articulo.getTallaMerchArticulo() != null) {
                sb.append(articulo.getTallaMerchArticulo());
            }

            HBox fila = generarNuevaFilaTicket(nombre, precio, cantidad,sb.toString());
            this.ticket.getChildren().add(fila);
        }
    }

    @FXML
    void buscarArticulo(KeyEvent ignored) throws IllegalArgumentException { //la excepción salta por el KeyEvent cuando le das a enter lol
        consultarArticulo();
    }
    @FXML
    void busquedaArticulo(ActionEvent ignoredEvent) {
        consultarArticulo();
        numeroArticulo.setText("");
    }

    private void consultarArticulo() {
        if (!numeroArticulo.getText().equals("")){
            try{
                if(ArticuloDAO.consultarArticulo(Integer.parseInt(numeroArticulo.getText())) != null) {
                    Articulo articulo = ServicioArticulos.consultarArticulo(Integer.parseInt(numeroArticulo.getText()));
                    articuloActual.setText(articulo.getNombre());
                    codArtActual.setText(String.valueOf(articulo.getCodArticulo()));
                    precioArtActual.setText(String.valueOf(articulo.getPrecio()));
                    descActual.setText(articulo.getDescripcion());
                    articuloBuscado = articulo;
                    mostrarImagen();
                }
                
            } catch(NumberFormatException e){
                alertaNumero();
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
                Logger.generarLineaLogInfo( "(ControladorTicket) consultarArticulo()", "[CARACTER NO VALIDO] - No se ha introducido un código numerico para buscar un artículo");
            }
        } else{
            imagenArticulo.setImage(null); //borramos la imagen siempre que no se encuentra el articulo
        }
    }

    private static void alertaNumero() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Código erróneo");
        alert.setContentText("Debes introducir un número de artículo");
        alert.showAndWait();
        alert.close();
    }

    private static void alertaArticulo() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("El artículo no existe");
        alert.setContentText("Debes introducir un artículo existente");
        alert.showAndWait();
        alert.close();
    }

    
    public void mostrarImagen() {
        Image imagen = new Image(Objects.requireNonNull(AppTienda.class.getResource("img/tiendaComics.png")).toString());
        imagenArticulo = new ImageView(imagen);
        imagenArticulo.setFitHeight(348);
        imagenArticulo.setFitWidth(649);
        imagenArticulo.setLayoutX(59);
        imagenArticulo.setLayoutY(155);
        imagenArticulo.setPickOnBounds(true);
        imagenArticulo.setPreserveRatio(true);
        contenedor.getChildren().add(imagenArticulo);
    }

    @FXML
    void cerrarTicket(ActionEvent ignoredEvent) {
        Ticket ticket = new Ticket(Integer.parseInt(numTicket.getText()),artTicketActual);
        ServicioTicket.modificarTicket(ticket); //actualizamos el insert del ticket antes de cerrarlo
        setNumNuevoTicket();
    
        this.ticket.getChildren().clear();
        artTicketActual.clear();
        importe.setText("0.00");
    }

    public void setNumNuevoTicket() {
        this.numTicket.setText(String.valueOf(ServicioTicket.ultimoTicket() + 1));
    }
    
    @FXML
    void eliminarTicket(ActionEvent ignoredEvent) {
        ServicioTicket.eliminarVentasTicket(Integer.parseInt(numTicket.getText()));
        Ticket ticket = new Ticket(Integer.parseInt(numTicket.getText()), artTicketActual);
        ServicioTicket.eliminarTicket(ticket);
        artTicketActual.clear();
        this.ticket.getChildren().clear();
        importe.setText("0.00");
    }

    @FXML
    void quitarUltimo(ActionEvent ignoredEvent) {
        try {
            Articulo articulo = artTicketActual.get(artTicketActual.size() - 1);
            ServicioTicket.eliminarVenta(Integer.parseInt(numTicket.getText()), articulo.getCodArticulo());
            artTicketActual.remove(artTicketActual.size() - 1);
            
        } catch (IndexOutOfBoundsException index){
            Logger.generarLineaLogError(index.getClass().getSimpleName(), index.getMessage());
            try{
                Ticket ticket = new Ticket(Integer.parseInt(numTicket.getText()),artTicketActual);
                ServicioTicket.eliminarTicket(ticket);
            } catch (NumberFormatException e){
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
            
        } catch (NumberFormatException e){
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
        
        if (artTicketActual.size() == 0){
            try {
                Ticket ticket = new Ticket(Integer.parseInt(numTicket.getText()), artTicketActual);
                ServicioTicket.eliminarTicket(ticket);
            } catch (NumberFormatException e){
                Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
            }
        }
        
        try {
            articuloEnTicket.getChildren().clear();
            setImporte(); //recalcular importe
        } catch (NullPointerException e){
            Logger.generarLineaLogError(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @FXML
    void cerrarSesion(ActionEvent event) throws IOException {
        super.cerrarSesion(event, anchoPestana, altoPestana);
        guardarEstado();
    }

    @FXML
    void pestanaArticulos(ActionEvent event) throws IOException{
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


    public void setImporte() {
        Ticket ticket = ServicioTicket.consultarTicket(Integer.parseInt(numTicket.getText()));
        ticket.calcularImporte();
        double importeTicketBuscado = ticket.getImporte();
        
        //para redondear importe a dos decimales
        double importeRedondeado = Math.round(importeTicketBuscado * 10000.0) / 10000.0;
        
        importe.setText(String.valueOf(importeRedondeado));
    }

    public void setUsuarioActivo() {
        this.usuarioActivo.setText(DAO.USUARIO);
    }
    
    public void crearTicketNuevo(){
        numTicket.setText(String.valueOf(ServicioTicket.ultimoTicket() + 1));
        artTicketActual.clear();
        Ticket ticketNuevo = new Ticket(Integer.parseInt(numTicket.getText()),this.artTicketActual);
        ServicioTicket.anadirTicketBD(ticketNuevo);
    }

    @FXML
    void cerrarTicketTarjeta(ActionEvent ignoredEvent) {
        //disabled
    }

    @FXML
    void realizarDevolucion(ActionEvent ignoredEvent) {
        //disabled
    }

    @FXML
    void eliminarArticulo(ActionEvent ignoredEvent) {
        //disabled
    }
}
