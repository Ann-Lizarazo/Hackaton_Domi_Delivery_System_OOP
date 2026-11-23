package servicio;

import modelo.Repartidor;

import java.util.Map;

/** Datos del cierre del día. El servicio los calcula; Main decide cómo mostrarlos. */
public class ReporteCierre {

    private final int totalPedidos;
    private final Map<String, Integer> pedidosPorEstado;
    private final double ingresosTotales;
    private final Map<String, Double> ingresosPorTipo;
    private final Repartidor repartidorEstrella; // null si nadie tiene calificaciones

    public ReporteCierre(int totalPedidos, Map<String, Integer> pedidosPorEstado,
                         double ingresosTotales, Map<String, Double> ingresosPorTipo,
                         Repartidor repartidorEstrella) {
        this.totalPedidos = totalPedidos;
        this.pedidosPorEstado = pedidosPorEstado;
        this.ingresosTotales = ingresosTotales;
        this.ingresosPorTipo = ingresosPorTipo;
        this.repartidorEstrella = repartidorEstrella;
    }

    public int getTotalPedidos() {
        return totalPedidos;
    }

    public Map<String, Integer> getPedidosPorEstado() {
        return pedidosPorEstado;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }

    public Map<String, Double> getIngresosPorTipo() {
        return ingresosPorTipo;
    }

    public Repartidor getRepartidorEstrella() {
        return repartidorEstrella;
    }
}