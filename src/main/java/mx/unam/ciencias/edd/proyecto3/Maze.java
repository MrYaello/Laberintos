package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.Pila;
import mx.unam.ciencias.edd.VerticeGrafica;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

public class Maze {
  private class Cell {
    int x, y;
    byte score;
    boolean visited;
    boolean far;
    boolean[] gates;
    byte[] gatesScore;

    public Cell() { }

    public Cell(boolean d, boolean l, boolean u, boolean r, byte score, int x, int y) {
      gates = new boolean[]{d, l, u, r};
      this.score = score;
      gatesScore = new byte[4];
      this.x = x;
      this.y = y;
    }

    public boolean right() {
      return gates[3];
    }

    public boolean up() {
      return gates[2];
    }

    public boolean left() {
      return gates[1];
    }

    public boolean down() {
      return gates[0];
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    @Override
    public String toString() {
      //return String.format("%d" + (isFar ? " {%s, %s, %s, %s}" : ""), score, down(), left(), up(), right());
      //return "{" + isFar + "}";
      return String.format("%d {%d, %d} %s", score, x, y, Arrays.toString(gates));
    }

    public boolean equals(Cell cell) {
      return this.x == cell.x && this.y == cell.y;
    }

    public byte toByte() {
      return (byte) (Integer.parseInt(Integer.toBinaryString(score) + (down() ? 1 : 0) + (left() ? 1 : 0) + (up() ? 1 : 0) + (right() ? 1 : 0), 2) & 0xFF);
    }
  }

  private Cell[][] cells;
  public Lista<Cell> solve;
  public Cell start, end;
  public int width;
  public int height;
  public long seed;
  public Grafica<Cell> maze = new Grafica<>();
  Random rng;
  public Maze() { }

  public void build(int[][] matrix) {
    cells = new Cell[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        String bitScore = String.format("%8s", Integer.toBinaryString(matrix[i][j])).replaceAll(" ", "0").substring(0, 4);
        char[] gates = String.format("%8s", Integer.toBinaryString(matrix[i][j])).replaceAll(" ", "0").substring(4, 8).toCharArray();
        byte score = (byte) Integer.parseInt(bitScore, 2);
        cells[i][j] = new Cell(gates[0] == '1', gates[1] == '1', gates[2] == '1', gates[3] == '1', score, j, i);
        maze.agrega(cells[i][j]);
        if (i == 0 && !cells[i][j].up()) cells[i][j].far = true;
        if (i == height - 1 && !cells[i][j].down()) cells[i][j].far = true;
        if (j == width - 1 && !cells[i][j].right()) cells[i][j].far = true;
        if (j == 0 && !cells[i][j].left()) cells[i][j].far = true;
        if (cells[i][j].far && start == null) {
          start = cells[i][j];
        }
        else if (cells[i][j].far && start != null) {
          end = cells[i][j];
        }
      }
    }
  }

  public void build(int w, int h, long seed) {
    width = w;
    height = h;
    this.seed = seed;
    generate();
  }

  public void generate() {
    rng = seed != 0 ? new Random(seed) : new Random();
    cells = new Cell[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        cells[i][j] = new Cell(true, true, true, true, (byte) rng.nextInt(16), j, i);
      }
    }
    int start, end;
    do {
      start = rng.nextInt(4);
      end = rng.nextInt(4);
    } while (start == end);
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
    this.start.far = true;
    this.end.far = true;
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

  private Cell selectFars(int side) {
    int random;
    switch (side) {
      case 0: // Down
        random = rng.nextInt(width);
        cells[height - 1][random].gates[0] = false;
        return cells[height - 1][random];
      case 1: // Left
        random = rng.nextInt(height);
        cells[random][0].gates[1] = false;
        return cells[random][0];
      case 2: // Up
        random = rng.nextInt(width);
        cells[0][random].gates[2] = false;
        return cells[0][random];
      case 3: // Right
        random = rng.nextInt(height);
        cells[random][width - 1].gates[3] = false;
        return cells[random][width - 1];
      default:
        //Esto nunca ocurre.
        return null;
    }
  }

