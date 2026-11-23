package modelo;

/**
 * Repartidor genérico. Es abstracta porque no existe "un repartidor a secas":
 * cada tipo (bici, moto, carro, dron) sabe calcular su propio costo, tiempo y límites.
 */
public abstract class Repartidor implements Calificable {

    private final String codigo;
    private String nombre;
    private boolean disponible = true;
    private int sumaCalificaciones = 0;
    private int cantidadCalificaciones = 0;

    protected Repartidor(String codigo, String nombre) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        this.codigo = codigo.trim();
        setNombre(nombre);
    }

    // ---- Comportamientos que cada tipo de repartidor define a su manera ----

    /** Nombre del tipo: "Bici", "Moto", etc. */
    public abstract String getTipo();

    public abstract double calcularCosto(double km);

    /** Tiempo estimado en minutos. */
    public abstract double calcularTiempo(double km);

    public abstract boolean puedeLlevar(double km, boolean esGrande);

    /** Por defecto la lluvia no afecta. Bici y Dron lo sobrescriben. */
    public boolean afectadoPorLluvia() {
        return false;
    }

    // ---- Datos comunes ----

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    // ---- Calificable: se guarda solo la suma y la cantidad (basta para el promedio) ----

    @Override
    public void calificar(int calificacion) {
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");
        }
        sumaCalificaciones += calificacion;
        cantidadCalificaciones++;
    }

    @Override
    public double getPromedio() {
        if (cantidadCalificaciones == 0) {
            return 0;
        }
        return (double) sumaCalificaciones / cantidadCalificaciones;
    }

    public int getCantidadCalificaciones() {
        return cantidadCalificaciones;
    }
}