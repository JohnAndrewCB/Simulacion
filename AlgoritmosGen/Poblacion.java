/*
    Poblacion: 
    - definir el total de individuos
    - inicializar data set (cromosomas y sus genes) segun el 'GA' (algoritmo genético)
    - definir el total de genes por cromosoma
*/

public class Poblacion {
    
    //ArrayList<Cromosoma> poblacion = new ArrayList<>();
    private Cromosoma[] individuos;

    private int tamanio; // total de poblacion 

    private int totalgenes; // sales y advertasing (varian) 

    public Poblacion(Cromosoma[] individuos, int tamanio, int totalgenes){  // ArrayList<Cromosoma> poblacion
        this.individuos = individuos;
        this.tamanio = tamanio;
        this.totalgenes = totalgenes;
    }

    // METODOS
    public void InicializarPoblacion(Poblacion poblacion, GeneticAlgorithm ga){ // (tamanio, TOTALGENES)
        
        Cromosoma[] individuos = new Cromosoma[poblacion.tamanio];

        int[] min = {165, 20};  int[] max = {169, 24}; // 167...  23...

        // construir algoritmo para crear los 100 cromosomas con sus
        // genes con valores aleatorios
        
        for(int i = 0; i < poblacion.tamanio; i++){
            Gen[] gens = new Gen[poblacion.totalgenes];

            for(int b = 0; b < poblacion.totalgenes; b++){
                double valorgen = ga.RandomNo(min[b], max[b]);

                gens[b] = new Gen(min[b], max[b], valorgen);
            }

            Cromosoma c = new Cromosoma(gens, 0);
            
            individuos[i] = c;
        }

        poblacion.setPoblacion(individuos);
    }

    //      fin metodos


    // getters
    public Cromosoma[] getPoblacion() {
        return individuos;
    }

    public int getTamanio(){
        return tamanio;
    }

    // setters
    public void setPoblacion(Cromosoma[] individuos) {
        this.individuos = individuos;
    }




}// class poblacion