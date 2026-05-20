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
class HozOxidada extends Arma {
    public HozOxidada() { super("Hoz Oxidada", 5); }
    @Override
    public String usarEfectoEspecial(Personaje portador, Personaje enemigo) {
        return "La hoz oxidada no tiene efectos especiales.";
    }
}