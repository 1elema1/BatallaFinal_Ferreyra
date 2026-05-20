package ieig2.vista;

public class PersonajeVM {
    public final String nombre;
    public final String apodo;        
    public final int vida;
    public final int vidaMax;        
    public final int bendicion;      
    public final String arma;
    public final String estadoEspecial;
    public final boolean enCritico;

    public PersonajeVM(String nombre, String apodo, int vida, int vidaMax,
                       int bendicion, String arma, String estadoEspecial, boolean enCritico) {
        this.nombre = nombre;
        this.apodo = apodo;
        this.vida = vida;
        this.vidaMax = vidaMax;
        this.bendicion = bendicion;
        this.arma = arma;
        this.estadoEspecial = estadoEspecial;
        this.enCritico = enCritico;
    }
}
