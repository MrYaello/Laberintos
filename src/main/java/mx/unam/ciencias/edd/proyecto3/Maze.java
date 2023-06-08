package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.Grafica;
import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.VerticeGrafica;
import java.util.Arrays;
import java.util.Random;

public class Maze {
  private class Cell {
    int x, y;
    byte score;
    boolean visited;
    boolean isFar;
    boolean[] gates;
    byte[] gatesScore;
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
  }

  private Cell[][] cells;
  public Lista<Cell> solve;
  private Cell start, end;
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
      }
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (i == 0 && !cells[i][j].up()) cells[i][j].isFar = true;
        if (i == height - 1 && !cells[i][j].down()) cells[i][j].isFar = true;
        if (j == width - 1 && !cells[i][j].right()) cells[i][j].isFar = true;
        if (j == 0 && !cells[i][j].left()) cells[i][j].isFar = true;
        if (cells[i][j].isFar && start == null) {
          start = cells[i][j];
        }
        else if (cells[i][j].isFar && start != null) {
          end = cells[i][j];
        }
        cells[i][j].gatesScore[3] = (byte) (1 + (cells[i][j].right() ? 0 : cells[i][j].score + (cells[i][j].isFar ? 0 : cells[i][j+1].score)));
        cells[i][j].gatesScore[2] = (byte) (1 + (cells[i][j].up() ? 0 : cells[i][j].score + (cells[i][j].isFar ? 0 : cells[i-1][j].score)));
        cells[i][j].gatesScore[1] = (byte) (1 + (cells[i][j].left() ? 0 : cells[i][j].score + (cells[i][j].isFar ? 0 : cells[i][j-1].score)));
        cells[i][j].gatesScore[0] = (byte) (1 + (cells[i][j].down() ? 0 : cells[i][j].score + (cells[i][j].isFar ? 0 : cells[i+1][j].score)));
        try {
          if (!cells[i][j].right() && j+1 < width) maze.conecta(cells[i][j], cells[i][j+1], cells[i][j].gatesScore[3]);
          if (!cells[i][j].up() && i-1 >= 0) maze.conecta(cells[i][j], cells[i-1][j], cells[i][j].gatesScore[2]);
          if (!cells[i][j].left() && j-1 >= 0) maze.conecta(cells[i][j], cells[i][j-1], cells[i][j].gatesScore[1]);
          if (!cells[i][j].down() && i+1 < height) maze.conecta(cells[i][j], cells[i+1][j], cells[i][j].gatesScore[0]);
        } catch (IllegalArgumentException ignored) {}
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
        cells[i][j] = new Cell(true, true, true, true, (byte) rng.nextInt(16), i, j);
      }
    }
    int start, end;
    do {
      start = rng.nextInt(4);
      end = rng.nextInt(4);
    } while (start == end);
    this.start = selectFars(start);
    this.end = selectFars(end);
    System.out.println(start);
    System.out.println(end);
    System.out.println(this.start);
    System.out.println(this.end);
  }

  private Cell selectFars(int side) {
    int random;
    switch (side) {
      case 0:
        random = rng.nextInt(width);
        cells[height - 1][random].gates[0] = false;
        return cells[height - 1][random];
      case 1:
        random = rng.nextInt(height);
        cells[random][0].gates[1] = false;
        return cells[random][0];
      case 2:
        random = rng.nextInt(width);
        cells[0][random].gates[2] = false;
        return cells[0][random];
      case 3:
        random = rng.nextInt(height);
        cells[random][width - 1].gates[3] = false;
        return cells[random][width - 1];
      default:
        //Esto nunca ocurre.
        return null;
    }
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
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        s.append(graph.drawCell(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20,
                cells[i][j].down(), cells[i][j].left(), cells[i][j].up(), cells[i][j].right()));
        if (cells[i][j].isFar) s.append(graph.drawCircle(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20, 7, "none", "purple"));
      }
    }
    if (solve) { this.solve(); s.append(drawSolution()); }
    s.append(graph.closeSVG());
    return s.toString();
  }

  public String drawSolution() {
    GrapherSVG graph = new GrapherSVG();
    StringBuilder s = new StringBuilder();
    Cell previous = null;
    for (Cell cell: solve) {
      if (previous == null) { previous = cell; continue; }
      s.append(graph.drawLine(10 + (previous.getX() + 1) * 20, 10 + (previous.getY() + 1) * 20, 10 + (cell.getX() + 1) * 20, 10 + (cell.getY() + 1) * 20, "purple"));
      previous = cell;
    }
    return s.toString();
  }

  public String saveMaze() {
    StringBuilder s = new StringBuilder();
    s.append("MAZE");
    s.append((char) width);
    s.append((char) height);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        s.append((char) (Integer.parseInt(Integer.toBinaryString(cells[i][j].score) + (cells[i][j].down() ? 1 : 0) + (cells[i][j].left() ? 1 : 0) + (cells[i][j].up() ? 1 : 0) + (cells[i][j].right() ? 1 : 0), 2)));
      }
    }
    return s.toString();
  }

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
