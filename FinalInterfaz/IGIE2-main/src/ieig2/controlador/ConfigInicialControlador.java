package ieig2.controlador;

import ieig2.modelo.*;
import ieig2.vista.ConfigInicialPanel;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class ConfigInicialControlador {

    // Callback para entregar la selección (lo usa tu main para abrir la batalla)
    public interface OnStartBattle {
        void onStart(java.util.List<Jugador> jugadores, ConfigPartida cfg);
    }

    private final ConfigInicialPanel view;
    private final java.util.List<Jugador> jugadores = new ArrayList<>();
    private ConfigPartida cfg = ConfigPartida.aleatoria();
    private final OnStartBattle callback;

    public ConfigInicialControlador(ConfigInicialPanel view, OnStartBattle callback) {
        this.view = view;
        this.callback = callback;

        aplicarCfgEnVista();

        view.btnAleatorio.addActionListener(e -> { cfg = ConfigPartida.aleatoria(); aplicarCfgEnVista(); });
        view.btnSalir.addActionListener(e -> System.exit(0));
        view.btnAgregar.addActionListener(e -> onAgregar());
        view.btnEliminar.addActionListener(e -> onEliminar());
        view.btnCargar.addActionListener(e -> onCargar());
        view.btnIniciar.addActionListener(e -> onIniciar());
    }

    private void aplicarCfgEnVista() {
        view.spVida.setValue(cfg.vida);
        view.spFuerza.setValue(cfg.fuerza);
        view.spDefensa.setValue(cfg.defensa);
        view.spBendicion.setValue(cfg.bendicion);
        view.cmbBatallas.setSelectedItem(cfg.batallas);
        view.chkSupremos.setSelected(cfg.supremos);
    }

    // === Registro ===
    private void onAgregar() {
        String nombre = view.txtNombre.getText().trim();
        String apodo  = view.txtApodo.getText().trim();
        TipoPersonaje tipo = (TipoPersonaje) view.cmbTipo.getSelectedItem();

        List<String> errs = new ArrayList<>();
        if (nombre.isEmpty()) errs.add("El nombre no puede estar vacío.");
        if (apodo.isEmpty())  errs.add("El apodo no puede estar vacío.");
        if (tipo == null)     errs.add("Seleccione un tipo.");

        // Si querés usar tu validador Apodo:
        try { if (!apodo.isEmpty()) new Apodo(apodo); } 
        catch (IllegalArgumentException ex) { errs.add("Apodo inválido: " + ex.getMessage()); }

        // Regla adicional opcional (solo letras/espacios)
        Pattern p = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{2,30}$");
        if (!nombre.isEmpty() && !p.matcher(nombre).matches())
            errs.add("Nombre: solo letras/espacios (2–30).");

        boolean apodoExiste = jugadores.stream().anyMatch(j -> j.getApodo().equalsIgnoreCase(apodo));
        if (apodoExiste) errs.add("El apodo ya existe.");

        if (!errs.isEmpty()) { setError(String.join(" ", errs)); return; }

        Jugador j = new Jugador(nombre, apodo, tipo);
        jugadores.add(j);
        view.jugadoresModel.addElement(j.toString());
        view.txtNombre.setText(""); view.txtApodo.setText("");
        view.lblError.setText(" "); view.txtNombre.requestFocus();
    }

    private void onEliminar() {
        int idx = view.lstJugadores.getSelectedIndex();
        if (idx < 0) { setError("Seleccione un personaje para eliminar."); return; }
        setError(" ");
        String s = view.jugadoresModel.get(idx);
        int a = s.indexOf('('), b = s.indexOf(')');
        String apodo = (a>=0 && b>a) ? s.substring(a+1, b) : null;
        if (apodo != null) jugadores.removeIf(j -> j.getApodo().equalsIgnoreCase(apodo));
        view.jugadoresModel.remove(idx);
    }

    // === Archivo ===
    private void onCargar() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try { cargarDesdeArchivo(f); setError("Batalla cargada."); }
            catch (IOException ex) { setError("Error al cargar: " + ex.getMessage()); }
        }
    }

    private void cargarDesdeArchivo(File f) throws IOException {
        // Formato:
        // [JUGADORES]
        // nombre,apodo,HEROE
        // ...
        // [CONFIG]
        // vida=120
        // fuerza=20
        // defensa=10
        // bendicion=60
        // batallas=3
        // supremos=true
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String s; while ((s = br.readLine()) != null) lines.add(s.trim());
        }
        jugadores.clear(); view.jugadoresModel.clear();
        int i=0;
        while (i<lines.size() && !"[JUGADORES]".equalsIgnoreCase(lines.get(i))) i++; i++;
        while (i<lines.size() && !lines.get(i).startsWith("[")) {
            String ln = lines.get(i++); if (ln.isEmpty()) continue;
            String[] p = ln.split(",");
            if (p.length>=3) {
                Jugador j = new Jugador(p[0], p[1], TipoPersonaje.valueOf(p[2]));
                jugadores.add(j); view.jugadoresModel.addElement(j.toString());
            }
        }
        while (i<lines.size() && !"[CONFIG]".equalsIgnoreCase(lines.get(i))) i++; i++;
        Map<String,String> kv = new HashMap<>();
        while (i<lines.size() && !lines.get(i).startsWith("[")) {
            String ln = lines.get(i++); if (ln.contains("=")) {
                String[] p = ln.split("="); kv.put(p[0].trim(), p[1].trim());
            }
        }
        cfg.vida      = Integer.parseInt(kv.getOrDefault("vida","120"));
        cfg.fuerza    = Integer.parseInt(kv.getOrDefault("fuerza","20"));
        cfg.defensa   = Integer.parseInt(kv.getOrDefault("defensa","10"));
        cfg.bendicion = Integer.parseInt(kv.getOrDefault("bendicion","60"));
        cfg.batallas  = Integer.parseInt(kv.getOrDefault("batallas","3"));
        cfg.supremos  = Boolean.parseBoolean(kv.getOrDefault("supremos","true"));
        aplicarCfgEnVista();
    }

    // === Iniciar ===
    private void onIniciar() {
        long heroes   = jugadores.stream().filter(j -> j.getTipo()==TipoPersonaje.HEROE).count();
        long villanos = jugadores.stream().filter(j -> j.getTipo()==TipoPersonaje.VILLANO).count();
        List<String> errs = new ArrayList<>();
        if (heroes < 1 || villanos < 1) errs.add("Debe haber al menos 1 HÉROE y 1 VILLANO.");

        int vida = (Integer) view.spVida.getValue();
        int fuerza = (Integer) view.spFuerza.getValue();
        int defensa = (Integer) view.spDefensa.getValue();
        int bendicion = (Integer) view.spBendicion.getValue();
        if (vida<100 || vida>160) errs.add("Vida fuera de rango (100–160).");
        if (fuerza<15 || fuerza>25) errs.add("Fuerza fuera de rango (15–25).");
        if (defensa<8  || defensa>13) errs.add("Defensa fuera de rango (8–13).");
        if (bendicion<30 || bendicion>100) errs.add("Bendición fuera de rango (30–100).");
        if (!errs.isEmpty()) { setError(String.join(" ", errs)); return; }

        cfg.vida = vida; cfg.fuerza = fuerza; cfg.defensa = defensa;
        cfg.bendicion = bendicion;
        cfg.batallas = (Integer) view.cmbBatallas.getSelectedItem();
        cfg.supremos = view.chkSupremos.isSelected();

        if (callback != null) callback.onStart(Collections.unmodifiableList(jugadores), cfg);
    }

    private void setError(String msg) { view.lblError.setText(msg); }
}
