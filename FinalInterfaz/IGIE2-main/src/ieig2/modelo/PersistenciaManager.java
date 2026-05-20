/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase encargada de manejar la persistencia de datos (Punto 5).
 * Archivos sugeridos por la consigna:
 *  - historial_batallas.txt   (ultimas 5 batallas + numero global)
 *  - batalla_guardada.txt     (estado actual completo para reanudar)
 *  - personajes.txt           (estadísticas permanentes)
 *
 * Nota: esta implementación usa acceso directo a campos protegidos de Personaje
 * (fuerza, defensa, bendicion) por estar en el mismo paquete ieig2.modelo.
 */
public class PersistenciaManager {

    private static final String HISTORIAL_FILE  = "historial_batallas.txt";
    private static final String PARTIDA_FILE    = "batalla_guardada.txt";
    private static final String PERSONAJES_FILE = "personajes.txt";

    // ================================================================
    // 1) Persistencia de Historial (historial_batallas.txt)
    //    Formato:
    //      <numeroBatallaGlobal>
    //      BATALLA #X - Heroe: ... | Villano: ... | Ganador: ... | Turnos: N
    //      ...
    // ================================================================

    /** Guarda el historial de batallas (ultimas 5) + numero global. */
    public static void guardarHistorial(HistorialBatallas historial) throws IOException {
        Objects.requireNonNull(historial, "historial no puede ser null");
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(HISTORIAL_FILE), StandardCharsets.UTF_8)) {

            writer.write(String.valueOf(historial.getNumeroBatallaGlobal()));
            writer.newLine();

            String[] batallas = historial.getHistorialBatallas();
            int cant = historial.getContadorBatallas();
            for (int i = 0; i < cant; i++) {
                if (batallas[i] != null) {
                    writer.write(batallas[i]);
                    writer.newLine();
                }
            }
        }
    }

    /** Carga el historial: numero global + últimas 5 entradas. */
    public static HistorialBatallas cargarHistorial() throws IOException {
        HistorialBatallas historial = new HistorialBatallas();
        Path p = Paths.get(HISTORIAL_FILE);
        if (!Files.exists(p)) return historial;

        try (BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            // 1) número de batalla global
            String lineaNumeroBatalla = reader.readLine();
            if (lineaNumeroBatalla != null && !lineaNumeroBatalla.isEmpty()) {
                try {
                    historial.setNumeroBatallaGlobal(Integer.parseInt(lineaNumeroBatalla));
                } catch (NumberFormatException nfe) {
                    historial.setNumeroBatallaGlobal(0); // tolerante a errores
                }
            }

            // 2) entradas del historial
            String linea;
            while ((linea = reader.readLine()) != null) {
                historial.guardarBatalla(linea);
            }
        }
        return historial;
    }

    // ================================================================
    // 2) Persistencia de Estado de Partida (batalla_guardada.txt)
    //    Formato:
    //      TURNO:<int>
    //      HEROE:Nombre,vida,fuerza,defensa,bendicion
    //      VILLANO:Nombre,vida,fuerza,defensa,bendicion
    // ================================================================

    /**
     * Guarda el estado completo de la partida (para retomar).
     * Usa acceso directo a fuerza/defensa/bendicion por estar en mismo paquete.
     */
    public static void guardarPartida(Personaje heroe, Personaje villano, int turnoActual) throws IOException {
    System.out.println("[DEBUG] Working dir = " + System.getProperty("user.dir"));
    System.out.println("[DEBUG] Guardando en: " + Paths.get("batalla_guardada.txt").toAbsolutePath());

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(PARTIDA_FILE), StandardCharsets.UTF_8)) {

            writer.write("TURNO:" + turnoActual);
            writer.newLine();

            writer.write("HEROE:" +
                    sanitize(heroe.getNombre()) + "," +
                    heroe.getVida() + "," +
                    heroe.fuerza + "," +
                    heroe.defensa + "," +
                    heroe.bendicion);
            writer.newLine();

            writer.write("VILLANO:" +
                    sanitize(villano.getNombre()) + "," +
                    villano.getVida() + "," +
                    villano.fuerza + "," +
                    villano.defensa + "," +
                    villano.bendicion);
            writer.newLine();
        }
    }

    /** DTO para cargar partida sin acoplar a constructores concretos. */
    public static class PartidaCargada {
        public int turno;
        public String heroeNombre;
        public int heroeVida, heroeFuerza, heroeDefensa, heroeBendicion;
        public String villanoNombre;
        public int villanoVida, villanoFuerza, villanoDefensa, villanoBendicion;

        @Override public String toString() {
            return "PartidaCargada{turno=" + turno +
                    ", HEROE=(" + heroeNombre + "," + heroeVida + "," + heroeFuerza + "," + heroeDefensa + "," + heroeBendicion + ")" +
                    ", VILLANO=(" + villanoNombre + "," + villanoVida + "," + villanoFuerza + "," + villanoDefensa + "," + villanoBendicion + ")}";
        }
    }

    /** Carga el estado de la partida para reanudar. Devuelve null si no hay archivo. */
    public static PartidaCargada cargarPartida() throws IOException {
        Path p = Paths.get(PARTIDA_FILE);
        if (!Files.exists(p)) return null;

        PartidaCargada dto = new PartidaCargada();

        try (BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String linea;

            // TURNO
            linea = reader.readLine();
            if (linea == null || !linea.startsWith("TURNO:")) {
                throw new IOException("Formato inválido: falta línea TURNO");
            }
            dto.turno = parseEnteroDespuesDePrefijo(linea, "TURNO:");

            // HEROE
            linea = reader.readLine();
            if (linea == null || !linea.startsWith("HEROE:")) {
                throw new IOException("Formato inválido: falta línea HEROE");
            }
            String[] h = parseCSVDespuesDePrefijo(linea, "HEROE:", 5);
            dto.heroeNombre     = h[0];
            dto.heroeVida       = parseEntero(h[1], "HEROE.vida");
            dto.heroeFuerza     = parseEntero(h[2], "HEROE.fuerza");
            dto.heroeDefensa    = parseEntero(h[3], "HEROE.defensa");
            dto.heroeBendicion  = parseEntero(h[4], "HEROE.bendicion");

            // VILLANO
            linea = reader.readLine();
            if (linea == null || !linea.startsWith("VILLANO:")) {
                throw new IOException("Formato inválido: falta línea VILLANO");
            }
            String[] v = parseCSVDespuesDePrefijo(linea, "VILLANO:", 5);
            dto.villanoNombre    = v[0];
            dto.villanoVida      = parseEntero(v[1], "VILLANO.vida");
            dto.villanoFuerza    = parseEntero(v[2], "VILLANO.fuerza");
            dto.villanoDefensa   = parseEntero(v[3], "VILLANO.defensa");
            dto.villanoBendicion = parseEntero(v[4], "VILLANO.bendicion");
        }

        return dto;
    }

    // ================================================================
    // 3) Persistencia de Personajes Permanentes (personajes.txt)
    //    Formato por línea: TIPO;Nombre;Vida;Fuerza;Defensa;Bendicion
    //    TIPO ∈ {HEROE, VILLANO, PERSONAJE}
    // ================================================================

    /** Guarda la lista de personajes permanentes. */
    // Nuevo método: guarda personajes + bloque estadístico con turnos/ganador reales
