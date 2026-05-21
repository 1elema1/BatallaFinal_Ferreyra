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
        
        // --- BOTÓN ACTUALIZAR ---
        JButton btnActualizar = new JButton("🔄 Actualizar Datos desde BD");
        btnActualizar.addActionListener(e -> cargarDatosDesdeBD());
        JPanel panelInferior = new JPanel();
        panelInferior.add(btnActualizar);
        add(panelInferior, BorderLayout.SOUTH);

        layout.show(panelPrincipal, seccionInicial);
        cargarDatosDesdeBD();

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
        cargarRankingDesdeBD();
        cargarEstadisticasDesdeBD();
        cargarHistorialDesdeTXT();
    }
    private void cargarDatosDesdeBD() {
        // Carga Ranking
        ieig2.modelo.PersonajeDAO dao = new ieig2.modelo.PersonajeDAO();
        List<String> ranking = dao.obtenerRanking();
        DefaultTableModel modelo = (DefaultTableModel) tablaRanking.getModel();
        modelo.setRowCount(0);

        for (String fila : ranking) {
            String[] p = fila.split("\\|");
            if (p.length >= 5) {
                modelo.addRow(new Object[]{p[0], "-", p[1], p[2], p[3], p[4]});
            }
        }
        
        // Cargar Historial
        ieig2.modelo.BatallaDAO bDao = new ieig2.modelo.BatallaDAO();
        List<String> hist = bDao.obtenerHistorial();
        areaHistorial.setText(String.join("\n", hist));
        
        areaStats.setText("Datos actualizados desde base de datos.");
    }

    private void cargarRankingDesdeBD() {
        try {
            ieig2.modelo.PersonajeDAO dao = new ieig2.modelo.PersonajeDAO();
            java.util.List<String> ranking = dao.obtenerRanking();
            
            // Volvemos a poner las 6 columnas
            String[] columnas = {"Nombre", "Apodo", "Tipo", "Vida Final", "Victorias", "Ataques Supremos"};
            javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(columnas, 0);

            for (String fila : ranking) {
                String[] partes = fila.split("\\|");
                if (partes.length >= 5) {
                    String nombreCompleto = partes[0];
                    String tipo = partes[1];
                    String vida = partes[2];
                    String victorias = partes[3];
                    String supremos = partes[4]; // Recuperamos los ataques supremos
                    
                    String nombreReal = nombreCompleto;
                    String apodo = "-";
                    int a = nombreCompleto.indexOf(" (");
                    if (a > 0 && nombreCompleto.endsWith(")")) {
                        apodo = nombreCompleto.substring(a + 2, nombreCompleto.length() - 1);
                        nombreReal = nombreCompleto.substring(0, a);
                    }

                    // Agregamos la fila con todos los datos
                    modelo.addRow(new Object[]{nombreReal, apodo, tipo, vida, victorias, supremos});
                }
            }
            tablaRanking.setModel(modelo);
        } catch (Exception e) {
            System.out.println("Error al cargar ranking en la tabla: " + e.getMessage());
        }
    }

    private void cargarEstadisticasDesdeBD() {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 ESTADÍSTICAS GENERALES (Desde Base de Datos)\n");
        sb.append("===============================================\n\n");

        // 1. Obtener totales de ataques supremos y armas invocadas
        String sqlTotales = "SELECT SUM(supremos_usados) AS tot_supremos, SUM(armas_invocadas) AS tot_armas FROM personajes";
        try (java.sql.Connection conn = ieig2.modelo.ConexionDB.conectar();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sqlTotales)) {
             
             if (rs.next()) {
                 sb.append("⚔️ Total de Ataques Supremos usados: ").append(rs.getInt("tot_supremos")).append("\n");
                 sb.append("🛡️ Total de Armas Invocadas en el juego: ").append(rs.getInt("tot_armas")).append("\n\n");
             }
        } catch (java.sql.SQLException e) {
            System.out.println("Error estadísticas totales: " + e.getMessage());
        }

        // 2. Obtener la batalla más larga registrada y total de partidas
        String sqlBatallas = "SELECT COUNT(*) AS total_batallas, MAX(turnos) AS max_turnos FROM batallas";
        try (java.sql.Connection conn = ieig2.modelo.ConexionDB.conectar();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sqlBatallas)) {
             
             if (rs.next()) {
                 sb.append("🎮 Cantidad de batallas disputadas: ").append(rs.getInt("total_batallas")).append("\n");
                 if (rs.getInt("max_turnos") > 0) {
                     sb.append("⏳ Batalla más larga registrada: ").append(rs.getInt("max_turnos")).append(" turnos\n\n");
                 } else {
                     sb.append("⏳ Batalla más larga registrada: 0 turnos\n\n");
                 }
             }
        } catch (java.sql.SQLException e) {
            System.out.println("Error estadísticas batallas: " + e.getMessage());
        }

        // 3. Obtener el personaje con más victorias (Líder del Ranking)
        String sqlLider = "SELECT nombre, victorias FROM personajes ORDER BY victorias DESC LIMIT 1";
        try (java.sql.Connection conn = ieig2.modelo.ConexionDB.conectar();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sqlLider)) {
             
             if (rs.next() && rs.getInt("victorias") > 0) {
                 sb.append("🏆 Líder invicto actual: ").append(rs.getString("nombre"))
                   .append(" con ").append(rs.getInt("victorias")).append(" victorias\n");
             } else {
                 sb.append("🏆 Líder invicto actual: Sin registros aún\n");
             }
        } catch (java.sql.SQLException e) {
            System.out.println("Error estadísticas líder: " + e.getMessage());
        }

        areaStats.setText(sb.toString());
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