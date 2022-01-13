package Energy;

import Lattice.AbstractSite;
import Lattice.Interfaces.FixedNeighborsSite;
import Lattice.Interfaces.GrainSite;
import Lattice.Interfaces.TypeSite;
import IO.IO;

public class ChookajornEnergy<Site extends AbstractSite & GrainSite & TypeSite &FixedNeighborsSite> extends AbstractEnergy<Site>{

    private double[][][][] ising;

    public ChookajornEnergy(String parameters){
        this.ising = ChookajornEnergy.parseEnergy(parameters);
    }

    @SuppressWarnings("unchecked")
    public double calculateEnergy(Site site){
        double energy = 0;
        Site[][] neighbors = (Site[][]) site.getNeighbors();
        for(int shell = 0; shell < neighbors.length; shell++){
            for(Site neighborSite: neighbors[shell]){
                //atoms in the same grain
                if(site.getGrain() == neighborSite.getGrain()) energy += ising[shell][0][site.getType()][neighborSite.getType()];
                //atoms in different grains
                else energy +=ising[shell][1][site.getType()][neighborSite.getType()];
            }
        }
        return 0.5*energy;
    }

    public double[][][][] getEnergyParameters(){
        return ising;
    }

    public static double[][][][] parseEnergy(String s){
        String[] lines = s.split(IO.LINESEP);
        int shells = Integer.parseInt(lines[0].replaceAll("shells\\s*\\|\\s*", ""));
        int types = Integer.parseInt(lines[1].replaceAll("types\\s*\\|\\s*", ""));
        double[][][][] ising = new double[shells][2][types][types];
        int index = 2;
        for(int shell = 0; shell < shells; shell++){
            index++;                                          //skip shell identifyer
            for(int c_gb = 0; c_gb < 2; c_gb++){
                index++;                                      //skip grain allegiance identifyer
                for(int row = 0; row < types; row++){
                    //if(row == 0) index++;                   //skip first line of matrix
                    String[] line = lines[index].strip().split("\\s+");
                    for(int col = 0; col < types; col++){
                        try{
                            ising[shell][c_gb][row][col] = Double.parseDouble(line[col+1]); //lineParameters[atomTypeNumber+1] "+1" is required due to the fact, that the atom type is given at lineParameters[0]
                            ising[shell][c_gb][col][row] = Double.parseDouble(line[col+1]); //matrix has to be symmetric. for same indices (diagonal elements) the previous entry is overwritten, which is not necessary (because it's the same) but was the easiest implementation.
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                    index++;
                }
            }
        }
        return ising;
    }
}
