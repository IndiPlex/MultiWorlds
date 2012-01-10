package de.indiplex.multiworlds.generators;

import de.indiplex.multiworlds.MultiWorlds;
import de.indiplex.multiworlds.MultiWorldsAPI;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 *
 * @author Cartan12
 */
public class PicGenerator extends MWGenerator {

    public int width;
    public int height;
    public HashMap<String, Integer[][]> datas = new HashMap<String, Integer[][]>();
    private ArrayList<String> initedWorlds = new ArrayList<String>();
    public HashMap<Integer, Integer> wools = new HashMap<Integer, Integer>();
    private MultiWorldsAPI api;

    public PicGenerator(MultiWorldsAPI api) {
        this.api = api;
        
        wools.put(0xFFe4e4e4, 0x0);
        wools.put(0xFFa0a7a7, 0x8);
        wools.put(0xFF414141, 0x7);
        wools.put(0xFF181414, 0xF);
        wools.put(0xFF9e2b27, 0xE);
        wools.put(0xFFea7e35, 0x1);
        wools.put(0xFFc2b51c, 0x4);
        wools.put(0xFF39ba2e, 0x5);
        wools.put(0xFF364b18, 0xD);
        wools.put(0xFF6387d2, 0x3);
        wools.put(0xFF267191, 0x9);
        wools.put(0xFF253193, 0xB);
        wools.put(0xFF7e34bf, 0xA);
        wools.put(0xFFbe49c9, 0x2);
        wools.put(0xFFd98199, 0x6);
        wools.put(0xFF56331c, 0xC);       

    }

    private BufferedImage scaleImage(BufferedImage image, File pic) throws IOException {
        MultiWorlds.log.info(MultiWorlds.pre + "Image is too big, scaling it to 300px");
        int biggerVal = Math.max(image.getWidth(), image.getHeight());
        int smallerVal = Math.min(image.getWidth(), image.getHeight());
        float f = (300 / ((float) biggerVal)) * smallerVal;
        int newWidth = biggerVal==image.getWidth()?300:Math.round(f);
        int newHeight = biggerVal==image.getHeight()?300:Math.round(f);
        
        Image tImage = image.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB_PRE);
        image.getGraphics().drawImage(tImage, 0, 0, null);
        if (!ImageIO.write(image, "png", pic)) {
            MultiWorlds.log.warning(MultiWorlds.pre + "Error saving Image!");
        }
        return image;
    }

    private int closestWoolColor(int of) {
        int min = Integer.MAX_VALUE;
        int closest = of;

        for (int v : wools.keySet()) {
            final int diff = Math.abs(v - of);

            if (diff < min) {
                min = diff;
                closest = v;
            }
        }

        return wools.get(closest);
    }

    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        if (!initedWorlds.contains(world.getName())) {
            init(world.getName());
        }
        byte[] blocks = new byte[32768];
        if (datas.get(world.getName())==null) {
            return blocks;
        }
        int y = 20;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int val = (x * 16 + z) * 128 + y;
                blocks[val] = (byte) Material.WOOL.getId();
                int absX = cx*16+x;
                int absZ = cz*16+z;
                if (absX<width&&absZ<height && absX>=0 && absZ>=0) {
                    blocks[val+80] = (byte) Material.GLASS.getId();
                }
            }
        }
        return blocks;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        ArrayList<BlockPopulator> pops = new ArrayList<BlockPopulator>();
        pops.add(new PicWoolPopulator(this));
        return pops;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, Math.round(width/2), 102, Math.round(height/2));
    }
    
    private void init(String wn) {
        Integer[][] data;        
        File pic = new File(api.getDataFolder(), api.getStringParam(wn, "picFile", "pic.png"));
        URL picURL = null;
        if (api.getStringParam(wn, "picURL", null)!=null) {
            try {
                picURL = new URI(api.getStringParam(wn, "picURL", null)).toURL();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        try {
            BufferedImage image = null;
            if(picURL==null){
                image = ImageIO.read(pic);
            } else {
                image = ImageIO.read(picURL);
            }
            if ((image.getWidth() > 300 || image.getHeight() > 300) && api.getBooleanParam(wn, "scale", true)) {
                image = scaleImage(image, pic);
            }

            width = image.getWidth();
            height = image.getHeight();
            data = new Integer[width][height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    data[x][y] = closestWoolColor(image.getRGB(x, y));                    
                }
            }
            datas.put(wn, data);
        } catch (IOException ex) {
            if (picURL==null) {
                MultiWorlds.log.warning(MultiWorlds.pre+"Can't load pic from URL "+picURL.toString());
            } else {
                MultiWorlds.log.warning(MultiWorlds.pre+"Can't load pic from File "+pic.toString());
            }
        }
    }
}
