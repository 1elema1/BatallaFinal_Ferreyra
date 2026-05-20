/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import ieig2.modelo.Personaje;
import ieig2.modelo.Arma;
import ieig2.modelo.BendicionDelVacio; 

public class Villano extends Personaje {
    public Villano(String nombre, int vida, int fuerza, int defensa, int bendicion) {
        super(nombre, vida, fuerza, defensa, bendicion);
    }

    private int leviatanTurnos = -1;      // -1=no, >0 casteando, 0 lanzar
    private int ultimoDanoLeviatan = 0;   

    public boolean leviatanInterrumpido() { return leviatanTurnos > 0; }
    public int getUltimoDanoLeviatan() { return ultimoDanoLeviatan; }

    // --- ¡CORRECCIÓN AQUÍ! ---
    // 1. Cambiado de 'void' a 'String'
    @Override
    public String invocarArma() {
        BendicionDelVacio maldicion = new BendicionDelVacio();
        this.arma = maldicion.obtenerArma(bendicion);
        registrarArmaInvocada(arma.getNombre()); // (para reporte de armas)
        return nombre + " invoca el arma: " + arma.getNombre(); // 2. Devuelve el log
    }

    // --- ¡CORRECCIÓN AQUÍ! ---
    // 3. Cambiado de 'void' a 'String' y se usa StringBuilder
    @Override
    public String decidirAccion(Personaje enemigo) {
        StringBuilder log = new StringBuilder();

        // 1) Si está casteando, avanza y termina turno
        if (leviatanTurnos > 0) {
            leviatanTurnos--;
            log.append(nombre + " está casteando Leviatán del Vacío... (" + leviatanTurnos + " turnos restantes)");
            return log.toString();
        }

        // 2) Si terminó el casteo, lanza y termina turno
        if (leviatanTurnos == 0) {
            log.append("¡" + nombre + " desata a Leviatán del Vacío!");
            int dano = Math.max(1, enemigo.getVida()); // daño igual a vida actual del enemigo
            
            // 4. Usamos recibirDanio (que devuelve int)
            int danoReal = enemigo.recibirDanio(dano);
            log.append("\n" + enemigo.getNombre() + " recibe " + danoReal + " de daño directo de Leviatán. Vida: " + enemigo.getVida());

            try { registrarSupremo("Leviatán del Vacío"); } catch (Exception ignored) {}
            try { this.ultimoDanoLeviatan = dano; } catch (Exception ignored) {}

            leviatanTurnos = -1; // limpia estado
            return log.toString();
        }

        // 3) Si puede iniciar el especial, inicia el casteo y termina turno
        if (bendicion >= 100) {
            leviatanTurnos = 2; // 2→1→0→lanza
            bendicion = 0;      // consume bendición
            log.append(nombre + " invoca a Leviatán del Vacío. Casteo iniciado.");
            return log.toString();
        }

        // 4) Flujo normal de siempre (capturando los strings)
        // 5. Capturamos los logs de los métodos modificados
        String logInvocacion = invocarArma();
        String logAtaque = atacar(enemigo);
        
        return logInvocacion + "\n" + logAtaque;
    }
}