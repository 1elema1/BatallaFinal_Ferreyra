/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import ieig2.modelo.Arma;
import ieig2.modelo.Personaje;

/**
 *
 * @author Fede
 */
class EspadaSimple extends Arma {
    public EspadaSimple() { super("Espada Simple", 5); }
    @Override
    public String usarEfectoEspecial(Personaje portador, Personaje enemigo) {
        return "La espada simple no tiene efectos especiales.";
    }
}