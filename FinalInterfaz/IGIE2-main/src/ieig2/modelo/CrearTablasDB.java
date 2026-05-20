/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CrearTablasDB {

    public static void crearTablas() {

        String sqlPersonajes = """
        CREATE TABLE IF NOT EXISTS personajes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            apodo TEXT UNIQUE,
            tipo TEXT,
            vida_final INTEGER,
            victorias INTEGER DEFAULT 0,
            derrotas INTEGER DEFAULT 0,
            supremos_usados INTEGER DEFAULT 0,
            armas_invocadas INTEGER DEFAULT 0
        );
        """;

        String sqlBatallas = """
        CREATE TABLE IF NOT EXISTS batallas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
            heroe_id INTEGER,
            villano_id INTEGER,
            ganador_id INTEGER,
            turnos INTEGER,

            FOREIGN KEY (heroe_id) REFERENCES personajes(id),
            FOREIGN KEY (villano_id) REFERENCES personajes(id),
            FOREIGN KEY (ganador_id) REFERENCES personajes(id)
        );
        """;

        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement()) {

            st.execute(sqlPersonajes);
            st.execute(sqlBatallas);

            System.out.println("Base de datos lista");

        } catch (SQLException e) {
            System.out.println("Error creando tablas: " + e.getMessage());
        }
    }
}