public static void guardarPersonajes(
        java.util.List<Personaje> personajes,
        int turnosDeEstaBatalla,
        String ganadorNombre
) throws IOException {
    Objects.requireNonNull(personajes, "personajes no puede ser null");
    try (BufferedWriter w = Files.newBufferedWriter(Paths.get(PERSONAJES_FILE), StandardCharsets.UTF_8)) {
        // 1) Línea por personaje (lo mismo que antes)
        for (Personaje p : personajes) {
            String tipo = (p instanceof Heroe) ? "HEROE" :
                          (p instanceof Villano) ? "VILLANO" : "PERSONAJE";
            w.write(tipo + ";" +
                    sanitize(p.getNombre()) + ";" +
                    p.getVida() + ";" +
                    p.fuerza + ";" +
                    p.defensa + ";" +
                    p.bendicion);
            w.newLine();
        }

        // 2) Bloque estadístico (con datos reales que nos pasan)
        if (personajes.size() >= 2) {
            Personaje p1 = personajes.get(0);
            Personaje p2 = personajes.get(1);

            w.write("-----"); w.newLine();

            // Mayor daño en un ataque (tolerante si el getter no existe)
            int md1 = safeMayorDanio(p1);
            int md2 = safeMayorDanio(p2);
            if (md1 >= md2) {
                w.write("MAYOR_DAÑO: " + md1 + " (" + sanitize(p1.getNombre()) + ")"); w.newLine();
            } else {
                w.write("MAYOR_DAÑO: " + md2 + " (" + sanitize(p2.getNombre()) + ")"); w.newLine();
            }

            // Batalla más larga: usamos los turnos reales y el ganador que nos pasaron
            w.write("BATALLA_MAS_LARGA: " + turnosDeEstaBatalla +
                    " turnos (ganador: " + sanitize(ganadorNombre) + ")"); w.newLine();

            // Armas invocadas (cantidad de tipos invocados)
            w.write("ARMAS_INVOCADAS: " +
                    sanitize(p1.getNombre()) + "=" + (p1.getArmasInvocadas() == null ? 0 : p1.getArmasInvocadas().size()) + ", " +
                    sanitize(p2.getNombre()) + "=" + (p2.getArmasInvocadas() == null ? 0 : p2.getArmasInvocadas().size())); w.newLine();

            // Supremos
            w.write("ATAQUES_SUPREMOS: " +
                    sanitize(p1.getNombre()) + "=" + p1.getSupremosUsados() + ", " +
                    sanitize(p2.getNombre()) + "=" + p2.getSupremosUsados()); w.newLine();

            // % victorias (para esta ejecución simple: 100% para el tipo ganador)
            boolean ganoHeroe = (p1 instanceof Heroe && p1.getNombre().equals(ganadorNombre))
                             || (p2 instanceof Heroe && p2.getNombre().equals(ganadorNombre));
            int porcHeroe = ganoHeroe ? 100 : 0;
            int porcVillano = 100 - porcHeroe;
            w.write(String.format("PORCENTAJE_VICTORIAS: HEROE=%d%%, VILLANO=%d%%", porcHeroe, porcVillano)); w.newLine();
        }
    }
}

