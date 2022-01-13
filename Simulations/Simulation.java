package Simulations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Random;

import Energy.AbstractEnergy;
import IO.InputFile;
import IO.MCOutput;
import Lattice.AbstractSite;
import Lattice.Lattice;
import Lattice.LatticeLoader.LatticeLoader;
import MC.MonteCarlo;
import MC.Events.AbstractEvent;
import MC.Events.EventManager;
import Temperature.AbstractTemperature;
import IO.IO;

public class Simulation<Site extends AbstractSite>{

    public final Map<String, String> INPUT_DATA;
    public final MonteCarlo<Site> MC;
    public final int STEPS;
    public final double[] RATE_OF_ACCEPTANCE;
    public final MCOutput<Site> OUTPUT;

    public Simulation(Map<String, String> INPUT_DATA) throws InputMismatchException{
        this.INPUT_DATA = INPUT_DATA;
        
        //INITIALIZE MC
        this.MC = new MonteCarlo<Site>(
            generateLattice(), 
            generateEnergy(), 
            generateRandom(), 
            generateEventManager(),
            generateTemperature()
        );

        //INITIALIZE REST
        this.OUTPUT = generateOutput();
        this.STEPS = Integer.parseInt(this.INPUT_DATA.get("steps"));
        this.RATE_OF_ACCEPTANCE = new double[this.STEPS + 1];
        this.RATE_OF_ACCEPTANCE[0] = 0;
    }

    public Simulation(File file) throws IOException{
        this(new InputFile(file.getAbsolutePath()).DATA);
    }

    protected Random generateRandom() throws InputMismatchException{
        Random random;
        try{
            String[] r = this.INPUT_DATA.get("random").split("\\s*\\|\\s*");
            if(r.length == 2){
                random = (Random) Class.forName(r[0]).getConstructor(long.class).newInstance(Long.parseLong(r[1]));
            } else {
                random = (Random) Class.forName(r[0]).getConstructor().newInstance();
            }
        } catch(Exception e){
            throw new InputMismatchException("Bad paramteters for input parameter \"random\": " + e.getLocalizedMessage());
        }
        return random;
    }

    protected AbstractTemperature generateTemperature() throws InputMismatchException{
        AbstractTemperature temperature;
        try{
            String[] t = this.INPUT_DATA.get("temperature_programm").split("\\s*\\|\\s*");
            temperature = (AbstractTemperature) Class.forName(t[0]).getConstructor(String.class).newInstance(t[1]);
        } catch(Exception e){
            throw new InputMismatchException("Bad paramteters for input parameter \"temperature_programm\": " + e.getLocalizedMessage());
        }
        return temperature;
    }

    @SuppressWarnings("unchecked")
    protected EventManager<Site> generateEventManager() throws InputMismatchException{
        String[] events = this.INPUT_DATA.get("events").split("\\s*,\\s*");
        Class<AbstractEvent<Site>>[] classes = new Class[events.length];
        double[] propabilites = new double[events.length];
        for(int i = 0; i < events.length; i++){
            try {
                String[] event = events[i].split("\\s*\\|\\s*");
                classes[i] = (Class<AbstractEvent<Site>>) Class.forName(event[0]);
                propabilites[i] = Double.parseDouble(event[1]);
            } catch (Exception e) {
                e.printStackTrace();
                throw new InputMismatchException("Bad paramteters for input parameter \"events\".");
            }
        }
        EventManager<Site> eventManager;
        try{
            eventManager = new EventManager<Site>(classes, propabilites);
        } catch (Exception e){
            throw new InputMismatchException(e.getLocalizedMessage());
        }
        return eventManager;
    }
    
    protected double[] generateEventPropabilities() throws InputMismatchException{
        String[] events = this.INPUT_DATA.get("events").split("\\s*,\\s*");
        double[] propabilities = new double[events.length];
        for(int i = 0; i < events.length; i++){
            try {
                propabilities[i] = (double) Double.parseDouble(events[i].replaceAll(".*\\|\\s*", ""));
            } catch (Exception e) {
                throw new InputMismatchException("Bad paramteters for input parameter \"events\": " + e.getLocalizedMessage());
            }
        }
        return propabilities;
    }

    @SuppressWarnings("unchecked")
    protected AbstractEnergy<Site> generateEnergy() throws InputMismatchException{
        try{
            return (AbstractEnergy<Site>) Class.forName(INPUT_DATA.get("energy_class"))
                                .getConstructor(String.class)
                                .newInstance(INPUT_DATA.get("ENERGY_PARAMETERS"));
        } catch(Exception e){
            throw new InputMismatchException("Bad paramteters for input parameter \"energy\": " + e.getLocalizedMessage());
        }
               
    }

