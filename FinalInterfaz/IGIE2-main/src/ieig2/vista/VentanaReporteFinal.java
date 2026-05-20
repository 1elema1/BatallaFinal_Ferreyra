package ieig2.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ieig2.modelo.PersistenciaManager;
import ieig2.modelo.HistorialBatallas;

public class VentanaReporteFinal extends JFrame {
    private final CardLayout layout = new CardLayout();
    private final JPanel panelPrincipal = new JPanel(layout);

    private JTable tablaRanking;
    private JTextArea areaStats;
    private JTextArea areaHistorial;

    public VentanaReporteFinal(String seccionInicial) {
        setTitle("Reporte Final del Juego");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear las tres secciones
        panelPrincipal.add(crearPanelRanking(), "ranking");
        panelPrincipal.add(crearPanelStats(), "stats");
        panelPrincipal.add(crearPanelHistorial(), "historial");
        add(panelPrincipal);

        // Mostrar la sección inicial
        layout.show(panelPrincipal, seccionInicial);

        // Cargar los datos al abrir
        cargarDatos();
    }

    // ==============================
    // PANEL RANKING
    // ==============================
    private JPanel crearPanelRanking() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Ranking de Personajes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String[] columnas = {"Nombre", "Apodo", "Tipo", "Vida Final", "Victorias", "Ataques Supremos"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        tablaRanking = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaRanking);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ==============================
    // PANEL ESTADÍSTICAS
    // ==============================
    private JPanel crearPanelStats() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Estadísticas Generales", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        areaStats = new JTextArea();
        areaStats.setEditable(false);
        areaStats.setFont(new Font("Consolas", Font.PLAIN, 13));
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaStats), BorderLayout.CENTER);

        return panel;
    }

    // ==============================
    // PANEL HISTORIAL
    // ==============================
    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Historial de Últimas 5 Partidas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(new Font("Consolas", Font.PLAIN, 13));
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaHistorial), BorderLayout.CENTER);

        return panel;
    }

    // ==============================
    // CARGA DE DATOS DESDE TXT
    // ==============================
    private void cargarDatos() {
        cargarRankingDesdeTXT();
        cargarEstadisticasDesdeTXT();
        cargarHistorialDesdeTXT();
    }

    private void cargarRankingDesdeTXT() {
        try {
            List<PersistenciaManager.PersonajeDTO> personajes = PersistenciaManager.cargarPersonajes();
            String[] columnas = {"Nombre", "Apodo", "Tipo", "Vida Final", "Victorias", "Ataques Supremos"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (PersistenciaManager.PersonajeDTO p : personajes) {
                String nombre = p.nombre;
                String apodo = null;
                int a = nombre.indexOf(" (");
                if (a > 0 && nombre.endsWith(")")) {
                    apodo = nombre.substring(a + 2, nombre.length() - 1);
                    nombre = nombre.substring(0, a);
                }
                String tipo = p.tipo;
                int vida = p.vida;
                int victorias = 0; // No guardamos victorias por personaje aún
                int ataquesSupremos = 0; // No guardamos supremos individuales en el TXT

                modelo.addRow(new Object[]{nombre, apodo == null ? "-" : apodo, tipo, vida, victorias, ataquesSupremos});
            }

            tablaRanking.setModel(modelo);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error cargando ranking: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEstadisticasDesdeTXT() {
        StringBuilder sb = new StringBuilder();
        try {
            // Cargar personajes
            List<PersistenciaManager.PersonajeDTO> personajes = PersistenciaManager.cargarPersonajes();

            // Mayor daño: usamos fuerza + bendición como aproximación
            PersistenciaManager.PersonajeDTO mayor = null;
            int mayorDanio = 0;
            for (PersistenciaManager.PersonajeDTO p : personajes) {
                int dano = p.fuerza + p.bendicion;
                if (dano > mayorDanio) {
                    mayorDanio = dano;
                    mayor = p;
                }
            }
            if (mayor != null) {
                sb.append("Mayor daño en un solo ataque: ")
                        .append(mayorDanio)
                        .append(" (")
                        .append(mayor.nombre)
                        .append(")\n");
            }

            // Batalla más larga
            HistorialBatallas historial = PersistenciaManager.cargarHistorial();
            sb.append("Batalla más larga: ")
                    .append(historial.getBatallaMasLargaTurnos())
                    .append(" turnos (Ganador: ")
                    .append(historial.getBatallaMasLargaGanador())
                    .append(")\n");

            // Armas invocadas y supremos
            if (personajes.size() >= 2) {
                sb.append("Armas invocadas: ")
                        .append(personajes.get(0).nombre).append("=?")
                        .append(", ").append(personajes.get(1).nombre).append("=?")
                        .append("\n");

                sb.append("Ataques supremos ejecutados: ")
                        .append(personajes.get(0).nombre).append("=?")
                        .append(", ").append(personajes.get(1).nombre).append("=?")
                        .append("\n");
            }

            areaStats.setText(sb.toString());
        } catch (IOException e) {
            areaStats.setText("Error cargando estadísticas: " + e.getMessage());
        }
    }

    private void cargarHistorialDesdeTXT() {
        try {
            HistorialBatallas historial = PersistenciaManager.cargarHistorial();
            String[] ultimas = historial.getHistorialBatallas();
            int cant = historial.getContadorBatallas();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cant; i++) {
                if (ultimas[i] != null) sb.append(ultimas[i]).append("\n");
            }

            areaHistorial.setText(sb.toString().isBlank() ? "No hay historial disponible todavía." : sb.toString());
        } catch (IOException e) {
            areaHistorial.setText("Error cargando historial: " + e.getMessage());
        }
    }

    // ==============================
    // TEST VISUAL (opcional)
    // ==============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaReporteFinal vrf = new VentanaReporteFinal("ranking");
            vrf.setVisible(true);
        });
    }
}
