package mx.unam.ciencias.edd.proyecto2;

import mx.unam.ciencias.edd.Lista;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Clase para leer la entrada estandar o archivos.
 */
public class Read {
  /* Lista que almacenará las lineas de texto. */
  private Lista<String> lista = new Lista<>();

  /* Contructor sin parámetros. */
  public Read() {}

  /** 
   * Método que crea un objeto para leer la entrada estandar.
   * @return Objeto InputStreamReader
   */
  public Reader standardInput() {
    return new InputStreamReader(System.in);
  }

  /** 
   * Método que crea un objeto para leer un archivo.
   * @param path - La ruta del archivo
   * @return Objeto FileReader
   */
  public Reader file(String path) {
    try {
      return new FileReader(path);
    } catch (FileNotFoundException e) {
      System.err.println("El archivo " + path + " no existe.");
      System.exit(1);
    }
    return null;
  } 

  /**
   * Regresa la lista que contiene las cadenas.
   * @return Lista<String> lista
   */
  public Lista<String> getLista() {
    return lista;
  }

  /**
   * Método que realiza la lectura de la entrada estandar o
   * del archivo según corresponda.
   * @param in - Establece el origen de la entrada.
   */
  public void read(Reader in) {
    if (in == null) return;
    BufferedReader reader = null;
    try {
      String cLine;
      reader = new BufferedReader(in);
      while ((cLine = reader.readLine()) != null) {
        if (cLine.trim().startsWith("#")) continue;
        cLine =  cLine.substring(0, !cLine.contains("#") ? cLine.length() : cLine.indexOf("#")).trim();
        lista.agrega(cLine);
      }
    } catch (IOException e) {
      e.printStackTrace(); 
    } finally {
      try {
        if (reader != null) reader.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
