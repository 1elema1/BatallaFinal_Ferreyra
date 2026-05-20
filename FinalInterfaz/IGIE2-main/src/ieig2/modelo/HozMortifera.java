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
class HozMortifera extends Arma {
    public HozMortifera() { super("Hoz Mortífera", 15); }
    @Override
    public String usarEfectoEspecial(Personaje portador, Personaje enemigo) {
        
        enemigo.vida -= 10;
        portador.vida += 10;
        return enemigo.getNombre() + " recibe veneno (-10 vida) y " + portador.nombre + " se cura +10.";
    }
}