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

    public List<String> obtenerRanking() {

        List<String> ranking = new ArrayList<>();

        String sql = """
                SELECT nombre,victorias,derrotas
                FROM personajes
                ORDER BY victorias DESC
                """;

        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ranking.add(
                        rs.getString("nombre") +
                        " | V:" + rs.getInt("victorias") +
                        " | D:" + rs.getInt("derrotas")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error ranking: " + e.getMessage());
        }

        return ranking;
    }
}