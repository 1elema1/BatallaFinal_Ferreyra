/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import ieig2.modelo.Validacion;

/**
 *
 * @author Ferreyra
 */


public class Apodo {
    private String valor;

    public Apodo(String apodoIngresado) {
        setValor(apodoIngresado);
    }

    public void setValor(String apodoIngresado) {
        String limpio = Validacion.normalizar(apodoIngresado);
        if (!Validacion.esApodoValido(limpio)) {
            throw new IllegalArgumentException(
                "Apodo inválido: debe tener 3 a 10 caracteres y solo letras/espacios."
            );
        }
        this.valor = limpio;
    }

    // Getter
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}

