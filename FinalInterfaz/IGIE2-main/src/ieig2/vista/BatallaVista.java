package ieig2.vista;

import javax.swing.*;
import java.awt.*;



public class BatallaVista extends JFrame {

    // Menú
    public final JMenuItem miPausar = new JMenuItem("Pausar");
    public final JMenuItem miGuardar = new JMenuItem("Guardar partida");
    public final JMenuItem miAvanzar = new JMenuItem("Avanzar turno");
    public final JMenuItem miAuto = new JMenuItem("Simulación automática");
    public final JMenuItem miSalir = new JMenuItem("Salir");

    // Menú Reportes
    public final JMenuItem miHistorial = new JMenuItem("Historial de Batallas");
    public final JMenuItem miStats = new JMenuItem("Estadísticas generales");
    public final JMenuItem miRanking = new JMenuItem("Ranking de personajes");

    // Paneles principales
    private final PanelInfoSuperior top = new PanelInfoSuperior();
    private final JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
    private final PanelPersonaje left = new PanelPersonaje("Héroe");
    private final PanelPersonaje right = new PanelPersonaje("Villano");
    private final PanelEventos logPanel = new PanelEventos();

    public BatallaVista() {
        super("Simulación de Batalla");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(980, 640));
        setJMenuBar(buildMenu());
        add(top, BorderLayout.NORTH);
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        center.add(left); center.add(right);
        add(center, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pack(); setLocationRelativeTo(null);
    }

    private JMenuBar buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu mPartida = new JMenu("Partida");
        mPartida.add(miPausar); mPartida.add(miGuardar); mPartida.add(miAvanzar);
        mPartida.add(miAuto); mPartida.addSeparator(); mPartida.add(miSalir);
        JMenu mReportes = new JMenu("Reportes");
        mReportes.add(miHistorial); mReportes.add(miStats); mReportes.add(miRanking);
        bar.add(mPartida); bar.add(mReportes);
        return bar;
    }

    
    // ==== API para el Controlador ====
    public void setBattleInfo(int currentBattle, int totalBattles, int turn) { top.setBattleInfo(currentBattle, totalBattles, turn); }
    public void mostrarTextoLargo(String titulo, String contenido) {
        JTextArea area = new JTextArea(contenido == null ? "" : contenido, 22, 72);
        area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, sp, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    public void mostrarAviso(String msg) { JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE); }
    public void mostrarError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    public void updateLeft(PersonajeVM vm) { left.updateFrom(vm); }
    public void updateRight(PersonajeVM vm) { right.updateFrom(vm); }
    public void appendEvent(String text) { logPanel.append(text); }
}




