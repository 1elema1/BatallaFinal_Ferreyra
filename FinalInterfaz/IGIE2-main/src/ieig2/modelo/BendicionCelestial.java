/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import ieig2.modelo.EspadaCelestial;
import ieig2.modelo.EspadaSagrada;
import ieig2.modelo.EspadaSimple;
import ieig2.modelo.Arma;

/**
 *
 * @author Fede
 */
class BendicionCelestial {
    public Arma obtenerArma(int porcentaje) {
        if (porcentaje < 40) return new EspadaSimple();
        else if (porcentaje < 70) return new EspadaSagrada();
        else return new EspadaCelestial();
    }
}

