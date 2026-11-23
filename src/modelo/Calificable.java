package modelo;

/**
 * Contrato para todo lo que puede recibir calificaciones.
 */
public interface Calificable {

    /** Recibe una calificación de 1 a 5. Cualquier otro valor lanza IllegalArgumentException. */
    void calificar(int calificacion);

    /** Promedio de las calificaciones recibidas (0 si aún no tiene ninguna). */
    double getPromedio();
}
