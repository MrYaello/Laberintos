package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.</p>
 *
 * <p>Un árbol instancia de esta clase siempre cumple que:</p>
 * <ul>
 *   <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 *       descendientes por la izquierda.</li>
 *   <li>Cualquier elemento en el árbol es menor o igual que todos sus
 *       descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>>
    extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        private Iterador() {
            pila = new Pila<Vertice>();
            Vertice actual = raiz;

            while (actual != null) { pila.mete(actual); actual = actual.izquierdo; }
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override public T next() {
            if (pila.esVacia()) throw new NoSuchElementException();
            Vertice actual = pila.saca();
            T elemento = actual.elemento;
            if (actual.hayDerecho()) {
                actual = actual.derecho;
                while (actual != null) { pila.mete(actual); actual = actual.izquierdo; }
            }
            return elemento;
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede
     * garantizar que existe <em>inmediatamente</em> después de haber agregado
     * un elemento al árbol. Si cualquier operación distinta a agregar sobre el
     * árbol se ejecuta después de haber agregado un elemento, el estado de esta
     * variable es indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() { super(); }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null) throw new IllegalArgumentException();
        Vertice v = nuevoVertice(elemento);
        elementos++;
        ultimoAgregado = v;

        if (raiz == null) raiz = v;
        else agrega(raiz, v);
    }

    private void agrega(Vertice actual, Vertice nuevo) {
        if (actual == null) throw new NoSuchElementException();
        if (actual.elemento.compareTo(nuevo.elemento) >= 0) {
            if (actual.izquierdo == null) {
                actual.izquierdo = nuevo;
                nuevo.padre = actual;
                return;
            }
            agrega(actual.izquierdo, nuevo);
        } else if (actual.elemento.compareTo(nuevo.elemento) < 0) {
            if (actual.derecho == null) {
                actual.derecho = nuevo;
                nuevo.padre = actual;
                return;
            }
            agrega(actual.derecho, nuevo);
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice v = vertice(busca(elemento));
        if (v == null) return;
        elimina(v); 
        elementos--;
    }

    private void elimina(Vertice v) {
        if (esHoja(v)) {
            if (v == raiz) raiz = null;
            else if (v.padre != null && v.padre.derecho == v) v.padre.derecho = null;
            else if (v.padre != null && v.padre.izquierdo == v) v.padre.izquierdo = null;
            return;
        }

        if (v.hayIzquierdo() && !v.hayDerecho() || !v.hayIzquierdo() && v.hayDerecho()) eliminaVertice(v);
        else {
            Vertice u = maxSubArbol(v.izquierdo);
            v.elemento = u.elemento;
            elimina(u);
        }
    }

    private boolean esHoja(Vertice v) {
        return v.izquierdo == null && v.derecho == null;
    }

    private Vertice maxSubArbol(Vertice v) {
        if (v.derecho == null) return v;
        return maxSubArbol(v.derecho);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más
     * un hijo.
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se
     *         intercambió. El vértice regresado tiene a lo más un hijo distinto
     *         de <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice vMax = maxSubArbol(vertice.izquierdo);
        T e = vMax.elemento;
        vMax.elemento = vertice.elemento;
        vertice.elemento = e;
        return vMax;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de
     * <code>null</code> subiendo ese hijo (si existe).
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo
     *                distinto de <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        Vertice h = vertice.izquierdo != null ? vertice.izquierdo : vertice.derecho;

        if (vertice.padre == null) raiz = h;
        else if (vertice.padre.izquierdo == vertice) vertice.padre.izquierdo = h;
        else vertice.padre.derecho = h;

        if (h != null) h.padre = vertice.padre;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo
     *         encuentra; <code>null</code> en otro caso.
     */
    @Override public VerticeArbolBinario<T> busca(T elemento) {
        return busca(raiz, elemento);
    }

    private VerticeArbolBinario<T> busca(Vertice actual, T  elemento) {
        if (actual == null) return null;
        if (actual.elemento.compareTo(elemento) == 0) return actual;
        else if (actual.elemento.compareTo(elemento) > 0) return busca(actual.izquierdo, elemento);
        else return busca(actual.derecho, elemento);
    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al
     * árbol. Este método sólo se puede garantizar que funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link
     * agrega}. Si cualquier operación distinta a agregar sobre el árbol se
     * ejecuta después de haber agregado un elemento, el comportamiento de este
     * método es indefinido.
     * @return el vértice que contiene el último elemento agregado al árbol, si
     *         el método es invocado inmediatamente después de agregar un
     *         elemento al árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no
     * tiene hijo izquierdo, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        if (esVacia() || vertice == null) return;
        Vertice q = vertice(vertice);

        if (!vertice.hayIzquierdo()) return;
        Vertice p = q.izquierdo;
        Vertice s = p.derecho;
        Vertice t = q.derecho;
        Vertice a = null;
        boolean b = false;

        if (q != raiz) a = q.padre;
        if (a != null && a.derecho == q) b = true;

        p.derecho = q;
        q.padre = p;
        q.izquierdo = s;
        q.derecho = t;

        if (s != null) s.padre = q;
        if (t != null) t.padre = q;
        if (a != null) {
            p.padre = a;
            if (b) a.derecho = p;
            else a.izquierdo = p;
        } else {
            p.padre = null;
            raiz = p;
        }
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        if (esVacia() || vertice == null) return;
        Vertice p = vertice(vertice);

        if (!vertice.hayDerecho()) return;
        Vertice q = p.derecho;
        Vertice s = q.izquierdo;
        Vertice t = q.derecho;
        Vertice a = null;
        boolean b = false;

        if (p != raiz) a = p.padre;
        if (a != null && a.izquierdo == p) b = true;

        q.izquierdo = p;
        p.padre = q;
        p.derecho = s;
        q.derecho = t;

        if (s != null) s.padre = p;
        if (t != null) t.padre = q;
        if (a != null) {
            q.padre = a;
            if (b) a.izquierdo = q;
            else a.derecho = q;
        } else {
            q.padre = null;
            raiz = q;
        }
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        if (raiz == null) return;
        dfsPreOrder(raiz, accion);
    }

    private void dfsPreOrder(VerticeArbolBinario<T> vertice, AccionVerticeArbolBinario<T> accion) {
        Pila<VerticeArbolBinario<T>> pila = new Pila<>();
        pila.mete(vertice);

        while (!pila.esVacia()) {
            vertice = pila.saca();
            accion.actua(vertice);

            if (vertice.hayDerecho()) pila.mete(vertice.derecho());
            if (vertice.hayIzquierdo()) pila.mete(vertice.izquierdo());
        }
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        if (raiz == null) return;
        dfsInOrder(raiz, accion);
    }

    private void dfsInOrder(VerticeArbolBinario<T> vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice != null) {
            if (vertice.hayIzquierdo()) dfsInOrder(vertice.izquierdo(), accion);
            accion.actua(vertice);
            if (vertice.hayDerecho()) dfsInOrder(vertice.derecho(), accion);
        }
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        if (raiz == null) return;
        dfsPostOrder(raiz, accion);
    }

    private void dfsPostOrder(VerticeArbolBinario<T> vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice != null) {
            if (vertice.hayIzquierdo()) dfsPostOrder(vertice.izquierdo(), accion);
            if (vertice.hayDerecho()) dfsPostOrder(vertice.derecho(), accion);
            accion.actua(vertice);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
