package servicio;

import modelo.Repartidor;

/** Respuesta de un repartidor a la pregunta "¿cuánto cobras y cuánto te demoras?". */
public class Cotizacion {

    private final Repartidor repartidor;
    private final double costo;
    private final double tiempo;

    public Cotizacion(Repartidor repartidor, double costo, double tiempo) {
        this.repartidor = repartidor;
        this.costo = costo;
        this.tiempo = tiempo;
    }

    public Repartidor getRepartidor() {
        return repartidor;
    }

    public double getCosto() {
        return costo;
    }

    public double getTiempo() {
        return tiempo;
    }
}