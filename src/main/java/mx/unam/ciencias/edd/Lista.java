package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase genérica para listas doblemente ligadas.</p>
 *
 * <p>Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.</p>
 *
 * <p>Las listas no aceptan a <code>null</code> como elemento.</p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

  /* Clase interna privada para nodos. */
  private class Nodo {
    /* El elemento del nodo. */
    private T elemento;
    /* El nodo anterior. */
    private Nodo anterior;
    /* El nodo siguiente. */
    private Nodo siguiente;

    /* Construye un nodo con un elemento. */
    private Nodo(T elemento) {
      this.elemento = elemento;
    }
  }

  /* Clase interna privada para iteradores. */
  private class Iterador implements IteradorLista<T> {
    /* El nodo anterior. */
    private Nodo anterior;
    /* El nodo siguiente. */
    private Nodo siguiente;

    /* Construye un nuevo iterador. */
    private Iterador() {
      anterior = null;
      siguiente = cabeza;
    }

    /* Nos dice si hay un elemento siguiente. */
    @Override public boolean hasNext() {
      return siguiente != null;
    }

    /* Nos da el elemento siguiente. */
    @Override public T next() {
      if (hasNext()) {
        anterior = siguiente;
        siguiente = siguiente.siguiente;
        return anterior.elemento;
      }
      throw new NoSuchElementException("No hay un elemento posterior.");
    }

    /* Nos dice si hay un elemento anterior. */
    @Override public boolean hasPrevious() {
      return anterior != null;
    }

    /* Nos da el elemento anterior. */
    @Override public T previous() {
      if (hasPrevious()) {
        siguiente = anterior;
        anterior = anterior.anterior;
        return siguiente.elemento;
      } 
      throw new NoSuchElementException("No hay un elemento anterior.");
    }

    /* Mueve el iterador al inicio de la lista. */
    @Override public void start() {
      anterior = null;
      siguiente = cabeza;
    }

    /* Mueve el iterador al final de la lista. */
    @Override public void end() {
      anterior = rabo;
      siguiente = null;
    }
  }

  /* Primer elemento de la lista. */
  private Nodo cabeza;
  /* Último elemento de la lista. */
  private Nodo rabo;
  /* Número de elementos en la lista. */
  private int longitud;

  /**
   * Regresa la longitud de la lista. El método es idéntico a {@link
   * #getElementos}.
   * @return la longitud de la lista, el número de elementos que contiene.
   */
  public int getLongitud() {
    return longitud;
  }

  /**
   * Regresa el número elementos en la lista. El método es idéntico a {@link
   * #getLongitud}.
   * @return el número elementos en la lista.
   */
  @Override public int getElementos() {
    return longitud;
  }

  /**
   * Nos dice si la lista es vacía.
   * @return <code>true</code> si la lista es vacía, <code>false</code> en
   *         otro caso.
   */
  @Override public boolean esVacia() {
    return longitud == 0;
  }

  /**
   * Agrega un elemento a la lista. Si la lista no tiene elementos, el
   * elemento a agregar será el primero y último. El método es idéntico a
   * {@link #agregaFinal}.
   * @param elemento el elemento a agregar.
   * @throws IllegalArgumentException si <code>elemento</code> es
   *         <code>null</code>.
   */
  @Override public void agrega(T elemento) {
    if (elemento == null) throw new IllegalArgumentException("Se esta intentando crear un Nodo con contenido Null.");
    Nodo n = new Nodo(elemento);
    longitud++;
    if (cabeza == null) {
      cabeza = n;
      rabo = n;
      return;
    }
    rabo.siguiente = n;
    n.anterior = rabo;
    rabo = n;
  }

  /**
   * Agrega un elemento al final de la lista. Si la lista no tiene elementos,
   * el elemento a agregar será el primero y último.
   * @param elemento el elemento a agregar.
   * @throws IllegalArgumentException si <code>elemento</code> es
   *         <code>null</code>.
   */
  public void agregaFinal(T elemento) {
    agrega(elemento); 
  }

  /**
   * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos,
   * el elemento a agregar será el primero y último.
   * @param elemento el elemento a agregar.
   * @throws IllegalArgumentException si <code>elemento</code> es
   *         <code>null</code>.
   */
  public void agregaInicio(T elemento) {
    if (elemento == null) throw new IllegalArgumentException("Se esta intentando crear un Nodo con contenido Null");
    Nodo n = new Nodo(elemento);
    longitud++;
    if (cabeza == null) {
      cabeza = n;
      rabo = n;
      return;
    } 
    cabeza.anterior = n;
    n.siguiente = cabeza;
    cabeza = n;
  }

  /**
   * Inserta un elemento en un índice explícito.
   *
   * Si el índice es menor o igual que cero, el elemento se agrega al inicio
   * de la lista. Si el índice es mayor o igual que el número de elementos en
   * la lista, el elemento se agrega al fina de la misma. En otro caso,
   * después de mandar llamar el método, el elemento tendrá el índice que se
   * especifica en la lista.
   * @param i el índice dónde insertar el elemento. Si es menor que 0 el
   *          elemento se agrega al inicio de la lista, y si es mayor o igual
   *          que el número de elementos en la lista se agrega al final.
   * @param elemento el elemento a insertar.
   * @throws IllegalArgumentException si <code>elemento</code> es
   *         <code>null</code>.
   */
  public void inserta(int i, T elemento) {
    if (elemento == null) throw new IllegalArgumentException("Se esta intentando crear un Nodo con contenido Null");  
    if (i <= 0) { agregaInicio(elemento); return; }
    if (i > longitud - 1) { agregaFinal(elemento); return; }
    Nodo n = new Nodo(elemento);
    Nodo s = buscaIndice(i);
    Nodo a = s.anterior;
    n.siguiente = s;
    n.anterior = a;
    a.siguiente = n;
    s.anterior = n;
    longitud++;
  }

  private Nodo buscaIndice(int i) {
    int j = i;
    Nodo n = cabeza;
    int r = 0;
    if (j < 0) j = 0;
    if (j > longitud - 1) j = longitud - 1;
    while (n != null) {
      if (r == i) return n;
      n = n.siguiente;
      r++;
    }
    return null;
  }

  /**
   * Elimina un elemento de la lista. Si el elemento no está contenido en la
   * lista, el método no la modifica.
   * @param elemento el elemento a eliminar.
   */
  @Override public void elimina(T elemento) { 
    Nodo n = buscaElemento(elemento);
    if (cabeza == null) return;
    if (cabeza == n) { eliminaPrimero(); return; }
    if (rabo == n) { eliminaUltimo(); return; }
    longitud--;
    if (cabeza == rabo) {
      cabeza = null;
      rabo = null;
      return;
    }
    n.anterior.siguiente = n.siguiente;
    n.siguiente.anterior = n.anterior;
  }

  private Nodo buscaElemento(T elemento) {
    Nodo n = cabeza;
    while(n != null) {
      if (n.elemento.equals(elemento)) return n;
      n = n.siguiente;
    }
    return null;
  }

  /**
   * Elimina el primer elemento de la lista y lo regresa.
   * @return el primer elemento de la lista antes de eliminarlo.
   * @throws NoSuchElementException si la lista es vacía.
   */
  public T eliminaPrimero() {
    if (esVacia()) throw new NoSuchElementException("La lista esta vacia.");
    Nodo n = cabeza;
    T elemento = n.elemento;
    longitud--;
    if (n.siguiente == null) {
      n = null;
      cabeza = null;
      rabo = null;
      return elemento;
    }
    cabeza = n.siguiente;
    cabeza.anterior = null;
    n = null;
    return elemento;
  }

  /**
   * Elimina el último elemento de la lista y lo regresa.
   * @return el último elemento de la lista antes de eliminarlo.
   * @throws NoSuchElementException si la lista es vacía.
   */
  public T eliminaUltimo() {
    if (esVacia()) throw new NoSuchElementException("La lista esta vacia.");
    Nodo n = rabo;
    T elemento = n.elemento;
    longitud--;
    if (n.anterior == null) {
      n = null;
      cabeza = null;
      rabo = null;
      return elemento;
    }
    rabo = n.anterior;
    rabo.siguiente = null;
    n = null;
    return elemento;
  }

  /**
   * Nos dice si un elemento está en la lista.
   * @param elemento el elemento que queremos saber si está en la lista.
   * @return <code>true</code> si <code>elemento</code> está en la lista,
   *         <code>false</code> en otro caso.
   */
  @Override public boolean contiene(T elemento) {
    return buscaElemento(elemento) != null;
  }

  /**
   * Regresa la reversa de la lista.
   * @return una nueva lista que es la reversa la que manda llamar el método.
   */
  public Lista<T> reversa() {
    Lista<T> l = new Lista<T>();
    Nodo n = rabo;
    while (n != null) {
      l.agrega(n.elemento);
      n = n.anterior;
    }
    return l;
  }

  /**
   * Regresa una copia de la lista. La copia tiene los mismos elementos que la
   * lista que manda llamar el método, en el mismo orden.
   * @return una copia de la lista.
   */
  public Lista<T> copia() {
    Lista<T> l = new Lista<T>();
    Nodo n = cabeza;
    while(n != null) {
      l.agrega(n.elemento);
      n = n.siguiente;
    }
    return l;
  }

  /**
   * Limpia la lista de elementos, dejándola vacía.
   */
  @Override public void limpia() {
    for (int i = 0; i < longitud - 1; i++) {
      Nodo n = buscaIndice(i); 
      n = null;
    }
    cabeza = null;
    rabo =  null;
    longitud = 0; 
  }

  /**
   * Regresa el primer elemento de la lista.
   * @return el primer elemento de la lista.
   * @throws NoSuchElementException si la lista es vacía.
   */
  public T getPrimero() {
    if (longitud == 0) throw new NoSuchElementException("La lista esta vacia.");
    return cabeza.elemento;
  }

  /**
   * Regresa el último elemento de la lista.
   * @return el primer elemento de la lista.
   * @throws NoSuchElementException si la lista es vacía.
   */
  public T getUltimo() {
    if (longitud == 0) throw new NoSuchElementException("La lista esta vacia.");
    return rabo.elemento;
  }

  /**
   * Regresa el <em>i</em>-ésimo elemento de la lista.
   * @param i el índice del elemento que queremos.
   * @return el <em>i</em>-ésimo elemento de la lista.
   * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
   *         igual que el número de elementos en la lista.
   */
  public T get(int i) {
    if (i < 0 || i >= longitud) throw new ExcepcionIndiceInvalido("Indice invalido.");
    Nodo n = buscaIndice(i);
    return n.elemento;
  }

  /**
   * Regresa el índice del elemento recibido en la lista.
   * @param elemento el elemento del que se busca el índice.
   * @return el índice del elemento recibido en la lista, o -1 si el elemento
   *         no está contenido en la lista.
   */
  public int indiceDe(T elemento) {
    Nodo n = cabeza;
    int i = 0;
    while(n != null) {
      if (n.elemento.equals(elemento)) return i;
      n = n.siguiente;
      i++;
    }
    return -1;
  }

  /**
   * Regresa una representación en cadena de la lista.
   * @return una representación en cadena de la lista.
   */
  @Override public String toString() {
    if (longitud == 0) return "[]";
    String cadena = "[";
    Nodo n = cabeza;
    while(n != null) {
      cadena += n.elemento + ", ";
      n = n.siguiente;
    }
    cadena = cadena.substring(0, cadena.lastIndexOf(", ")) + "]";
    return cadena;
  }

  /**
   * Nos dice si la lista es igual al objeto recibido.
   * @param objeto el objeto con el que hay que comparar.
   * @return <code>true</code> si la lista es igual al objeto recibido;
   *         <code>false</code> en otro caso.
   */
  @Override public boolean equals(Object objeto) {
    if (objeto == null || getClass() != objeto.getClass())
        return false;
    @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;

    if (lista.longitud != longitud) return false;

    boolean equals = true;
    Nodo i = cabeza;
    Nodo j = lista.cabeza;
    while (i != null && j != null) {
      if (!i.elemento.equals(j.elemento)) { equals = false; break; }
      i = i.siguiente;
      j = j.siguiente;
    }
    return equals;
  }

  /**
   * Regresa un iterador para recorrer la lista en una dirección.
   * @return un iterador para recorrer la lista en una dirección.
   */
  @Override public Iterator<T> iterator() {
    return new Iterador();
  }

  /**
   * Regresa un iterador para recorrer la lista en ambas direcciones.
   * @return un iterador para recorrer la lista en ambas direcciones.
   */
  public IteradorLista<T> iteradorLista() {
    return new Iterador();
  } 

  /**
   * Regresa una copia de la lista, pero ordenada. Para poder hacer el
   * ordenamiento, el método necesita una instancia de {@link Comparator} para
   * poder comparar los elementos de la lista.
   * @param comparador el comparador que la lista usará para hacer el
   *                   ordenamiento.
   * @return una copia de la lista, pero ordenada.
   */
  public Lista<T> mergeSort(Comparator<T> comparador) {
    if (this.getLongitud() <= 1) return this;

    Lista<T> izquierda = new Lista<T>();
    Lista<T> derecha = new Lista<T>();

    int mid = this.getLongitud() / 2;
    Nodo n = this.cabeza;

    for (int i = 0; i < mid; i++) {
      izquierda.agrega(n.elemento);
      n = n.siguiente;
    }

    while (n != null) {
      derecha.agrega(n.elemento);
      n = n.siguiente;
    }

    izquierda = izquierda.mergeSort(comparador);
    derecha = derecha.mergeSort(comparador);

    return merge(izquierda, derecha, comparador);
  }

  private Lista<T> merge(Lista<T> izquierda, Lista <T> derecha, Comparator<T> c) {
    Lista<T> lista = new Lista<T>();
    Nodo i = izquierda.cabeza;
    Nodo j = derecha.cabeza;

    while (i != null && j != null) {
      if (c.compare(i.elemento, j.elemento) <= 0) {
        lista.agrega(i.elemento);
        i = i.siguiente;
      } else {
        lista.agrega(j.elemento);
        j = j.siguiente;
      }
    }

    while (i != null) { lista.agrega(i.elemento); i = i.siguiente; }
    while (j != null) { lista.agrega(j.elemento); j = j.siguiente; }
    return lista;
  }

  /**
   * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
   * tiene que contener nada más elementos que implementan la interfaz {@link
   * Comparable}.
   * @param <T> tipo del que puede ser la lista.
   * @param lista la lista que se ordenará.
   * @return una copia de la lista recibida, pero ordenada.
   */
  public static <T extends Comparable<T>>
  Lista<T> mergeSort(Lista<T> lista) {
    return lista.mergeSort((a, b) -> a.compareTo(b));
  }

  /**
   * Busca un elemento en la lista ordenada, usando el comparador recibido. El
   * método supone que la lista está ordenada usando el mismo comparador.
   * @param elemento el elemento a buscar.
   * @param comparador el comparador con el que la lista está ordenada.
   * @return <code>true</code> si el elemento está contenido en la lista,
   *         <code>false</code> en otro caso.
   */
  public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
    Nodo n = cabeza;
    if (comparador.compare(elemento, cabeza.elemento) < 0 
        || comparador.compare(elemento, rabo.elemento) > 0) return false;
    while (n != null) {
      if (comparador.compare(n.elemento, elemento) == 0) return true;
      n = n.siguiente;
    }
    return false;
  }

  /**
   * Busca un elemento en una lista ordenada. La lista recibida tiene que
   * contener nada más elementos que implementan la interfaz {@link
   * Comparable}, y se da por hecho que está ordenada.
   * @param <T> tipo del que puede ser la lista.
   * @param lista la lista donde se buscará.
   * @param elemento el elemento a buscar.
   * @return <code>true</code> si el elemento está contenido en la lista,
   *         <code>false</code> en otro caso.
   */
  public static <T extends Comparable<T>>
  boolean busquedaLineal(Lista<T> lista, T elemento) {
    return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
  }
}
