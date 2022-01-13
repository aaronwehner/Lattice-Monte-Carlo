package MC.Events;

import MC.MonteCarlo;
import Lattice.*;
import Lattice.Interfaces.*;

public class GrainSwapEvent<Site extends AbstractSite & GrainSite & FixedNeighborsSite> extends AbstractEvent<Site>{

    public GrainSwapEvent(MonteCarlo<Site> MC) {
        super(MC);
    }

    @Override
    public Double perform() {
        //choose random lattice site
        int indexA = MC.RANDOM.nextInt(MC.LATTICE.LATTICE.length);
        Site siteA = MC.LATTICE.LATTICE[indexA];
        int indexB = MC.RANDOM.nextInt(MC.LATTICE.LATTICE.length);
        Site siteB = MC.LATTICE.LATTICE[indexB];

        //calculate energy before switch
        double energyBefore = MC.ENERGY.calculateEnergy(siteA) + MC.ENERGY.calculateEnergy(siteB);

        //switch grains
        int storeGrainA = siteA.getGrain();
        siteA.setGrain(siteB.getGrain());
        siteB.setGrain(storeGrainA);

        //calculate energy difference
        double energyAfterA = MC.ENERGY.calculateEnergy(siteA);
        double energyAfterB = MC.ENERGY.calculateEnergy(siteB);
        
        //Factor 2 because bot only the site energy of the actual sites but also these of its neighbors is changed
        //and the energy difference here is not only referring to the difference in site energy of the involved sites but
        //the energy difference of the whole lattice caused by the switch.
        double energyDifference = 2*(energyAfterA + energyAfterB - energyBefore);

        //test if switching event is accepted
        if(super.accepted(energyDifference)){
            return energyDifference;
        }
        else {
            siteB.setGrain(siteA.getGrain());
            siteA.setGrain(storeGrainA);
            return null;
        }
    }

    @Override
    public void refresh() {
        //do nothing
    }
}
