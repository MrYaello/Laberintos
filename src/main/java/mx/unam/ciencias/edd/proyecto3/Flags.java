package mx.unam.ciencias.edd.proyecto3;

/**
  * Clase que procesa las banderas recibidas por linea de comandos.
  */
public class Flags {
  /* ¿Se deberá generar un laberinto? */
  private static boolean generate;
  /* La semilla que se utilizará para generar el laberinto */
  private static long seed;
  /* Parámetro que indica el número de renglones del laberinto */
  private static int height;
  /* Parámetro que indica el número de columnas del laberinto */
  private static int width;
  
  /**
    * Método que procesa los argumentos recibidos por linea 
    * de comandos.
    * @param args - Argumentos de linea de comandos.
    */
  public static void flagsChecker(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-g")) generate = true;
      if (args[i].equals("-s"))
        seed = Long.parseLong(args[i+1]);
      if (args[i].equals("-w"))
        width = Integer.parseInt(args[i+1]);
      if (args[i].equals("-h"))
        height = Integer.parseInt(args[i+1]);
    }
  }

  public static boolean generate() {
    return generate;
  }

  public static int getWidth() {
    return width;
  }

  public static int getHeight() {
    return height;
  }

  public static long getSeed() {
    return seed;
  }
}
