package mx.unam.ciencias.edd;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, mapeando un conjunto de <em>llaves</em> a una colección
 * de <em>valores</em>.
 */
public class Diccionario<K, V> implements Iterable<V> {

  /* Clase interna privada para entradas. */
  private class Entrada {

    /* La llave. */
    public K llave;
    /* El valor. */
    public V valor;

    /* Construye una nueva entrada. */
    public Entrada(K llave, V valor) {
      this.llave = llave;
      this.valor = valor;
    }
  }

  /* Clase interna privada para iteradores. */
  private class Iterador {

    /* En qué lista estamos. */
    private int indice;
    /* Iterador auxiliar. */
    private Iterator<Entrada> iterador;

    /* Construye un nuevo iterador, auxiliándose de las listas del
     * diccionario. */
    public Iterador() {
      indice = -1;
      moverIterador();
    }

    /* Nos dice si hay una siguiente entrada. */
    public boolean hasNext() {
      return iterador != null;
    }

    /* Regresa la siguiente entrada. */
    public Entrada siguiente() {
      if (!hasNext()) throw new NoSuchElementException("No hay siguiente elemento.");
      Entrada next = iterador.next();
      if (!iterador.hasNext()) moverIterador();
      return next;
    }

    private void moverIterador() {
      while (++indice < entradas.length)
        if (entradas[indice] != null) {
          iterador = entradas[indice].iterator();
          return;
        }
      iterador = null;
    }
  }

  /* Clase interna privada para iteradores de llaves. */
  private class IteradorLlaves extends Iterador
    implements Iterator<K> {

    /* Regresa el siguiente elemento. */
    @Override public K next() {
      return siguiente().llave;
    }
  }

  /* Clase interna privada para iteradores de valores. */
  private class IteradorValores extends Iterador
    implements Iterator<V> {

    /* Regresa el siguiente elemento. */
    @Override public V next() {
      return siguiente().valor;
    }
  }

  /** Máxima carga permitida por el diccionario. */
  public static final double MAXIMA_CARGA = 0.72;

  /* Capacidad mínima; decidida arbitrariamente a 2^6. */
  private static final int MINIMA_CAPACIDAD = 64;

  /* Dispersor. */
  private Dispersor<K> dispersor;
  /* Nuestro diccionario. */
  private Lista<Entrada>[] entradas;
  /* Número de valores. */
  private int elementos;

  /* Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo
     Java implementa sus genéricos; de otra forma obtenemos advertencias del
     compilador. */
  @SuppressWarnings("unchecked")
  private Lista<Entrada>[] nuevoArreglo(int n) {
    return (Lista<Entrada>[])Array.newInstance(Lista.class, n);
  }

  /**
   * Construye un diccionario con una capacidad inicial y dispersor
   * predeterminados.
   */
  public Diccionario() {
    this(MINIMA_CAPACIDAD, (K llave) -> llave.hashCode());
  }

  /**
   * Construye un diccionario con una capacidad inicial definida por el
   * usuario, y un dispersor predeterminado.
   * @param capacidad la capacidad a utilizar.
   */
  public Diccionario(int capacidad) {
    this(capacidad, (K llave) -> llave.hashCode());
  }

  /**
   * Construye un diccionario con una capacidad inicial predeterminada, y un
   * dispersor definido por el usuario.
   * @param dispersor el dispersor a utilizar.
   */
  public Diccionario(Dispersor<K> dispersor) {
    this(MINIMA_CAPACIDAD, dispersor);
  }

  /**
   * Construye un diccionario con una capacidad inicial y un método de
   * dispersor definidos por el usuario.
   * @param capacidad la capacidad inicial del diccionario.
   * @param dispersor el dispersor a utilizar.
   */
  public Diccionario(int capacidad, Dispersor<K> dispersor) {
    this.dispersor = dispersor;
    capacidad = capacidad < MINIMA_CAPACIDAD ? MINIMA_CAPACIDAD : capacidad;
    capacidad = (int) Math.pow(2, (int) Math.ceil(Math.log(capacidad * 2) / Math.log(2))); 
    this.entradas = nuevoArreglo(capacidad);
  }

  /**
   * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si
   * la llave ya había sido utilizada antes para agregar un valor, el
   * diccionario reemplaza ese valor con el recibido aquí.
   * @param llave la llave para agregar el valor.
   * @param valor el valor a agregar.
   * @throws IllegalArgumentException si la llave o el valor son nulos.
   */
  public void agrega(K llave, V valor) {
    if (llave == null || valor == null) throw new IllegalArgumentException("La llave y el valor no deben ser nulos.");
    int i = dispersor.dispersa(llave) & (entradas.length - 1);
    Entrada e = new Entrada(llave, valor);
    if (entradas[i] ==  null) entradas[i] = new Lista<Entrada>();
    Entrada c = encontrarEnLista(i, llave);
    if (c == null) {
      entradas[i].agrega(e);
      elementos++;
    } else c.valor = valor;

    if (carga() >= MAXIMA_CARGA) reordenar();
  }

