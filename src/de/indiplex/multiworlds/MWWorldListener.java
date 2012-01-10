package de.indiplex.multiworlds;

import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.WorldListener;

/**
 *
 * @author temp
 */
public class MWWorldListener extends WorldListener {

    private MultiWorlds MWorlds;
    private BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public MWWorldListener(MultiWorlds MWorlds) {
        this.MWorlds = MWorlds;
    }

    @Override
    public void onPortalCreate(PortalCreateEvent event) {        
        ArrayList<Block> blocks = event.getBlocks();
        Sign sign = null;
        for (Block b : blocks) {
            boolean bool = false;
            for (BlockFace f : faces) {
                Block bt = b.getRelative(f);
                if (bt.getState() instanceof Sign) { 
                    String[] lines = ((Sign) bt.getState()).getLines();                    
                    bool = false;
                    for (String s : lines) {
                        if (MWorlds.getServer().getWorld(s) != null) {
                            sign = (Sign) bt.getState();
                            bool = true;
                            break;
                        }
                    }
                    if (bool) {
                        break;
                    }
                }
            }
            if (bool) {
                break;
            }
        }
        if (sign != null) {
            event.setCancelled(true);
            for (String line : sign.getLines()) {
                MWorlds.getServer().broadcastMessage(line);
            }
        }
    }
}
