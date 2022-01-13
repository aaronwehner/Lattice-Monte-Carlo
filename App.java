import java.io.File;

import Simulations.Simulation;

public class App {
    public static void main(String[] args) throws Exception {
        Simulation test = new Simulation(new File(args[0]));
        test.start();
    }
    //"/nfshome/wehner/MonteCarlo/06_Workspace/12-10/Test_GS/input.txt"
}