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
class EspadaSagrada extends Arma {
    public EspadaSagrada() { super("Espada Sagrada", 10); }
    @Override
    public String usarEfectoEspecial(Personaje portador, Personaje enemigo) {
        portador.vida += 10;
        return portador.getNombre() + " se cura 10 de vida gracias a la espada sagrada.";
        
    }
}
