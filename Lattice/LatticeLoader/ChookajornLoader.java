package Lattice.LatticeLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Stream;

import Lattice.ChookajornSite;
import Lattice.Lattice;

public class ChookajornLoader implements LatticeLoader<ChookajornSite>{

    @Override
    public Lattice<ChookajornSite> load(String path) throws LoadingException{

        //I have no idea how to make this more elegant. But it works.
        
        //READ HEADER
        LammpsInput input;
        Map<String, String> header;
        
        try{
            input = new LammpsInput(new File(path));
        } catch(Exception e){
            throw new LoadingException("Wasn't able to create path.");
        }

        try{                         
            header = input.mapHeader();
        } catch(Exception e){
            throw new LoadingException("Wasn't able to map header.");
        }   
        
        //READ HEADER VARIABLES
        int atoms = Integer.valueOf(header.get("atoms"));
        int types = Integer.valueOf(header.get("atom types"));
        int shells = Integer.valueOf(header.get("bond types"));

        double[] cell = new double[] {
            Double.valueOf(header.get("xlo xhi").split("\\s+")[0]), 	//xlo
            Double.valueOf(header.get("xlo xhi").split("\\s+")[1]),     //xhi
            Double.valueOf(header.get("ylo yhi").split("\\s+")[0]),     //ylo
            Double.valueOf(header.get("ylo yhi").split("\\s+")[1]),     //yhi
            Double.valueOf(header.get("zlo zhi").split("\\s+")[0]),     //zhi
            Double.valueOf(header.get("zlo zhi").split("\\s+")[1])      //zhi
        };

        //CREATE LATTICE SITES

        
        ChookajornSite[] lattice = new ChookajornSite[atoms];
        try(Stream<String> stream = input.getSection("Atoms")){
            stream
            .forEach(s -> {                         //parse all sites and add them to the lattice
                String[] site = s.split("\\s+");
                int id =  Integer.parseInt(site[0]);
                lattice[id - 1] = new ChookajornSite(
                    Double.parseDouble(site[3]),
                    Double.parseDouble(site[4]),
                    Double.parseDouble(site[5]),
                    id - 1,
                    Integer.parseInt(site[2]) - 1,
                    Integer.parseInt(site[1]) - 1
                );
                }
            );
        } catch(Exception e){
            throw new LoadingException("Wasn't able to read atoms.");
        }
        
        //CREATE NEIGHBROING LISTS
        Map<Integer, Map<Integer, List<Integer>>> neighbors = new TreeMap<Integer, Map<Integer, List<Integer>>>(); //maps: AtomID -> type/shell -> List of AtomIDs
        try(Stream<String> stream = input.getSection("Bonds")){
            stream
            .forEach(s -> {                         //parse all bonds and add them to the map of neighbors
                String[] bond = s.split("\\s+");
                int atomID1 = Integer.parseInt(bond[2]) - 1;
                int type = Integer.parseInt(bond[1]) - 1;
                int atomID2 = Integer.parseInt(bond[3]) - 1;
                if(!neighbors.containsKey(atomID1)) neighbors.put(atomID1, new TreeMap<Integer, List<Integer>>());          //add atomID2 as neighbor for atomID1
                if(!neighbors.get(atomID1).containsKey(type)) neighbors.get(atomID1).put(type, new ArrayList<Integer>());
                neighbors.get(atomID1).get(type).add(atomID2);
                if(!neighbors.containsKey(atomID2)) neighbors.put(atomID2, new TreeMap<Integer, List<Integer>>());
                if(!neighbors.get(atomID2).containsKey(type)) neighbors.get(atomID2).put(type, new ArrayList<Integer>());   //add atomID1 as neighbor for atomID2
                neighbors.get(atomID2).get(type).add(atomID1);
            });
        } catch(Exception e){
            throw new LoadingException("Wasn't able to read bonds.");
        }
        
        //can u make it even more complicated?

        for(Entry<Integer, Map<Integer, List<Integer>>> id : neighbors.entrySet()){ //key: atomID, value: map (shell -> list of neighboring atomIDs)
            ChookajornSite[][] n = new ChookajornSite[id.getValue().size()][];
            for(Entry<Integer, List<Integer>> shell : id.getValue().entrySet()){    //key: shell, value: list of neighborng atomIDs
                    n[shell.getKey()] = new ChookajornSite[shell.getValue().size()];
                for(int i = 0; i < shell.getValue().size(); i++){
                    n[shell.getKey()][i] = lattice[shell.getValue().get(i)];
                }
            }
            lattice[id.getKey()].setNeighbors(n);
        }
        return new Lattice<ChookajornSite>(lattice, cell, new int[] {types, shells});
    }
}
