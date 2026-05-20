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
class EspadaCelestial extends Arma {
    public EspadaCelestial() { super("Espada Celestial", 15); }
    @Override
    public String usarEfectoEspecial(Personaje portador, Personaje enemigo) {
        
        portador.vida += 20;
        portador.defensa += 5;
        return portador.getNombre() + " se cura 20 de vida y gana +5 defensa.";
    }
}