    @SuppressWarnings("unchecked")
    protected Lattice<Site> generateLattice() throws InputMismatchException{
        String[] p = this.INPUT_DATA.get("lattice_loader").split("\\s*\\|\\s*");
        Lattice<Site> lattice;
        try{
            LatticeLoader<Site> loader = (LatticeLoader<Site>) Class.forName(p[0])
                                                    .getConstructor()
                                                    .newInstance();
            lattice = (Lattice<Site>) loader.load(p[1]);
        } catch (Exception e){
            throw new InputMismatchException(e.getLocalizedMessage());
        }
        return lattice;         
    }

    @SuppressWarnings("unchecked")
    protected MCOutput<Site> generateOutput() throws InputMismatchException{
        MCOutput<Site> output;
        try{
            output = (MCOutput<Site>) Class.forName(this.INPUT_DATA.get("output_class"))
                                                    .getConstructor(String.class, String.class, MonteCarlo.class, int.class)
                                                    .newInstance(
                                                        this.INPUT_DATA.get("output_path"),
                                                        "Lattices",
                                                        this.MC,
                                                        Integer.parseInt(this.INPUT_DATA.get("output_frequency"))
                                                    );
        } catch (Exception e){
            throw new InputMismatchException(e.getLocalizedMessage());
        }
        return output; 
    }

    public boolean start() throws SimulationErrorExcpetion{

        //Show Message
        System.out.println("_______________________________________");
        System.out.println("Simulation has started:");
        System.out.println();
        System.out.println(this.INPUT_DATA.get("COMMENT"));
        System.out.println("_______________________________________");

        //Time of Start.
        long time_i = System.currentTimeMillis();

        //Perform Simulation
        try{
            //Copy input file to output.
            if(this.INPUT_DATA.containsKey("input_path")) Files.copy(
                Paths.get(this.INPUT_DATA.get("input_path")),
                Paths.get(this.OUTPUT.MAINPATH + IO.PATHSEP + "used_input.mc")
            );
            
            //Write initial state.
            
            this.OUTPUT.writeLattice();
            //Perform steps.
            for(int i = 1; i <= this.STEPS; i++){
                this.MC.performStep();
                this.OUTPUT.writeLatticeIfNecessary();
            }
            return true;
        } catch(Exception e){
            e.printStackTrace();
            throw new SimulationErrorExcpetion("An error occured during the simulation: " + e.getLocalizedMessage());
        } finally{
            try {
                this.OUTPUT.saveData();
            } catch (IOException e) {
                throw new SimulationErrorExcpetion("Data could not be saved: " + e.getLocalizedMessage());
            }
            long time_d = System.currentTimeMillis() - time_i;

            System.out.println("_______________________________________");
            System.out.println("Simulation finished after " + ((double) time_d / 60000) + "min.");
            System.out.println("_______________________________________");
        }
    }

