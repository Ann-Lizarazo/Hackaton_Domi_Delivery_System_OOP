package modelo;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Pedido {

    public static final String EN_CAMINO = "EN_CAMINO";
    public static final String ENTREGADO = "ENTREGADO";
    public static final String CANCELADO = "CANCELADO";

    /** Estados permitidos (un Set permite revisar rápido si un texto está entre ellos). */
    public static final Set<String> ESTADOS = new LinkedHashSet<>(
            Arrays.asList(EN_CAMINO, ENTREGADO, CANCELADO));

    private static int contador = 0;

    private final int numero;
    private final Cliente cliente;
    private final String descripcion;
    private final boolean grande;
    private final double km;
    private final Repartidor repartidor;
    private final double costo;
    private final double tiempo;
    private String estado;

    public Pedido(Cliente cliente, String descripcion, boolean grande, double km,
                  Repartidor repartidor, double costo, double tiempo) {
        if (cliente == null) {
            throw new IllegalArgumentException("El pedido necesita un cliente.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del pedido no puede estar vacía.");
        }
        if (repartidor == null) {
            throw new IllegalArgumentException("El pedido necesita un repartidor.");
        }
        if (km <= 0) {
            throw new IllegalArgumentException("Los km deben ser mayores que 0.");
        }
        if (costo < 0 || tiempo < 0) {
            throw new IllegalArgumentException("El costo y el tiempo no pueden ser negativos.");
        }
        this.cliente = cliente;
        this.descripcion = descripcion.trim();
        this.grande = grande;
        this.km = km;
        this.repartidor = repartidor;
        this.costo = costo;
        this.tiempo = tiempo;
        this.estado = EN_CAMINO;
        this.numero = ++contador; // consecutivo automático, empieza en 1
    }

    public int getNumero() {
        return numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isGrande() {
        return grande;
    }

    public double getKm() {
        return km;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || !ESTADOS.contains(estado)) {
            throw new IllegalArgumentException(
                    "Estado no válido. Solo se permite: " + String.join(", ", ESTADOS) + ".");
        }
        this.estado = estado;
    }

    public boolean isEnCamino() {
        return EN_CAMINO.equals(estado);
    }
}