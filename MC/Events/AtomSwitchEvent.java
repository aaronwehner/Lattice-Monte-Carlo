package MC.Events;

import java.util.Comparator;

import Lattice.AbstractSite;
import Lattice.Interfaces.TypeSite;
import MC.MonteCarlo;

public class AtomSwitchEvent<Site extends AbstractSite & TypeSite> extends AbstractEvent<Site>{ 
    //can be made more general if a class/interface is created that defines an Absract site having a type
    
    private Site[] orderedSites;
    private int[] atomTypes;
    private int[] atomTypeDistribution;

    public AtomSwitchEvent(MonteCarlo<Site> MC){
        super(MC);
        this.orderedSites = MC.LATTICE.getOrderedLattice(Comparator.comparingInt(s -> s.getType()));
        this.atomTypes = MC.LATTICE.countSiteProperty(s -> s.getType());
        this.atomTypeDistribution = MC.LATTICE.calculatePropertyDistribution(s -> s.getType());
    }

    @Override
    public Double perform(){
        //choose random lattice site
        int indexA = MC.RANDOM.nextInt(MC.LATTICE.LATTICE.length);
        Site siteA = this.orderedSites[indexA];
        int indexB = this.getRandomIndexOfNotType(siteA.getType());
        Site siteB = this.orderedSites[indexB];

        //calculate energy before switch
        double energyBefore = MC.ENERGY.calculateEnergy(siteA) + MC.ENERGY.calculateEnergy(siteB);

        //switch atom types 
        int storeAtomTypeA = siteA.getType();
        siteA.setType(siteB.getType());
        siteB.setType(storeAtomTypeA);

        //calculate energy difference
        double energyAfterA = MC.ENERGY.calculateEnergy(siteA);
        double energyAfterB = MC.ENERGY.calculateEnergy(siteB);
        
        //Factor 2 because bot only the site energy of the actual sites but also these of its neighbors is changed
        //and the energy difference here is not only referring to the difference in site energy of the involved sites but
        //the energy difference of the whole lattice caused by the switch.
        double energyDifference = 2*(energyAfterA + energyAfterB - energyBefore);

        //test if switching event is accepted
        if(super.accepted(energyDifference)){
            //orderedSites has to be updated
            swapChangedSitesInOrderedArray(indexA, indexB);
            return energyDifference;
        }
        else {
            siteB.setType(siteA.getType());
            siteA.setType(storeAtomTypeA);
            return null;
        }
    }

    private void swapChangedSitesInOrderedArray(int i, int j){
        Site store = orderedSites[i];
        orderedSites[i] = orderedSites[j];
        orderedSites[j] = store;
    }

    private int getRandomIndexOfNotType(int type){
        int index = MC.RANDOM.nextInt(MC.LATTICE.SIZE - this.atomTypes[type]);
        return index >= atomTypeDistribution[type] ? index + this.atomTypes[type] : index;
    }

    @Override
    public void refresh(){
        //do nothing
    }
}
