package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>>
    implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
          return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
          if (!hasNext()) throw new NoSuchElementException("No hay elemento siguiente.");
          return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T  extends Comparable<T>>
        implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
          this.elemento = elemento;
          indice = -1;
        }

        /* Regresa el índice. */
        @Override public int getIndice() {
          return indice;
        }

        /* Define el índice. */
        @Override public void setIndice(int indice) {
          this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override public int compareTo(Adaptador<T> adaptador) {
          return elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /* Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo
       Java implementa sus genéricos; de otra forma obtenemos advertencias del
       compilador. */
    @SuppressWarnings("unchecked") private T[] nuevoArreglo(int n) {
      return (T[])(new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar {@link
     * #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
      arbol = nuevoArreglo(100);
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
      this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos
     * uno por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
      arbol = nuevoArreglo(n);
      for (T e : iterable) agregaIndex(e, elementos++);
      for (int i = (n / 2) - 1; i >= 0; i--) acomodarAbajo(i);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override public void agrega(T elemento) {
      if (arbol.length == elementos) {
        T[] nuevo = nuevoArreglo(elementos * 2);
        for (int i = 0; i < elementos; i++) nuevo[i] = arbol[i];
        arbol = nuevo;
      }

      agregaIndex(elemento, elementos++);
      acomodarArriba(elementos - 1);
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override public T elimina() {
      if (elementos == 0) throw new IllegalStateException("El montículo es vacio.");
      T t = arbol[0];
      intercambia(0, elementos - 1);
      arbol[elementos-- - 1].setIndice(-1);
      acomodarAbajo(0);
      return t;
    }

    /**
     * Elimina un elemento del montículo.
     * @param elemento a eliminar del montículo.
     */
    @Override public void elimina(T elemento) {
      int i = elemento.getIndice();
      if (i >= elementos || i < 0) return;
      intercambia(i, elementos - 1);
      arbol[elementos-- - 1].setIndice(-1);
      if (i < elementos) reordena(arbol[i]);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
      int i = elemento.getIndice();
      if (i >= elementos || i < 0) return false;
      return arbol[i].equals(elemento);
    }

    /**
     * Nos dice si el montículo es vacío.
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean esVacia() {
      return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override public void limpia() {
      elementos = 0;
    }

   /**
     * Reordena un elemento en el árbol.
     * @param elemento el elemento que hay que reordenar.
     */
    @Override public void reordena(T elemento) {
      int i = elemento.getIndice();
      acomodarArriba(i);
      acomodarAbajo(i);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * @return el número de elementos en el montículo mínimo.
     */
    @Override public int getElementos() {
      return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual
     *         que el número de elementos.
     */
    @Override public T get(int i) {
      if (i >= elementos || i < 0) throw new NoSuchElementException("No existe el elemento con indice " + i);
      return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * @return una representación en cadena del montículo mínimo.
     */
    @Override public String toString() {
      String s = "";
      for (T e : arbol) s += e + ", ";
      return s;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo
     *         igual al que llama el método; <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
      if (objeto == null || getClass() != objeto.getClass())
          return false;
      @SuppressWarnings("unchecked") MonticuloMinimo<T> monticulo =
          (MonticuloMinimo<T>)objeto;
      
      if (monticulo.elementos != elementos) return false;
      for (int i = 0; i < elementos; i++)
        if (!arbol[i].equals(monticulo.arbol[i])) return false;
      return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se
     * itera en orden BFS.
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * @param <T> tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>>
    Lista<T> heapSort(Coleccion<T> coleccion) {
      Lista<Adaptador<T>> l1 = new Lista<>();
      Lista<T> l2 = new Lista<>();

      for (T e : coleccion) l1.agrega(new Adaptador<T>(e));
      MonticuloMinimo<Adaptador<T>> monticulo = new MonticuloMinimo<>(l1);
      while (!monticulo.esVacia()) {
        Adaptador<T> del = monticulo.elimina();
        l2.agrega(del.elemento);
      }
      return l2;
    }

    private void intercambia(int i, int j) {
      T t = arbol[i];
      arbol[i] = arbol[j];
      arbol[j] = t;
      arbol[i].setIndice(i);
      arbol[j].setIndice(j);
    }
    
    private void acomodarArriba(int i) {
      int p = (i - 1) / 2;

      if (i > 0 && arbol[i].compareTo(arbol[p]) < 0) {
        intercambia(i, p);
        acomodarArriba(p);
      }
    }

    private void acomodarAbajo(int i) {
      int hi = 2 * i + 1;
      int hd = 2 * i + 2;
      int m = i;

      if (hd < elementos && arbol[hd].compareTo(arbol[m]) < 0) m = hd;
      if (hi < elementos && arbol[hi].compareTo(arbol[m]) < 0) m = hi;
      if (m != i) {
        intercambia(i, m);
        acomodarAbajo(m);
      }
    }

    private void agregaIndex(T e, int i) {
      arbol[i] = e;
      e.setIndice(i);
    }
}
