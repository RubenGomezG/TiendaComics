package com.example.tiendacomics.controller;

import com.example.tiendacomics.AppTienda;
import com.example.tiendacomics.Logger.Logger;
import com.example.tiendacomics.dao.DAO;
import com.example.tiendacomics.model.Articulo;
import com.example.tiendacomics.model.Ticket;
import com.example.tiendacomics.service.ServicioHistorial;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

@SuppressWarnings("ClassEscapesDefinedScope")
public class ControladorHistorial extends Controlador implements Initializable {
    @FXML
    private Pane contenedor;
    
    @FXML
    private Label nombreActual;
    @FXML
    private Label codArtActual;
    @FXML
    private Label masInfoActual;

    @FXML
    private VBox ticket;
    private HBox articuloEnTicket;
    private HashMap<Integer,ObservableList<Node>> filasTicket = new HashMap<>();
    private  Integer indexArtTicket = 0;

    @FXML
    private ImageView imagenArticulo;
    @FXML
    private Label importe;
    @FXML
    private Label numTicket;
    @FXML
    private TextField numeroTicketBuscar;
    @FXML
    private Label precioArtActual;

    @FXML
    private Label usuarioActivo;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUsuarioActivo();
        cargarEstado();
        consulta();
        mostrarImagen();
    }

    private void guardarEstado() {
        FileWriter salida = null;
        try {
            salida = new FileWriter("src/main/resources/com/example/tiendacomics/log/loghistorial.txt");
            salida.write(numeroTicketBuscar.getText());
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
            entrada = new BufferedReader(new FileReader("src/main/resources/com/example/tiendacomics/log/loghistorial.txt"));
            String linea = entrada.readLine();
            String numeroTicket = "";

            while (linea != null) {
                // Dividir la línea en dos strings utilizando un separador
                String[] strings = linea.split(";");

                // Asignar los strings a variables separadas
                numeroTicket = strings[0];
                linea = entrada.readLine();
            }
            numeroTicketBuscar.setText(numeroTicket);

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


    /**
     * Cuando le des a la tacla enter es cuando te va a buscar
     * si ese codigo de ticket existe en la BD y, si existe,
     * te mostrará sus detalles en el ticket
     */
    @FXML
    void buscarTicket(ActionEvent ignoredEvent) {
        consulta();
    }

    private void consulta() {
        ticket.getChildren().clear();
        int codTicketBuscado = 0;
        try {
            codTicketBuscado = Integer.parseInt(numeroTicketBuscar.getText());
        }
        catch (NumberFormatException ignored){
            return;
        }
        if(ServicioHistorial.existeTicketBuscado(codTicketBuscado)) {
            setNumTicket(codTicketBuscado);
            setImporteTicket(codTicketBuscado);
            mostrarTicket(codTicketBuscado);
            indexArtTicket=0;
            filasTicket=new HashMap<>();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El ticket buscado no existe");
            alert.setContentText("Debes introducir un ticket existente");
            alert.showAndWait();
            alert.close();
        }
    }

    private void setImporteTicket(int codTicketBuscado) {
        double importeTicketBuscado = ServicioHistorial.consultarTicketBuscado(codTicketBuscado).getImporte();
        
        //para redondear a dos decimales
        double importeRedondeado = Math.round(importeTicketBuscado * 10000.0) / 10000.0;
        
        importe.setText(String.valueOf(importeRedondeado));
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
    
    public void mostrarDetallesArtSeleccTicket(Articulo articuloSeleccionado){
        nombreActual.setText(articuloSeleccionado.getNombre());
        codArtActual.setText(String.valueOf(articuloSeleccionado.getCodArticulo()));
        precioArtActual.setText(String.valueOf(articuloSeleccionado.getPrecio()));
        masInfoActual.setText(articuloSeleccionado.getInfoExtraArticulo());
        mostrarImagen();
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

    public void setNumTicket(int codTicketBuscado) { //cada vez que le des al intro en la barra del buscador
        if (ServicioHistorial.existeTicketBuscado(Integer.parseInt(numeroTicketBuscar.getText()))){
            this.numTicket.setText(String.valueOf(ServicioHistorial.consultarTicketBuscado(codTicketBuscado).getNumTicket()));
        } else{
            this.numTicket.setText(String.valueOf(0));
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
        try {
            articuloEnTicket.setOnMouseClicked(
                /*
                  Este metodo se activa cuando haces click
                  sobre una de las filas del ticket
                  para seleccionar uno de sus articulos
                 */
                new EventHandler<MouseEvent>() {
                    @Override //le añadimos la propiedad de onMouseClicked event
                    public void handle(MouseEvent event) throws IndexOutOfBoundsException{
                        ObservableList<Node> camposFila = articuloEnTicket.getChildren(); //creamos una lista con tod0s los labels del HBOX actual
                        filasTicket.put(indexArtTicket, camposFila); //añadimos ese HBOX a nuestro hashmap con un índice que los ubica en el ticket

                        Label infoExtraArtTicket = (Label) camposFila.get(3);

                        Articulo artSeleccionado = ServicioHistorial.consultaTicket(Integer.parseInt(numTicket.getText())).get(indexArtTicket);
                        infoExtraArtTicket.setText(artSeleccionado.getInfoExtraArticulo());
                        mostrarDetallesArtSeleccTicket(artSeleccionado);

                        if (indexArtTicket == camposFila.size() - 1) {
                            indexArtTicket = 0;
                        } else {
                            indexArtTicket++;
                        }

                        articuloEnTicket.setStyle("-fx-background-color: lightgrey;"); //resaltamos el articulo seleccionado dentro del ticket
                    }
                }
            );
        }catch (IndexOutOfBoundsException index){
            Logger.generarLineaLogError(index.getClass().getSimpleName(), index.getMessage());
            Logger.generarLineaLogInfo( "(ControladorHistorial) generarNuevaFilaTicket().handle()", "Imposible seguir iterando, ya no hay más articulos en el ticket");
        }

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

        articuloEnTicket.getChildren().addAll(nombreTicket,precioTicket,cantidadTicket,infoExtraTicket);
        return articuloEnTicket;
    }

    /**
     * Añade tod0s los articulos de un mismo ticket al ticket,
     * todos deben insertartse según los articulos guardados
     * bajo el mismo codticket
     * @param codTicket Código de ticket
     */
    public void mostrarTicket(int codTicket){
        Ticket ticket = new Ticket(codTicket,new ArrayList<>());
        ArrayList<Articulo> articulosEnTicket = ServicioHistorial.articulosTicket(codTicket);
        ticket.anadirTodosATicket(articulosEnTicket);
        
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

    public void setUsuarioActivo() {
        this.usuarioActivo.setText(DAO.USUARIO);
    }

    @FXML
    void abrirCaja(ActionEvent ignoredEvent) {
        //disabled
    }
}
