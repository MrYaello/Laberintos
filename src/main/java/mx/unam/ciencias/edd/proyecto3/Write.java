package mx.unam.ciencias.edd.Proyecto1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

/**
  * Clase para escribir en archivos.
  */
public class Write {
  /* Ruta del archivo */
  private String path;
  
  /**
    * Constructor único que recibe la ruta del archivo a escribir.
    * @param path - Ruta del archivo.
    */
  public Write(String path) {
    this.path = path;
  } 

  /**
    * Método que escribe en un archivo.
    * @param s - Cadena que será escrita en el archivo.
    */
  public void writeFile(String s) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(path));
      writer.write(s);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

