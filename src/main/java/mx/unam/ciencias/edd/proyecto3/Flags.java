package mx.unam.ciencias.edd.proyecto3;

/**
  * Clase que procesa las banderas recibidas por linea de comandos.
 * @author Yael Lozano
  */
public class Flags {
  /** ¿Se deberá generar un laberinto? */
  private boolean generate;
  /** La semilla que se utilizará para generar el laberinto */
  private long seed;
  /** Parámetro que indica el número de renglones del laberinto */
  private int height;
  /** Parámetro que indica el número de columnas del laberinto */
  private int width;
  
  /**
    * Método que procesa los argumentos recibidos por linea 
    * de comandos.
    * @param args argumentos de linea de comandos
    */
  public void flagsChecker(String[] args) {
    if (args.length == 0) return;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-g")) generate = true;
      if (args[i].equals("-s"))
        seed = Long.parseLong(args[i+1]);
      if (args[i].equals("-w"))
        width = Integer.parseInt(args[i+1]);
      if (args[i].equals("-h"))
        height = Integer.parseInt(args[i+1]);
    }
    if (generate() && (width == 0 || height == 0)) throw new IllegalArgumentException("Se debe proporcionar altura y ancho.");
    if (width < 2 || width > 255 || height < 2 || height > 255) throw new IllegalArgumentException("Los valores de altura o ancho son inválidos.");
  }


  /**
   * Getter para generate.
   *
   * @return generate
   */
  public boolean generate() {
    return generate;
  }

  /**
   * Getter para el número de columnas.
   *
   * @return width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Getter para el número de filas.
   *
   * @return height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Getter para la semilla.
   *
   * @return seed
   */
  public long getSeed() {
    return seed;
  }
}
