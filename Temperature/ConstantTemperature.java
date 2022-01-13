package Temperature;

public class ConstantTemperature extends AbstractTemperature{
    
    public final double t;

    public ConstantTemperature(String parameters) throws NumberFormatException{
        super(parameters);
        this.t = Double.parseDouble(parameters.strip());
        this.temp = this.t;
    }

    public ConstantTemperature(double t){
        this.t = t;
        this.temp = this.t;
    }

    public double newTemp(){
        return temp;
    }
}
