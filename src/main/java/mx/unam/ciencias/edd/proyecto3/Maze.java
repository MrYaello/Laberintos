package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.*;
import java.util.Random;

public class Maze {
  private class Cell {
    byte score;
    boolean[] gates;
    byte[] gatesScore;
    public Cell(boolean r, boolean u, boolean l, boolean d, byte score) {
      gates = new boolean[]{r, u, l, d};
      this.score = score;
    }
  }

  private Cell[][] cells;
  private Grafica<Byte> maze;
  private int width;
  private int height;

  public Maze() { }

  public Maze(int height, int width) {
    this.height = height;
    this.width = width;
  }

  private void build() {

  }

  private void build(int[][] matrix) {

    build();
  }

  public void generate() {}
  public void solve() {}
}