  private void createGraph() {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        maze.agrega(cells[i][j]);
        if (i == 0 && !cells[i][j].up()) cells[i][j].far = true;
        if (i == height - 1 && !cells[i][j].down()) cells[i][j].far = true;
        if (j == width - 1 && !cells[i][j].right()) cells[i][j].far = true;
        if (j == 0 && !cells[i][j].left()) cells[i][j].far = true;
        if (cells[i][j].far && start == null) {
          start = cells[i][j];
        }
        else if (cells[i][j].far && start != null) {
          end = cells[i][j];
        }
      }
    }
  }

  private void connectEm() {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        cells[i][j].gatesScore[3] = (byte) (1 + (cells[i][j].right() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i][j + 1].score)));
        cells[i][j].gatesScore[2] = (byte) (1 + (cells[i][j].up() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i - 1][j].score)));
        cells[i][j].gatesScore[1] = (byte) (1 + (cells[i][j].left() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i][j - 1].score)));
        cells[i][j].gatesScore[0] = (byte) (1 + (cells[i][j].down() ? 0 : cells[i][j].score + (cells[i][j].far ? 0 : cells[i + 1][j].score)));
        try {
          if (!cells[i][j].right() && j+1 < width) maze.conecta(cells[i][j], cells[i][j+1], cells[i][j].gatesScore[3]);
          if (!cells[i][j].up() && i-1 >= 0) maze.conecta(cells[i][j], cells[i-1][j], cells[i][j].gatesScore[2]);
          if (!cells[i][j].left() && j-1 >= 0) maze.conecta(cells[i][j], cells[i][j-1], cells[i][j].gatesScore[1]);
          if (!cells[i][j].down() && i+1 < height) maze.conecta(cells[i][j], cells[i+1][j], cells[i][j].gatesScore[0]);
        } catch (IllegalArgumentException ignored) {}
      }
    }
  }

  private Lista<Cell> possibleMoves(Cell c) {
    Lista<Cell> l = new Lista<>();
    if (isValidMove(c.x, c.y + 1)) l.agrega(cells[c.y + 1][c.x]); // Down
    if (isValidMove(c.x - 1, c.y)) l.agrega(cells[c.y][c.x - 1]); // Left
    if (isValidMove(c.x, c.y - 1)) l.agrega(cells[c.y - 1][c.x]); // Up
    if (isValidMove(c.x + 1, c.y)) l.agrega(cells[c.y][c.x + 1]); // Right
    return l;
  }

  private boolean isValidMove(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height && !cells[y][x].visited;
  }

  private Cell dig(Cell c) {
    Lista<Cell> l = possibleMoves(c);
    if (l.getElementos() == 0) return null;
    Cell goTo = l.get(rng.nextInt(l.getLongitud() < 1 ? 0 : l.getLongitud()));
    if (goTo.y == c.y + 1) {
      c.gates[0] = false;
      goTo.gates[2] = false;
    } // Down
    if (goTo.x == c.x - 1) {
      c.gates[1] = false;
      goTo.gates[3] = false;
    } // Left
    if (goTo.y == c.y - 1) {
      c.gates[2] = false;
      goTo.gates[0] = false;
    } // Up
    if (goTo.x == c.x + 1) {
      c.gates[3] = false;
      goTo.gates[1] = false;
    } // Right
    return goTo;
  }

  public Lista<Cell> solve() {
    solve = new Lista<>();
    for (VerticeGrafica<Cell> c : maze.dijkstra(start, end)) {
      solve.agrega(c.get());
    }
    return solve;
  }

  public String drawMaze(boolean solve) {
    if (cells == null) throw new IllegalStateException("El laberinto no está inicializado");
    GrapherSVG graph = new GrapherSVG();
    StringBuilder s = new StringBuilder();
    s.append(graph.initSVG((width * 20) + (20 * 2), (height * 20) + (20 * 2)));
    if (solve) {
      if (maze.getElementos() == 0) createGraph();
      connectEm();
      solve();
      s.append(drawSolution());
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        boolean drawL = !cells[i][j - 1 < 0 ? 0 : j - 1].right() || j == 0;
        boolean drawU = !cells[i - 1 < 0 ? 0 : i - 1][j].down() || i == 0;
        s.append(graph.drawCell(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20,
                cells[i][j].down(), cells[i][j].left() && drawL, cells[i][j].up() && drawU, cells[i][j].right()));
        if (cells[i][j].equals(end)) s.append(graph.drawCircle(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20, 7, "none", "red"));
        if (cells[i][j].equals(start)) s.append(graph.drawCircle(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20, 7, "none", "blue"));
        if (cells[i][j].far) s.append(graph.drawCircle(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20, 3, "none", "purple"));
      }
    }
    s.append(graph.closeSVG());
    return s.toString();
  }

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

  /*
  public String saveMaze() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
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
    return out.toString(StandardCharsets.ISO_8859_1);
  }

   */


  public void saveMaze() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      BufferedOutputStream out = new BufferedOutputStream(baos);
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
      System.out.println(e);
    }
    System.out.print(baos.toString(StandardCharsets.ISO_8859_1));
  }
/*
  public String saveMaze() {
    StringBuilder s = new StringBuilder();
    s.append("M");
    s.append("A");
    s.append("Z");
    s.append("E");
    s.append((char) height);
    s.append((char) width);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        s.append((char)Integer.parseInt(Integer.toBinaryString(cells[i][j].score) + (cells[i][j].down() ? 1 : 0) + (cells[i][j].left() ? 1 : 0) + (cells[i][j].up() ? 1 : 0) + (cells[i][j].right() ? 1 : 0), 2));
      }
    }
    return s.toString();
  }
 */


  public void print() {
    for (Cell[] cell: cells) {
      System.out.println(Arrays.toString(cell));
    }
  }

  /*
  public void print() {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        System.out.print("⌜" + (cells[i][j].up() ? "--" : "  ") + "⌝");
      }
      System.out.println();
      for (int j = 0; j < width; j++) {
        System.out.print((cells[i][j].left() ? "|" : " ") + " " + (cells[i][j].right() ? "|" : " "));
      }
      System.out.println();
      for (int j = 0; j < width; j++) {
        System.out.print("⌞" + (cells[i][j].down() ? "--" : "  ") + "⌟");
      }
      System.out.println();
    }
  }
   */
}
