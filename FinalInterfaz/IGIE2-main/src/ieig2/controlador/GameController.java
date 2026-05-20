package ieig2.controlador;

import ieig2.modelo.*;
import ieig2.vista.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;

public class GameController {

    private final BatallaVista view;
    private final Heroe h;
    private final Villano v;
    private final HistorialBatallas historial;

    private int currentBattle;
    private final int totalBattles;
    private int turn = 1;

    private boolean paused = false;
    private Timer autoTimer;
    private boolean batallaTerminada = false;

    private int victoriasHeroe = 0;
    private int victoriasVillano = 0;

    // guardar vida inicial para restaurar entre batallas
    private final int vidaInicialHeroe;
    private final int vidaInicialVillano;

    // Base de datos
    private final PersonajeDAO personajeDAO = new PersonajeDAO();
    private final BatallaDAO batallaDAO = new BatallaDAO();

    public GameController(Heroe heroe, Villano villano, HistorialBatallas hist, int currentBattle, int totalBattles) {

        this.h = heroe;
        this.v = villano;
        this.historial = hist;
        this.currentBattle = currentBattle;
        this.totalBattles = totalBattles;

        this.vidaInicialHeroe = heroe.getVida();
        this.vidaInicialVillano = villano.getVida();

        this.view = new BatallaVista();

        bindMenu();
        refreshAll();

        this.view.setVisible(true);

        // ====================
        // EXTRA → TORNEO
        // ====================
        onEvent("\n==============================");
        onEvent("🏆 MODO TORNEO ACTIVADO");
        onEvent("Batallas totales: " + totalBattles);
        onEvent("==============================\n");
    }

    private void bindMenu() {

        view.miPausar.addActionListener(this::onPausar);
        view.miGuardar.addActionListener(this::onGuardar);
        view.miSalir.addActionListener(e -> System.exit(0));
        view.miAvanzar.addActionListener(e -> advanceOneTurn());
        view.miAuto.addActionListener(e -> toggleAuto());

        view.miRanking.addActionListener(e -> abrirReporte("ranking"));
        view.miStats.addActionListener(e -> abrirReporte("stats"));
        view.miHistorial.addActionListener(e -> abrirReporte("historial"));
    }

    private void abrirReporte(String seccion) {
        VentanaReporteFinal vrf = new VentanaReporteFinal(seccion);
        vrf.setVisible(true);
    }

    private void onPausar(ActionEvent e) {

        paused = !paused;
        onEvent(paused ? "Partida en pausa" : "Partida reanudada");
    }

    private void onGuardar(ActionEvent e) {

        try {

            PersistenciaManager.guardarPartida(h, v, turn);
            onEvent("Partida guardada correctamente (turno " + turn + ").");

            JOptionPane.showMessageDialog(view, "Partida guardada.\nTurno: " + turn);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(view, "Error al guardar: " + ex.getMessage());
        }
    }

    public void advanceOneTurn() {

        if (paused) {
            onEvent("(pausado)");
            return;
        }

        if (isFinished()) {
            checkEndAndShowReportIfNeeded();
            return;
        }

        String logH = h.decidirAccion(v);
        if (!logH.isBlank()) onEvent(logH);

        if (!v.estaVivo()) {
            endTurn();
            return;
        }

        String logV = v.decidirAccion(h);
        if (!logV.isBlank()) onEvent(logV);

        endTurn();
    }

    private void endTurn() {

        onEvent("\n--- Fin del Turno " + turn + " ---");

        turn++;

        refreshAll();

        checkEndAndShowReportIfNeeded();
    }

    private boolean isFinished() {
        return !h.estaVivo() || !v.estaVivo();
    }

    private void toggleAuto() {

        if (autoTimer != null && autoTimer.isRunning()) {

            autoTimer.stop();
            onEvent("(auto) detenido");
            return;
        }

        autoTimer = new Timer(900, e -> {

            if (paused) return;

            if (isFinished()) {

                ((Timer) e.getSource()).stop();
                checkEndAndShowReportIfNeeded();
                return;
            }

            advanceOneTurn();
        });

        onEvent("(auto) iniciado");
        autoTimer.start();
    }

    public void onEvent(String text) {
        view.appendEvent(text);
    }

    public void refreshAll() {

        view.setBattleInfo(currentBattle, totalBattles, turn);
        view.updateLeft(Mapper.toVM(h));
        view.updateRight(Mapper.toVM(v));
    }

