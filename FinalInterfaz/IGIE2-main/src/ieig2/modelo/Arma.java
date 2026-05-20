/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

import ieig2.modelo.Personaje;

/**
 *
 * @author Fede
 */
abstract class Arma {
    protected String nombre;
    protected int danioExtra;

    public Arma(String nombre, int danioExtra) {
        this.nombre = nombre;
        this.danioExtra = danioExtra;
    }

    public String getNombre() { return nombre; }
    public int getDanioExtra() { return danioExtra; }

    public abstract String usarEfectoEspecial(Personaje portador, Personaje enemigo);
}