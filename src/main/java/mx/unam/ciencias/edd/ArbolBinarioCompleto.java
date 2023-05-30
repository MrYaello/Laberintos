package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Clase para árboles binarios completos.</p>
 *
 * <p>Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.</p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        private Iterador() {
            cola = new Cola<Vertice>();
            if (raiz != null) cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            Vertice siguiente = cola.saca();
            if (siguiente.izquierdo != null) cola.mete(siguiente.izquierdo);
            if (siguiente.derecho != null) cola.mete(siguiente.derecho);
            return siguiente.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() { super(); }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        binario completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca
     * a la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void agrega(T elemento) {
        if (elemento ==  null) throw new IllegalArgumentException();
        Vertice v = nuevoVertice(elemento);
        if (esVacia()) { raiz = v; elementos++; }
        else { 
            int a = pow(2, log2(elementos+1)); 
            agrega(raiz, null, a, a, elementos+1, v); 
        }
    }

    private int pow(int base, int exponente) {
        int res = 1;
        for (int i = 0; i < exponente; i++) res *= base;
        return res;
    }

    private int log2(int n) {
        int res = 0;
        while (n > 1) { res++; n /= 2; }
        return res;
    }

    private void agrega(Vertice v, Vertice ant, int menor, int nElementos, int num, Vertice w) {
        if (nElementos == 1) {
            elementos++;
            if (elementos % 2 == 0) {
                ant.izquierdo = w;
                w.padre = ant;
            } else {
                ant.derecho = w;
                w.padre = ant;
            }
        } else {
            int n = nElementos / 2;
            if (num <= menor + n - 1) agrega(v.izquierdo, v, menor, n, elementos+1, w);
            else agrega(v.derecho, v, menor + n, n, elementos+1, w);
        }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con
     * el último elemento del árbol al recorrerlo por BFS, y entonces es
     * eliminado.
     * @param elemento el elemento a eliminar.
     */
    @Override public void elimina(T elemento) {
        Vertice v = (Vertice) busca(elemento);
        if (v == null) return;
        elementos--;
        if (elementos == 0) {
            raiz = null;
            return;
        }
        Vertice ultimo = ultimoVertice();
        v.elemento = ultimo.elemento;
        if (ultimo.padre.izquierdo == ultimo) ultimo.padre.izquierdo = null;
        else ultimo.padre.derecho = null;
        ultimo.padre = null;
    }

    private Vertice ultimoVertice() {
        Cola<Vertice> cola = new Cola<>();
        cola.mete(raiz);
        Vertice ultimo = null;
        while (!cola.esVacia()) {
            ultimo = cola.saca();
            if (ultimo.hayIzquierdo()) cola.mete(ultimo.izquierdo);
            if (ultimo.hayDerecho()) cola.mete(ultimo.derecho);
        }
        return ultimo;
    } 

    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo
     * siempre es ⌊log<sub>2</sub><em>n</em>⌋.
     * @return la altura del árbol.
     */
    @Override public int altura() {
        if (raiz == null) return -1;
        int altura = 0;
        int nodosEnNivel = 1;
        while (nodosEnNivel <= elementos) { altura++; nodosEnNivel *= 2; }
        return altura - 1;
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en
     * cada elemento del árbol.
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        if (raiz == null) return;
        Cola <VerticeArbolBinario<T>> cola = new Cola<>();
        cola.mete(raiz);
        while (!cola.esVacia()) {
            VerticeArbolBinario<T> vertice = cola.saca();
            accion.actua(vertice);
            if (vertice.hayIzquierdo()) cola.mete(vertice.izquierdo());
            if (vertice.hayDerecho()) cola.mete(vertice.derecho());
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * @return un iterador para iterar el árbol.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }
}
