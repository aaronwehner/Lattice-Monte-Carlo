package Lattice.Interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map.Entry;

import Lattice.Lattice;

public interface GrainSite {
    public abstract int getGrain();
    public abstract void setGrain(int grain);

    public static int refreshGrainNumbers(Lattice<? extends GrainSite> LATTICE){
        //grainnumber, counts
        TreeMap<Integer, Integer> grainNumbers = new TreeMap<Integer, Integer>();
        //old greinnumber, new grainnumber
        TreeMap<Integer, Integer> newGrainNumbers = new TreeMap<Integer, Integer>();
        //get all grain Numbers
        for(GrainSite site: LATTICE){
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
        for(GrainSite site: LATTICE){
            site.setGrain(newGrainNumbers.get(site.getGrain()));
        }
        return grainNumbersList.size(); //new nucleation grain number
    }
}
