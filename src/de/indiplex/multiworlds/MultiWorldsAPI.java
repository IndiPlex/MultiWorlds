package de.indiplex.multiworlds;

import de.indiplex.manager.API;
import de.indiplex.multiworlds.generators.MWGenerator;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.YamlConfiguration;

public class MultiWorldsAPI implements API {

    private MultiWorlds MW;

    public MultiWorldsAPI(MultiWorlds MW) {
        this.MW = MW;
    }

    public void setGenerator(String name, Class<? extends MWGenerator> env) {
        MW.generators.put(name, env);
    }

    public MWGenerator getGenerator(String name) {
        try {
            MWGenerator gen = MW.generators.get(name).newInstance();
            gen.initAPI(this);
            return gen;
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean removeGenerator(String name) {
        Class<? extends MWGenerator> remove = MW.generators.remove(name);
        if (remove == null) {
            return false;
        }
        return true;
    }

    public boolean hasWorld(String world) {
        return MW.config.get("worlds." + world) != null;
    }

    public boolean isEnv(String env) {
        return MW.generators.get(env) != null;
    }
    
    public int getIntParam(String world, String param, int standard) {
        return MW.config.getInt("worlds."+world+".params."+param, standard);
    }
    
    public boolean getBooleanParam(String world, String param, boolean standard) {
        return MW.config.getBoolean("worlds."+world+".params."+param, standard);
    }
    
    public String getStringParam(String world, String param, String standard) {
        return MW.config.getString("worlds."+world+".params."+param, standard);
    }
    
    public File getDataFolder() {
        return MultiWorlds.getAPI().getDataFolder();
    }

    public String worldHasEnv(String world) {
        return MW.config.getString("worlds." + world + ".environment");
    }

    public boolean worldHasEnv(String world, String env) {
        return MW.config.getString("worlds." + world + ".environment").equalsIgnoreCase(env);
    }
    
    public MWGenerator getGenByWorld(String world) {
        return MW.genHashMap.get(world);
    }

    public World registerWorld(String world, String generator, Environment env, boolean asExtern) {
        YamlConfiguration conf = MW.config;
        conf.set("worlds." + world + ".environment", env.toString());
        conf.set("worlds." + world + ".generator", generator);
        conf.set("worlds." + world + ".extern", asExtern);
        MultiWorlds.log.info(MultiWorlds.pre + "Added world \"" + world + "\" with environment \"" + env + "\" and generator \"" + generator + "\"");
        MW.save();
        reload(false);
        return MW.getServer().getWorld(world);
    }

    public void reload(boolean unloadWorlds) {
        if (unloadWorlds) {
            for (MWWorld w : MW.worlds) {
                MW.getServer().unloadWorld(w.getName(), true);
            }
        }

        MW.loadConfig(true);
        MW.loadWorlds(true);
    }

    public ArrayList<MWWorld> getWorlds() {
        return new ArrayList<MWWorld>(MW.worlds);
    }
    
    public boolean resetWorld(String worldname, int range) {
        World world = MW.getServer().getWorld(worldname);
        if (world==null) {
            return false;
        } else {
            resetWorld(world, range);
            return true;
        }
    }
    
    public void resetWorld(World world, int range) {
        for (int x=-range;x<=range;x++) {
            for (int y=-range;y<=range;y++) {
                if (world.isChunkLoaded(x, y)) {
                    if (!world.regenerateChunk(x, y)) {
                        System.out.println("Error2 at "+x+" "+y);
                    }
                } else {
                    if (!world.getChunkAt(x, y).load(true)) {
                        System.out.println("Error at "+x+" "+y);
                    }
                }
            }
        }
    }
}
