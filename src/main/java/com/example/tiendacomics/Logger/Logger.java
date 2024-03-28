package com.example.tiendacomics.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String PATH_LOGGER = "src/main/resources/com/example/tiendacomics/log/tiendaComics.log";
   
    /**
     * Eliminar el logger anterior antes de crear uno nuevo
     */
    public static void eliminarLoggerAnterior(){
        File logger = new File(PATH_LOGGER);
        if (logger.exists()) {
            logger.delete();
        }
    }
    
    /**
     * Añadir una línea nueva al último
     * archivo de logger creado
     * (el logger de la sesión actual)
     * NOTA: si el archivo no existe, se creará
     */
    public static void anadirLineaLog(String lineaLog) {
        FileWriter fw = null;
        try{
            fw = new FileWriter(PATH_LOGGER, true); // añadimos contenido al archivo en vez de sobreescribirlo
            fw.write(lineaLog);

        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally{
            if(fw != null){
                try{
                    fw.close();
                }
                catch(IOException ignored){
                }
            }
        }
    }
    
    public static void generarLineaLogError(String nombreError, String mensajeError){
        StringBuilder lineaLog = new StringBuilder();
        
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHora = dateTime.format(formatter);
        
        lineaLog.append(fechaHora).append(" [ERROR] --> ").append(nombreError).append(": ").append(mensajeError);
        anadirLineaLog(lineaLog.toString());
    }
    
    public static void generarLineaLogInfo(String nombreInfo, String mensajeInfo){
        StringBuilder lineaLog = new StringBuilder();
        
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHora = dateTime.format(formatter);
        
        lineaLog.append(fechaHora).append(" [INFO] --> ").append(nombreInfo).append(": ").append(mensajeInfo);
        anadirLineaLog(lineaLog.toString());
    }
    
    public static void generarLineaLogDebug(String nombreDebug, String mensajeDebug){
        StringBuilder lineaLog = new StringBuilder();
        
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHora = dateTime.format(formatter);
        
        lineaLog.append(fechaHora).append(" [DEBUG] --> ").append(nombreDebug).append(": ").append(mensajeDebug);
        anadirLineaLog(lineaLog.toString());
    }
    
}
