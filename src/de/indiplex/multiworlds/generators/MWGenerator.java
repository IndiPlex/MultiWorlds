/*
 * MultiWorlds
 * Copyright (C) 2011 IndiPlex
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.indiplex.multiworlds.generators;

import de.indiplex.multiworlds.MultiWorldsAPI;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author IndiPlex <Cartan12@indiplex.de>
 */
public abstract class MWGenerator extends ChunkGenerator {
    
    protected MultiWorldsAPI API;

    public void initAPI(MultiWorldsAPI API) {
        this.API = API;
    }     
    
}
