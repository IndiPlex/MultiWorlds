package de.indiplex.multiworlds.generators.util;

import de.indiplex.multiworlds.generators.SpherePopulator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author Kahabrakh
 */
public class Sphere implements Serializable {

    private static final int MIN_SPHERE_DISTANCE = 16;
    private int rX;
    private int rY;
    private int rZ;
    private Point3D position;
    private Type type;
    private World world;
    private Random r;
    private static final ArrayList<Integer> blackList = new ArrayList<Integer>();

    static {
        blackList.add(Material.LAVA.getId());
        blackList.add(Material.STATIONARY_LAVA.getId());
        blackList.add(Material.WATER.getId());
        blackList.add(Material.STATIONARY_WATER.getId());
        blackList.add(Material.BEDROCK.getId());
        blackList.add(Material.BED_BLOCK.getId());
        blackList.add(Material.BURNING_FURNACE.getId());
        blackList.add(Material.FURNACE.getId());
        blackList.add(Material.WALL_SIGN.getId());
        blackList.add(Material.SIGN_POST.getId());
        blackList.add(Material.RAILS.getId());
        blackList.add(Material.POWERED_RAIL.getId());
        blackList.add(Material.DETECTOR_RAIL.getId());
        blackList.add(Material.IRON_DOOR_BLOCK.getId());
        blackList.add(Material.WOODEN_DOOR.getId());
        blackList.add(Material.LADDER.getId());
        blackList.add(Material.SNOW.getId());
        blackList.add(Material.CACTUS.getId());
        blackList.add(Material.PORTAL.getId());
        blackList.add(Material.REDSTONE_TORCH_OFF.getId());
        blackList.add(Material.REDSTONE_TORCH_ON.getId());
        blackList.add(Material.REDSTONE_WIRE.getId());
        blackList.add(Material.PISTON_MOVING_PIECE.getId());
        blackList.add(Material.PISTON_EXTENSION.getId());
        blackList.add(Material.RED_ROSE.getId());
        blackList.add(Material.YELLOW_FLOWER.getId());
        blackList.add(Material.RED_MUSHROOM.getId());
        blackList.add(Material.BROWN_MUSHROOM.getId());
        blackList.add(Material.LONG_GRASS.getId());
        blackList.add(Material.DEAD_BUSH.getId());
        blackList.add(Material.LEAVES.getId());
        blackList.add(Material.FIRE.getId());
        blackList.add(Material.WOOD_PLATE.getId());
        blackList.add(Material.STONE_PLATE.getId());
        blackList.add(Material.STONE_BUTTON.getId());
        blackList.add(Material.TNT.getId());
        blackList.add(Material.TORCH.getId());
        blackList.add(Material.LEVER.getId());
        blackList.add(Material.SAPLING.getId());
        blackList.add(Material.AIR.getId());
        blackList.add(Material.CROPS.getId());
        blackList.add(Material.MOB_SPAWNER.getId());
        blackList.add(Material.SUGAR_CANE_BLOCK.getId());
        blackList.add(Material.DIODE_BLOCK_OFF.getId());
        blackList.add(Material.DIODE_BLOCK_ON.getId());
        blackList.add(Material.ICE.getId());
        blackList.add(Material.TRAP_DOOR.getId());
        blackList.add(Material.MELON_BLOCK.getId());        
        blackList.add(Material.VINE.getId());
    }

    public Sphere(int rX, int rY, int rZ, Point3D position, World world) {
        this(rX, rY, rZ, position, world, new Random());
    }

    public Sphere(int rX, int rY, int rZ, Point3D position, World world, Random r) {
        this.rX = rX;
        this.rY = rY;
        this.rZ = rZ;
        this.position = position;
        this.world = world;
        this.r = r;
        type = Type.getTypeByChance(r.nextInt(10000));
    }

    public int getrX() {
        return rX;
    }

    public World getWorld() {
        return world;
    }

    public Type getType() {
        return type;
    }

    public int getrZ() {
        return rZ;
    }

    public int getrY() {
        return rY;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setrX(int rX) {
        this.rX = rX;
    }

    public void setrY(int rY) {
        this.rY = rY;
    }

    public void setrZ(int rZ) {
        this.rZ = rZ;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Sphere) && obj.hashCode() == hashCode();
    }

    public Material getMatAtPos(Point3D pos) {
        switch (type) {
            case FILLED_LAVA:
                return Material.STATIONARY_LAVA;
            case FILLED_RANDOM:
                Material mat = null;
                boolean b = false;
                while (!b) {
                    mat = Material.getMaterial(r.nextInt(256));
                    if (mat != null) {
                        if (!blackList.contains(mat.getId())) {
                            if (mat.isBlock()) {
                                b = true;
                            }
                        }
                    }
                }
                return mat;
            default:
                return Material.AIR;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.rX;
        hash = 83 * hash + this.rY;
        hash = 83 * hash + this.rZ;
        hash = 83 * hash + (this.position != null ? this.position.hashCode() : 0);
        return hash;
    }

    public boolean checkSphereCollision(Point3D center, int rX, int rY, int rZ) {
        center = new Point3D(center);
        int r1 = Math.max(rX, rZ);
        int r2 = Math.max(this.rX, this.rZ);
        center.X = center.X < position.X ? center.X + MIN_SPHERE_DISTANCE : center.X - MIN_SPHERE_DISTANCE;
        center.Z = center.Z < position.Z ? center.Z + MIN_SPHERE_DISTANCE : center.Z - MIN_SPHERE_DISTANCE;

        return sqr(center.X - position.X) + sqr(center.Z - position.Z) < sqr(r1 + r2);
    }

    private int sqr(int i) {
        return i * i;
    }

    public enum Type implements Serializable {
        
        FILLED_RANDOM(10),
        FILLED_LAVA(100),        
        EMPTY(10000);
        private int chance;

        private Type(int chance) {
            this.chance = chance;
        }

        public int getChance() {
            return chance;
        }

        public static Type getTypeByChance(int c) {
            for (Type t : values()) {
                if (t.getChance() > c) {
                    return t;
                }
            }
            return null;
        }

        public static int[] getChanceArray() {
            Type[] types = values();
            int[] data = new int[types.length];
            for (int i = 0; i < types.length; i++) {
                data[i] = types[i].getChance();
            }
            return data;
        }
    }
}
