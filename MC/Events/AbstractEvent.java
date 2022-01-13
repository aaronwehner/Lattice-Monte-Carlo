package MC.Events;

import Lattice.AbstractSite;
import MC.MonteCarlo;

public abstract class AbstractEvent<T extends AbstractSite> {
    public static final double k_B = 8.617333262e-5; //Boltzmann-Constant in eV/K
    protected final MonteCarlo<T> MC;

    public AbstractEvent(MonteCarlo<T> MC){
        this.MC = MC;
    }

    public boolean accepted(double e){
        return e <= 0 ? true : (Math.exp(-e/(k_B*MC.TEMPERATURE.getTemp())) > MC.RANDOM.nextDouble());
    }

    public abstract Double perform();
    public abstract void refresh();
}
