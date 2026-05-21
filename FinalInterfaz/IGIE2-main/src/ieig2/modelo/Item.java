/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ieig2.modelo;

/**
 *
 * @author ferre
 */


public class Item {
    private String nombre;
    private int efectoVida;

    // ESTE ES EL CONSTRUCTOR QUE EL CÓDIGO ESTÁ BUSCANDO
    public Item(String nombre, int efectoVida) {
        this.nombre = nombre;
        this.efectoVida = efectoVida;
    }

    public String getNombre() { return nombre; }
    public int getEfectoVida() { return efectoVida; }
}


