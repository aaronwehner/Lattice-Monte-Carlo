package Temperature;

public class ExponentialTemperature extends AbstractTemperature{

    public final double init, fin, c;

    public ExponentialTemperature(String parameters) throws NumberFormatException{
        String[] param = parameters.split("\\s*,\\s*");
        this.init = Double.parseDouble(param[0]);
        super.temp = this.init;
        this.fin = Double.parseDouble(param[1]);
        this.c = Double.parseDouble(param[2]);
    }

    public ExponentialTemperature(double init, double fin, double c){
        this.init = init;
        this.temp = this.init;
        this.fin = fin;
        this.c = c;
    }

    public double newTemp(){
        temp = temp - (temp-fin)/c;
        return temp;
    }
}
