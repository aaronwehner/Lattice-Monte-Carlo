package MC.Events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map.Entry;

import Lattice.AbstractSite;
import Lattice.Interfaces.FixedNeighborsSite;
import Lattice.Interfaces.GrainSite;
import MC.MonteCarlo;

public class GrainShellSwitchEvent<Site extends AbstractSite & GrainSite & FixedNeighborsSite> extends AbstractEvent<Site>{
    
    private int nucleationGrainNumber;
    private double[] propabilities;

    public GrainShellSwitchEvent(MonteCarlo<Site> MC){
        super(MC);
        nucleationGrainNumber = 0;
        for(Site site : MC.LATTICE){
            if(site.getGrain() >= nucleationGrainNumber) nucleationGrainNumber = site.getGrain() + 1;
        }
        this.calculatePropabilities();
    }

    private void calculatePropabilities(){
        int shells = this.MC.LATTICE.LATTICE[0].getNeighbors().length;
        this.propabilities = new double[shells];
        for(int i = 1; i <= shells; i++){
            if(i == shells) this.propabilities[i-1] = 1;
            else this.propabilities[i-1] = 1 - Math.exp(-i);
        }
    }

    @SuppressWarnings("unchecked")  //type cast of site.getNeighbors() does not need to be checked.
    public Double perform(){
        //choose random lattice site
        Site site = MC.LATTICE.LATTICE[MC.RANDOM.nextInt(MC.LATTICE.SIZE)];

        int shell = 0;
        double p = this.MC.RANDOM.nextDouble();
        for(int i = 0; i < propabilities.length; i++){
            if(p < propabilities[i]){
                shell = i;
                break;
            }
        }

        //determine grain numbers of first next neighboring grains
        ArrayList<Integer> grainNumbers = new ArrayList<Integer>();
        for(Site neighbor : (Site[]) site.getNeighbors()[shell]){
            if(!grainNumbers.contains(neighbor.getGrain())) grainNumbers.add(neighbor.getGrain());
        }           
        

        //The followig condition is needed to sample the markov chain correctly. If a site has already no neighbors with the same grain allegiance
        //it is not allowed to nucleate a new grain. This would lead to a local equivalent configuration (but maybe to a globally different one).
        if(grainNumbers.contains(site.getGrain())){
            grainNumbers.add(this.nucleationGrainNumber);
            grainNumbers.remove((Integer) site.getGrain());
        }

        double energyBefore = MC.ENERGY.calculateEnergy(site);

        //choose a new grain number
        int storeGrainNumber = site.getGrain();
        int newNumber = grainNumbers.get(MC.RANDOM.nextInt(grainNumbers.size()));
        site.setGrain(newNumber);

        //calculate energy difference
        double energyAfter = MC.ENERGY.calculateEnergy(site);

        //Factor 2 because bot only the site energy of the actual sites but also these of its neighbors is changed
        //and the energy difference here is not only referring to the difference in site energy of the involved sites but
        //the energy difference of the whole lattice caused by the switch.
        double energyDifference = 2*(energyAfter - energyBefore);

        //test if grain switch was accepted
        if(accepted(energyDifference)){
            if(newNumber == this.nucleationGrainNumber) this.nucleationGrainNumber++;
            return energyDifference; 
        } else {
            site.setGrain(storeGrainNumber);
        }
        
        //if nothing was changed
        return null;
    }

    @Override
    public void refresh() {
        //TODO geht safe schöner mit streams
        // refresh grain numbers
        TreeMap<Integer, Integer> grainNumbers = new TreeMap<Integer, Integer>();
        //old greinnumber, new grainnumber
        TreeMap<Integer, Integer> newGrainNumbers = new TreeMap<Integer, Integer>();

        //get all grain Numbers
        for(Site site: this.MC.LATTICE.LATTICE){
            int number = site.getGrain();
            grainNumbers.putIfAbsent(number, 0); //if this grain number is not in the map at the moment it is added
            grainNumbers.put(number, grainNumbers.get(number)+1); //increases value (count) by one
        }
        //sorts the grainnumbers, so that the grainNumber with the highest count of atoms is at the beginning of the list
        ArrayList<Entry<Integer, Integer>> grainNumbersList = new ArrayList<>(grainNumbers.entrySet());
		grainNumbersList.sort(Entry.comparingByValue());
        Collections.reverse(grainNumbersList);

        //get the new grainnumbers, starting with zero
        for(int i = 0; i < grainNumbers.size(); i++){
            newGrainNumbers.put(grainNumbersList.get(i).getKey(), i);
        }
        
        //set the new grain numbers
        for(Site site: this.MC.LATTICE.LATTICE){
            site.setGrain(newGrainNumbers.get(site.getGrain()));
        }

        this.nucleationGrainNumber = grainNumbersList.size(); //new nucleation grain number
    }
}
