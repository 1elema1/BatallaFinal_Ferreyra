package ieig2.vista;

import ieig2.modelo.*;
import ieig2.controlador.GameController;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ConfigInicialPanel extends JPanel {
    // Registro
    public JTextField txtNombre = new JTextField(14);
    public JTextField txtApodo  = new JTextField(12);
    public JComboBox<TipoPersonaje> cmbTipo =
            new JComboBox<>(TipoPersonaje.values());
    public JButton btnAgregar   = new JButton("Agregar personaje");
    public JButton btnEliminar  = new JButton("Eliminar seleccionado");
    public DefaultListModel<String> jugadoresModel = new DefaultListModel<>();
    public JList<String> lstJugadores = new JList<>(jugadoresModel);
    public JLabel lblError = new JLabel(" ");

    // Config de partida
    public JSpinner spVida      = new JSpinner(new SpinnerNumberModel(120,100,160,1));
    public JSpinner spFuerza    = new JSpinner(new SpinnerNumberModel(20,15,25,1));
    public JSpinner spDefensa   = new JSpinner(new SpinnerNumberModel(10,8,13,1));
    public JSpinner spBendicion = new JSpinner(new SpinnerNumberModel(60,30,100,1));
    public JComboBox<Integer> cmbBatallas = new JComboBox<>(new Integer[]{2,3,5});
    public JCheckBox chkSupremos = new JCheckBox("Activar ataques supremos", true);
    public JButton btnAleatorio = new JButton("Defaults aleatorios");
    public JButton btnCargar    = new JButton("Cargar batalla guardada...");

    // Control
    public JButton btnIniciar = new JButton("Iniciar batalla");
    public JButton btnSalir   = new JButton("Salir");

    public ConfigInicialPanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Registro ---
        JPanel reg = new JPanel(new GridBagLayout());
        reg.setBorder(BorderFactory.createTitledBorder("Registro de jugadores"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;
        int r=0;
        g.gridx=0; g.gridy=r; reg.add(new JLabel("Nombre:"), g);
        g.gridx=1; reg.add(txtNombre, g); r++;
        g.gridx=0; g.gridy=r; reg.add(new JLabel("Apodo:"), g);
        g.gridx=1; reg.add(txtApodo, g); r++;
        g.gridx=0; g.gridy=r; reg.add(new JLabel("Tipo:"), g);
        g.gridx=1; reg.add(cmbTipo, g); r++;
        g.gridx=0; g.gridy=r; reg.add(btnAgregar, g);
        g.gridx=1; reg.add(btnEliminar, g); r++;

        g.gridx=0; g.gridy=r; g.gridwidth=2;
        lblError.setForeground(new Color(180,0,0));
        reg.add(lblError, g);

        JPanel lista = new JPanel(new BorderLayout(4,4));
        lista.add(new JLabel("Personajes cargados:"), BorderLayout.NORTH);
        lstJugadores.setVisibleRowCount(8);
        lista.add(new JScrollPane(lstJugadores), BorderLayout.CENTER);

        JPanel izquierda = new JPanel(new BorderLayout(8,8));
        izquierda.add(reg, BorderLayout.NORTH);
        izquierda.add(lista, BorderLayout.CENTER);

        // --- Config ---
        JPanel cfg = new JPanel(new GridBagLayout());
        cfg.setBorder(BorderFactory.createTitledBorder("Configuración de partida"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4); c.fill = GridBagConstraints.HORIZONTAL;
        int i=0;
        c.gridx=0; c.gridy=i; cfg.add(new JLabel("Vida (100–160):"), c);
        c.gridx=1; cfg.add(spVida, c); i++;
        c.gridx=0; c.gridy=i; cfg.add(new JLabel("Fuerza (15–25):"), c);
        c.gridx=1; cfg.add(spFuerza, c); i++;
        c.gridx=0; c.gridy=i; cfg.add(new JLabel("Defensa (8–13):"), c);
        c.gridx=1; cfg.add(spDefensa, c); i++;
        c.gridx=0; c.gridy=i; cfg.add(new JLabel("Bendición (30–100):"), c);
        c.gridx=1; cfg.add(spBendicion, c); i++;
        c.gridx=0; c.gridy=i; cfg.add(new JLabel("Batallas:"), c);
        c.gridx=1; cfg.add(cmbBatallas, c); i++;
        c.gridx=0; c.gridy=i; c.gridwidth=2; cfg.add(chkSupremos, c); i++;
        c.gridx=0; c.gridy=i; c.gridwidth=2; cfg.add(btnAleatorio, c); i++;
        c.gridx=0; c.gridy=i; c.gridwidth=2; cfg.add(btnCargar, c);

        // --- Botonera ---
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnIniciar);
        acciones.add(btnSalir);

        add(izquierda, BorderLayout.CENTER);
        add(cfg, BorderLayout.EAST);
        add(acciones, BorderLayout.SOUTH);

        // ==========================================================
        // ACCIÓN: Cargar partida guardada
        // ==========================================================
        btnCargar.addActionListener(e -> {
            try {
                PersistenciaManager.PartidaCargada pc = PersistenciaManager.cargarPartida();
                if (pc == null) {
                    JOptionPane.showMessageDialog(this, "No hay partida guardada.", "Cargar partida", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Heroe h = new Heroe(pc.heroeNombre, pc.heroeVida, pc.heroeFuerza, pc.heroeDefensa, pc.heroeBendicion);
                Villano v = new Villano(pc.villanoNombre, pc.villanoVida, pc.villanoFuerza, pc.villanoDefensa, pc.villanoBendicion);

                HistorialBatallas hist;
                try {
                    hist = PersistenciaManager.cargarHistorial();
                } catch (IOException ex) {
                    hist = new HistorialBatallas();
                }

                // Cerramos esta ventana y abrimos la batalla cargada
                SwingUtilities.getWindowAncestor(this).dispose();
                new GameController(h, v, hist, 1, 1);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar partida: " + ex.getMessage(),
                        "Cargar partida", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
}
