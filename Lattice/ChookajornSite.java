package Lattice;

import Lattice.Interfaces.FixedNeighborsSite;
import Lattice.Interfaces.GrainSite;
import Lattice.Interfaces.TypeSite;

public class ChookajornSite extends AbstractSite implements Comparable<ChookajornSite>, GrainSite, TypeSite, FixedNeighborsSite{

    public static ChookajornSite parseSite(String s){
        String[] site = s.split("\\s+");
        return new ChookajornSite(
                    Double.parseDouble(site[3]),
                    Double.parseDouble(site[4]),
                    Double.parseDouble(site[5]),
                    Integer.parseInt(site[1]),
                    Integer.parseInt(site[2]),
                    Integer.parseInt(site[0])
                );
    }
    
    public final int ID;
    private ChookajornSite[][] neighbors;
    private int type, grain;

    public ChookajornSite(double X, double Y, double Z,int ID, int type, int grain){
        super(X, Y, Z);
        this.ID = ID;
        this.grain = grain;
        this.type = type;
    }

    public ChookajornSite(double X, double Y, double Z,int ID, int type, int grain, ChookajornSite[][] neighbors){
        super(X, Y, Z);
        this.ID = ID;
        this.grain = grain;
        this.type = type;
        this.neighbors = neighbors;
    }

    public int getType(){
        return this.type;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getGrain(){
        return this.grain;
    }

    public void setGrain(int grain){
        this.grain = grain;
    }

    public ChookajornSite[][] getNeighbors(){
        return this.neighbors;
    }

    public void setNeighbors(ChookajornSite[][] neighbors){
        this.neighbors = neighbors;
    }

    public void setNeighbor(int shell, int index, ChookajornSite site) throws IndexOutOfBoundsException{
        this.neighbors[shell][index] = site;
    }

    public int compareTo(ChookajornSite site){
        if(this.type < site.type) return -1;
        else if(this.type > site.type) return 1;
        else return 0;
    }

    @Override
    public String toString(){
        return (this.ID + 1) + "\t" + (this.grain + 1) + "\t" + (this.type + 1) + "\t" + this.X + "\t" + this.Y + "\t" + this.Z;
    }
}
