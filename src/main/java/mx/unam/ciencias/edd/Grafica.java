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
  private class Vertice implements VerticeGrafica<T>,
          ComparableIndexable<Vertice> {

    /* El elemento del vértice. */
    private T elemento;
    /* El color del vértice. */
    private Color color;
    /* La distancia del vértice. */
    private double distancia;
    /* El índice del vértice. */
    private int indice;
    /* El diccionario de vecinos del vértice. */
    private Diccionario<T, Vecino> vecinos;

    /* Crea un nuevo vértice a partir de un elemento. */
    public Vertice(T elemento) {
      this.elemento = elemento;
      color = Color.NINGUNO;
      vecinos = new Diccionario<>();
    }

    /* Regresa el elemento del vértice. */
    @Override public T get() {
      return elemento;
    }

    /* Regresa el grado del vértice. */
    @Override public int getGrado() {
      return vecinos.getElementos();
    }

    /* Regresa el color del vértice. */
    @Override public Color getColor() {
      return color;
    }

    /* Regresa un iterable para los vecinos. */
    @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
      return vecinos;
    }

    /* Define el índice del vértice. */
    @Override public void setIndice(int indice) {
      this.indice = indice;
    }

    /* Regresa el índice del vértice. */
    @Override public int getIndice() {
      return indice;
    }

    /* Compara dos vértices por distancia. */
    @Override public int compareTo(Vertice vertice) {
      return Double.compare(distancia, vertice.distancia);
    }
  }

  /* Clase interna privada para vértices vecinos. */
  private class Vecino implements VerticeGrafica<T> {

    /* El vértice vecino. */
    public Vertice vecino;
    /* El peso de la arista conectando al vértice con su vértice vecino. */
    public double peso;

    /* Construye un nuevo vecino con el vértice recibido como vecino y el
     * peso especificado. */
    public Vecino(Vertice vecino, double peso) {
      this.vecino =  vecino;
      this.peso = peso;
    }

    /* Regresa el elemento del vecino. */
    @Override public T get() {
      return vecino.elemento;
    }

    /* Regresa el grado del vecino. */
    @Override public int getGrado() {
      return vecino.getGrado();
    }

    /* Regresa el color del vecino. */
    @Override public Color getColor() {
      return vecino.color;
    }

    /* Regresa un iterable para los vecinos del vecino. */
    @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
      return vecino.vecinos;
    }
  }

  /* Interface para poder usar lambdas al buscar el elemento que sigue al
   * reconstruir un camino. */
  @FunctionalInterface
  private interface BuscadorCamino<T> {
    /* Regresa true si el vértice se sigue del vecino. */
    public boolean seSiguen(Grafica<T>.Vertice v, Grafica<T>.Vecino a);
  }

  /* Vértices. */
  private Diccionario<T, Vertice> vertices;
  /* Número de aristas. */
  private int aristas;

  /**
   * Constructor único.
   */
  public Grafica() {
    vertices = new Diccionario<>();
  }

  /**
   * Regresa el número de elementos en la gráfica. El número de elementos es
   * igual al número de vértices.
   * @return el número de elementos en la gráfica.
   */
  @Override public int getElementos() {
    return vertices.getElementos();
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
    vertices.agrega(elemento, v);
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
    conecta(a, b, 1);
  }

  /**
   * Conecta dos elementos de la gráfica. Los elementos deben estar en la
   * gráfica.
   * @param a el primer elemento a conectar.
   * @param b el segundo elemento a conectar.
   * @param peso el peso de la nueva vecino.
   * @throws NoSuchElementException si a o b no son elementos de la gráfica.
   * @throws IllegalArgumentException si a o b ya están conectados, si a es
   *         igual a b, o si el peso es no positivo.
   */
  public void conecta(T a, T b, double peso) {
    if (a.equals(b)) throw new IllegalArgumentException("No se pueden conectar elementos iguales.");
    if (sonVecinos(a,b)) throw new IllegalArgumentException("Los elementos ya se encuentran conectados.");
    if (peso <= 0) throw new IllegalArgumentException("El peso no puede ser negativo.");
    Vertice vA = (Vertice) vertice(a); 
    Vertice vB = (Vertice) vertice(b);

    vA.vecinos.agrega(b, new Vecino(vB, peso));
    vB.vecinos.agrega(a, new Vecino(vA, peso));
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

    vA.vecinos.elimina(b);
    vB.vecinos.elimina(a);
    aristas--;
  }

  /**
   * Nos dice si el elemento está contenido en la gráfica.
   * @return <code>true</code> si el elemento está contenido en la gráfica,
   *         <code>false</code> en otro caso.
   */
  @Override public boolean contiene(T elemento) {
    return vertices.contiene(elemento);
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
    for (Vecino n : v.vecinos) desconecta(v.elemento, n.vecino.elemento);
    vertices.elimina(elemento);
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

    return vA.vecinos.contiene(b);
  }

  /**
   * Regresa el peso de la arista que comparten los vértices que contienen a
   * los elementos recibidos.
   * @param a el primer elemento.
   * @param b el segundo elemento.
   * @return el peso de la arista que comparten los vértices que contienen a
   *         los elementos recibidos.
   * @throws NoSuchElementException si a o b no son elementos de la gráfica.
   * @throws IllegalArgumentException si a o b no están conectados.
   */
  public double getPeso(T a, T b) {
    if (!contiene(b)) throw new NoSuchElementException("El elemento no está en la gráfica.");
    if (!sonVecinos(a, b)) throw new IllegalArgumentException("Los vértices no están conectados.");
    Vertice v = (Vertice) vertice(a);
    return v.vecinos.get(b).peso;
  }

  /**
   * Define el peso de la arista que comparten los vértices que contienen a
   * los elementos recibidos.
   * @param a el primer elemento.
   * @param b el segundo elemento.
   * @param peso el nuevo peso de la arista que comparten los vértices que
   *        contienen a los elementos recibidos.
   * @throws NoSuchElementException si a o b no son elementos de la gráfica.
   * @throws IllegalArgumentException si a o b no están conectados, o si peso
   *         es menor o igual que cero.
   */
  public void setPeso(T a, T b, double peso) {
    Vertice vA = (Vertice) vertice(a);
    Vertice vB = (Vertice) vertice(b);
    if (!sonVecinos(vA.elemento, vB.elemento)) throw new IllegalArgumentException("Los elementos no se encuentran conectados.");
    if (peso <= 0) throw new IllegalArgumentException("El peso no puede ser negativo.");
    vA.vecinos.get(b).peso = peso;
    vB.vecinos.get(a).peso = peso;
  }

  /**
   * Regresa el vértice correspondiente el elemento recibido.
   * @param elemento el elemento del que queremos el vértice.
   * @throws NoSuchElementException si elemento no es elemento de la gráfica.
   * @return el vértice correspondiente el elemento recibido.
   */
  public VerticeGrafica<T> vertice(T elemento) {
    if (!vertices.contiene(elemento)) throw new NoSuchElementException("El elemento no se encuentra en la gráfica.");
    return vertices.get(elemento);
  }

  /**
   * Define el color del vértice recibido.
   * @param vertice el vértice al que queremos definirle el color.
   * @param color el nuevo color del vértice.
   * @throws IllegalArgumentException si el vértice no es válido.
   */
  public void setColor(VerticeGrafica<T> vertice, Color color) {
    if (vertice == null || 
      (vertice.getClass() != Vertice.class && 
      vertice.getClass() != Vecino.class)) 
      throw new IllegalArgumentException("Vértice Inválido.");
    
    if (vertice.getClass() == Vertice.class) {
      Vertice v = (Vertice) vertice;
      v.color = color;
    }

    if (vertice.getClass() == Vecino.class) {
      Vecino v = (Vecino) vertice;
      v.vecino.color = color;
    }
  }

  /**
   * Nos dice si la gráfica es conexa.
   * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
   *         otro caso.
   */
  public boolean esConexa() {
    for (Vertice v : vertices) { recorrer(v.elemento, e -> {}, new Cola<>()); break; }
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
      for (Vecino n : v.vecinos)
        if (!agregados.contiene(n.vecino.elemento))
          s += "(" + v.elemento + ", " + n.vecino.elemento + "), ";
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
      vertices.getElementos() != grafica.vertices.getElementos()) 
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

  /**
   * Calcula una trayectoria de distancia mínima entre dos vértices.
   * @param origen el vértice de origen.
   * @param destino el vértice de destino.
   * @return Una lista con vértices de la gráfica, tal que forman una
   *         trayectoria de distancia mínima entre los vértices <code>a</code> y
   *         <code>b</code>. Si los elementos se encuentran en componentes conexos
   *         distintos, el algoritmo regresa una lista vacía.
   * @throws NoSuchElementException si alguno de los dos elementos no está en
   *         la gráfica.
   */
  public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
    if (!contiene(destino) || !contiene(origen)) throw new NoSuchElementException("Los elementos no son parte de la gráfica.");

    Vertice v = (Vertice) vertice(origen);
    if (origen.equals(destino)) {
      Lista<VerticeGrafica<T>> l = new Lista<>();
      l.agrega(v);
      return l;
    }

    paraCadaVertice((va) -> setDistancia((Vertice) va, Double.MAX_VALUE));
    v.distancia = 0;

    Cola<Vertice> i = new Cola<>();
    i.mete(v);

    while (!i.esVacia()) {
      v = i.saca();
      for (Vecino n : v.vecinos)
        if (n.vecino.distancia == Double.MAX_VALUE) {
          setDistancia((Vertice) n.vecino, v.distancia + 1);
          i.mete(n.vecino);
        }
    }
    
    return reconstruyeTrayectoria(
            (a, n) -> n.vecino.distancia == a.distancia - 1,
            (Vertice) vertice(destino));
  }

  /**
   * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
   * de destino.
   * @param origen el vértice origen.
   * @param destino el vértice destino.
   * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
   *         el vértice <code>destino</code>. Si los vértices están en componentes
   *         conexas distintas, regresa una lista vacía.
   * @throws NoSuchElementException si alguno de los dos elementos no está en
   *         la gráfica.
   */
  public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
    if (!contiene(destino) || !contiene(origen)) throw new NoSuchElementException("Los elementos no son parte de la gráfica.");
    
    paraCadaVertice((v) -> setDistancia((Vertice) v, Double.MAX_VALUE));
    Vertice vo = (Vertice) vertice(origen);
    vo.distancia = 0;

    MonticuloDijkstra<Vertice> i;
    if (aristas > ((vertices.getElementos()*(vertices.getElementos() - 1))/2) - vertices.getElementos()) i = new MonticuloArreglo<>(vertices, vertices.getElementos());
    else i = new MonticuloMinimo<>(vertices, vertices.getElementos());

    while(!i.esVacia()) {
      Vertice v = i.elimina();
      for (Vecino n : v.vecinos)
        if (n.vecino.distancia > v.distancia + n.peso) {
          setDistancia((Vertice) n.vecino, v.distancia + n.peso);
          i.reordena(n.vecino); 
        }
    }

    return reconstruyeTrayectoria(
            (v, n) -> n.vecino.distancia + n.peso == v.distancia,
            (Vertice) vertice(destino));
  }

  /**
   * Reconstruye la trayectoria desde el vértice destino hasta
   * el vertice de origen.
   * @param buscador nos indica si dos vertices son vecinos.
   * @param destino el vértice desde el cuál se reconstruye la tractoria.
   * @return la trayectoria en forma de lista.
   */
  private Lista<VerticeGrafica<T>> reconstruyeTrayectoria(BuscadorCamino<T> buscador, Vertice destino) {
    Vertice v = destino;
    if (v.distancia == Double.MAX_VALUE) return new Lista<VerticeGrafica<T>>();

    Lista<VerticeGrafica<T>> tray = new Lista<>();
    tray.agrega(v);
    while (v.distancia != 0) {
      for (Vecino n : v.vecinos) {
        if (buscador.seSiguen(v, n)) {
          tray.agrega(n.vecino);
          v = n.vecino;
          break;
        }
      }
    }
    return tray.reversa();
  }

  private void recorrer(T elemento, AccionVerticeGrafica<T> accion, MeteSaca<Vertice> i) {
    Vertice v = (Vertice) vertice(elemento);
    paraCadaVertice((e) -> setColor(e, Color.ROJO));
    v.color = Color.NEGRO;
    i.mete(v);

    while(!i.esVacia()) {
      v = i.saca();
      accion.actua(v);
      for (Vecino n : v.vecinos) {
        if (n.vecino.color == Color.ROJO) {
          n.vecino.color = Color.NEGRO;
          i.mete(n.vecino);
        }
      }
    }
  }

  private void setDistancia(Vertice v, Double distancia) {
    v.distancia = distancia;
  }
}