    public static class SimulationErrorExcpetion extends Exception{
        public SimulationErrorExcpetion(){};
        public SimulationErrorExcpetion(String msg){
            super(msg);
        }
    }
}

    /*
    public static Map<String, Object> parseInputFile(File file) throws IOException{
        Map<String, String> data = Input.readInputFile(file);
        Map<String, Object> p = new TreeMap<String, Object>();

        p.put("comment", data.get("COMMENT"));
        if(Boolean.parseBoolean(data.get("use_seed"))) p.put("random", new MersenneTwister());
        else p.put("random", new MersenneTwister(Long.parseLong(data.get("seed"))));
        p.put("lattice", Input.latticeFromLammpsFile(new File(data.get("path_of_initialization"))));
        p.put("steps", Integer.parseInt(data.get("number_of_steps")));
        p.put("gs", Double.parseDouble(data.get("propability_of_grain_switch")));
        p.put("energy", Simulation.parseEnergyParameters(data.get("ENERGY"), ((SimpleLattice) p.get("lattice")).TYPES, ((SimpleLattice) p.get("lattice")).SHELLS));
        p.put("t_init", Double.parseDouble(data.get("initial_temperature")));
        p.put("t_fin", Double.parseDouble(data.get("final_temperature")));
        p.put("conv", Double.parseDouble(data.get("convergence_parameter")));
        p.put("output_path", Paths.get(data.get("output_path")));
        p.put("freq", Integer.parseInt(data.get("saving_frequency")));
        p.put("input", Path.of(data.get("input_path")));
        return p;
    }

    public static double[][][][] parseEnergyParameters(String s, int types, int shells){
        String[] lines = s.split(System.lineSeparator());
        double[][][][] ising = new double[shells][2][types][types];
        int index = 0;
        for(int shell = 0; shell < shells; shell++){
            index++;                                        //skip shell identifyer
            for(int c_gb = 0; c_gb < 2; c_gb++){
                index++;                                    //skip grain allegiance identifyer
                for(int row = 0; row < types; row++){
                    if(row == 0) index++;                   //skip first line of matrix
                    String[] line = lines[index].replaceAll("^\\D+", "").strip().split("\\s+");
                    for(int col = 0; col < types; col++){
                        try{
                            ising[shell][c_gb][row][col] = Double.parseDouble(line[col]); //lineParameters[atomTypeNumber+1] "+1" is required due to the fact, that the atom type is given at lineParameters[0]
                            ising[shell][c_gb][col][row] = Double.parseDouble(line[col]); //matrix has to be symmetric. for same indices (diagonal elements) the previous entry is overwritten, which is not necessary (because it's the same) but was the easiest implementation.
                        } catch (Exception e) {
                        
                        } finally {
                            index++;
                        }
                    }
                }
            }
        }
        return ising;
    }
    
    public static void performSimulation(Map<String, Object> parameters){
        try{
            //Read data from Map
            MersenneTwister random = (MersenneTwister) parameters.get("random");
            double[][][][] energy = (double[][][][]) parameters.get("energy");
            Path output_path = (Path) parameters.get("output_path");
            double t_init = (double) parameters.get("t_init");
            double t_fin = (double) parameters.get("t_fin");
            double convergence = (double) parameters.get("conv");
            int steps = (int) parameters.get("steps");
            double gs = (double) parameters.get("gs");
            int freq = (int) parameters.get("freq");
            String info = (String) parameters.get("comment");
            SimpleLattice lattice = (SimpleLattice) parameters.get("lattice");
            Path input = (Path) parameters.get("input");
            MonteCarlo mc = new MonteCarlo(lattice, random, energy);
            MCOutput output_writer = new MCOutput(output_path.toString(), mc, "Lattices");
            //Create Objects used for Simulation
            Files.copy(input, Paths.get(output_writer.mainpath + IO.PATHSEP + "usedInput.mc"));
            performSimulation(mc, output_writer, t_init, t_fin, convergence, gs, steps, freq, info);
        } catch(Exception e){
            System.err.println("System could not be built: " + IO.LINESEP + e.getLocalizedMessage());
            e.printStackTrace();
            System.err.println();
            System.err.println("Simulation will be terminated. Maybe you have to delete files that have been created.");
            System.exit(0);
        }
        
    }

    public static void performSimulation(MonteCarlo mc, MCOutput output, double t_init, double t_fin, double conv, double gs, int steps, int freq, String info){
            
            System.out.println("SIMULATION HAS STARTED:");
            System.out.println(info);

        long time_init = System.currentTimeMillis();
        ArrayList<Object[]> energyHistory = new ArrayList<Object[]>();
        double t = t_init;
        //Step 0 is referring to initial state.
        energyHistory.add(new Double[] {0.0, t, mc.getTotalEnergy()});
        output.writeLattice(mc.lattice, "Lattices", 0 + ".lmp");

        int count;
        for(count  = 1; count <= steps; count++){
            mc.increaseTotalEnergy(mc.performStep(t, gs)); //perform MC-step
            energyHistory.add(new Double[] {(double) count, t, mc.getTotalEnergy()}); 
            t = mc.getNewTemp(t, t_fin, conv);
            if(count%(freq) == 0){
                mc.nucleationGrainNumber =  mc.lattice.refreshGrainNumbers();
                output.writeLattice(mc.lattice, "Lattices", count + ".lmp");
            } 
        }

        if((count-1)%(freq) != 0){ //if final structure has not yet been written to a file.
            mc.nucleationGrainNumber = mc.lattice.refreshGrainNumbers();
            output.writeLattice(mc.lattice, "Lattices", count + ".lmp");
        }
         
        output.writeArrayListWithColumns(energyHistory, "", "Energy.dat");
        long time_fin = System.currentTimeMillis();
        long time_needed = time_fin - time_init;
        String time = "Time needed for this simulation: " + time_needed/1000 + "s = " + (double) time_needed/60000 + "min = " + (double) time_needed/3600000 + "h";
        output.writeString(time, "", "time.txt");

            System.out.println(time);
            System.out.println("SIMULATION IS FINISHED");
            System.out.println();
    }*/

