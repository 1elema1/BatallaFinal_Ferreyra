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

        // 1. Extraer nombre y apodo de la cadena "Nombre (Apodo)"
        String nombreReal = p.getNombre();
        String apodo = p.getNombre(); 
        
        if (p.getNombre().contains("(") && p.getNombre().contains(")")) {
            int start = p.getNombre().indexOf("(");
            int end = p.getNombre().indexOf(")");
            apodo = p.getNombre().substring(start + 1, end);
            nombreReal = p.getNombre().substring(0, start).trim();
        } else {
            // Si por algún motivo no tiene paréntesis, le agregamos la hora para que sea único
            apodo = apodo + "_" + System.currentTimeMillis();
        }

        // 2. Buscar si ya existe en la BD por su apodo (para respetar el UNIQUE)
        String sqlSelect = "SELECT id FROM personajes WHERE apodo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
            psSelect.setString(1, apodo);
            ResultSet rs = psSelect.executeQuery();
            if (rs.next()) {
                // Si el personaje ya existe, devolvemos su ID y evitamos que el programa explote
                return rs.getInt("id"); 
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar personaje: " + e.getMessage());
        }

        // 3. Si no existe, lo insertamos en la base de datos
        String sqlInsert = "INSERT INTO personajes(nombre, apodo, tipo, vida_final, victorias, derrotas, supremos_usados, armas_invocadas) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombreReal);
            ps.setString(2, apodo); // <- Acá cumplimos con la consigna del apodo
            ps.setString(3, tipo);
            ps.setInt(4, p.getVida());
            ps.setInt(5, 0);
            ps.setInt(6, 0);
            ps.setInt(7, p.getSupremosUsados());
            ps.setInt(8, p.getArmasInvocadas().size());

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