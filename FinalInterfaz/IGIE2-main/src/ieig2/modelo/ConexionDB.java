/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:batallas.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver SQLite cargado");
        } catch (ClassNotFoundException e) {
            System.out.println("Error cargando driver SQLite: " + e.getMessage());
        }
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
