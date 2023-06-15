package mx.unam.ciencias.edd.proyecto3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Clase que unifica todos los métodos, es decir, se encarga del funcionamiento principal.
 * Además de la lectura de la entrada estandar.
 *
 * @author Yael Lozano
 */
public class Main extends Flags {

  /** Mensaje de error para cómo debe ser usado el programa. */
  private final String USE = "Para resolver un laberinto (.mze) se debe enviar por entrada estandar ej.:" + "\n" +
          "a) java -jar target/proyecto3.jar < ejemplo.mze > solucion.svg"  + "\n" +
          "b) cat ejemplo.mze | java -jar target/proyecto3.jar > solucion.svg"  + "\n" +
          "Para generar un laberinto se debe invocar de la siguiente forma ej.:" + "\n" +
          "a) java -jar target/proyecto3.jar -g -s <Semilla> -w <Ancho> -h <Alto>"  + "\n" +
          "-) -g           --- Indica que hay que generar un laberinto."  + "\n" +
          "-) -s <Semilla> --- (Opcional) La semilla para generar el laberinto."  + "\n" +
          "-) -w <Ancho>   --- Número de columnas del laberinto. Min. 2 | Max. 255"  + "\n" +
          "-) -h <Alto>    --- Número de renglones del laberinto.  Min. 2 | Max. 255"  + "\n";
  /** Matriz de enteros que almacenará la entrada. */
  private int[][] matrix;
  /** La instancia de la clase laberinto. */
  public Maze maze;

  /**
   * Constructor que inicializa la instancia de la clase laberinto.
   */
  public Main() {
    maze = new Maze();
  }

  /**
   * Funcionamiento principal.
   * Definirá el modo de ejecución según los argumentos.
   *
   * @param args argumentos de linea de comandos
   */
  public void start(String[] args) {
    try {
      flagsChecker(args);
    } catch (Exception e) {
      error(USE);
    }

    if (!generate()) {
      read();
      maze.build(matrix);
      System.out.println(maze.drawMaze(true));
    } else {
      maze.build(getWidth(), getHeight(), getSeed());
      maze.saveMaze();
    }
  }

  /**
   * Método que leerá la entrada estandar y verificará el formato del archivo .mze
   */
  public void read() {
    /* Se define el codec con el que se leerán los bytes */
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.ISO_8859_1));
    try {
      int ch;
      int M = reader.read();
      int A = reader.read();
      int Z = reader.read();
      int E = reader.read();
      maze.height = reader.read();
      maze.width =  reader.read();
      if (M != 77 || A != 65 || Z != 90 || E != 69) throw new Exception("El formato del archivo es inválido.");
      if ((maze.height < 2 || maze.height > 255) || (maze.width < 2 || maze.width > 255)) throw new Exception("El formato del archivo es inválido: el ancho y el alto son inválidos.");
      matrix = new int[maze.height][maze.width];
      for (int i = 0; i < maze.height; i++)
        for (int j = 0; j < maze.width; j++)
          if ((ch = reader.read()) == -1 || ch > 255) throw new Exception("El formato del archivo es inválido.");
          else matrix[i][j] = ch;
      if (reader.read() != -1) throw new Exception("El formato del archivo es inválido: tiene más elementos de los necesarios.");
    } catch (Exception e) {
      error(e.toString());
    } finally {
      try {
        reader.close();
      } catch (IOException ex) {
        error("Esto no deberia pasar.");
      }
    }
  }

  /**
   * Método para imprimir errores y terminar la ejecución.
   * @param err cadena que especifica el error
   */
  private void error(String err) {
    System.err.println(err);
    System.exit(-1);
  }
}