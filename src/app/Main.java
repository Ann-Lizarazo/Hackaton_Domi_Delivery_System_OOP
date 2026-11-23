package app;

import modelo.Cliente;
import modelo.FabricaRepartidores;
import modelo.Pedido;
import modelo.Repartidor;
import servicio.Cotizacion;
import servicio.DomiService;
import servicio.ReporteCierre;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Terminal del operador. Pregunta y muestra; las reglas viven en DomiService.
 */
public class Main {

    private static final Locale CO = Locale.forLanguageTag("es-CO");

    private final Scanner scanner = new Scanner(System.in);
    private final DomiService servicio = new DomiService();

    public static void main(String[] args) {
        new Main().ejecutar();
    }

    // =====================================================================
    // Ciclo principal
    // =====================================================================

    private void ejecutar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerOpcion();
            try {
                procesar(opcion);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠ Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (opcion != 0);
        System.out.println("¡Hasta pronto! Recuerda: los datos del día se pierden al cerrar el programa.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("══════════════════ DOMI · OPERADOR ══════════════════");
        if (servicio.isLluvia()) {
            System.out.println("🌧 MODO LLUVIA ACTIVO");
        }
        System.out.println(" CLIENTES      1. Registrar     2. Listar");
        System.out.println("               3. Editar        4. Eliminar");
        System.out.println(" REPARTIDORES  5. Registrar     6. Listar");
        System.out.println("               7. Cambiar disponibilidad");
        System.out.println(" PEDIDOS       8. Crear pedido (cotiza y asigna)");
        System.out.println("               9. Entregar     10. Cancelar");
        System.out.println("              11. Listar pedidos");
        System.out.println("              14. Historial de un cliente");
        System.out.println(" OPERACIÓN    12. Activar / desactivar lluvia");
        System.out.println("              13. Cierre del día");
        System.out.println("               0. Salir");
        System.out.println("═════════════════════════════════════════════════════");
    }

    private int leerOpcion() {
        try {
            System.out.print("Elige una opción: ");
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        } catch (NoSuchElementException e) {
            return 0; // se cerró la entrada: salir sin caerse
        }
    }

    private void procesar(int opcion) {
        switch (opcion) {
            case 1: registrarCliente(); break;
            case 2: listarClientes(); break;
            case 3: editarCliente(); break;
            case 4: eliminarCliente(); break;
            case 5: registrarRepartidor(); break;
            case 6: listarRepartidores(); break;
            case 7: cambiarDisponibilidad(); break;
            case 8: crearPedido(); break;
            case 9: entregarPedido(); break;
            case 10: cancelarPedido(); break;
            case 11: listarPedidos(); break;
            case 12: alternarLluvia(); break;
            case 13: cierreDelDia(); break;
            case 14: historialCliente(); break;
            case 0: break;
            default: System.out.println("⚠ Opción no válida. Elige un número del menú.");
        }
    }

    // =====================================================================
    // Clientes
    // =====================================================================

    private void registrarCliente() {
        String cedula = leerTexto("Cédula: ");
        String nombre = leerTexto("Nombre: ");
        String telefono = leerTexto("Teléfono: ");
        System.out.println("Barrios con cobertura: " + String.join(", ", servicio.getBarriosCobertura()));
        String barrio = leerTexto("Barrio: ");
        String direccion = leerTexto("Dirección: ");
        Cliente c = servicio.registrarCliente(cedula, nombre, telefono, barrio, direccion);
        System.out.println("✔ Cliente registrado: " + c.getNombre() + " · " + c.getBarrio());
    }

    private void listarClientes() {
        List<Cliente> clientes = servicio.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Cliente c : clientes) {
            System.out.println(c.getCedula() + " · " + c.getNombre() + " · Tel. " + c.getTelefono()
                    + " · " + c.getBarrio() + " · " + c.getDireccion());
        }
    }

