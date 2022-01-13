package IO;

import java.io.BufferedWriter;
import java.io.IOException;

import Lattice.AbstractSite;
import Lattice.ChookajornSite;
import MC.MonteCarlo;

public class ChookajornOutput extends MCOutput<ChookajornSite>{

    public ChookajornOutput(String mainpath, String LATTICEPATH, MonteCarlo<ChookajornSite> MC, int FREQUENCY) {
        super(mainpath, LATTICEPATH, MC, FREQUENCY);
    }

    @Override
    public void writeHeader(BufferedWriter bw) throws IOException{

         //write comment
        bw.write(
            ("#Output of MC-Simulation") + LINESEP
        );
        //write header
        bw.write(
            this.MC.LATTICE.SIZE + " atoms" + LINESEP
            + this.MC.LATTICE.CONSTANT_PROPERTIES[0] + " atom types" + LINESEP
            + this.MC.LATTICE.CELL[0] + " " + this.MC.LATTICE.CELL[1] + " xlo xhi" + LINESEP
            + this.MC.LATTICE.CELL[2] + " " + this.MC.LATTICE.CELL[3] + " ylo yhi" + LINESEP
            + this.MC.LATTICE.CELL[4] + " " + this.MC.LATTICE.CELL[5] + " zlo zhi" + LINESEP
            + LINESEP
        );
    }

    @Override
    public void writeSites(BufferedWriter bw) throws IOException{
        //write atoms
        bw.write("Atoms" + LINESEP + LINESEP);
        for(AbstractSite site : MC.LATTICE.LATTICE){
            bw.write(site + LINESEP);
        }
    }
}

