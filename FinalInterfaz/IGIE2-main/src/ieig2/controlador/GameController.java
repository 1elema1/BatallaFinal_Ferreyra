package ieig2.controlador;

import ieig2.modelo.*;
import ieig2.vista.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import ieig2.modelo.Item; // <--- Agrégalo junto a los otros imports

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

    private final int vidaInicialHeroe;
    private final int vidaInicialVillano;

    private final PersonajeDAO personajeDAO = new PersonajeDAO();
    private final BatallaDAO batallaDAO = new BatallaDAO();

    public GameController(Heroe heroe, Villano villano, HistorialBatallas hist, int currentBattle, int totalBattles) {
        this.h = heroe;
        this.v = villano;
        this.historial = hist;
        this.currentBattle = currentBattle;
        this.totalBattles = totalBattles;

        h.equiparItem(new Item("Poción de Salud", 50)); 
        
        this.vidaInicialHeroe = heroe.getVida();
        this.vidaInicialVillano = villano.getVida();

        this.view = new BatallaVista();

        bindMenu();
        refreshAll();

        this.view.setVisible(true);

        onEvent("\n==============================");
        onEvent("🏆 MODO TORNEO ACTIVADO");
        onEvent("Batallas totales: " + totalBattles);
        onEvent("Ítem inicial equipado: Poción de Salud (+50 Vida)");
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

        int heroeID = personajeDAO.insertarPersonaje(h, "HEROE");
        int villanoID = personajeDAO.insertarPersonaje(v, "VILLANO");
        batallaDAO.guardarBatalla(heroeID, villanoID, h.estaVivo() ? heroeID : villanoID, turnos);

        if (currentBattle < totalBattles) {
            currentBattle++;
            h.vida = vidaInicialHeroe;
            v.vida = vidaInicialVillano;
            turn = 1;
            batallaTerminada = false;
            refreshAll();
        } else {
            view.appendEvent("\n🏆 FIN DEL TORNEO. GANADOR: " + (victoriasHeroe > victoriasVillano ? h.getNombre() : v.getNombre()));
        }
    }

    private static class Mapper {
        static PersonajeVM toVM(Object p) {
            String nombre = callStr(p, "getNombre");
            int vida = callInt(p, "getVida");
            return new PersonajeVM(nombre, null, vida, 160, 0, "—", null, false);
        }
        static Object callObj(Object o, String m) {
            try { return o.getClass().getMethod(m).invoke(o); } catch (Exception e) { return null; }
        }
        static String callStr(Object o, String m) { Object r = callObj(o, m); return r == null ? "-" : r.toString(); }
        static int callInt(Object o, String m) { Object r = callObj(o, m); return (r instanceof Number) ? ((Number) r).intValue() : 0; }
    }
}