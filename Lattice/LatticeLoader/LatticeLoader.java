package Lattice.LatticeLoader;

import Lattice.AbstractSite;
import Lattice.Lattice;

public interface LatticeLoader<Site extends AbstractSite>{

    public Lattice<Site> load(String parameters) throws LoadingException;


    public static class LoadingException extends Exception{

        public LoadingException(String s){
            super(s);
        }

        public LoadingException(){
        }
    }
}
