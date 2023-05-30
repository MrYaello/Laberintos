package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

      /** El color del vértice. */
      public Color color;

      /**
       * Constructor único que recibe un elemento.
       * @param elemento el elemento del vértice.
       */
      public VerticeRojinegro(T elemento) {
        super(elemento);
        color = Color.NINGUNO;
      }

      /**
       * Regresa una representación en cadena del vértice rojinegro.
       * @return una representación en cadena del vértice rojinegro.
       */
      @Override public String toString() {
        return (getColor(this) == Color.NEGRO ? "N" : "R") + "{" + elemento + "}";
      }

      /**
       * Compara el vértice con otro objeto. La comparación es
       * <em>recursiva</em>.
       * @param objeto el objeto con el cual se comparará el vértice.
       * @return <code>true</code> si el objeto es instancia de la clase
       *         {@link VerticeRojinegro}, su elemento es igual al elemento de
       *         éste vértice, los descendientes de ambos son recursivamente
       *         iguales, y los colores son iguales; <code>false</code> en
       *         otro caso.
       */
      @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked")
            VerticeRojinegro vertice = (VerticeRojinegro)objeto;
        return color == vertice.color && super.equals(vertice); 
      }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
      return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
      return verticeRojinegro(vertice).color;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
      super.agrega(elemento);
      VerticeRojinegro v = verticeRojinegro(ultimoAgregado);
      v.color = Color.ROJO;
      rebalanceaAgrega(v);
    }

    private void rebalanceaAgrega(VerticeRojinegro v) {
      VerticeRojinegro p = getPadre(v);
      VerticeRojinegro t = getTio(v);
      VerticeRojinegro a = getAbuelo(v);
      VerticeRojinegro aux  = null;
      if (p == null) { v.color = Color.NEGRO; return; }
      if (color(p) == Color.NEGRO) return;
      if (color(t) == Color.ROJO) {
        t.color = Color.NEGRO;
        p.color = Color.NEGRO;
        a.color = Color.ROJO;
        rebalanceaAgrega(a);
        return;
      }
      
      if (estaCruzado(p, v)) {
        if (estaCruzadoIzquierda(p, v)) {
          super.giraIzquierda(p);
          aux = v;
          v = p;
          p = aux;
        }
        if (estaCruzadoDerecha(p, v)) {
          super.giraDerecha(p);
          aux = v;
          v = p;
          p = aux;
        }
      }
      p.color = Color.NEGRO;
      a.color = Color.ROJO;

      if (esDerecho(v)) super.giraIzquierda(a);
      if (esIzquierdo(v)) super.giraDerecha(a);
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
      VerticeRojinegro v = verticeRojinegro(busca(elemento));
      if (v == null) return;

      elementos--;
      if(v.izquierdo != null && v.derecho != null) v = verticeRojinegro(intercambiaEliminable(v));
      VerticeRojinegro h;
      VerticeRojinegro f = null;

      if (v.izquierdo == null && v.derecho == null) {
        f = verticeRojinegro(nuevoVertice(null));
        f.color = Color.NEGRO;
        f.padre = v;
        v.izquierdo = f;
        h = f;
      } 
      else h = verticeRojinegro(v.izquierdo != null ? v.izquierdo : v.derecho);

      eliminaVertice(v);

      if (color(h) == Color.ROJO || color(v) == Color.ROJO) h.color = Color.NEGRO;
      else rebalanceaElimina(h);

      if (f != null) eliminaVertice(f);
    }

    private void rebalanceaElimina(VerticeRojinegro v) {
      VerticeRojinegro p = getPadre(v);
      if (p == null) return;
      VerticeRojinegro b = getHermano(v);
      if (color(b) == Color.ROJO) {
        p.color = Color.ROJO;
        b.color = Color.NEGRO;

        if (esIzquierdo(v)) super.giraIzquierda(p);
        if (esDerecho(v)) super.giraDerecha(p);
      
         p = getPadre(v);
         b = getHermano(v);
      }

      VerticeRojinegro bi = verticeRojinegro(b.izquierdo);
      VerticeRojinegro bd = verticeRojinegro(b.derecho);

      if (color(p) == Color.NEGRO && color(b) == Color.NEGRO &&
          color(bi) == Color.NEGRO && color(bd) == Color.NEGRO) {
        b.color = Color.ROJO;
        rebalanceaElimina(p);
        return;
      }

      if (color(b) == Color.NEGRO && color(bi) == Color.NEGRO &&
          color(bd) == Color.NEGRO && color(p) == Color.ROJO) {
        b.color = Color.ROJO;
        p.color = Color.NEGRO;
        return;
      }

      if (esIzquierdo(v) && color(bi) == Color.ROJO && color(bd) == Color.NEGRO || 
          esDerecho(v) && color(bi) == Color.NEGRO && color(bd) == Color.ROJO) {
        b.color = Color.ROJO;
        if (color(bd) == Color.ROJO) bd.color = Color.NEGRO;
        if (color(bi) == Color.ROJO) bi.color = Color.NEGRO;
        
        if (esIzquierdo(v)) super.giraDerecha(b);
        if (esDerecho(v)) super.giraIzquierda(b);

        b = getHermano(v);
        bi = verticeRojinegro(b.izquierdo);
        bd = verticeRojinegro(b.derecho);
      }

      b.color = p.color;
      p.color = Color.NEGRO;

      if (esIzquierdo(v)) { bd.color = Color.NEGRO; super.giraIzquierda(p); }
      if (esDerecho(v)) { bi.color = Color.NEGRO; super.giraDerecha(p); }
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }

    private VerticeRojinegro verticeRojinegro(VerticeArbolBinario<T> vertice) {
      return (VerticeRojinegro) vertice;
    }

    private Color color(VerticeRojinegro vertice) {
      return vertice != null ? vertice.color : Color.NEGRO;
    }

    private VerticeRojinegro getPadre(VerticeRojinegro v) {
      if (v != null && v.hayPadre()) return (VerticeRojinegro) v.padre;
      return null;
    }

    private VerticeRojinegro getAbuelo(VerticeRojinegro v) {
      return getPadre(getPadre(v));
    }

    private VerticeRojinegro getHermano(VerticeRojinegro v) {
      if (v != null && v.hayPadre()) {
        if (esIzquierdo(v) && v.padre.hayDerecho()) return (VerticeRojinegro) v.padre.derecho;
        if (esDerecho(v) && v.padre.hayIzquierdo()) return (VerticeRojinegro) v.padre.izquierdo;
      }
      return null;
    }

    private VerticeRojinegro getTio(VerticeRojinegro v) {
      return getHermano(getPadre(v));
    }

    private boolean esIzquierdo(VerticeRojinegro v) {
      return v == v.padre.izquierdo;
    }

    private boolean esDerecho(VerticeRojinegro v) {
      return v == v.padre.derecho;
    }

    private boolean estaCruzado(VerticeRojinegro v1, VerticeRojinegro v2) {
      return estaCruzadoIzquierda(v1, v2) || estaCruzadoDerecha(v1, v2);
    }

    private boolean estaCruzadoIzquierda(VerticeRojinegro v1, VerticeRojinegro v2) {
      return esIzquierdo(v1) && esDerecho(v2);
    } 

    private boolean estaCruzadoDerecha(VerticeRojinegro v1, VerticeRojinegro v2) {
      return esDerecho(v1) && esIzquierdo(v2);
    }
}
