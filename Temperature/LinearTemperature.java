package Temperature;

public class LinearTemperature extends AbstractTemperature{
    
    public final double init, fin;
    public final int steps;
    public final double delta;

    public LinearTemperature(String parameters) throws NumberFormatException{
        String[] param = parameters.split("\\s*,\\s*");
        this.init = Double.parseDouble(param[0]);
        super.temp = this.init;
        this.fin = Double.parseDouble(param[1]);
        this.steps = Integer.parseInt(param[2]);
        this.delta = (init-fin)/steps;
    }

    public LinearTemperature(double init, double fin, int steps){
        this.init = init;
        this.temp = this.init;
        this.fin = fin;
        this.steps = steps;
        this.delta = (init-fin)/steps;
    }

    public double newTemp(){
        if(this.mc.getStep() <= this.steps) return temp = temp - delta;
        else return temp;
    }
}
