package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

  /* Clase interna privada para iteradores. */
  private class Iterador implements Iterator<T> {

    /* Iterador auxiliar. */
    private Iterator<Vertice> iterador;

    /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
    public Iterador() {
      iterador = vertices.iterator();
    }

    /* Nos dice si hay un siguiente elemento. */
    @Override public boolean hasNext() {
      return iterador.hasNext();
    }

    /* Regresa el siguiente elemento. */
    @Override public T next() {
      return iterador.next().elemento;
    }
  }

  /* Clase interna privada para vértices. */
  private class Vertice implements VerticeGrafica<T> {

    /* El elemento del vértice. */
    private T elemento;
    /* El color del vértice. */
    private Color color;
    /* La lista de vecinos del vértice. */
    private Lista<Vertice> vecinos;

    /* Crea un nuevo vértice a partir de un elemento. */
    public Vertice(T elemento) {
      this.elemento = elemento;
      color = Color.NINGUNO;
      vecinos = new Lista<>();
    }

    /* Regresa el elemento del vértice. */
    @Override public T get() {
      return elemento;
    }

    /* Regresa el grado del vértice. */
    @Override public int getGrado() {
      return vecinos.getLongitud();
    }

    /* Regresa el color del vértice. */
    @Override public Color getColor() {
      return color;
    }

    /* Regresa un iterable para los vecinos. */
    @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
      return vecinos;
    }
  }

  /* Vértices. */
  private Lista<Vertice> vertices;
  /* Número de aristas. */
  private int aristas;

  /**
   * Constructor único.
   */
  public Grafica() {
    vertices = new Lista<>();
  }

  /**
   * Regresa el número de elementos en la gráfica. El número de elementos es
   * igual al número de vértices.
   * @return el número de elementos en la gráfica.
   */
  @Override public int getElementos() {
    return vertices.getLongitud();
  }

  /**
   * Regresa el número de aristas.
   * @return el número de aristas.
   */
  public int getAristas() {
    return aristas;
  }

  /**
   * Agrega un nuevo elemento a la gráfica.
   * @param elemento el elemento a agregar.
   * @throws IllegalArgumentException si el elemento ya había sido agregado a
   *         la gráfica.
   */
  @Override public void agrega(T elemento) {
    if (elemento == null) throw new IllegalArgumentException("El elemento es nulo.");
    if (contiene(elemento)) throw new IllegalArgumentException("El elemento ya está en los vértices.");
    Vertice v = new Vertice(elemento);
    vertices.agrega(v);
  }

  /**
   * Conecta dos elementos de la gráfica. Los elementos deben estar en la
   * gráfica. El peso de la arista que conecte a los elementos será 1.
   * @param a el primer elemento a conectar.
   * @param b el segundo elemento a conectar.
   * @throws NoSuchElementException si a o b no son elementos de la gráfica.
   * @throws IllegalArgumentException si a o b ya están conectados, o si a es
   *         igual a b.
   */
  public void conecta(T a, T b) {
    if (a.equals(b)) throw new IllegalArgumentException("No se pueden conectar elementos iguales.");
    if (sonVecinos(a,b)) throw new IllegalArgumentException("Los elementos ya se encuentran conectados.");
    Vertice vA = (Vertice) vertice(a); 
    Vertice vB = (Vertice) vertice(b);

    vA.vecinos.agrega(vB);
    vB.vecinos.agrega(vA);
    aristas++;
  }

  /**
   * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
   * gráfica y estar conectados entre ellos.
   * @param a el primer elemento a desconectar.
   * @param b el segundo elemento a desconectar.
   * @throws NoSuchElementException si a o b no son elementos de la gráfica.
   * @throws IllegalArgumentException si a o b no están conectados.
   */
  public void desconecta(T a, T b) {
    if (!sonVecinos(a,b)) throw new IllegalArgumentException("Los elementos no están conectados.");
    Vertice vA = (Vertice) vertice(a);
    Vertice vB = (Vertice) vertice(b);

    vA.vecinos.elimina(vB);
    vB.vecinos.elimina(vA);
    aristas--;
  }

  /**
   * Nos dice si el elemento está contenido en la gráfica.
   * @return <code>true</code> si el elemento está contenido en la gráfica,
   *         <code>false</code> en otro caso.
   */
  @Override public boolean contiene(T elemento) {
    for (Vertice v : vertices)
      if (v.elemento.equals(elemento)) return true;
    return false;
  }

  /**
   * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
   * en la gráfica.
   * @param elemento el elemento a eliminar.
   * @throws NoSuchElementException si el elemento no está contenido en la
   *         gráfica.
   */
  @Override public void elimina(T elemento) {
    Vertice v = (Vertice) vertice(elemento);
    for (Vertice n : v.vecinos) desconecta(v.elemento, n.elemento);
    vertices.elimina(v);
  }

  /**
   * Nos dice si dos elementos de la gráfica están conectados. Los elementos
   * deben estar en la gráfica.
   * @param a el primer elemento.
   * @param b el segundo elemento.
   * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
   * @throws NoSuchElementException si a o b no son elementos de la gráfica.
   */
  public boolean sonVecinos(T a, T b) {
    Vertice vA = (Vertice) vertice(a);
    Vertice vB = (Vertice) vertice(b);
    return vA.vecinos.contiene(vB) && vB.vecinos.contiene(vA);
  }

  /**
   * Regresa el vértice correspondiente el elemento recibido.
   * @param elemento el elemento del que queremos el vértice.
   * @throws NoSuchElementException si elemento no es elemento de la gráfica.
   * @return el vértice correspondiente el elemento recibido.
   */
  public VerticeGrafica<T> vertice(T elemento) {
    for (Vertice v : vertices)
      if (v.elemento.equals(elemento)) return v;

    throw new NoSuchElementException("El elemento no está en la gráfica.");
  }

  /**
   * Define el color del vértice recibido.
   * @param vertice el vértice al que queremos definirle el color.
   * @param color el nuevo color del vértice.
   * @throws IllegalArgumentException si el vértice no es válido.
   */
  public void setColor(VerticeGrafica<T> vertice, Color color) {
    if (vertice.getClass() != Vertice.class) 
      throw new IllegalArgumentException("El vértice no es instancia de Vertice.");
    Vertice vAux = (Vertice) vertice;
    vAux.color = color;
  }

  /**
   * Nos dice si la gráfica es conexa.
   * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
   *         otro caso.
   */
  public boolean esConexa() {
   recorrer(vertices.getPrimero().elemento, e -> {}, new Cola<>());
   for (Vertice v : vertices)
     if (v.color == Color.ROJO) return false;
   return true;
  }

  /**
   * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
   * el orden en que fueron agregados.
   * @param accion la acción a realizar.
   */
  public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
    for (Vertice v : vertices) accion.actua(v);
  }

  /**
   * Realiza la acción recibida en todos los vértices de la gráfica, en el
   * orden determinado por BFS, comenzando por el vértice correspondiente al
   * elemento recibido. Al terminar el método, todos los vértices tendrán
   * color {@link Color#NINGUNO}.
   * @param elemento el elemento sobre cuyo vértice queremos comenzar el
   *        recorrido.
   * @param accion la acción a realizar.
   * @throws NoSuchElementException si el elemento no está en la gráfica.
   */
  public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
    recorrer(elemento, accion, new Cola<>());
    paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
  }

  /**
   * Realiza la acción recibida en todos los vértices de la gráfica, en el
   * orden determinado por DFS, comenzando por el vértice correspondiente al
   * elemento recibido. Al terminar el método, todos los vértices tendrán
   * color {@link Color#NINGUNO}.
   * @param elemento el elemento sobre cuyo vértice queremos comenzar el
   *        recorrido.
   * @param accion la acción a realizar.
   * @throws NoSuchElementException si el elemento no está en la gráfica.
   */
  public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
    recorrer(elemento, accion, new Pila<>());
    paraCadaVertice((v) ->setColor(v, Color.NINGUNO));
  }

  /**
   * Nos dice si la gráfica es vacía.
   * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
   *         otro caso.
   */
  @Override public boolean esVacia() {
    return vertices.esVacia(); 
  }

  /**
   * Limpia la gráfica de vértices y aristas, dejándola vacía.
   */
  @Override public void limpia() {
    vertices.limpia();
    aristas = 0;
  }

  /**
   * Regresa una representación en cadena de la gráfica.
   * @return una representación en cadena de la gráfica.
   */
  @Override public String toString() {
    String s = "{";
    for (Vertice v : vertices) s += v.elemento + ", ";
    s += "}, {";

    Lista<T> agregados = new Lista<>();
    for (Vertice v : vertices) {
      for (Vertice n : v.vecinos)
        if (!agregados.contiene(n.elemento))
          s += "(" + v.elemento + ", " + n.elemento + "), ";
      agregados.agrega(v.elemento);
    }
    s += "}";
    return s;
  }

  /**
   * Nos dice si la gráfica es igual al objeto recibido.
   * @param objeto el objeto con el que hay que comparar.
   * @return <code>true</code> si la gráfica es igual al objeto recibido;
   *         <code>false</code> en otro caso.
   */
  @Override public boolean equals(Object objeto) {
    if (objeto == null || getClass() != objeto.getClass())
        return false;
    @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
    if (aristas != grafica.aristas || 
        vertices.getLongitud() != grafica.vertices.getLongitud()) 
      return false;

    for (Vertice v : vertices)
      for (Vertice e : vertices)
        if(v.elemento != e.elemento && sonVecinos(v.elemento, e.elemento) &&
           !grafica.sonVecinos(v.elemento, e.elemento))
          return false;
    return true;
  }

  /**
   * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
   * orden en que fueron agregados sus elementos.
   * @return un iterador para iterar la gráfica.
   */
  @Override public Iterator<T> iterator() {
      return new Iterador();
  }

  private void recorrer(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Vertice> i) {
    Vertice v = (Vertice) vertice(elemento);
    paraCadaVertice((e) -> setColor(e, Color.ROJO));
    v.color = Color.NEGRO;
    i.mete(v);

    while(!i.esVacia()) {
      v = i.saca();
      accion.actua(v);
      for (Vertice n : v.vecinos) {
        if (n.color == Color.ROJO) {
          n.color = Color.NEGRO;
          i.mete(n);
        }
      }
    }
  }
}