// helper tolerante si aún no tenés getMayorDanio() en todos
private static int safeMayorDanio(Personaje p) {
    try {
        Integer v = p.getMayorDanio();
        return (v == null) ? 0 : v;
    } catch (Throwable t) {
        return 0;
    }
}


    /** DTO para reconstruir personajes sin acoplar a constructores concretos. */
    public static class PersonajeDTO {
        public String tipo;   // "HEROE"/"VILLANO"/"PERSONAJE"
        public String nombre;
        public int vida, fuerza, defensa, bendicion;
    }

    /** Carga personajes permanentes desde archivo como DTOs. */
    public static List<PersonajeDTO> cargarPersonajes() throws IOException {
        List<PersonajeDTO> out = new ArrayList<>();
        Path p = Paths.get(PERSONAJES_FILE);
        if (!Files.exists(p)) return out;

        try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length != 6) continue; // línea inválida, la salto

                PersonajeDTO dto = new PersonajeDTO();
                dto.tipo      = parts[0].trim();
                dto.nombre    = parts[1].trim();
                dto.vida      = parseEntero(parts[2], "vida");
                dto.fuerza    = parseEntero(parts[3], "fuerza");
                dto.defensa   = parseEntero(parts[4], "defensa");
                dto.bendicion = parseEntero(parts[5], "bendicion");
                out.add(dto);
            }
        }
        return out;
    }

    // ================================================================
    // Utilitarios de parseo/seguridad simples
    // ================================================================

    private static String sanitize(String s) {
        // Evita comas y saltos de línea que rompan el CSV simple
        if (s == null) return "";
        return s.replace(",", " ").replace("\r", " ").replace("\n", " ").trim();
    }

    private static int parseEnteroDespuesDePrefijo(String linea, String prefijo) throws IOException {
        try {
            return Integer.parseInt(linea.substring(prefijo.length()).trim());
        } catch (Exception e) {
            throw new IOException("No se pudo parsear entero en: " + linea, e);
        }
    }

    private static int parseEntero(String val, String campo) throws IOException {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            throw new IOException("No se pudo parsear entero en " + campo + ": '" + val + "'", e);
        }
    }

    private static String[] parseCSVDespuesDePrefijo(String linea, String prefijo, int esperados) throws IOException {
        String datos = linea.substring(prefijo.length()).trim();
        String[] arr = datos.split(",", -1);
        if (arr.length != esperados) {
            throw new IOException("Cantidad de campos inválida en " + prefijo + " (esperados " + esperados + ", got " + arr.length + ")");
        }
        return arr;
    }
}
