/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatallaDAO {

    public void guardarBatalla(int heroeID, int villanoID, int ganadorID, int turnos) {

        String sql = """
                INSERT INTO batallas(heroe_id,villano_id,ganador_id,turnos)
                VALUES(?,?,?,?)
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, heroeID);
            ps.setInt(2, villanoID);
            ps.setInt(3, ganadorID);
            ps.setInt(4, turnos);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error guardar batalla: " + e.getMessage());
        }
    }

    public List<String> obtenerHistorial() {

        List<String> lista = new ArrayList<>();

        String sql = """
                SELECT b.fecha,
                       p1.nombre as heroe,
                       p2.nombre as villano,
                       p3.nombre as ganador,
                       b.turnos
                FROM batallas b
                JOIN personajes p1 ON b.heroe_id = p1.id
                JOIN personajes p2 ON b.villano_id = p2.id
                JOIN personajes p3 ON b.ganador_id = p3.id
                ORDER BY b.fecha DESC
                LIMIT 10
                """;

        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                lista.add(
                        rs.getString("fecha") +
                        " | H:" + rs.getString("heroe") +
                        " vs V:" + rs.getString("villano") +
                        " | Ganador:" + rs.getString("ganador") +
                        " | Turnos:" + rs.getInt("turnos")
                );

            }

        } catch (SQLException e) {
            System.out.println("Error historial: " + e.getMessage());
        }

        return lista;
    }
}
