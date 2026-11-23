package servicio;

import modelo.Cliente;
import modelo.FabricaRepartidores;
import modelo.Pedido;
import modelo.Repartidor;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Guarda los datos y aplica las reglas del negocio.
 * No lee del teclado ni imprime nada.
 */
public class DomiService {

    private static final double RECARGO_LLUVIA = 1.20;

    private final Map<String, Cliente> clientes = new HashMap<>();          // cédula -> cliente
    private final Set<String> barriosCobertura = new HashSet<>();           // barrios sin repetir
    private final Map<String, Double> distancias = new HashMap<>();         // barrio -> km
    private final List<Repartidor> repartidores = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    private boolean lluvia = false;

    public DomiService() {
        cargarDatosDeArranque();
    }

    // =====================================================================
    // Datos de arranque
    // =====================================================================

    private void cargarDatosDeArranque() {
        agregarBarrio("Chapinero", 1.5);
        agregarBarrio("Teusaquillo", 3.0);
        agregarBarrio("La Candelaria", 4.5);
        agregarBarrio("Usaquén", 6.0);
        agregarBarrio("Cedritos", 8.0);
        agregarBarrio("Fontibón", 11.0);
        agregarBarrio("Suba", 12.0);
        agregarBarrio("Kennedy", 14.0);
        agregarBarrio("Bosa", 18.0);

        registrarRepartidor("Bici", "R-01", "Camila Torres");
        registrarRepartidor("Moto", "R-02", "Andrés Mejía");
        registrarRepartidor("Carro", "R-03", "Jorge Pardo");

        registrarCliente("1020304050", "Laura Gómez", "3001234567", "Teusaquillo", "Calle 40 # 15-20");
        registrarCliente("1030405060", "Mateo Rincón", "3109876543", "Usaquén", "Carrera 7 # 120-15");
    }

    private void agregarBarrio(String barrio, double km) {
        barriosCobertura.add(barrio);
        distancias.put(barrio, km);
    }

    // =====================================================================
    // Barrios
    // =====================================================================

    public List<String> getBarriosCobertura() {
        List<String> lista = new ArrayList<>(barriosCobertura);
        Collections.sort(lista);
        return lista;
    }

    /** Devuelve el nombre oficial del barrio (sin importar mayúsculas ni tildes) o lanza excepción. */
    private String validarBarrio(String barrio) {
        if (barrio == null || barrio.trim().isEmpty()) {
            throw new IllegalArgumentException("El barrio no puede estar vacío.");
        }
        String buscado = sinTildes(barrio);
        for (String oficial : barriosCobertura) {
            if (sinTildes(oficial).equals(buscado)) {
                return oficial;
            }
        }
        throw new IllegalArgumentException("El barrio '" + barrio.trim() + "' no tiene cobertura. "
                + "Barrios con cobertura: " + String.join(", ", getBarriosCobertura()) + ".");
    }

    private static String sinTildes(String texto) {
        String normalizado = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{M}", "").toLowerCase();
    }

    public double getDistancia(String barrio) {
        Double km = distancias.get(barrio);
        if (km == null) {
            throw new IllegalArgumentException("No hay distancia registrada para el barrio '" + barrio + "'.");
        }
        return km;
    }

    // =====================================================================
    // Clientes
    // =====================================================================

    public Cliente registrarCliente(String cedula, String nombre, String telefono,
                                    String barrio, String direccion) {
        String barrioOficial = validarBarrio(barrio);
        Cliente nuevo = new Cliente(cedula, nombre, telefono, barrioOficial, direccion);
        if (clientes.containsKey(nuevo.getCedula())) {
            throw new IllegalArgumentException("Ya existe un cliente con la cédula " + nuevo.getCedula() + ".");
        }
        clientes.put(nuevo.getCedula(), nuevo);
        return nuevo;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>(clientes.values());
        lista.sort(Comparator.comparing(c -> c.getNombre().toLowerCase()));
        return lista;
    }

