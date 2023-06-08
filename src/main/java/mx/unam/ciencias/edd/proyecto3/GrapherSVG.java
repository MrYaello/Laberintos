package mx.unam.ciencias.edd.proyecto3;

public class GrapherSVG {

  public GrapherSVG() {}

  public String initSVG(int w, int h) {
    return String.format("<?xml version='1.0' encoding='UTF-8' ?>" + "\n" +
        "<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='%d' height='%d'>\n\t<g>" + "\n", w, h);
  }

  public String closeSVG() {
    return "\t</g>\n</svg>";
  }

  public String drawLine(int x1, int y1, int x2, int y2, String color) {
    return String.format("\t\t<line x1='%d' y1='%d' x2='%d' y2='%d'" +
            " stroke='%s' stroke-width='3'/>" + "\n", x1, y1, x2, y2, color);
  }

  public String drawCircle(int x, int y, int r, String stroke, String fill) {
    return String.format("\t\t<circle cx='%d' cy='%d' r='%d' stroke='%s' stroke-width='3' fill='%s'/>" + "\n",
            x, y, r, stroke, fill);
  }

  public String drawCell(int x, int y, boolean d, boolean l, boolean u, boolean r) {
    return (d ? drawLine(x - 10, y + 10, x + 10, y + 10, "black") : "\n") +
            (l ? drawLine(x - 10, y - 10, x - 10, y + 10, "black") : "\n") +
            (u ? drawLine(x - 10, y - 10, x + 10, y - 10, "black") : "\n") +
            (r ? drawLine(x + 10, y - 10, x + 10, y + 10, "black") : "\n");
  }
}
