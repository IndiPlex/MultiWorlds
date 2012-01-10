package de.indiplex.multiworlds.generators;

import de.indiplex.multiworlds.MultiWorlds;
import de.indiplex.multiworlds.MultiWorldsAPI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

/**
 *
 * @author temp
 */
public class ObjGenerator extends MWGenerator {

    private boolean[][][] data;
    private int sizeX = Integer.MIN_VALUE;
    private int sizeY = Integer.MIN_VALUE;
    private int sizeZ = Integer.MIN_VALUE;
    private int fX = Integer.MAX_VALUE;
    private int fY = Integer.MAX_VALUE;
    private int fZ = Integer.MAX_VALUE;
    private MultiWorldsAPI api;

    public ObjGenerator(MultiWorldsAPI api) {
        MultiWorlds.wLog("This generator isn't supported yet! The world will be empty");
        this.api = api;
    }
    
    private void init(String wn) {
        File obj = new File(api.getDataFolder(), api.getStringParam(wn, "objFile", "object.obj"));
        try {
            BufferedReader in = new BufferedReader(new FileReader(obj));
            ArrayList<BlockVector> xs = new ArrayList<BlockVector>();

            while (in.ready()) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("v ")) {
                    BlockVector P3D = parseP3D(line);
                    if (P3D.getBlockY() > 12.7) {
                        P3D.setY(12.7);
                    }
                    int x = (int) Math.round(P3D.getX() * 10);
                    int y = (int) Math.round(P3D.getY() * 10);
                    int z = (int) Math.round(P3D.getZ() * 10);

                    if (x > sizeX) {
                        sizeX = x;
                    }
                    if (x < fX) {
                        fX = x;
                    }
                    if (y > sizeY) {
                        sizeY = y;
                    }
                    if (y < fY) {
                        fY = y;
                    }
                    if (z > sizeZ) {
                        sizeZ = z;
                    }
                    if (z < fZ) {
                        fZ = z;
                    }

                    xs.add(P3D);
                }
            }
            fX = Math.abs(fX);
            fY = Math.abs(fY);
            fZ = Math.abs(fZ);
            sizeX+=fX;
            sizeY+=fY;
            sizeZ+=fZ;
            data = new boolean[sizeX][sizeY][sizeZ];
            for (int x = 0; x < sizeX; x++) {
                for (int z = 0; z < sizeZ; z++) {
                    for (int y = 0; y < sizeY; y++) {
                        data[x][y][z] = false;
                    }
                }
            }
            for (BlockVector v:xs) {
                try {
                    v.setY(v.getY()+fY);
                    data[v.getBlockX()+fX][v.getBlockY()+fY][v.getBlockZ()+fZ] = true;
                } catch (Exception e) {
                    MultiWorlds.log.warning(MultiWorlds.pre + "Error: "+v.getBlockX()+" "+v.getBlockY()+" "+v.getBlockZ());
                }
            }
            in.close();
            MultiWorlds.log.info(MultiWorlds.pre+"Loaded obj");
        } catch (IOException ex) {
            MultiWorlds.log.warning(MultiWorlds.pre + "Can't find file " + obj);
        }
    }

    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        byte[] blocks = new byte[32768];
        return blocks;
        /*
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int val = (x * 16 + z) * 128 + y;
                    int absX = cx * 16 + x;
                    int absZ = cz * 16 + z;
                    if (data[absX+fX][y+fY][absZ+fZ] || (absX>-10 && absX<10 && absZ>-10 && absZ<10)) {
                        blocks[val] = 0x1;
                    }
                }
            }
        }
        return blocks;
         */
    }

    private BlockVector parseP3D(String line) {
        StringTokenizer token = new StringTokenizer(line, " ");
        token.nextToken();

        try {
            float x = Float.parseFloat(token.nextToken());
            float y = Float.parseFloat(token.nextToken());
            float z = Float.parseFloat(token.nextToken());

            return new BlockVector(x, y, z);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }
}
