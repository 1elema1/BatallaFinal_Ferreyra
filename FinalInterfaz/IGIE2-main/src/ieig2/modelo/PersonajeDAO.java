/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonajeDAO {

    public int insertarPersonaje(Personaje p, String tipo) {

        String sql = "INSERT INTO personajes(nombre,tipo,vida_final,victorias,derrotas,supremos_usados,armas_invocadas) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, tipo);
            ps.setInt(3, p.getVida());
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setInt(6, p.getSupremosUsados());
            ps.setInt(7, p.getArmasInvocadas().size());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error insert personaje: " + e.getMessage());
        }

        return -1;
    }

    public void actualizarEstadisticas(int id, boolean gano, int supremos, int armas) {

        String sql = """
                UPDATE personajes
                SET victorias = victorias + ?,
                    derrotas = derrotas + ?,
                    supremos_usados = supremos_usados + ?,
                    armas_invocadas = armas_invocadas + ?
                WHERE id = ?
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gano ? 1 : 0);
            ps.setInt(2, gano ? 0 : 1);
            ps.setInt(3, supremos);
            ps.setInt(4, armas);
            ps.setInt(5, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error update stats: " + e.getMessage());
        }
    }

    public java.util.List<String> obtenerRanking() {
        java.util.List<String> ranking = new java.util.ArrayList<>();

        // Usamos SUM para acumular victorias y supremos, y GROUP BY para no repetir personajes
        String sql = """
                SELECT nombre, 
                       MAX(tipo) AS tipo, 
                       MAX(vida_final) AS vida_final, 
                       SUM(victorias) AS total_victorias, 
                       SUM(supremos_usados) AS total_supremos
                FROM personajes
                GROUP BY nombre
                ORDER BY total_victorias DESC
                """;

        try (java.sql.Connection conn = ieig2.modelo.ConexionDB.conectar();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ranking.add(
                        rs.getString("nombre") + "|" +
                        rs.getString("tipo") + "|" +
                        rs.getInt("vida_final") + "|" +
                        rs.getInt("total_victorias") + "|" +
                        rs.getInt("total_supremos")
                );
            }

        } catch (java.sql.SQLException e) {
            System.out.println("Error ranking: " + e.getMessage());
        }

        return ranking;
    }
}