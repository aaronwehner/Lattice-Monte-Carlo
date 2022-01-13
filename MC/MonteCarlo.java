package MC;

import java.util.InputMismatchException;
import java.util.Random;

import Energy.AbstractEnergy;
import Lattice.AbstractSite;
import Lattice.Lattice;
import MC.Events.EventManager;
import Temperature.AbstractTemperature;

public class MonteCarlo<Site extends AbstractSite> {

    public final Random RANDOM;
    public final AbstractTemperature TEMPERATURE;
    public final AbstractEnergy<Site> ENERGY;
    public final Lattice<Site> LATTICE;
    public final EventManager<Site> EVENTS;

    private int step = 0;
    
    public MonteCarlo(Lattice<Site> LATTICE, AbstractEnergy<Site> ENERGY, Random RANDOM, EventManager<Site> EVENTS, AbstractTemperature TEMPERATURE)
    throws InputMismatchException{
        this.ENERGY = ENERGY;
        this.LATTICE = LATTICE;
        this.RANDOM = RANDOM;
        this.EVENTS = EVENTS;
        this.TEMPERATURE = TEMPERATURE;
        try{
            this.ENERGY.initialize(this);
            this.EVENTS.initialize(this);
            this.TEMPERATURE.initialize(this);
        } catch(Exception e){
            throw new InputMismatchException("Wasn't able to create the MonteCarlo instance: " + e.getLocalizedMessage());
        }
    }

    public void performStep(){
        for(int i  = 0; i < this.LATTICE.SIZE; i++){
            this.EVENTS.performRandomEvent();
        }
        step++;
        this.ENERGY.addTotalEnergyToHistory();
        this.TEMPERATURE.newTemp();
    }

    public int getStep(){
        return this.step;
    }
} 