  /**
   * Regresa el valor del diccionario asociado a la llave proporcionada.
   * @param llave la llave para buscar el valor.
   * @return el valor correspondiente a la llave.
   * @throws IllegalArgumentException si la llave es nula.
   * @throws NoSuchElementException si la llave no está en el diccionario.
   */
  public V get(K llave) {
    if (llave == null) throw new IllegalArgumentException("La llave no debe ser nula.");
    int i = dispersor.dispersa(llave) & (entradas.length - 1);
    Entrada e = encontrarEnLista(i, llave);
    if (e == null) throw new NoSuchElementException("No existe un elemento con esta llave.");
    return e.valor;
  }

  /**
   * Nos dice si una llave se encuentra en el diccionario.
   * @param llave la llave que queremos ver si está en el diccionario.
   * @return <code>true</code> si la llave está en el diccionario,
   *         <code>false</code> en otro caso.
   */
  public boolean contiene(K llave) {
    return llave != null && encontrarEnLista(dispersor.dispersa(llave) & (entradas.length - 1), llave) != null;
  }

  /**
   * Elimina el valor del diccionario asociado a la llave proporcionada.
   * @param llave la llave para buscar el valor a eliminar.
   * @throws IllegalArgumentException si la llave es nula.
   * @throws NoSuchElementException si la llave no se encuentra en
   *         el diccionario.
   */
  public void elimina(K llave) {
    if (llave == null) throw new IllegalArgumentException("La llave no debe ser nula.");
    int i = dispersor.dispersa(llave) & (entradas.length - 1);
    Entrada e = encontrarEnLista(i, llave);
    if (e == null) throw new NoSuchElementException("No existe un elemento con esta llave.");
    entradas[i].elimina(e);
    if (entradas[i].getLongitud() == 0) entradas[i] = null;
    elementos--;
  }

  /**
   * Nos dice cuántas colisiones hay en el diccionario.
   * @return cuántas colisiones hay en el diccionario.
   */
  public int colisiones() {
    int total = 0;
    for (Lista<Entrada> l : entradas) if (l != null) total += l.getLongitud();
    return total == 0 ? total : total - 1;
  }

  /**
   * Nos dice el máximo número de colisiones para una misma llave que tenemos
   * en el diccionario.
   * @return el máximo número de colisiones para una misma llave.
   */
  public int colisionMaxima() {
    int max = 0;
    for (Lista<Entrada> l : entradas) if (l != null && l.getLongitud() > max) max = l.getLongitud();
    return max - 1;
  }

  /**
   * Nos dice la carga del diccionario.
   * @return la carga del diccionario.
   */
  public double carga() {
    return (double) elementos / entradas.length;
  }

  /**
   * Regresa el número de entradas en el diccionario.
   * @return el número de entradas en el diccionario.
   */
  public int getElementos() {
    return elementos;
  }

  /**
   * Nos dice si el diccionario es vacío.
   * @return <code>true</code> si el diccionario es vacío, <code>false</code>
   *         en otro caso.
   */
  public boolean esVacia() {
    return elementos == 0;
  }

  /**
   * Limpia el diccionario de elementos, dejándolo vacío.
   */
  public void limpia() {
    elementos = 0;
    entradas = nuevoArreglo(entradas.length);
  }

  /**
   * Regresa una representación en cadena del diccionario.
   * @return una representación en cadena del diccionario.
   */
  @Override public String toString() {
    if (elementos == 0) return "{}";
    String s = "{ ";
    Iterador it = new Iterador();

    while (it.hasNext()) {
      Entrada e = it.siguiente();
      s += "\'" + e.llave + "\': \'" + e.valor + "\', ";
    }
    return s + "}";
  }

  /**
   * Nos dice si el diccionario es igual al objeto recibido.
   * @param o el objeto que queremos saber si es igual al diccionario.
   * @return <code>true</code> si el objeto recibido es instancia de
   *         Diccionario, y tiene las mismas llaves asociadas a los mismos
   *         valores.
   */
  @Override public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    @SuppressWarnings("unchecked") Diccionario<K, V> d = (Diccionario<K, V>) o;
    if (d.elementos != elementos) return false;
    Iterador it = new Iterador();
    while (it.hasNext()) {
      Entrada e = it.siguiente();
      if (!d.contiene(e.llave) || !d.get(e.llave).equals(e.valor)) return false;
    }
    return true;
  }

  /**
   * Regresa un iterador para iterar las llaves del diccionario. El
   * diccionario se itera sin ningún orden específico.
   * @return un iterador para iterar las llaves del diccionario.
   */
  public Iterator<K> iteradorLlaves() {
    return new IteradorLlaves();
  }

  /**
   * Regresa un iterador para iterar los valores del diccionario. El
   * diccionario se itera sin ningún orden específico.
   * @return un iterador para iterar los valores del diccionario.
   */
  @Override public Iterator<V> iterator() {
    return new IteradorValores();
  }

  private Entrada encontrarEnLista(int i, K key) {
    if (entradas[i] == null) return null;
    for (Entrada e : entradas[i]) if (e.llave.equals(key)) return e;
    return null;
  }

  private void reordenar() {
    Lista<Entrada>[] n = nuevoArreglo(entradas.length * 2);
    Iterador it = new Iterador();
    while (it.hasNext()) {
      Entrada e = it.siguiente();
      int i = dispersor.dispersa(e.llave) & (n.length - 1);
      if (n[i] == null) n[i] = new Lista<Entrada>();
      n[i].agrega(e);
    }
    entradas = n;
  }
}
