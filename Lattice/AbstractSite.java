package Lattice;

public abstract class AbstractSite{
    public final double X, Y, Z;    //coordinates of the site

    public AbstractSite(double X, double Y, double Z){
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }
}
