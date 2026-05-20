/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

/**
 *
 * @author Mondino
 */
import java.util.regex.Pattern;



public class Validacion {
    private static final Pattern REGEX_APODO = Pattern.compile("^[A-Za-z ]{3,10}$");

    public static String normalizar(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    public static boolean esApodoValido(String s) {
        return s != null && REGEX_APODO.matcher(s).matches();
    }
}