    public void checkEndAndShowReportIfNeeded() {

        if (batallaTerminada) return;
        if (!isFinished()) return;

        batallaTerminada = true;

        view.appendEvent("¡Fin de batalla!");

        String ganador = h.estaVivo() ? h.getNombre() : v.getNombre();
        int turnos = turn - 1;

        if (h.estaVivo()) victoriasHeroe++;
        else victoriasVillano++;

        JOptionPane.showMessageDialog(view,
                "Ganador batalla " + currentBattle + ": " + ganador + "\nTurnos: " + turnos);

        int heroeID = personajeDAO.insertarPersonaje(h, "HEROE");
        int villanoID = personajeDAO.insertarPersonaje(v, "VILLANO");

        int ganadorID = h.estaVivo() ? heroeID : villanoID;

        batallaDAO.guardarBatalla(heroeID, villanoID, ganadorID, turnos);

        String entrada = historial.crearEntradaBatalla(
                h.getNombre(),
                v.getNombre(),
                ganador,
                turnos
        );

        historial.guardarBatalla(entrada);

        try {
            PersistenciaManager.guardarHistorial(historial);
        } catch (Exception ex) {
            System.err.println("No se pudo guardar historial: " + ex.getMessage());
        }

        try {

            PersistenciaManager.guardarPersonajes(
                    Arrays.asList(h, v),
                    turnos,
                    ganador
            );

        } catch (IOException e) {

            System.err.println("No se pudo guardar personajes.txt: " + e.getMessage());
        }

        view.appendEvent("\n--- RANKING ---");

        for (String r : personajeDAO.obtenerRanking()) {
            view.appendEvent(r);
        }

        view.appendEvent("\n--- HISTORIAL BD ---");

        for (String h : batallaDAO.obtenerHistorial()) {
            view.appendEvent(h);
        }

        mostrarLogros(turnos);

        // ====================
        // MODO TORNEO
        // ====================

        if (currentBattle < totalBattles) {

            currentBattle++;

            view.appendEvent("\n🏆 TORNEO CONTINÚA");
            view.appendEvent("⚔ BATALLA " + currentBattle + " DE " + totalBattles);
            view.appendEvent("=========================\n");

            // restaurar vida
            h.vida = vidaInicialHeroe;
            v.vida = vidaInicialVillano;

            turn = 1;
            batallaTerminada = false;

            refreshAll();

        } else {

            String ganadorTorneo;

            if (victoriasHeroe > victoriasVillano)
                ganadorTorneo = h.getNombre();
            else if (victoriasVillano > victoriasHeroe)
                ganadorTorneo = v.getNombre();
            else
                ganadorTorneo = "EMPATE";

            view.appendEvent("\n====================");
            view.appendEvent("🏆 FIN DEL TORNEO");
            view.appendEvent("====================");

            view.appendEvent("Victorias Héroe: " + victoriasHeroe);
            view.appendEvent("Victorias Villano: " + victoriasVillano);
            view.appendEvent("🏆 GANADOR DEL TORNEO: " + ganadorTorneo);
        }
    }

    private void mostrarLogros(int turnos) {

        view.appendEvent("\n--- LOGROS DESBLOQUEADOS ---");

        boolean logro = false;

        if (turnos < 15) {

            view.appendEvent("⚔ Maestro de batalla → ganar en menos de 15 turnos");
            logro = true;
        }

        int armasHeroe = h.getArmasInvocadas().size();
        int armasVillano = v.getArmasInvocadas().size();

        if (armasHeroe >= 3 || armasVillano >= 3) {

            view.appendEvent("🔥 Invocador → invocar 3 o más armas");
            logro = true;
        }

        int vidaGanador = h.estaVivo() ? h.getVida() : v.getVida();

        if (vidaGanador > 100) {

            view.appendEvent("💀 Dominador → ganar con más de 100 de vida restante");
            logro = true;
        }

        if (!logro) {
            view.appendEvent("(ningún logro desbloqueado)");
        }
    }

    private static class Mapper {

        static PersonajeVM toVM(Object p) {

            String nombre = callStr(p, "getNombre");
            int vida = callInt(p, "getVida");
            int vidaMax = callInt(p, "getVidaMax");

            if (vidaMax <= 0) vidaMax = 160;

            int bend = clamp(tryGet(p, "getBendicion"), 0, 100);

            String arma = String.valueOf(callObj(p, "getArma"));
            if ("null".equals(arma)) arma = "—";

            boolean critico = vida <= Math.max(1, (int) (vidaMax * 0.15));

            return new PersonajeVM(nombre, null, vida, vidaMax, bend, arma, null, critico);
        }

        static int clamp(int v, int lo, int hi) {
            return Math.max(lo, Math.min(hi, v));
        }

        static Object callObj(Object o, String m) {
            try {
                return o.getClass().getMethod(m).invoke(o);
            } catch (Exception e) {
                return null;
            }
        }

        static String callStr(Object o, String m) {
            Object r = callObj(o, m);
            return r == null ? "-" : r.toString();
        }

        static int callInt(Object o, String m) {
            Object r = callObj(o, m);
            return (r instanceof Number) ? ((Number) r).intValue() : 0;
        }

        static int tryGet(Object o, String m) {
            Object r = callObj(o, m);
            return (r instanceof Number) ? ((Number) r).intValue() : -1;
        }
    }
}