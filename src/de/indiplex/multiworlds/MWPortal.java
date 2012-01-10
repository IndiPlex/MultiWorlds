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
package de.indiplex.multiworlds;

import org.bukkit.Location;

/**
 *
 * @author IndiPlex <Cartan12@indiplex.de>
 */
public class MWPortal {
    private Location from;
    private Location to = null;
    private String toWorld;

    public MWPortal(Location from, String toWorld) {
        this.from = from;
        this.toWorld = toWorld;
    }

    public void setTo(Location to) {     
        this.to = to;
    }
    
}
