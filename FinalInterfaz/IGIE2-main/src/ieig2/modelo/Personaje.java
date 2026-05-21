/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;


import java.util.*;
import ieig2.modelo.Arma;
import ieig2.modelo.Item; // Asegúrate de importar tu clase Item

public abstract class Personaje {
    //  Estado base del personaje 
    protected String nombre;
    public int vida;
    protected int fuerza;
    protected int defensa;
    protected Arma arma = null;
    protected int bendicion; // 0..100

    //  Para el reporte final 
    protected Map<String, Integer> armasInvocadas = new LinkedHashMap<>();
    protected int supremosUsados = 0;
    protected List<String> logEspeciales = new ArrayList<>();
    
    protected int mayorDanio = 0;     // daño más alto hecho en un ataque
    protected int turnosTotales = 0;  // cantidad total de turnos jugados

    // --- NUEVO ATRIBUTO PARA ITEMS ---
    protected List<Item> inventario = new ArrayList<>();

    public Personaje(String nombre, int vida, int fuerza, int defensa, int bendicion) {
        this.nombre = nombre;
        this.vida = vida;
        this.fuerza = fuerza;
        this.defensa = defensa;
        this.bendicion = bendicion;
    }

    public void equiparItem(Item item) {
        this.inventario.add(item);
        this.vida += item.getEfectoVida();
    }

    public List<Item> getInventario() {
        return inventario;
    }
    // -------------------------------------------------

    public boolean estaVivo() { 
        return vida > 0; 
    }

    public int recibirDanio(int danio) {
        int real = Math.max(0, danio - defensa);
        vida -= real;
        return real;
    }

    public String atacar(Personaje enemigo) {
        StringBuilder log = new StringBuilder();
        
        if (!inventario.isEmpty()) {
            log.append("--- Ítems activos de " + nombre + ": ---\n");
            for (Item item : inventario) {
                log.append("-> " + item.getNombre() + " (Efecto: +" + item.getEfectoVida() + " Vida)\n");
            }
        }
        
        int total = fuerza + (arma != null ? arma.getDanioExtra() : 0);
        String armaUsada = (arma != null ? arma.getNombre() : "puños");

        log.append(nombre + " ataca con fuerza total de " + total + " usando " + armaUsada + ".");

        // Calcula el daño real e impacta al enemigo
        int danioReal = enemigo.recibirDanio(total);
        log.append("\n" + enemigo.getNombre() + " recibe " + danioReal + " de daño. Vida: " + enemigo.getVida());

        // 🔹 ACTUALIZA EL MAYOR DAÑO
        registrarDanio(danioReal);

        if (arma != null) {
            String efectoLog = arma.usarEfectoEspecial(this, enemigo);
            log.append("\n" + efectoLog);
        }
        
        return log.toString();
    }

    //  Helpers para el reporte 
    protected void registrarArmaInvocada(String armaNombre) {
        armasInvocadas.merge(armaNombre, 1, Integer::sum);
    }

    protected void registrarSupremo(String descripcion) {
        supremosUsados++;
        logEspeciales.add(descripcion);
        
    }

    //  Getters usados por IEIG1.generarReporteFinal 
    public int getSupremosUsados() { return supremosUsados; }
    public Map<String, Integer> getArmasInvocadas() { return armasInvocadas; }
    public List<String> getLogEspeciales() { return logEspeciales; }
    public String getNombre() { return nombre; }
    public int getVida() { return Math.max(vida, 0); }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve el mayor daño hecho en un solo ataque */
    public Integer getMayorDanio() {
        return mayorDanio;
    }

    /** Devuelve la cantidad total de turnos jugados por el personaje */
    public int getTurnosTotales() {
        return turnosTotales;
    }

    /** Permite actualizar el valor del mayor daño si se supera el anterior */
    public void registrarDanio(int danioHecho) {
        if (danioHecho > mayorDanio) {
            mayorDanio = danioHecho;
        }
    }

    /** Permite sumar un turno más al contador */
    public void incrementarTurno() {
        turnosTotales++;
    }

    //  Contrato para las subclases 
    public abstract String invocarArma();
    public abstract String decidirAccion(Personaje enemigo);

    @Override
    public String toString() {
        return nombre + " [vida=" + vida + ", fuerza=" + fuerza + ", defensa=" + defensa +
               ", arma=" + (arma != null ? arma.getNombre() : "ninguna") +
               ", bendición=" + bendicion + "]";
    }
}