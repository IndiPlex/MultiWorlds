package de.indiplex.multiworlds;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

public class MWWorld {

    private World world;
    private ChunkGenerator generator;
    private World.Environment env;
    private String name;

    public MWWorld(String name, ChunkGenerator generator, Environment env) {
        this.name = name;
        this.generator = generator;
        this.env = env;
    }

    public World getWorld() {
        return world;
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public boolean createWorld() {
        WorldCreator wc = WorldCreator.name(name);
        if (generator != null) {
            wc.generator(generator);
        }
        if (env == null) {
            env = Environment.NORMAL;
        }
        wc.environment(env);
        world = wc.createWorld();
        if (world == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getName() {
        return name;
    }

    public ChunkGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(ChunkGenerator generator) {
        this.generator = generator;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
