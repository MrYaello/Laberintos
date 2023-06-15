package mx.unam.ciencias.edd.proyecto3;

/**
 * Clase que contiene métodos auxiliares para generar elementos SVG.
 *
 * @author Yael Lozano
 */
public class GrapherSVG {


  /**
   * Constructor sin parámetros.
   */
  public GrapherSVG() {}

  /**
   * Método que declara el formato del archivo SVG. Contiene declaración W3 para poder abrirlo en el navegador.
   *
   * @param w el ancho del lienzo
   * @param h el alto del lienzo
   * @return inicializador de lienzo
   */
  public String initSVG(int w, int h) {
    return String.format("<?xml version='1.0' encoding='UTF-8' ?>" + "\n" +
        "<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' width='%d' height='%d'>\n\t<g>" + "\n", w, h);
  }

  /**
   * Método que declara el fin del archivo SVG.
   *
   * @return remate del lienzo
   */
  public String closeSVG() {
    return "\t</g>\n</svg>";
  }

  /**
   * Método que dibuja una linea en SVG.
   *
   * @param x1    coordenada en x del primer punto
   * @param y1    coordenada en y del primer punto
   * @param x2    coordenada en x del segundo punto
   * @param y2    coordenada en y del segundo punto
   * @param color color de la linea
   * @param s     grosor de la linea
   * @return linea en formato SVG
   */
  public String drawLine(int x1, int y1, int x2, int y2, String color, int s) {
    return String.format("\t\t<line x1='%d' y1='%d' x2='%d' y2='%d'" +
            " stroke='%s' stroke-width='%d'/>" + "\n", x1, y1, x2, y2, color, s);
  }

  /**
   * Método que dibuja un círculo en SVG.
   *
   * @param x      coordenada para el centro en x
   * @param y      coordenada para el centro en y
   * @param r      radio del circulo
   * @param stroke color del borde del circulo
   * @param fill   color del relleno del circulo
   * @return circulo en formato SVG
   */
  public String drawCircle(int x, int y, int r, String stroke, String fill) {
    return String.format("\t\t<circle cx='%d' cy='%d' r='%d' stroke='%s' stroke-width='3' fill='%s'/>" + "\n",
            x, y, r, stroke, fill);
  }

  /**
   * Método que dibuja una celda en SVG.
   *
   * @param x coordenada para el centro en x
   * @param y coordenada para el centro en y
   * @param d ¿Hay pared abajo?
   * @param l ¿Hay pared izquierda?
   * @param u ¿Hay pared arriba?
   * @param r ¿Hay pared derecha?
   * @return celda en formato SVG
   */
  public String drawCell(int x, int y, boolean d, boolean l, boolean u, boolean r) {
    return (d ? drawLine(x - 10, y + 10, x + 10, y + 10, "black",3) : "") +
            (l ? drawLine(x - 10, y - 10, x - 10, y + 10, "black",3 ) : "") +
            (u ? drawLine(x - 10, y - 10, x + 10, y - 10, "black", 3) : "") +
            (r ? drawLine(x + 10, y - 10, x + 10, y + 10, "black", 3) : "");
  }
}
