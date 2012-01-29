package de.indiplex.multiworlds.generators;

import de.indiplex.multiworlds.MultiWorlds;
import de.indiplex.multiworlds.generators.util.MWActionGenerator;
import de.indiplex.multiworlds.generators.util.Point3D;
import de.indiplex.multiworlds.generators.util.Sphere;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 *
 * @author temp
 */
public class SpherePopulator extends BlockPopulator implements MWActionGenerator {

    private static final int MAX_SPHERE_SIZE = 40;
    private static final int GLOWSTONE_CHANCE = 1000; // Of 10000
    private static final int SPHERE_CHANCE = 400; // Of 10000
    private static final int MIN_SPHERE_SIZE = 16;    
    private static final int MIN_SPHERE_DISTANCE_FROM_GROUND = 4;
    
    private ArrayList<Sphere> spheres;

    public SpherePopulator() {               
        spheres = new ArrayList<Sphere>();
        try {
            load();
        } catch (IOException ex) {
            System.out.println("rofl");
        } catch (ClassNotFoundException ex) {
            System.out.println("xD");
        }
    }
    
    private void save() throws IOException {
        MultiWorlds.wLog(String.valueOf(MultiWorlds.getAPI().putData("spheres", "data", spheres)));        
    }
    
    private void load() throws IOException, ClassNotFoundException {
        Object data = MultiWorlds.getAPI().getData("spheres", "data");
        if(data==null) {
            return;
        } else if (!(data instanceof List)) {            
            MultiWorlds.wLog("Can't load spheres... :"+data.getClass().toString());
            return;
        } 
        spheres = (ArrayList<Sphere>) data;
        MultiWorlds.wLog("DEBUG: loaded "+spheres.size()+" spheres");
    }
    
    /**
     * Makes a sphere or ellipsoid. (Taken from WorldEdit)
     *
     * @param pos Center of the sphere or ellipsoid
     * @param block The block pattern to use
     * @param radiusX The sphere/ellipsoid's largest north/south extent
     * @param radiusY The sphere/ellipsoid's largest up/down extent
     * @param radiusZ The sphere/ellipsoid's largest east/west extent
     * @param filled If false, only a shell will be generated.
     * @return number of blocks changed
     * @throws MaxChangedBlocksException
     */
    private int makeSphere(Sphere s, Random r) {
        boolean filled = false;
        ArrayList<Point3D> toClearAndFill = new ArrayList<Point3D>();
        ArrayList<Point3D> toFillWithGlass = new ArrayList<Point3D>();
        int affected = 0;
                
        float radiusX = s.getrX()+0.5f;
        float radiusY = s.getrY()+0.5f;
        float radiusZ = s.getrZ()+0.5f;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusY = (int) Math.ceil(radiusY);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX:
        for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY:
            for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ:
                for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (!filled) {
                        if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                            toClearAndFill.add(new Point3D(x, y, z));
                            continue;
                        }
                    }                    
                    toFillWithGlass.add(new Point3D(x, y, z));
                }
            }
        }
        for(Point3D pos:toFillWithGlass) {
            boolean useGlow = r.nextInt(10000)<GLOWSTONE_CHANCE;
            setAllBlock(s.getPosition(), pos, s.getWorld(), useGlow?Material.GLOWSTONE:Material.GLASS);
        }
        for(Point3D pos:toClearAndFill) {
            setAllBlock(s.getPosition(), pos, s.getWorld(), Material.AIR);
        }
        if (!s.getType().equals(Sphere.Type.EMPTY)) {
            for(Point3D pos:toClearAndFill) {
                setAllBlock(s.getPosition(), pos, s.getWorld(), s.getMatAtPos(pos));
            }
        }
        return affected;
    }

    private double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    private void setAllBlock(Point3D pos, Point3D otherPos, World world, Material mat) {
        int x = otherPos.X;
        int y = otherPos.Y;
        int z = otherPos.Z;
        setBlock(pos.add(x, y, z), world, mat);
        setBlock(pos.add(-x, y, z), world, mat);
        setBlock(pos.add(x, y, -z), world, mat);
        setBlock(pos.add(-x, y, -z), world, mat);
        
        setBlock(pos.add(x, -y, z), world, mat);
        setBlock(pos.add(-x, -y, z), world, mat);                
        setBlock(pos.add(x, -y, -z), world, mat);        
        setBlock(pos.add(-x, -y, -z), world, mat);
    }

    private void setBlock(Point3D pos, World world, Material mat) {
        Location loc = new Location(world, pos.X, pos.Y, pos.Z);
        Chunk c = world.getChunkAt(loc);
        if (!world.isChunkLoaded(c)) {
            world.loadChunk(c);
        }
        world.getBlockAt(loc).setType(mat);
    }

    private synchronized boolean canSphereBuildHere(int rX, int rY, int rZ, Point3D pos) {
        for (Sphere s:spheres) {
            if (s.checkSphereCollision(pos, rX, rY, rZ)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized void populate(World w, Random r, Chunk c) {
        //for (int i = 1; i <= 3; i++) {
        //  w.loadChunk(c.getX() + i, c.getZ() + i);
        //w.loadChunk(c.getX() - i, c.getZ() - i);
        //}        
        if (r.nextInt(10000) > SPHERE_CHANCE) {            
            return;
        }

        int radiusX = r.nextInt(MAX_SPHERE_SIZE - MIN_SPHERE_SIZE) + MIN_SPHERE_SIZE;
        int radiusY = r.nextInt(MAX_SPHERE_SIZE - MIN_SPHERE_SIZE) + MIN_SPHERE_SIZE;
        int radiusZ = r.nextInt(MAX_SPHERE_SIZE - MIN_SPHERE_SIZE) + MIN_SPHERE_SIZE;
        int posX = 16 * c.getX() + r.nextInt(16);
        int posZ = 16 * c.getZ() + r.nextInt(16);
        int posY = r.nextInt(128  - 2*(MIN_SPHERE_DISTANCE_FROM_GROUND + radiusY)) + radiusY +MIN_SPHERE_DISTANCE_FROM_GROUND;
        
        Point3D pos = new Point3D(posX, posY, posZ);
        if (canSphereBuildHere(radiusX, radiusY, radiusZ, pos)) {
            Sphere s = new Sphere(radiusX, radiusY, radiusZ, pos, w, r);
            spheres.add(s);
            makeSphere(s, r);                        
            MultiWorlds.iLog("Placed Sphere at " + pos + " with type "+s.getType().toString());
        }
    }

    @Override
    public void onDisable() {
        try {
            save();
        } catch (IOException ex) {
            System.out.println("lol");
        }
    }
}
