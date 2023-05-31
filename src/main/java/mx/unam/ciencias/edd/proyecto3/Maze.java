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
      return String.format("%d {%d, %d}", score, x, y);
    }
  }

  private Cell[][] cells;
  public Lista<Cell> solve;
  private Cell start, end;
  private int width;
  private int height;
  public Grafica<Cell> maze = new Grafica<>();
  public Maze() { }

  public Maze(int height, int width) {
    this.height = height;
    this.width = width;
  }

  public void build(int[][] matrix) {
    height = matrix.length;
    width = matrix[0].length;
    cells = new Cell[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        String[] bitScore = String.format("%8s", Integer.toBinaryString(matrix[i][j])).replaceAll(" ", "0").substring(0, 4).split("");
        char[] gates = String.format("%8s", Integer.toBinaryString(matrix[i][j])).replaceAll(" ", "0").substring(4, 8).toCharArray();
        byte score = (byte) (Integer.parseInt(bitScore[3]) + Integer.parseInt(bitScore[2]) * 2 + Integer.parseInt(bitScore[1]) * 4 + Integer.parseInt(bitScore[0]) * 8);
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

  public void generate() {}
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
    String s = "";
    s += graph.initSVG((width * 20) + (20 * 2), (height * 20) + (20 * 2));
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        s += graph.drawCell(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20,
                cells[i][j].down(), cells[i][j].left(), cells[i][j].up(), cells[i][j].right());
        if (cells[i][j].isFar) s += graph.drawCircle(10 + (cells[i][j].getX() + 1) * 20, 10 + (cells[i][j].getY() + 1) * 20, 7, "none", "purple");
      }
    }
    if (solve) { this.solve(); s += drawSolution(); }
    s += graph.closeSVG();
    return s;
  }
  public String drawSolution() {
    GrapherSVG graph = new GrapherSVG();
    String s = "";
    Cell previous = null;
    for (Cell cell: solve) {
      if (previous == null) { previous = cell; continue; }
      s += graph.drawLine(10 + (previous.getX() + 1) * 20, 10 + (previous.getY() + 1) * 20, 10 + (cell.getX() + 1) * 20, 10 + (cell.getY() + 1) * 20, "purple");
      previous = cell;
    }
    return s;
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
