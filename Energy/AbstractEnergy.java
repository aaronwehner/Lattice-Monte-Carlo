package Energy;

import java.util.Map;
import java.util.TreeMap;

import Lattice.AbstractSite;
import MC.MonteCarlo;

public abstract class AbstractEnergy<Site extends AbstractSite> {
    
    private MonteCarlo<Site> mc;
    private double totalEnergy;
    private Map<Integer, Double> energyHistory;


    //TODO
    public AbstractEnergy(String parameters){
    }

    protected AbstractEnergy(){
    }

    public void initialize(MonteCarlo<Site> mc){
        this.mc = mc;
        this.totalEnergy = calculateTotalEnergy();
        this.energyHistory = new TreeMap<Integer,Double>();
        this.addTotalEnergyToHistory();
    }

    public void increaseTotalEnergy(Double energy){
        totalEnergy += energy;
    }

    public double getTotalEnergy(){
        return totalEnergy;
    }

    public double calculateTotalEnergy(){
        double e = 0;
        for(Site site : this.mc.LATTICE){
            e += calculateEnergy(site);
        }
        return e;
    }
    
    public Map<Integer, Double> getEnergyHistory(){
        return this.energyHistory;
    }

    public void addTotalEnergyToHistory(){
        this.energyHistory.put(this.mc.getStep(), this.totalEnergy);
    }

    public abstract double calculateEnergy(Site site);
}