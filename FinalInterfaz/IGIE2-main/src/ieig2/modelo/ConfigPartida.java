package ieig2.modelo;

import java.util.Random;

public class ConfigPartida {
    public int vida;      // 100–160
    public int fuerza;    // 15–25
    public int defensa;   // 8–13
    public int bendicion; // 30–100
    public int batallas;  // 2,3,5
    public boolean supremos;

    public static ConfigPartida aleatoria() {
        Random r = new Random();
        ConfigPartida c = new ConfigPartida();
        c.vida      = 100 + r.nextInt(61);
        c.fuerza    = 15  + r.nextInt(11);
        c.defensa   = 8   + r.nextInt(6);
        c.bendicion = 30  + r.nextInt(71);
        c.batallas  = 3;
        c.supremos  = true;
        return c;
    }
}

