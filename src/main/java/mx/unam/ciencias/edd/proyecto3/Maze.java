package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.Pila;
import mx.unam.ciencias.edd.VerticeGrafica;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.io.BufferedOutputStream;

/**
 * Clase que administra y genera laberintos.
 *
 * @author Yael Lozano
 */
public class Maze {
  /**
   * Clase que administra las celdas del laberinto.
   *
   * @author Yael Lozano
   */
  private class Cell {
    /** Coordenada en x. */
    int x;
    /** Coordenada en y. */
    int y;
    /** Puntuación de la celda. */
    byte score;
    /** Marca a la celda como visitada. Utilizado para generar el laberinto. */
    boolean visited;
    /** Marca a la celda como extremo. Utilizada para resolver el laberinto. */
    boolean far;
    /** Arreglo de longitud 4 que representa las paredes de la celda. */
    boolean[] gates;
    /** Arreglo de longitud 4 que representa la puntuación de las puertas. */
    byte[] gatesScore;

    /**
     * Constructor sin parámetros.
     */
    public Cell() { }

    /**
     * Constructor para inicializar una celda.
     *
     * @param d     ¿Hay pared abajo?
     * @param l     ¿Hay pared izquierda?
     * @param u     ¿Hay pared arriba?
     * @param r     ¿Hay pared derecha?
     * @param score puntuación de la celda
     * @param x     coordenada en x
     * @param y     coordenada en y
     */
    public Cell(boolean d, boolean l, boolean u, boolean r, byte score, int x, int y) {
      gates = new boolean[]{d, l, u, r};
      this.score = score;
      gatesScore = new byte[4];
      this.x = x;
      this.y = y;
    }

    /**
     * ¿Hay pared derecha?
     *
     * @return booleano que indica si hay pared derecha
     */
    public boolean right() {
      return gates[3];
    }

    /**
     * ¿Hay pared arriba?
     *
     * @return booleano que indica si hay pared arriba
     */
    public boolean up() {
      return gates[2];
    }

    /**
     * ¿Hay pared izquierda?
     *
     * @return booleano que indica si hay pared izquierda
     */
    public boolean left() {
      return gates[1];
    }

    /**
     * ¿Hay pared abajo?
     *
     * @return booleano que indica si hay pared abajo
     */
    public boolean down() {
      return gates[0];
    }

    /**
     * Getter para la coordenada en x.
     *
     * @return coordenada en x
     */
    public int getX() {
      return x;
    }

    /**
     * Getter para la coordenada en y.
     *
     * @return coordenada en y
     */
    public int getY() {
      return y;
    }

    /**
     * Representación en cadena de una celda, con el siguiente formato:
     * score {x, y} [d, l, u, r]
     * Donde d, l, u y r son las paredes de la celda.
     * @return cadena que representa una celda
     */
    @Override
    public String toString() {
      return String.format("%d {%d, %d} %s", score, x, y, Arrays.toString(gates));
    }

    /**
     * Método para comparar celdas por coordenadas.
     *
     * @param cell la celda a comparar
     * @return true si tienen las mismas coordenadas, false de lo contrario
     */
    public boolean equals(Cell cell) {
      return this.x == cell.x && this.y == cell.y;
    }

    /**
     * Método que convierte la celda a byte.
     *
     * @return byte que representa la celda
     */
    public byte toByte() {
      return (byte) (Integer.parseInt(Integer.toBinaryString(score) + (down() ? 1 : 0) + (left() ? 1 : 0) + (up() ? 1 : 0) + (right() ? 1 : 0), 2) & 0xFF);
    }
  }
  /** Matriz de celdas que es la estructura del laberinto en sí. */
  private Cell[][] cells;
  /** Lista de celdas que almacena la solución al laberinto. */
  public Lista<Cell> solve;
  /** Celda que almacena el inicio del laberinto. */
  public Cell start;
  /** Celda que almacena el final del laberinto. */
  public Cell end;
  /** Parámetro que indica el número de columnas del laberinto */
  public int width;
  /** Parámetro que indica el número de filas del laberinto */
  public int height;
  /** La semilla que se utilizará para generar el laberinto */
  public long seed;
  /** Gráfica que representará el laberinto para encontrar una solución */
  private Grafica<Cell> maze = new Grafica<>();
  /** El generador de números aleatorios. */
  Random rng;

  /** Constructor sin parámetros */
  public Maze() { }

