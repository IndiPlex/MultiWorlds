package de.indiplex.multiworlds.generators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

/**
 *
 * @author temp
 */
public class PicWoolPopulator extends BlockPopulator {
    
    private PicGenerator pg;

    public PicWoolPopulator(PicGenerator pg) {
        this.pg = pg;        
    }        

    @Override
    public void populate(World world, Random random, Chunk source) {
        int y = 20;
        Integer[][] data = pg.datas.get(world.getName());
        if (data==null) return;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {                
                int absX = source.getX()*16+x;
                int absZ = source.getZ()*16+z;
                if (absX<pg.width&&absZ<pg.height && absX>=0 && absZ>=0) {
                    Block block = source.getBlock(x, y, z);
                    try {
                        block.setData( data[absX][absZ].byteValue());
                    } catch(ArrayIndexOutOfBoundsException e) {
                        System.out.println("Error: "+absX+" "+absZ);
                    }
                }
            }
        }
    }
    
}
