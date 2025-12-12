public class Cromosoma {

    private Gen[] cadena;

    private double fitness;

    // Definimos lo es ser un cromosoma... Un Individuo
    public Cromosoma(Gen[] cadGens, double fitness){
        this.cadena = cadGens;
        this.fitness = fitness;
    }


    // getters
    public Gen[] getCadena() {
        return cadena;
    }
    public double getFitness() {
        return fitness;
    }

    // setters
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

}// class cromos