  /**
   * Construye el laberinto utilizando la matriz de enteros recibida.
   *
   * @param matrix la matriz de enteros de la entrada
   */
  public void build(int[][] matrix) {
    /* Se inicializa la matriz */
    cells = new Cell[height][width];
    /* Se itera la matriz para crear las celdas */
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        /* Se extrae la puntuación y las paredes de la matriz */
        String bitScore = String.format("%8s", Integer.toBinaryString(matrix[i][j])).replaceAll(" ", "0").substring(0, 4);
        char[] gates = String.format("%8s", Integer.toBinaryString(matrix[i][j])).replaceAll(" ", "0").substring(4, 8).toCharArray();
        byte score = (byte) Integer.parseInt(bitScore, 2);
        /* Se crea una nueva celda con la información obtenida */
        cells[i][j] = new Cell(gates[0] == '1', gates[1] == '1', gates[2] == '1', gates[3] == '1', score, j, i);
        /* Se crea la gráfica */
        createGraph(cells[i][j]);
      }
    }
  }

  /**
   * Construye y genera el laberinto utilizando los parámetros.
   *
   * @param w    el número de columnas del laberinto
   * @param h    el número de filas del laberinto
   * @param seed semilla que se utilizará para generar el laberinto
   */
  public void build(int w, int h, long seed) {
    width = w;
    height = h;
    this.seed = seed;
    generate();
  }

  /**
   * Genera el laberinto utilizando DFS y un generador de números aleatorios con congruencias lineales.
   */
  public void generate() {
    /* Se inicializa el generador de números aleatorios, si no hay semilla se utiliza el reloj de la computadora */
    rng = seed != 0 ? new Random(seed) : new Random();
    /* Se inicializa la matriz */
    cells = new Cell[height][width];
    /* Se itera la matriz para crear las celdas con una puntación aleatoria */
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        cells[i][j] = new Cell(true, true, true, true, (byte) rng.nextInt(16), j, i);
      }
    }
    /* Selecciona las celdas de inicio y fin aleatoriamente */
    int start, end;
    do {
      start = rng.nextInt(4);
      end = rng.nextInt(4);
    } while (start == end);
    /* Puede que existan colisiones pues son completamente aleatorias.
    * Entonces nos aseguramos de que no colisionen. */
    do {
      this.start = selectFars(start);
      this.end = selectFars(end);
      if (this.start.equals(this.end)) {
        this.start.gates[0] = true;
        this.start.gates[1] = true;
        this.start.gates[2] = true;
        this.start.gates[3] = true;
      }
    } while (this.start.equals(this.end));
    /* Marca a las celdas de inicio y fin como extremos. Realmente sólo con la finalidad
    * de poder resolver un laberinto recién creado. */
    this.start.far = true;
    this.end.far = true;
    /* Algoritmo DFS para crear el laberinto aleatoriamente. */
    Pila<Cell> dfs = new Pila<>();
    dfs.mete(this.start);
    this.start.visited = true;
    while (!dfs.esVacia()) {
      Cell cA = dfs.mira();
      Cell cB = dig(cA);
      if (cB == null) {
        dfs.saca();
        continue;
      }
      cB.visited = true;
      dfs.mete(cB);
    }
  }

  /**
   * Selecciona aleatoriamente un extremo del laberinto.
   * @param side 0 - abajo, 1 - izquierda, 2 - arriba, 3 - derecha
   * @return celda que ahora es el nuevo extremo
   */
  private Cell selectFars(int side) {
    int random;
    switch (side) {
      case 0: // Abajo
        random = rng.nextInt(width);
        cells[height - 1][random].gates[0] = false;
        return cells[height - 1][random];
      case 1: // Izquierda
        random = rng.nextInt(height);
        cells[random][0].gates[1] = false;
        return cells[random][0];
      case 2: // Arriba
        random = rng.nextInt(width);
        cells[0][random].gates[2] = false;
        return cells[0][random];
      case 3: // Derecha
        random = rng.nextInt(height);
        cells[random][width - 1].gates[3] = false;
        return cells[random][width - 1];
      default:
        //Esto nunca ocurre.
        return null;
    }
  }

  /**
   * Construye el laberinto cómo gráfica.
   * @param c celda que se agregará a la gráfica
   */
  private void createGraph(Cell c) {
    /* Agrega la celda a la gráfica */
    maze.agrega(c);
    /* Verifica si la celda es un extremo del laberinto */
    if (c.getY() == 0 && !c.up()) c.far = true;
    if (c.getY() == height - 1 && !c.down()) c.far = true;
    if (c.getX() == width - 1 && !c.right()) c.far = true;
    if (c.getX() == 0 && !c.left()) c.far = true;
    /* Se establece el inicio y el final del laberinto basado en si es extremo */
    if (c.far && start == null) {
      start = c;
    }
    else if (c.far && start != null) {
      end = c;
    }
  }

  /**
   * Conecta las celdas en la gráfica si comparten una puerta, además le asigna una puntación a las puertas.
   */
  private void connectEm() {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        cells[i][j].gatesScore[3] = (byte) (1 + (cells[i][j].right() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i][j + 1].score)));
        cells[i][j].gatesScore[2] = (byte) (1 + (cells[i][j].up() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i - 1][j].score)));
        cells[i][j].gatesScore[1] = (byte) (1 + (cells[i][j].left() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i][j - 1].score)));
        cells[i][j].gatesScore[0] = (byte) (1 + (cells[i][j].down() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i + 1][j].score)));
        if (!cells[i][j].right() && j+1 < width) maze.conecta(cells[i][j], cells[i][j+1], cells[i][j].gatesScore[3]);
        if (!cells[i][j].down() && i+1 < height) maze.conecta(cells[i][j], cells[i+1][j], cells[i][j].gatesScore[0]);
      }
    }
  }

  /**
   * Regresa una lista con los movimientos posibles, celdas adyacentes válidas no visitadas.
   * @param c celda origen
   * @return lista con los movimientos posibles
   */
  private Lista<Cell> possibleMoves(Cell c) {
    Lista<Cell> l = new Lista<>();
    if (isValidMove(c.x, c.y + 1)) l.agrega(cells[c.y + 1][c.x]); // Down
    if (isValidMove(c.x - 1, c.y)) l.agrega(cells[c.y][c.x - 1]); // Left
    if (isValidMove(c.x, c.y - 1)) l.agrega(cells[c.y - 1][c.x]); // Up
    if (isValidMove(c.x + 1, c.y)) l.agrega(cells[c.y][c.x + 1]); // Right
    return l;
  }

  /**
   * Verifica si es un movimiento válido, es decir, no se sale de la matriz y no ha sido
   * visitada.
   * @param x coordenada x del destino
   * @param y coordenada y del destino
   * @return true si es válido, de lo contrario false
   */
  private boolean isValidMove(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height && !cells[y][x].visited;
  }

  /**
   * Crea una puerta entre una celda origen y una destino seleccionada aleatoriamente
   * de la lista de movimientos posibles.
   * @param c celda origen
   * @return celda destino seleccionada aleatoriamente
   */
  private Cell dig(Cell c) {
    Lista<Cell> l = possibleMoves(c);
    if (l.getElementos() == 0) return null;
    Cell goTo = l.get(rng.nextInt(l.getLongitud() < 1 ? 0 : l.getLongitud()));
    if (goTo.y == c.y + 1) {
      c.gates[0] = false;
      goTo.gates[2] = false;
    } // Abajo
    if (goTo.x == c.x - 1) {
      c.gates[1] = false;
      goTo.gates[3] = false;
    } // Izquierda
    if (goTo.y == c.y - 1) {
      c.gates[2] = false;
      goTo.gates[0] = false;
    } // Arriba
    if (goTo.x == c.x + 1) {
      c.gates[3] = false;
      goTo.gates[1] = false;
    } // Derecha
    return goTo;
  }

  /**
   * Regresa una lista de celdas con la solución del laberinto, utilizando dijkstra.
   * @return lista que contiene la solución
   */
  public Lista<Cell> solve() {
    solve = new Lista<>();
    for (VerticeGrafica<Cell> c : maze.dijkstra(start, end)) {
      solve.agrega(c.get());
    }
    return solve;
  }

  /**
   * Regresa una cadena de texto en formato SVG con el laberinto y su solución.
   *
   * @param solve ¿Deberá dibujarse la solución?
   * @return SVG del laberinto
   */
  public String drawMaze(boolean solve) {
    if (cells == null) throw new IllegalStateException("El laberinto no está inicializado");
    GrapherSVG graph = new GrapherSVG();
    StringBuilder s = new StringBuilder();
    s.append(graph.initSVG((width * 20) + (20 * 2), (height * 20) + (20 * 2)));
    if (solve) {
      /* Para resolver un laberinto recién generado */
      if (maze.getElementos() == 0)
        for (int i = 0; i < height; i++)
          for (int j = 0; j < width ; j++)
            createGraph(cells[i][j]);
      connectEm();
      solve();
      /* Dibuja la solución */
      s.append(drawSolution());
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        /* Para evitar que se dibujen las paredes dos veces */
        boolean drawL = !cells[i][j - 1 < 0 ? 0 : j - 1].right() || j == 0;
        boolean drawU = !cells[i - 1 < 0 ? 0 : i - 1][j].down() || i == 0;
        /* Dibuja la celda */
        s.append(graph.drawCell(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20,
                cells[i][j].down(), cells[i][j].left() && drawL, cells[i][j].up() && drawU, cells[i][j].right()));
        /* Si es extremo dibuja un circulo para denotarlo */
        if (cells[i][j].far) s.append(graph.drawCircle(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20, 5, "none", "pink"));
      }
    }
    s.append(graph.closeSVG());
    return s.toString();
  }

  /**
   * Regresa una cadena de texto en formato SVG con la solución del laberinto.
   * @return SVG de la trayectoria de la solución
   */
  public String drawSolution() {
    GrapherSVG graph = new GrapherSVG();
    StringBuilder s = new StringBuilder();
    Cell previous = null;
    for (Cell cell: solve) {
      if (previous == null) { previous = cell; continue; }
      s.append(graph.drawLine(10 + (previous.getX() + 1) * 20, 10 + (previous.getY() + 1) * 20, 10 + (cell.getX() + 1) * 20, 10 + (cell.getY() + 1) * 20, "purple", 4));
      previous = cell;
    }
    return s.toString();
  }

  /**
   * Imprime el laberinto representado en bytes.
   */
  public void saveMaze() {
    try {
      BufferedOutputStream out = new BufferedOutputStream(System.out);
      out.write(77);
      out.write(65);
      out.write(90);
      out.write(69);
      out.write(height);
      out.write(width);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          out.write(cells[i][j].toByte());
        }
      }
      out.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }
}