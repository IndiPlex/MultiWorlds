package de.indiplex.multiworlds.generators;

import de.indiplex.multiworlds.MultiWorldsAPI;
import de.indiplex.multiworlds.generators.util.MWActionGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author temp
 */
public class WaterGenerator extends MWGenerator implements MWActionGenerator {
    
    private MultiWorldsAPI api;
    private ArrayList<BlockPopulator> pops = new ArrayList<BlockPopulator>();

    public WaterGenerator(MultiWorldsAPI api) {
        pops.add(new SpherePopulator(api));
        this.api = api;
    }
    
    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        byte[] blocks = new byte[32768];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 108; y++) {
                    int val = (x * 16 + z) * 128 + y;
                    blocks[val] = (byte) Material.STATIONARY_WATER.getId();
                    if(y==0) {
                        blocks[val] = (byte) Material.BEDROCK.getId();
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {               
        return pops;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return false;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 10, 0);
    }

    @Override
    public void onDisable() {
        for(BlockPopulator bp:pops) {
            if (bp instanceof MWActionGenerator) {
                ((MWActionGenerator) bp).onDisable();
            }
        }
    }
    
}
