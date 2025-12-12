public class Gen {
    
    // Rango de valores posibles
    private int li;  // Limite Inferior
    private int ls;  // Limite Superior

    // caracteristica posible dentro del rango definido
    private double valor;

    // lo que significa ser un gen
    public Gen(int li, int ls, double valor){
        this.li = li;
        this.ls = ls;
        this.valor = valor;
    }


    // setters
    public void setValor(double valor) {
        this.valor = valor;
    }

    // getters
    public double getValor() {
        return valor;
    }

    public int getLi() {
        return li;
    }

    public int getLs() {
        return ls;
    }





}// class gen