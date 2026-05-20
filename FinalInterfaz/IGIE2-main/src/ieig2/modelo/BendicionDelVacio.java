/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import ieig2.modelo.HozMortifera;
import ieig2.modelo.HozVenenosa;
import ieig2.modelo.HozOxidada;
import ieig2.modelo.Arma;

/**
 *
 * @author Fede
 */
class BendicionDelVacio {
    public Arma obtenerArma(int porcentaje) {
        if (porcentaje < 40) return new HozOxidada();
        else if (porcentaje < 70) return new HozVenenosa();
        else return new HozMortifera();
    }
}