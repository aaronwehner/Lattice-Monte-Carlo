package MC.Events;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.TreeMap;

import Lattice.AbstractSite;
import MC.MonteCarlo;

public class EventManager<Site extends AbstractSite>{

    public final AbstractEvent<Site>[] EVENTS;
    public final double[] PROPABILITIES;

    private final Class<AbstractEvent<Site>>[] EVENT_CLASSES;
    private final double[] PROPABILITY_DISTRIBUTION;

    private MonteCarlo<Site> mc;
    private int acceptedEvents = 0;
    private Map<Integer, Double> rateOfAcceptanceHistory = new TreeMap<Integer, Double>();

    @SuppressWarnings("unchecked")
    public EventManager(Class<AbstractEvent<Site>>[] EVENT_CLASSES, double[] PROPABILITIES)
    throws UndefinedPropabilitiesException{
        
        this.EVENTS = new AbstractEvent[EVENT_CLASSES.length];
        this.EVENT_CLASSES = EVENT_CLASSES;

        if(Arrays.stream(PROPABILITIES).sum() != 1 || Arrays.stream(PROPABILITIES).anyMatch(d -> (d < 0 || d > 1))) throw new UndefinedPropabilitiesException();
        this.PROPABILITIES = PROPABILITIES;

        this.PROPABILITY_DISTRIBUTION = new double[this.PROPABILITIES.length];
        this.PROPABILITY_DISTRIBUTION[0] = this.PROPABILITIES[0];
        for(int i = 1; i < this.PROPABILITIES.length; i++){
            this.PROPABILITY_DISTRIBUTION[i] = this.PROPABILITY_DISTRIBUTION[i-1] + this.PROPABILITIES[i];
        }   
    }

    public void initialize(MonteCarlo<Site> mc) throws Exception{
        if(this.mc == null) this.mc = mc;
        else throw new Exception("EventManager is already initialized.");

        try{
            for(int i = 0; i < EVENT_CLASSES.length; i++){
                Class<AbstractEvent<Site>> clazz = EVENT_CLASSES[i];
                this.EVENTS[i] = clazz.getConstructor(MonteCarlo.class).newInstance(this.mc);
            }
        } catch(Exception e){
            throw new InputMismatchException("Error while Event-Creation. Pease check Input-File.");
        } 
    }

    //TODO performance-critical method
    public void performRandomEvent(){
        double p = mc.RANDOM.nextDouble();
        for(int i = 0; i < this.PROPABILITY_DISTRIBUTION.length; i++){
            if(p < this.PROPABILITY_DISTRIBUTION[i]){
                Double e = this.EVENTS[i].perform();
                if(e != null){
                    this.mc.ENERGY.increaseTotalEnergy(e);
                    this.acceptedEvents++;
                }
                break;
            } 
        }   
    }

    public void addRateOfAccaptanceToHistory(){
        double rate =   ((double) this.acceptedEvents)
                        /
                        ((double) this.mc.getStep() * (double) this.mc.LATTICE.SIZE);
        this.rateOfAcceptanceHistory.put(this.mc.getStep(), rate);
    }

    public Map<Integer, Double> getRateOfAccepanceHistory(){
        return this.rateOfAcceptanceHistory;
    }
    
    public void refresh(){
        for(AbstractEvent<Site> event : this.EVENTS){
            event.refresh();
        }
    }

    private static class UndefinedPropabilitiesException extends Exception{
        /*
        public UndefinedPropabilitiesException(String s){
            super(s);
        }
        */
        public UndefinedPropabilitiesException(){
            super("Propabilities given are not defined.");
        }
    }    
}
