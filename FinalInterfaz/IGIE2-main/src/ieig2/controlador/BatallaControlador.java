package ieig2.controlador;

import ieig2.modelo.*;
import ieig2.vista.BatallaVistaConsola;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BatallaControlador {

    private BatallaVistaConsola vista;
    private Heroe heroe;
    private Villano villano;
    private HistorialBatallas historial;
    private Random rnd = new Random();
    private int turnoActual = 0;

    private PersonajeDAO personajeDAO = new PersonajeDAO();
    private BatallaDAO batallaDAO = new BatallaDAO();

    public BatallaControlador(BatallaVistaConsola vista) {
        this.vista = vista;
        try {
            this.historial = PersistenciaManager.cargarHistorial();
        } catch (IOException e) {
            this.historial = new HistorialBatallas();
            System.err.println("No se pudo cargar historial: " + e.getMessage());
        }
    }

    public void iniciarBatallaCon(Heroe h, Villano v) {
        this.heroe = h;
        this.villano = v;

        vista.mostrarMensaje("\nComienza la batalla");
        vista.mostrarEstadoPersonajes(heroe, villano);

        turnoActual = 0;
        while (heroe.estaVivo() && villano.estaVivo()) {
            turnoActual++;

            String logHeroe = heroe.decidirAccion(villano);
            vista.mostrarMensaje(logHeroe);
            if (!villano.estaVivo()) break;

            String logVillano = villano.decidirAccion(heroe);
            vista.mostrarMensaje(logVillano);

            vista.mostrarMensaje("\n--- Fin del Turno " + turnoActual + " ---");
            vista.mostrarEstadoPersonajes(heroe, villano);
        }

        finalizarBatalla(turnoActual);
    }

    public void iniciarBatalla() {
        setupBatalla();

        vista.mostrarMensaje("\nComienza la batalla");
        vista.mostrarEstadoPersonajes(heroe, villano);

        turnoActual = 0;
        while (heroe.estaVivo() && villano.estaVivo()) {
            turnoActual++;

            String logHeroe = heroe.decidirAccion(villano);
            vista.mostrarMensaje(logHeroe);

            if (!villano.estaVivo()) break;

            String logVillano = villano.decidirAccion(heroe);
            vista.mostrarMensaje(logVillano);

            vista.mostrarMensaje("\n--- Fin del Turno " + turnoActual + " ---");
            vista.mostrarEstadoPersonajes(heroe, villano);
        }

        finalizarBatalla(turnoActual);
    }

    private void setupBatalla() {

        String nombreHeroe = vista.pedirEntrada("Ingrese nombre del héroe: ");

        heroe = new Heroe(
                nombreHeroe,
                100 + rnd.nextInt(41),
                21 + rnd.nextInt(11),
                5 + rnd.nextInt(8),
                rnd.nextInt(102)
        );

        String nombreVillano = vista.pedirEntrada("Ingrese nombre del villano: ");

        villano = new Villano(
                nombreVillano,
                90 + rnd.nextInt(41),
                20 + rnd.nextInt(11),
                6 + rnd.nextInt(8),
                rnd.nextInt(101)
        );
    }

    private void finalizarBatalla(int turno) {

        String ganadorNombre = heroe.estaVivo() ? heroe.getNombre() : villano.getNombre();

        vista.mostrarGanador(ganadorNombre, turno);

        // =============================
        // GUARDAR EN BASE DE DATOS
        // =============================

        int heroeID = personajeDAO.insertarPersonaje(heroe, "HEROE");
        int villanoID = personajeDAO.insertarPersonaje(villano, "VILLANO");

        int ganadorID = heroe.estaVivo() ? heroeID : villanoID;

        batallaDAO.guardarBatalla(heroeID, villanoID, ganadorID, turno);

        // =============================
        // HISTORIAL (archivo viejo)
        // =============================

        String entrada = historial.crearEntradaBatalla(
                heroe.getNombre(),
                villano.getNombre(),
                ganadorNombre,
                turno
        );

        historial.guardarBatalla(entrada);

        try {
            PersistenciaManager.guardarHistorial(historial);
        } catch (IOException e) {
            System.err.println("No se pudo guardar historial: " + e.getMessage());
        }

        vista.mostrarMensaje(historial.obtenerHistorialComoString());

        // =============================
        // RANKING DESDE BD
        // =============================

        vista.mostrarMensaje("\n--- RANKING ---");

        List<String> ranking = personajeDAO.obtenerRanking();

        for (String r : ranking) {
            vista.mostrarMensaje(r);
        }

        // =============================
        // HISTORIAL DESDE BD
        // =============================

        vista.mostrarMensaje("\n--- HISTORIAL DESDE BASE DE DATOS ---");

        List<String> historialBD = batallaDAO.obtenerHistorial();

        for (String h : historialBD) {
            vista.mostrarMensaje(h);
        }

        vista.cerrarScanner();
    }
}
