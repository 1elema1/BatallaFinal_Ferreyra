/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;
    
import ieig2.modelo.Personaje;
import ieig2.modelo.BendicionCelestial;


/**
 *
 * @author Fede y Lucas
 */
public class Heroe extends Personaje {
    // último daño del Castigo Bendito para el reporte
    private int ultimoDanoCastigo = 0;

    public Heroe(String nombre, int vida, int fuerza, int defensa, int bendicion) {
        super(nombre, vida, fuerza, defensa, bendicion);
    }

    public int getUltimoDanoCastigo() {
        return ultimoDanoCastigo;
    }

    // --- ¡CORRECCIÓN AQUÍ! ---
    // 1. Se añade @Override
    // 2. Se cambia 'InvocarArma' por 'invocarArma' (con 'i' minúscula)
    @Override
    public String invocarArma() {
        BendicionCelestial bend = new BendicionCelestial();
        this.arma = bend.obtenerArma(bendicion);
        registrarArmaInvocada(arma.getNombre());
        return nombre + " invoca el arma: " + arma.getNombre();
    }

    @Override
    public String decidirAccion(Personaje enemigo) {
        StringBuilder log = new StringBuilder();

        // 1) Si puede usar el especial
        if (bendicion >= 100) {
            log.append("Heroe ¡" + nombre + " desata el Castigo Bendito! ✨");
            log.append("\nUn rayo divino desciende desde los cielos...");
            int dano = Math.max(1, (int)(this.vida * 0.5)); // 50% de vida ACTUAL
            
            // Usamos recibirDanio (que ahora devuelve int)
            int danoReal = enemigo.recibirDanio(dano);
            log.append("\n" + enemigo.getNombre() + " recibe " + danoReal + " de daño directo del rayo divino. Vida: " + enemigo.getVida());

            try { registrarSupremo("Castigo Bendito"); } catch (Exception ignored) {}
            try { this.ultimoDanoCastigo = dano; } catch (Exception ignored) {}

            bendicion = 0; // consume la bendición
            return log.toString();        // termina el turno
        }

        // 2) Flujo normal (capturando los strings)
        // Esta línea ahora funcionará porque 'invocarArma' (línea 30) tiene el nombre correcto
        String logInvocacion = invocarArma(); 
        String logAtaque = atacar(enemigo);
        
        return logInvocacion + "\n" + logAtaque;
    }
    
    // --- ¡CORRECCIÓN AQUÍ! ---
    // 3. Borramos el método 'usarCastigoBendito' (líneas 66-82)
    //    porque ya no es necesario y usa System.out.println.
    
}