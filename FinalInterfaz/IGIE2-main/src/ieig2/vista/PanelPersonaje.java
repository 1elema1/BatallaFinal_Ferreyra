package ieig2.vista;

import javax.swing.*;
import java.awt.*;

public class PanelPersonaje extends JPanel {
    private final JLabel lblNombre = new JLabel("Nombre — Apodo");
    private final JProgressBar pbVida = new JProgressBar(0, 160);  // max 160 según consigna
    private final JProgressBar pbBend = new JProgressBar(0, 100);  // 0–100
    private final JLabel lblArma  = new JLabel("Arma: —");
    private final JLabel lblEstado = new JLabel(" ");

    public PanelPersonaje(String rol) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(rol),
                BorderFactory.createEmptyBorder(8,8,8,8)));
        pbVida.setStringPainted(true);
        pbBend.setStringPainted(true);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; c.gridwidth=2; add(lblNombre, c);
        c.gridy++; add(new JLabel("Vida"), c);
        c.gridy++; add(pbVida, c);
        c.gridy++; add(new JLabel("Bendición"), c);
        c.gridy++; add(pbBend, c);
        c.gridy++; add(lblArma, c);
        c.gridy++; add(lblEstado, c);
    }

    public void updateFrom(PersonajeVM vm) {
        lblNombre.setText(vm.nombre + (vm.apodo == null || vm.apodo.isBlank() ? "" : " — " + vm.apodo));

        pbVida.setMaximum(vm.vidaMax);
        pbVida.setValue(Math.max(0, Math.min(vm.vida, vm.vidaMax)));
        pbVida.setString(vm.vida + "/" + vm.vidaMax);

        pbBend.setValue(Math.max(0, Math.min(vm.bendicion, 100)));
        pbBend.setString(vm.bendicion >= 0 ? (vm.bendicion + "%") : "—");

        lblArma.setText("Arma: " + (vm.arma == null ? "—" : vm.arma));
        lblEstado.setText(vm.estadoEspecial == null ? " " : vm.estadoEspecial);

        setCritical(vm.enCritico);
    }

    private void setCritical(boolean critical) {
        if (critical) { setBackground(new Color(255, 235, 238)); setOpaque(true); }
        else { setOpaque(false); }
        repaint();
    }
}
