package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

  /* Constructor privado para evitar instanciación. */
  private Dispersores() {}

  /**
   * Función de dispersión XOR.
   * @param llave la llave a dispersar.
   * @return la dispersión de XOR de la llave.
   */
  public static int dispersaXOR(byte[] llave) {
    int i = 0, l = llave.length, r = 0;
    while (l >= 4) {
      r ^= bigE(b(llave, i), b(llave, i + 1), b(llave, i + 2), b(llave, i + 3));
      i += 4;
      l -= 4;
    }

    int t = 0;
    switch (l) {
      case 3: t |= bigE(b(llave, i), b(llave, i + 1), b(llave, i + 2), 0);
      case 2: t |= bigE(b(llave, i), b(llave, i + 1), 0, 0);
      case 1: t |= bigE(b(llave, i), 0, 0, 0);
    }
    return r ^ t;
  }

  /**
   * Función de dispersión de Bob Jenkins.
   * @param llave la llave a dispersar.
   * @return la dispersión de Bob Jenkins de la llave.
   */
  public static int dispersaBJ(byte[] llave) {
    int a = 0x9E3779B9, b = 0x9E3779B9, c = 0xFFFFFFFF;
    int l = llave.length;
    int i = 0;
    int[] arr = new int[3];
    while (l >= 12) {
      a += littleE(b(llave, i), b(llave, i + 1), b(llave, i + 2), b(llave, i + 3));
      b += littleE(b(llave, i + 4), b(llave, i + 5), b(llave, i + 6), b(llave, i + 7));
      c += littleE(b(llave, i + 8), b(llave, i + 9), b(llave, i + 10), b(llave, i + 11));
      arr = mezcla(a, b, c);
      a = arr[0]; b = arr[1]; c = arr[2];
      i += 12;
      l -= 12;
    }
    c += llave.length;
    switch (l) {
      case 11: c += (b(llave, i+10) << 24);
      case 10: c += (b(llave, i+9) << 16);
      case 9 : c += (b(llave, i+8) << 8);
      case 8 : b += (b(llave, i+7) << 24);
      case 7 : b += (b(llave, i+6) << 16);
      case 6 : b += (b(llave, i+5) << 8);
      case 5 : b += (b(llave, i+4));
      case 4 : a += (b(llave, i+3) << 24);
      case 3 : a += (b(llave, i+2)  << 16);
      case 2 : a += (b(llave, i+1)  << 8);
      case 1 : a += (b(llave, i));
    }
    arr = mezcla(a, b, c);
    return arr[2];
  }

  /**
   * Función de dispersión Daniel J. Bernstein.
   * @param llave la llave a dispersar.
   * @return la dispersión de Daniel Bernstein de la llave.
   */
  public static int dispersaDJB(byte[] llave) {
    int h = 5381;
    for (int i = 0; i < llave.length; i++) h += (h << 5) + b(llave, i);
    return h;
  }

  private static int littleE(int a, int b, int c, int d) {
    return a | (b << 8) | (c << 16) | (d << 24);
  }

  private static int bigE(int a, int b, int c, int d) {
    return (a << 24) | (b << 16) | (c << 8) | d;
  }

  private static int b(byte[] llave, int i) {
    return i < llave.length ? (0xFF & llave[i]) : 0;
  }

  private static int[] mezcla(int a, int b, int c) {
    int[] arr = new int[3];
    a -= b; a -= c; a ^= (c >>> 13);
    b -= c; b -= a; b ^= (a <<  8);
    c -= a; c -= b; c ^= (b >>> 13);
    a -= b; a -= c; a ^= (c >>> 12);
    b -= c; b -= a; b ^= (a <<  16);
    c -= a; c -= b; c ^= (b >>> 5);
    a -= b; a -= c; a ^= (c >>> 3);
    b -= c; b -= a; b ^= (a <<  10);
    c -= a; c -= b; c ^= (b >>> 15);
    arr[0] = a; arr[1] = b; arr[2] = c;
    return arr;
  }
}