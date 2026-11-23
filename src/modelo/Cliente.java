package modelo;

public class Cliente {

    private final String cedula; // identifica al cliente: por eso no se edita
    private String nombre;
    private String telefono;
    private String barrio;
    private String direccion;

    public Cliente(String cedula, String nombre, String telefono, String barrio, String direccion) {
        this.cedula = validarTexto(cedula, "La cédula");
        setNombre(nombre);
        setTelefono(telefono);
        setBarrio(barrio);
        setDireccion(direccion);
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "El nombre");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        String limpio = validarTexto(telefono, "El teléfono");
        for (int i = 0; i < limpio.length(); i++) {
            char c = limpio.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("El teléfono solo puede tener números.");
            }
        }
        if (limpio.length() < 7 || limpio.length() > 10) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 10 dígitos.");
        }
        this.telefono = limpio;
    }

    public String getBarrio() {
        return barrio;
    }

    /** Aquí solo se valida que no esté vacío; la cobertura la valida el servicio. */
    public void setBarrio(String barrio) {
        this.barrio = validarTexto(barrio, "El barrio");
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = validarTexto(direccion, "La dirección");
    }
}