    private void editarCliente() {
        Cliente c = servicio.buscarCliente(leerTexto("Cédula del cliente a editar: "));
        System.out.println("Editando a " + c.getNombre() + " (la cédula no se puede cambiar).");
        System.out.println("Deja vacío lo que no quieras cambiar.");
        String telefono = leerTexto("Nuevo teléfono [" + c.getTelefono() + "]: ");
        System.out.println("Barrios con cobertura: " + String.join(", ", servicio.getBarriosCobertura()));
        String barrio = leerTexto("Nuevo barrio [" + c.getBarrio() + "]: ");
        String direccion = leerTexto("Nueva dirección [" + c.getDireccion() + "]: ");
        servicio.editarCliente(c.getCedula(), telefono, barrio, direccion);
        System.out.println("✔ Cliente actualizado.");
    }

    private void eliminarCliente() {
        String cedula = leerTexto("Cédula del cliente a eliminar: ");
        Cliente c = servicio.buscarCliente(cedula);
        servicio.eliminarCliente(cedula);
        System.out.println("✔ Cliente eliminado: " + c.getNombre());
    }

    // =====================================================================
    // Repartidores
    // =====================================================================

    private void registrarRepartidor() {
        String codigo = leerTexto("Código (ej. R-04): ");
        String nombre = leerTexto("Nombre: ");
        List<String> tipos = FabricaRepartidores.tipos();
        for (int i = 0; i < tipos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + tipos.get(i));
        }
        String entrada = leerTexto("Tipo (número o nombre): ");
        String tipo = entrada;
        try {
            int indice = Integer.parseInt(entrada.trim());
            if (indice >= 1 && indice <= tipos.size()) {
                tipo = tipos.get(indice - 1);
            }
        } catch (NumberFormatException e) {
            // no era un número: se usa el texto tal cual y el servicio lo valida
        }
        Repartidor r = servicio.registrarRepartidor(tipo, codigo, nombre);
        System.out.println("✔ Repartidor registrado: " + r.getCodigo() + " " + r.getNombre() + " (" + r.getTipo() + ")");
    }

    private void listarRepartidores() {
        List<Repartidor> repartidores = servicio.listarRepartidores();
        if (repartidores.isEmpty()) {
            System.out.println("No hay repartidores registrados.");
            return;
        }
        for (Repartidor r : repartidores) {
            System.out.println(r.getCodigo() + " · " + r.getNombre() + " · " + r.getTipo()
                    + " · " + (r.isDisponible() ? "Disponible" : "No disponible")
                    + " · Promedio " + String.format(CO, "%.1f", r.getPromedio())
                    + " (" + r.getCantidadCalificaciones() + " calif.)");
        }
    }

    private void cambiarDisponibilidad() {
        Repartidor r = servicio.cambiarDisponibilidad(leerTexto("Código del repartidor: "));
        System.out.println("✔ " + r.getNombre() + " ahora está "
                + (r.isDisponible() ? "DISPONIBLE" : "NO DISPONIBLE") + ".");
    }

    // =====================================================================
    // Pedidos
    // =====================================================================

    private void crearPedido() {
        String cedula = leerTexto("Cédula del cliente: ");
        Cliente cliente = servicio.buscarCliente(cedula);
        String descripcion = leerTexto("Descripción del pedido: ");
        boolean grande = leerSiNo("¿Es un pedido grande? (s/n): ");

        double km = servicio.getDistancia(cliente.getBarrio());
        System.out.println();
        System.out.println("Cotizando pedido para " + cliente.getNombre() + " · " + cliente.getBarrio()
                + " (" + String.format(CO, "%.1f", km) + " km)");
        for (Cotizacion c : servicio.cotizar(cedula, grande)) {
            Repartidor r = c.getRepartidor();
            System.out.println("  " + r.getCodigo() + " " + r.getNombre() + " (" + r.getTipo() + ")  "
                    + dinero(c.getCosto()) + "  " + minutos(c.getTiempo()));
        }

        Pedido p = servicio.crearPedido(cedula, descripcion, grande);
        System.out.println("✔ Pedido #" + p.getNumero() + " asignado a " + p.getRepartidor().getNombre()
                + " (" + p.getRepartidor().getTipo() + ") · " + p.getEstado());
    }

    private void entregarPedido() {
        int numero = leerEntero("Número del pedido: ");
        servicio.buscarPedidoEnCamino(numero, "entregar");
        int calificacion = leerEntero("Calificación del cliente (1 a 5): ");
        Pedido p = servicio.entregar(numero, calificacion);
        System.out.println("✔ Pedido #" + p.getNumero() + " entregado. Gracias por calificar a "
                + p.getRepartidor().getNombre() + ".");
    }

    private void cancelarPedido() {
        int numero = leerEntero("Número del pedido: ");
        Pedido p = servicio.cancelar(numero);
        System.out.println("✔ Pedido #" + p.getNumero() + " cancelado.");
    }

    private void listarPedidos() {
        List<Pedido> pedidos = servicio.listarPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos todavía.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.println(describir(p));
        }
    }

    private void historialCliente() {
        Cliente c = servicio.buscarCliente(leerTexto("Cédula del cliente: "));
        List<Pedido> historial = servicio.historialCliente(c.getCedula());
        if (historial.isEmpty()) {
            System.out.println(c.getNombre() + " no tiene pedidos.");
            return;
        }
        System.out.println("Historial de " + c.getNombre() + ":");
        for (Pedido p : historial) {
            System.out.println(describir(p));
        }
    }

    private String describir(Pedido p) {
        return "#" + p.getNumero() + " · " + p.getCliente().getNombre()
                + " · " + p.getRepartidor().getNombre() + " (" + p.getRepartidor().getTipo() + ")"
                + " · " + dinero(p.getCosto()) + " · " + minutos(p.getTiempo())
                + " · " + p.getEstado();
    }

    // =====================================================================
    // Operación
    // =====================================================================

    private void alternarLluvia() {
        servicio.setLluvia(!servicio.isLluvia());
        if (servicio.isLluvia()) {
            System.out.println("🌧 Modo lluvia ACTIVADO: sin bicis ni drones y con 20 % de recargo.");
        } else {
            System.out.println("☀ Modo lluvia DESACTIVADO.");
        }
    }

    private void cierreDelDia() {
        ReporteCierre r = servicio.cierreDelDia();
        System.out.println("════════ CIERRE DEL DÍA ════════");
        System.out.println("Total de pedidos: " + r.getTotalPedidos());
        for (Map.Entry<String, Integer> e : r.getPedidosPorEstado().entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue());
        }
        System.out.println("Ingresos del día (solo ENTREGADOS): " + dinero(r.getIngresosTotales()));
        System.out.println("Ingresos por tipo de repartidor:");
        for (Map.Entry<String, Double> e : r.getIngresosPorTipo().entrySet()) {
            System.out.println("  " + e.getKey() + ": " + dinero(e.getValue()));
        }
        Repartidor estrella = r.getRepartidorEstrella();
        if (estrella == null) {
            System.out.println("Repartidor estrella: aún nadie tiene calificaciones.");
        } else {
            System.out.println("⭐ Repartidor estrella: " + estrella.getNombre() + " (" + estrella.getTipo()
                    + ") con promedio " + String.format(CO, "%.1f", estrella.getPromedio()));
        }
    }

    // =====================================================================
    // Lectura y formato
    // =====================================================================

    /** Todo se lee como texto con nextLine() para evitar la trampa del buffer del Scanner. */
    private String leerTexto(String pregunta) {
        System.out.print(pregunta);
        return scanner.nextLine();
    }

    private int leerEntero(String pregunta) {
        String texto = leerTexto(pregunta).trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un número entero válido.");
        }
    }

    private boolean leerSiNo(String pregunta) {
        String r = leerTexto(pregunta).trim().toLowerCase();
        if (r.equals("s") || r.equals("si") || r.equals("sí")) {
            return true;
        }
        if (r.equals("n") || r.equals("no")) {
            return false;
        }
        throw new IllegalArgumentException("Responde 's' (sí) o 'n' (no).");
    }

    private static String dinero(double valor) {
        return "$" + String.format(CO, "%,.0f", valor);
    }

    private static String minutos(double valor) {
        if (Math.abs(valor - Math.rint(valor)) < 0.05) {
            return String.format(CO, "%.0f min", valor);
        }
        return String.format(CO, "%.1f min", valor);
    }
}