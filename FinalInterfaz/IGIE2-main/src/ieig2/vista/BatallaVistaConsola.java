// Source code rewritten (mejorado para consola)
// Ubicación: src/ieig2/vista/BatallaVistaConsola.java
package ieig2.vista;

import ieig2.modelo.Heroe;
import ieig2.modelo.Villano;
import java.util.Scanner;

public class BatallaVistaConsola {
    private final Scanner sc;

    public BatallaVistaConsola() {
        this.sc = new Scanner(System.in);
    }

    /* ==========================
       Salida por consola
       ========================== */
    public void mostrarMensaje(String mensaje) {
        if (mensaje == null) return;
        System.out.println(mensaje);
    }

    /* ==========================
       Entrada por consola
       ========================== */
    public String pedirEntrada(String prompt) {
        if (prompt != null && !prompt.isBlank()) {
            System.out.print(prompt + " ");
        }
        String line = sc.nextLine();
        return line == null ? "" : line.trim();
    }

    /**
     * Pide un apodo para un tipo de personaje (Héroe/Villano).
     * Valida: no vacío, longitud 2..20, solo letras/números/espacios/_-.
     */
    public String pedirApodo(String tipoPersonaje) {
        final String tipo = (tipoPersonaje == null || tipoPersonaje.isBlank())
                ? "personaje" : tipoPersonaje;
        final String regex = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 _-]{2,20}$";

        while (true) {
            System.out.print("Ingresá un apodo para el " + tipo + " (2–20 caracteres): ");
            String apodo = sc.nextLine();
            if (apodo == null) apodo = "";
            apodo = apodo.trim();

            if (apodo.matches(regex)) {
                return apodo;
            }
            System.out.println("✖ Apodo inválido. Permitidos letras, números, espacios, guion y guion bajo.");
        }
    }

    /* ==========================
       Estado de personajes
       ========================== */
    public void mostrarEstadoPersonajes(Heroe h, Villano v) {
        System.out.println("\n================= ESTADO DE BATALLA =================");
        // --- HÉROE ---
        String hNombre   = safeNombre(h);
        String hApodo    = safeApodo(h);
        int hVida        = safeVida(h);      
        int hVidaMax     = safeVidaMax(h);  
        int hBend        = safeBendicion(h); 
        String hArma     = safeArma(h);         

        // --- VILLANO ---
        String vNombre   = safeNombre(v);
        String vApodo    = safeApodo(v);
        int vVida        = safeVida(v);
        int vVidaMax     = safeVidaMax(v);
        int vBend        = safeBendicion(v);
        String vArma     = safeArma(v);

        // Impresión bonita con barras
        System.out.printf("HÉROE    : %s (%s)%n", hNombre, hApodo);
        System.out.printf("  Vida   : %s %d/%d%n", barra(hVida, hVidaMax, 24), hVida, hVidaMax);
        System.out.printf("  Bendic.: %s %d%%%n", barra(hBend, 100, 24), hBend);
        System.out.printf("  Arma   : %s%n", hArma);

        System.out.println("-----------------------------------------------------");

        System.out.printf("VILLANO  : %s (%s)%n", vNombre, vApodo);
        System.out.printf("  Vida   : %s %d/%d%n", barra(vVida, vVidaMax, 24), vVida, vVidaMax);
        System.out.printf("  Bendic.: %s %d%%%n", barra(vBend, 100, 24), vBend);
        System.out.printf("  Arma   : %s%n", vArma);

        System.out.println("=====================================================\n");
    }

    /* ==========================
       Ganador
       ========================== */
    public void mostrarGanador(String ganadorNombre, int turnos) {
        System.out.println("\n🏁 FIN DE BATALLA");
        System.out.println("Ganador: " + (ganadorNombre == null ? "(desconocido)" : ganadorNombre));
        System.out.println("Turnos: " + turnos);
        System.out.println("Gracias por jugar.\n");
    }

    /* ==========================
       Recursos
       ========================== */
    public void cerrarScanner() {
        try {
            if (this.sc != null) this.sc.close();
        } catch (Exception ignored) { }
    }

    /* ==========================
       Utilitarios
       ========================== */
    public void pausa(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // Dibuja una barra ASCII [#####-----] proporcionada al valor/max
    private String barra(int valor, int max, int ancho) {
        if (max <= 0) max = 1;
        if (valor < 0) valor = 0;
        if (valor > max) valor = max;
        if (ancho < 6) ancho = 6;

        int llenos = (int) Math.round((valor * 1.0 / max) * ancho);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ancho; i++) sb.append(i < llenos ? '#' : '-');
        sb.append(']');
        return sb.toString();
    }

    /* ==========================
       Safe getters (ajustá si tu modelo usa otros nombres)
       ========================== */
    // Para Héroe / Villano comparto helpers con sobrecarga simple

    private String safeNombre(Object p) {
        try { return (String) p.getClass().getMethod("getNombre").invoke(p); }
        catch (Exception ignored) { }
        try { return (String) p.getClass().getMethod("nombre").invoke(p); }
        catch (Exception ignored) { }
        return "Desconocido";
    }

    private String safeApodo(Object p) {
        try { return (String) p.getClass().getMethod("getApodo").invoke(p); }
        catch (Exception ignored) { }
        try { return (String) p.getClass().getMethod("apodo").invoke(p); }
        catch (Exception ignored) { }
        return "-";
    }

    private int safeVida(Object p) {
        try { return (int) p.getClass().getMethod("getVida").invoke(p); }
        catch (Exception ignored) { }
        try { return (int) p.getClass().getMethod("getVidaActual").invoke(p); }
        catch (Exception ignored) { }
        try { return (int) p.getClass().getMethod("vida").invoke(p); }
        catch (Exception ignored) { }
        return 0;
    }

    private int safeVidaMax(Object p) {
        try { return (int) p.getClass().getMethod("getVidaMax").invoke(p); }
        catch (Exception ignored) { }
        try { return (int) p.getClass().getMethod("getVidaMaxima").invoke(p); }
        catch (Exception ignored) { }
        try { return (int) p.getClass().getMethod("vidaMax").invoke(p); }
        catch (Exception ignored) { }
        return 100;
    }

    private int safeBendicion(Object p) {
        try { return (int) p.getClass().getMethod("getBendicion").invoke(p); }
        catch (Exception ignored) { }
        try { return (int) p.getClass().getMethod("getBendicionActual").invoke(p); }
        catch (Exception ignored) { }
        try { return (int) p.getClass().getMethod("bendicion").invoke(p); }
        catch (Exception ignored) { }
        return 0;
    }

    private String safeArma(Object p) {
        try { 
            Object arma = p.getClass().getMethod("getArma").invoke(p);
            return arma == null ? "-" : arma.toString();
        } catch (Exception ignored) { }
        try { 
            Object arma = p.getClass().getMethod("arma").invoke(p);
            return arma == null ? "-" : arma.toString();
        } catch (Exception ignored) { }
        return "-";
    }
}
