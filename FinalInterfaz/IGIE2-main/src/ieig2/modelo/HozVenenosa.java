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
class HozVenenosa extends Arma {
    public HozVenenosa() { super("Hoz Venenosa", 10); }
    @Override
    public String usarEfectoEspecial(Personaje portador, Personaje enemigo) {
        enemigo.vida -= 5;
        return enemigo.getNombre() + " ha sido envenenado (-5 de vida por turno).";
    }
}
