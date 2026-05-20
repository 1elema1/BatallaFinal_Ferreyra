package ieig2.vista;

import javax.swing.*;
import java.awt.*;

public class PanelEventos extends JPanel {
    private final JTextArea txt = new JTextArea(8, 20);
    private final JScrollPane scroll = new JScrollPane(txt);
    private boolean autoScroll = true;

    public PanelEventos() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Eventos"));
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        add(scroll, BorderLayout.CENTER);
    }
    public void append(String s) {
        if (s == null || s.isEmpty()) return;
        txt.append(s.endsWith("\n") ? s : s + "\n");
        if (autoScroll) txt.setCaretPosition(txt.getDocument().getLength());
    }
    public void setAutoScroll(boolean enabled) { this.autoScroll = enabled; }
}
