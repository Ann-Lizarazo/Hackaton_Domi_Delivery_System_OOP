package modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Único lugar que conoce los tipos de repartidor disponibles al registrar uno.
 * Para agregar un tipo nuevo: crear su clase y añadirlo aquí. El servicio y el menú no cambian.
 */
public final class FabricaRepartidores {

    private static final List<String> TIPOS = Collections.unmodifiableList(
            Arrays.asList("Bici", "Moto", "Carro", "Dron"));

    private FabricaRepartidores() {
    }

    public static List<String> tipos() {
        return TIPOS;
    }

    public static Repartidor crear(String tipo, String codigo, String nombre) {
        String t = tipo == null ? "" : tipo.trim().toLowerCase();
        switch (t) {
            case "bici":
                return new Bici(codigo, nombre);
            case "moto":
                return new Moto(codigo, nombre);
            case "carro":
                return new Carro(codigo, nombre);
            case "dron":
                return new Dron(codigo, nombre);
            default:
                throw new IllegalArgumentException(
                        "Tipo de repartidor no válido: '" + (tipo == null ? "" : tipo.trim())
                                + "'. Opciones: " + String.join(", ", TIPOS) + ".");
        }
    }
}
