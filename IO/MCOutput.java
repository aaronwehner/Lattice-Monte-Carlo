package IO;

import java.io.BufferedWriter;
import java.io.IOException;

import Lattice.AbstractSite;
import MC.MonteCarlo;

public abstract class MCOutput<Site extends AbstractSite> extends Output{

    public final MonteCarlo<Site> MC;
    public final String LATTICEPATH;
    public final int FREQUENCY;

    public MCOutput(String mainpath, String LATTICEPATH, MonteCarlo<Site> MC, int FREQUENCY){
        super(mainpath);
        this.MC = MC;
        this.LATTICEPATH = LATTICEPATH;
        this.FREQUENCY = FREQUENCY;
    }
    
    public void writeLattice() throws LatticeWritingException{
        this.writeLattice(this.MC.getStep() + ".lmp");
    }

    public void writeLattice(String filename) throws LatticeWritingException{
        try(BufferedWriter bw = this.getBufferedWriter(this.LATTICEPATH, filename)){
            this.MC.EVENTS.refresh();
            this.writeHeader(bw);
            this.writeSites(bw);
        } catch (Exception e) {
            throw new LatticeWritingException("Failed to write the lattice to file:" + e.getLocalizedMessage());
        }
    }

    public void writeLatticeIfNecessary() throws IOException{
        if(this.MC.getStep() % this.FREQUENCY == 0) this.writeLattice();
    }

    public void saveData() throws IOException{
        if(this.MC.getStep() % this.FREQUENCY != 0) this.writeLattice(this.MC.getStep() + ".lmp");  //if final state has not been saved yet
        super.writeMap(this.MC.ENERGY.getEnergyHistory(), "", "energy.dat");
        super.writeMap(this.MC.EVENTS.getRateOfAccepanceHistory(), "", "rate_of_acceptance.dat");
    }

    protected abstract void writeHeader(BufferedWriter bw) throws IOException;
    protected abstract void writeSites(BufferedWriter bw) throws IOException;

    public static class LatticeWritingException extends IOException{
        public LatticeWritingException(){
            super();
        }
        public LatticeWritingException(String msg){
            super(msg);
        }
    }


}
