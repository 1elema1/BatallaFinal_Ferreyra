package ieig2.modelo;

import java.util.Objects;

public class Jugador {
    private final String nombre;
    private final String apodo;
    private final TipoPersonaje tipo;

    public Jugador(String nombre, String apodo, TipoPersonaje tipo) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.tipo = tipo;
    }

    public String getNombre() { return nombre; }
    public String getApodo() { return apodo; }
    public TipoPersonaje getTipo() { return tipo; }

    @Override public String toString() { return nombre + " (" + apodo + ") - " + tipo; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jugador)) return false;
        Jugador j = (Jugador) o;
        return apodo != null && apodo.equalsIgnoreCase(j.apodo);
    }

    @Override public int hashCode() { return Objects.hash(apodo == null ? "" : apodo.toLowerCase()); }
}
