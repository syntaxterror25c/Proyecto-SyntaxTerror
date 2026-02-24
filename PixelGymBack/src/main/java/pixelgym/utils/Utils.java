package pixelgym.utils;

public class Utils {

    // Método auxiliar para que no se descuadre la tabla si un nombre es muy largo
    public static String truncar(String texto, int largo) {
        if (texto == null) return "---";
        if (texto.length() <= largo) return texto;
        return texto.substring(0, largo - 2) + "..";
    }
}
