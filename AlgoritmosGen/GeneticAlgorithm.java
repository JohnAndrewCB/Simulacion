import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgorithm {
    
    // Problema de estudio (y) (data set 'Benetton')
    private double[] y = {651, 762, 856, 1063, 1190, 1298, 1421, 1440, 1518};
    private double[] x = {23, 26, 30, 34, 43, 48, 52, 57, 58};

    private double[] desconocidos = {59, 60, 61, 62, 63}; // predecir puntos en el espacio (y)
    // parametros
        // - evolucion 
        private double crossoverRate;
        private double mutationRate;
        private int elitism;
        private Cromosoma[] newGeneration; 
        // - criterios de stop
        private double fitnessExpectation; 
    
    public GeneticAlgorithm(double fitnessExpectation, double crossoverRate, double mutationRate, int elitism, Cromosoma[] newGeneration){
        this.fitnessExpectation = fitnessExpectation;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitism = elitism;
        this.newGeneration = newGeneration;
    }

    //                                    MÉTODOS
    public Cromosoma[] Fitness(Poblacion poblacion, GeneticAlgorithm ga){ // Asignar calidad genetica de cada individuo (actualiza "poblacion.individuos")

        // 1. Determinar qué conjunto de individuos evaluar
        Cromosoma[] individuos;

        if (poblacion != null) {
            // Fitness inicial o cuando evaluas la población actual
            individuos = poblacion.getPoblacion();
        } else {
            // Fitness de la nueva generación
            individuos = ga.getNewGeneration();
        }

        int n = y.length;

        // Calcular promedio de valores reales
        double suma = 0;
        for (int i = 0; i < n; i++) {
            suma += y[i];
        }
        double p = suma / n;

        // Evaluar el fitness de cada individuo
        for (Cromosoma c : individuos) {

            Gen[] genes = c.getCadena();
            double[] b = new double[n];

            // y_hat = ax + b
            for (int i = 0; i < n; i++) {
                b[i] = genes[1].getValor() * x[i] + genes[0].getValor();
            }

            // t = ∑ (y - y_hat)^2
            double t = 0;
            for (int i = 0; i < n; i++) {
                t += Math.pow((y[i] - b[i]), 2);
            }

            // v = ∑ (y - p)^2
            double v = 0;
            for (int i = 0; i < n; i++) {
                v += Math.pow((y[i] - p), 2);
            }

            // Fitness r²
            double fitness = 1 - (t / v);

            c.setFitness(fitness);
        }

        // 2. Retornar los individuos ya evaluados
        return individuos;
    }

    public void AgregarGeneracion(Cromosoma c, Cromosoma[] individuos, GeneticAlgorithm ga){
        // cubre los casos: 1) añadir un cromosoma 2) añadir más de uno
        Cromosoma[] nueva = ga.getNewGeneration();
        if(c != null){
            for (int i = 0; i < nueva.length; i++){
                if(nueva[i] == null){
                    nueva[i] = c;
                    break;
                }
            }
        }else if(individuos != null){
            int a = 0;
            for (int i = 0; i < nueva.length && a < individuos.length; i++){
                if(nueva[i] == null){
                    nueva[i] = individuos[a];
                    a++;
                }
            } 
        }

        ga.setNewGeneration(nueva); // actualizamos los integrantes de la nueva poblacion

    }

    public void InsertarElitismo(Cromosoma[] elitistas){ // agregar lo mejores individuosde la generacion anterior en la nueva
        Cromosoma[] nuevaGen = this.newGeneration;

        for(int i = 0; i < elitistas.length; i++){
            nuevaGen[i] = elitistas[i];  // sustituye los peores
        }

        this.newGeneration = nuevaGen;
    }

    // calcula valores a partir del fitness de todos los cromosomas y compara los resultados con los criterios a cumplir
    public Cromosoma[] Elitism(Poblacion poblacion, GeneticAlgorithm ga){

        int elitism = ga.getElitism();

        Cromosoma[] losMejores = new Cromosoma[elitism]; 

        Cromosoma[] individuos = poblacion.getPoblacion().clone();

        Arrays.sort(individuos, (a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        
        for (int i = 0; i < elitism; i++) {
            losMejores[i] = individuos[i];
        }

        AgregarGeneracion(null, losMejores, ga);

        return losMejores;
    }

    // mover discret maths.. ¿?
    public double RandomNo(int min, int max){  // generar un numero aleatorio
        double valor = ThreadLocalRandom.current().nextDouble(min, max);

        return valor;
    }
    
        //  Evolucion de poblacion
    public Cromosoma[] Seleccion(Poblacion poblacion, GeneticAlgorithm ga){

        int E = ga.getElitism();
        Cromosoma[] padres = new Cromosoma[2];

        
        Cromosoma[] individuos = poblacion.getPoblacion().clone();
        Arrays.sort(individuos, (a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        
        int tamPool = individuos.length - E;
        Cromosoma[] pool = new Cromosoma[tamPool];

        for(int i = 0; i < tamPool; i++){
            pool[i] = individuos[E + i];  // omite índices 0..E-1
        }

    
        // Selección del Primer Padre
        int candidato1 = 0;
        boolean bandera = true;

        do{
            candidato1 = (int) Ruleta(poblacion, pool);
            padres[0] = pool[candidato1];
            bandera = false;

        }while(bandera);

        // Selección del Segundo Padre
        int candidato2 = 0;

        do{
            candidato2 = (int) Ruleta(poblacion, pool);
        }while(candidato2 == candidato1); // evitar el mismo individuo

        padres[1] = pool[candidato2];

        return padres;
    }

    public int RuletaLanzamiento(double[][] ruleta){ 
        double random = ThreadLocalRandom.current().nextDouble(); // [0,1)

        for(int i = 0; i < ruleta.length; i++){
            if(random <= ruleta[i][2]){
                return (int) ruleta[i][0]; 
            }
        }

        
        return (int) ruleta[ruleta.length - 1][0];
    }

    public int Ruleta(Poblacion poblacion, Cromosoma[] individuos){

        int n = individuos.length;
        double[][] ruleta = new double[n][3];

        // fitness mínimo
        double minFitness = individuos[0].getFitness();
        for (Cromosoma c : individuos){
            if(c.getFitness() < minFitness){
                minFitness = c.getFitness();
            }
        }

        // Ajuste para asegurar fitness positivos
        double ajuste = 0;
        if(minFitness <= 0){
            ajuste = (-minFitness) + 1; // evitar ceros
        }

        // Calcular totalFitness ajustado
        double totalFitness = 0;
        for (Cromosoma c : individuos){
            totalFitness += (c.getFitness() + ajuste);
        }

        // Evitar división entre cero
        if(totalFitness == 0){
            // selección uniforme (todos tienen las mismas "chances")
            return ThreadLocalRandom.current().nextInt(0, n);
        }

        // Llenar tabla ruleta
        double acumulado = 0;
        for(int i = 0; i < n; i++){

            double fitAjustado = individuos[i].getFitness() + ajuste;
            double prob = fitAjustado / totalFitness;
            acumulado += prob;

            ruleta[i][0] = i;         // índice
            ruleta[i][1] = prob;      // prob individual
            ruleta[i][2] = acumulado; // prob acumulada
        }

        // 5. Lanzamiento usando tu método
        return RuletaLanzamiento(ruleta);
    }

            // Crossover
    public void CruzarPoblacion(Poblacion poblacion, GeneticAlgorithm ga){

        Cromosoma[] nuevaGen = ga.getNewGeneration();

        while(espaciosLibres(nuevaGen) > 0){

            // Seleccionar padres
            Cromosoma[] padres = Seleccion(poblacion, ga);
            Cromosoma p1 = padres[0];
            Cromosoma p2 = padres[1];

            // Decidir cruza
            double r = RandomNo(0, 1);

            if(r <= ga.getCrossoverRate()){

                // hacer crossover
                Cromosoma[] hijos = CrossoverAritmetico(p1, p2);

                // mutar hijos 
                Mutacion(hijos, ga);

                // agregar hijos a la nueva generacion
                AgregarGeneracion(null, hijos, ga);

            } else {

                // no hay cruza, se copian directamente a la nueva generacion
                AgregarGeneracion(null, new Cromosoma[]{p1, p2}, ga);
            }
        }
    }

    public Cromosoma[] CrossoverAritmetico(Cromosoma p1, Cromosoma p2){
    
        Gen[] g1 = p1.getCadena();
        Gen[] g2 = p2.getCadena();

        int n = g1.length;

        Gen[] h1 = new Gen[n];
        Gen[] h2 = new Gen[n];

        double alpha = RandomNo(0, 1);

        for(int i = 0; i < n; i++){

            double v1 = alpha * g1[i].getValor() + (1 - alpha) * g2[i].getValor();
            double v2 = alpha * g2[i].getValor() + (1 - alpha) * g1[i].getValor();

            // aplicar límites del gen
            int li = g1[i].getLi();
            int ls = g1[i].getLs();

            if(v1 < li) v1 = li;
            if(v1 > ls) v1 = ls;

            if(v2 < li) v2 = li;
            if(v2 > ls) v2 = ls;

            // Crear nuevos genes
            h1[i] = new Gen(li, ls, v1);
            h2[i] = new Gen(li, ls, v2);
        }

        // fitness inicial = 0 ( se recalcula luego con Fitness() )
        Cromosoma hijo1 = new Cromosoma(h1, 0);
        Cromosoma hijo2 = new Cromosoma(h2, 0);

        return new Cromosoma[]{hijo1, hijo2};
    }


    private int espaciosLibres(Cromosoma[] arr){
        int cont = 0;
        for(Cromosoma c : arr){
            if(c == null) cont++;
        }
        return cont;
    }
        // fin crossover 

        // Mutation
    public void Mutacion(Cromosoma[] hijos, GeneticAlgorithm ga) {

        for (Cromosoma hijo : hijos) {

            Gen[] genes = hijo.getCadena();

            for (int i = 0; i < genes.length; i++) {

                double r = RandomNo(0, 1);

                // ¿Muta este gen?
                if (r <= ga.getMutationRate()) {

                    Gen g = genes[i];
                    int li = g.getLi();
                    int ls = g.getLs();

                    // Nueva mutación uniforme dentro de los límites
                    double nuevoValor = ThreadLocalRandom.current().nextDouble(li, ls);

                    // Aplicar mutación
                    genes[i].setValor(nuevoValor);
                }
            }
        }
    }        
        // fin mutation

    // fin  Evolucion de poblacion


    // criterios de stop
    public boolean FitnessUmbral(Cromosoma[] elitism, GeneticAlgorithm ga){ // Uno de los individuos alcanza cierto umbral de fitness
        for(int i = 0; i < elitism.length; i++){
            if(elitism[i].getFitness() >= ga.getFitnessExpectation()){
                return false;
            }
        }
        return true;
    }


    // Predicciones --> mejor individuo encontrado es evaluado por la funcion objetivo para predeccir el 'espacio de busqueda'
    public void Predicciones(Poblacion poblacion){
        Cromosoma[] individuos = poblacion.getPoblacion();
        Cromosoma theBest  = individuos[0]; // arreglo ordenado del mejor al peor (según fitness)
        Gen[] variables = theBest.getCadena();

        double b0 = variables[0].getValor();
        double b1 = variables[1].getValor();

        double[] predicciones = new double[desconocidos.length];

        System.out.println("Predicciones: y ="+b1+" * (x) + "+b0);
        for(int i=0; i<predicciones.length; i++){
            predicciones[i] = b1 * desconocidos[i] + b0; //forma de la funcion en la que se trabaja
            System.out.println("y("+desconocidos[i]+") = "+predicciones[i]);
        }
        
    }

    //                                        fin metodos



    //              getters
    public double getFitnessExpectation() {
        return fitnessExpectation;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public int getElitism() {
        return elitism;
    }
    
    public Cromosoma[] getNewGeneration(){
        return newGeneration;
    }

    //      setter
    public void setNewGeneration(Cromosoma[] newGeneration) {
        this.newGeneration = newGeneration;
    }

}// class genetic alg