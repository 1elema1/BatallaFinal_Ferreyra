package ieig2;

import ieig2.controlador.ConfigInicialControlador;
import ieig2.controlador.GameController;
import ieig2.modelo.*;
import ieig2.vista.ConfigInicialPanel;

import javax.swing.*;
import java.util.List;

public class IEIG2 {

    public static void main(String[] args) {

        // =============================
        // CREAR BASE DE DATOS Y TABLAS
        // =============================
        CrearTablasDB.crearTablas();

        SwingUtilities.invokeLater(() -> {

            JFrame f = new JFrame("IEIG2 — Configuración Inicial");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ConfigInicialPanel panel = new ConfigInicialPanel();

            ConfigInicialControlador.OnStartBattle cb = (List<Jugador> jugadores, ConfigPartida cfg) -> {

                Jugador jH = jugadores.stream()
                        .filter(j -> j.getTipo() == TipoPersonaje.HEROE)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Falta HÉROE"));

                Jugador jV = jugadores.stream()
                        .filter(j -> j.getTipo() == TipoPersonaje.VILLANO)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Falta VILLANO"));

                String nH = jH.getNombre() + " (" + jH.getApodo() + ")";
                String nV = jV.getNombre() + " (" + jV.getApodo() + ")";

                Heroe heroe = new Heroe(
                        nH,
                        cfg.vida,
                        cfg.fuerza,
                        cfg.defensa,
                        cfg.bendicion
                );

                Villano villano = new Villano(
                        nV,
                        cfg.vida,
                        cfg.fuerza,
                        cfg.defensa,
                        cfg.bendicion
                );

                HistorialBatallas hist;

                try {
                    hist = PersistenciaManager.cargarHistorial();
                } catch (Exception e) {
                    hist = new HistorialBatallas();
                    System.err.println("No se pudo cargar historial: " + e.getMessage());
                }

                f.dispose();

                // currentBattle = 1
                // totalBattles = cfg.batallas
                new GameController(
                        heroe,
                        villano,
                        hist,
                        1,
                        cfg.batallas
                );
            };

            new ConfigInicialControlador(panel, cb);

            f.setContentPane(panel);
            f.pack();
            f.setSize(900, 600);
            f.setLocationRelativeTo(null);
            f.setVisible(true);

        });
    }
}

