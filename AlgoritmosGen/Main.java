import java.util.Arrays;
public class Main {
      
    public static void main(String[] args) {

        Poblacion poblacion = new Poblacion(null, 80, 2);
        GeneticAlgorithm ga = new GeneticAlgorithm(0.9767, 0.9, 0.2, 4, new Cromosoma[80]);

        poblacion.InicializarPoblacion(poblacion, ga);
        ga.Fitness(poblacion, null);

        
        boolean bandera = true;
        while (bandera) {
            Impresion(poblacion);
            // Elitismo (población actual)
            Cromosoma[] elitistas = ga.Elitism(poblacion, ga);

            //  Selección(ruleta.. etc) + Crossover + Mutación → genera newGeneration
            ga.CruzarPoblacion(poblacion, ga);

            // Insertar elitismo en la nueva generación
            ga.InsertarElitismo(elitistas);

            // Evaluar fitness de newGeneration
            ga.Fitness(null, ga);

            // Sustituir población old --> new
            poblacion.setPoblacion(ga.getNewGeneration());

            // evaluar criterio de stop
            bandera = ga.FitnessUmbral(poblacion.getPoblacion(), ga);
            
        }

        System.out.println("///////////////////////////////////////////////////////////////////////////////");
        // Generacion en la que se alcanzo el 'fitness umbral'
        Impresion(poblacion);
        
        // Rescatamos el mejor cromosa y realizamos predicciones 
        ga.Predicciones(poblacion);


    }//mian

    private static void Impresion(Poblacion poblacion){ // Imprime la poblacion de la generacion de ese momento
        // Impresion de la poblacion
        Cromosoma[] individuos = poblacion.getPoblacion();
        Arrays.sort(individuos, (a, b) -> Double.compare(b.getFitness(), a.getFitness()));

        int i = 1; 
        for(Cromosoma c : individuos){

            System.out.println("\n----------");
            System.out.println("cromo("+i+"): ");
            System.out.println("----------");
            System.out.println(" fitness: "+c.getFitness());
            int a = 1;
            for(Gen g : c.getCadena()){
                System.out.println("    gen "+a+": "+g.getValor());
                a++;
            }
            i++;
        }
        System.out.println(""); 
    }

}//casmain class
