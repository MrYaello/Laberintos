package mx.unam.ciencias.edd.Proyecto1;

import mx.unam.ciencias.edd.Cola;

/**
  * Clase que procesa las banderas recibidas por linea de comandos.
  */
public class Flags {
  /* ¿Se deberá aplicar reverse? */
  private static boolean reverse;
  /* Ruta del archivo donde se guardará la salida */
  private static String outPathFile;
  /* Cola de rutas de los archivos de entrada */
  private static Cola<String> filePaths = new Cola<>();
  
  /**
    * Método que procesa los argumentos recibidos por linea 
    * de comandos.
    * @param args - Argumentos de linea de comandos.
    */
  public static void flagsChecker(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (!args[i-1 < 0 ? i : i-1].equals("-o") && args[i].contains(".")) { 
        filePaths.mete(args[i]);
      }
      if (args[i].equals("-r")) reverse = true;
      if (args[i].equals("-o")) 
        if (i+1 < args.length) outPathFile = args[i+1];
    }
  }
  
  /**
    * Regresa la cola de rutas de archivos de entrada.
    * @return Cola<String> filePaths
    */
  public static Cola<String> getFilePaths() {
    return filePaths;
  }
  
  /**
    * Regresa si deberá aplicarse reversa.
    * @return reverse
    */
  public static boolean doReverse() {
    return reverse;
  }
  
  /**
    * Regresa la ruta del archivo de salida.
    * @return outPathFie
    */
  public static String getOutPathFile() {
    return outPathFile;
  }

}
