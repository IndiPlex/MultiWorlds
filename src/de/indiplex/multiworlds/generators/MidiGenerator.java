package de.indiplex.multiworlds.generators;

import de.indiplex.multiworlds.MultiWorlds;
import de.indiplex.multiworlds.MultiWorldsAPI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author temp
 */
public class MidiGenerator extends MWGenerator {
    private ArrayList<String> initedWorlds = new ArrayList<String>();
    private HashMap<String, Integer> pData = new HashMap<String, Integer>();
    private HashMap<String, Integer[]> vData = new HashMap<String, Integer[]>();
    private MultiWorldsAPI api;

    public MidiGenerator(MultiWorldsAPI api) {
        this.api = api;
        
    }

    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        String wn = world.getName();
        if (!initedWorlds.contains(wn)) {
            init(wn);
        }
        byte[] blocks = new byte[32768];
        if (cz == 0 && cx == 0) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 60; y++) {
                        int val = (x * 16 + z) * 128 + y;
                        blocks[val] = (byte) Material.DIRT.getId();
                    }
                }
            }
            return blocks;
        }
        if (cz != 0 || cx < 1) {
            return blocks;
        }
        int point = pData.get(wn);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < vData.get(wn)[point]; y++) {
                    int val = (x * 16 + z) * 128 + y;
                    blocks[val] = (byte) Material.DIRT.getId();
                }
            }
            point++;
            pData.put(wn, point);
        }
        return blocks;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 7, 62, 8);
    }
    
    private void init(String wn) {
        File midiFile = new File(api.getDataFolder(), api.getStringParam(wn, "midiFile", "midi.mid"));
        try {
            Sequence seq = MidiSystem.getSequence(midiFile);
            Track guitar1Track = seq.getTracks()[3];
            Integer[] values = new Integer[guitar1Track.size()];
            for (int i = 0; i < guitar1Track.size(); i++) {
                MidiMessage message = guitar1Track.get(i).getMessage();
                values[i] = (int) message.getMessage()[1];
            }
            initedWorlds.add(wn);
            pData.put(wn, 0);
            vData.put(wn, values);
            MultiWorlds.log.info("Loaded midi " + midiFile);
        } catch (InvalidMidiDataException ex) {
            MultiWorlds.log.warning("Invalid midi file " + midiFile);
        } catch (IOException ex) {
            MultiWorlds.log.warning("Can't read midi file " + midiFile);
        }
    }
}
