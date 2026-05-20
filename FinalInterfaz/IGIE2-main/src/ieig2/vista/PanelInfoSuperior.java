package ieig2.vista;

import javax.swing.*;
import java.awt.*;

public class PanelInfoSuperior extends JPanel {
    private final JLabel lblPartida = new JLabel("Partida 1/1");
    private final JLabel lblTurno   = new JLabel("Turno 1");

    public PanelInfoSuperior() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Información de la partida"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,10,5,10);
        lblPartida.setFont(lblPartida.getFont().deriveFont(Font.BOLD, 16f));
        lblTurno.setFont(lblTurno.getFont().deriveFont(Font.PLAIN, 16f));
        c.gridx=0; c.gridy=0; add(lblPartida, c);
        c.gridx=1; c.gridy=0; add(new JSeparator(SwingConstants.VERTICAL), c);
        c.gridx=2; c.gridy=0; add(lblTurno, c);
    }
    public void setBattleInfo(int currentBattle, int totalBattles, int turn) {
        lblPartida.setText("Partida " + currentBattle + "/" + totalBattles);
        lblTurno.setText("Turno " + turn);
    }
}
