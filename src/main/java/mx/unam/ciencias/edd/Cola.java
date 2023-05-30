package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
      if (cabeza == null) return "";
      String cadena = "";
      Nodo n = cabeza;
      while(n != null) {
        cadena += n.elemento + ",";
        n = n.siguiente;
      }
      return cadena; 
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
      if (elemento == null) throw new IllegalArgumentException("Se está intentando crear un Nodo con contenido null");
      Nodo n = new Nodo(elemento);
      if (cabeza == null) { cabeza = rabo = n; return; }
      rabo.siguiente = n;
      rabo = n;
    }
}
