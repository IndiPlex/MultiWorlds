package de.indiplex.multiworlds.generators.util;

import java.io.Serializable;

/**
 *
 * @author temp
 */
public class Point3D implements Serializable {
    
    public int X;
    public int Y;
    public int Z;

    public Point3D(int X, int Y, int Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public Point3D(Point3D p) {
        this.X = p.X;
        this.Y = p.Y;
        this.Z = p.Z;
    }
    
    public Point3D add(int x, int y, int z) {
        return new Point3D(X+x, Y+y, Z+z);
    }
    
    public void addToThis(int x, int y, int z) {
        X+=x;
        Y+=y;
        Z+=z;
    }
    
    public void addToThis(int f) {
        X+=f;
        Y+=f;
        Z+=f;
    }

    @Override
    public String toString() {
        return X+" "+Y+" "+Z;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Point3D) && obj.hashCode()==hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.X;
        hash = 53 * hash + this.Y;
        hash = 53 * hash + this.Z;
        return hash;
    }
        
}