    public Cliente buscarCliente(String cedula) {
        Cliente c = cedula == null ? null : clientes.get(cedula.trim());
        if (c == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula "
                    + (cedula == null ? "" : cedula.trim()) + ".");
        }
        return c;
    }

    /** Un dato vacío (o null) significa "dejar como está". La cédula no se puede editar. */
    public Cliente editarCliente(String cedula, String telefono, String barrio, String direccion) {
        Cliente c = buscarCliente(cedula);
        String barrioOficial = esVacio(barrio) ? null : validarBarrio(barrio);
        if (!esVacio(telefono)) {
            c.setTelefono(telefono);
        }
        if (barrioOficial != null) {
            c.setBarrio(barrioOficial);
        }
        if (!esVacio(direccion)) {
            c.setDireccion(direccion);
        }
        return c;
    }

    public void eliminarCliente(String cedula) {
        Cliente c = buscarCliente(cedula);
        for (Pedido p : pedidos) {
            if (p.getCliente() == c && p.isEnCamino()) {
                throw new IllegalArgumentException("No se puede eliminar a " + c.getNombre()
                        + ": tiene el pedido #" + p.getNumero() + " en camino.");
            }
        }
        clientes.remove(c.getCedula());
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // =====================================================================
    // Repartidores
    // =====================================================================

    public Repartidor registrarRepartidor(String tipo, String codigo, String nombre) {
        Repartidor nuevo = FabricaRepartidores.crear(tipo, codigo, nombre);
        for (Repartidor r : repartidores) {
            if (r.getCodigo().equalsIgnoreCase(nuevo.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un repartidor con el código " + nuevo.getCodigo() + ".");
            }
        }
        repartidores.add(nuevo);
        return nuevo;
    }

    public List<Repartidor> listarRepartidores() {
        return new ArrayList<>(repartidores);
    }

    public Repartidor buscarRepartidor(String codigo) {
        if (codigo != null) {
            for (Repartidor r : repartidores) {
                if (r.getCodigo().equalsIgnoreCase(codigo.trim())) {
                    return r;
                }
            }
        }
        throw new IllegalArgumentException("No existe un repartidor con el código "
                + (codigo == null ? "" : codigo.trim()) + ".");
    }

    public Repartidor cambiarDisponibilidad(String codigo) {
        Repartidor r = buscarRepartidor(codigo);
        r.setDisponible(!r.isDisponible());
        return r;
    }

    // =====================================================================
    // Lluvia
    // =====================================================================

    public boolean isLluvia() {
        return lluvia;
    }

    public void setLluvia(boolean lluvia) {
        this.lluvia = lluvia;
    }

    // =====================================================================
    // Pedidos: cotizar y asignar (polimorfismo: solo se le pregunta a cada repartidor)
    // =====================================================================

    /** Cotización de todos los repartidores disponibles que pueden llevar el pedido. */
    public List<Cotizacion> cotizar(String cedula, boolean esGrande) {
        Cliente cliente = buscarCliente(cedula);
        double km = getDistancia(cliente.getBarrio());

        List<Cotizacion> cotizaciones = new ArrayList<>();
        for (Repartidor r : repartidores) {
            if (!r.isDisponible()) {
                continue;
            }
            if (lluvia && r.afectadoPorLluvia()) {
                continue;
            }
            if (!r.puedeLlevar(km, esGrande)) {
                continue;
            }
            double costo = r.calcularCosto(km);
            if (lluvia) {
                costo = Math.round(costo * RECARGO_LLUVIA);
            }
            cotizaciones.add(new Cotizacion(r, costo, r.calcularTiempo(km)));
        }
        return cotizaciones;
    }

    /** Elige menor costo; si empatan, el más rápido. Crea el pedido EN_CAMINO. */
    public Pedido crearPedido(String cedula, String descripcion, boolean esGrande) {
        Cliente cliente = buscarCliente(cedula);
        List<Cotizacion> cotizaciones = cotizar(cedula, esGrande);
        if (cotizaciones.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ningún repartidor disponible puede llevar este pedido. El pedido no se creó.");
        }

        Cotizacion mejor = Collections.min(cotizaciones,
                Comparator.comparingDouble(Cotizacion::getCosto)
                        .thenComparingDouble(Cotizacion::getTiempo));

        Pedido pedido = new Pedido(cliente, descripcion, esGrande, getDistancia(cliente.getBarrio()),
                mejor.getRepartidor(), mejor.getCosto(), mejor.getTiempo());
        mejor.getRepartidor().setDisponible(false);
        pedidos.add(pedido);
        return pedido;
    }

    public Pedido buscarPedido(int numero) {
        for (Pedido p : pedidos) {
            if (p.getNumero() == numero) {
                return p;
            }
        }
        throw new IllegalArgumentException("No existe el pedido #" + numero + ".");
    }

    /** Devuelve el pedido solo si está EN_CAMINO; si no, explica por qué no se puede. */
    public Pedido buscarPedidoEnCamino(int numero, String accion) {
        Pedido p = buscarPedido(numero);
        if (!p.isEnCamino()) {
            throw new IllegalArgumentException("El pedido #" + numero + " ya está " + p.getEstado()
                    + " y no se puede " + accion + ".");
        }
        return p;
    }

    public Pedido entregar(int numero, int calificacion) {
        Pedido p = buscarPedidoEnCamino(numero, "entregar");
        p.getRepartidor().calificar(calificacion); // si es inválida lanza excepción y nada cambia
        p.setEstado(Pedido.ENTREGADO);
        p.getRepartidor().setDisponible(true);
        return p;
    }

    public Pedido cancelar(int numero) {
        Pedido p = buscarPedidoEnCamino(numero, "cancelar");
        p.setEstado(Pedido.CANCELADO);
        p.getRepartidor().setDisponible(true);
        return p;
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public List<Pedido> historialCliente(String cedula) {
        Cliente c = buscarCliente(cedula);
        List<Pedido> historial = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getCliente() == c) {
                historial.add(p);
            }
        }
        return historial;
    }

    // =====================================================================
    // Cierre del día
    // =====================================================================

    public ReporteCierre cierreDelDia() {
        Map<String, Integer> porEstado = new LinkedHashMap<>();
        for (String estado : Pedido.ESTADOS) {
            porEstado.put(estado, 0);
        }
        Map<String, Double> ingresosPorTipo = new LinkedHashMap<>();
        for (String tipo : FabricaRepartidores.tipos()) {
            ingresosPorTipo.put(tipo, 0.0);
        }

        double ingresos = 0;
        for (Pedido p : pedidos) {
            porEstado.put(p.getEstado(), porEstado.get(p.getEstado()) + 1);
            if (Pedido.ENTREGADO.equals(p.getEstado())) {
                ingresos += p.getCosto();
                String tipo = p.getRepartidor().getTipo();
                ingresosPorTipo.merge(tipo, p.getCosto(), Double::sum);
            }
        }

        Repartidor estrella = null; // "el mejor hasta ahora"
        for (Repartidor r : repartidores) {
            if (r.getCantidadCalificaciones() == 0) {
                continue;
            }
            if (estrella == null || r.getPromedio() > estrella.getPromedio()) {
                estrella = r;
            }
        }

        return new ReporteCierre(pedidos.size(), porEstado, ingresos, ingresosPorTipo, estrella);
    }
}