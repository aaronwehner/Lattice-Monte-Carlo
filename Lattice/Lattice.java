package Lattice;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.function.Function;

public class Lattice<T extends AbstractSite> implements Iterable<T>{
    public final T[] LATTICE;
    public final double[] CELL; //XLO, XHI, YLO, YHI, ZLO, ZHI
    //public final double ANGLES; (can be stored in CELL)
    public final int[] CONSTANT_PROPERTIES; //like atom types, etc.
    public final int SIZE;

    public Lattice(T[] LATTICE, double[] CELL, int[] CONSTANT_PROPERTIES){
        this.LATTICE = LATTICE;
        this.CELL = CELL;
        this.CONSTANT_PROPERTIES = CONSTANT_PROPERTIES;
        this.SIZE = LATTICE.length;
    }

    public Iterator<T> iterator(){
        return new Iterator<T>() {
            private int pos = 0;

            public boolean hasNext() {
                return pos < LATTICE.length;
            }

            public T next() throws NoSuchElementException {
                if(hasNext()) return LATTICE[pos++];
                else throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public T[] getOrderedLattice(){
        T[] t = this.LATTICE.clone();
        Arrays.sort(t);
        return t;
    }

    public T[] getOrderedLattice(Comparator<T> c){
        T[] t = this.LATTICE.clone();
        Arrays.sort(t, c);
        return t;
    }

    /**
     * Returns an int-array that contains the quantity of a property of a site of the @Lattice. 
     * The array is ordered by the natural ordering of the property of the site.
     * This is especially useful when using the @getOrderedLattice() function.
     * @param getter Getter-Method for the property of the site.
     * @return int-array containing the wanted quantity
     */
    public int[] countSiteProperty(Function<T, Object> getter){
        TreeMap<Object, Integer> map = new TreeMap<Object, Integer>();
        Arrays.stream(this.LATTICE)
        .forEach(e -> {
            Object prop = getter.apply(e);
            if(map.containsKey(prop)) map.put(prop, map.get(prop) + 1);
            else map.put(prop, 1);
        });
        int[] props = new int[map.size()];
        int n = map.size();
        for(int i = 0; i < n; i++){
            props[i] = map.pollFirstEntry().getValue();
        }
        return props;
    }

    public int[] calculatePropertyDistribution(Function<T, Object> getter){
        int[] props = countSiteProperty(getter);
        int[] dist = new int[props.length];
        dist[0] = 0;
        for(int i = 1; i < props.length; i++){
            dist[i] = dist[i-1] + props[i-1];
        }
        return dist;
    }
}
