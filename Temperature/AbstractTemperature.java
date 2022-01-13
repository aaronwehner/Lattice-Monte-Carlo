package Temperature;

import MC.MonteCarlo;

public abstract class AbstractTemperature {
    protected double temp;
    protected MonteCarlo<?> mc;

    public AbstractTemperature(String parameters){
    }

    protected AbstractTemperature(){
    }

    public void initialize(MonteCarlo<?> mc) throws Exception{
        if(this.mc == null) this.mc = mc;
        else throw new Exception("Temperature Program is already initalized.");
    }

    public double getTemp(){
        return temp;
    }

    public abstract double newTemp();
}