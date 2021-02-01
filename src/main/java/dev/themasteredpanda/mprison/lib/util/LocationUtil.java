package dev.themasteredpanda.mprison.lib.util;

import dev.themasteredpanda.mprison.lib.exception.UtilException;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtil
{
    public LocationUtil() throws UtilException
    {
        throw new UtilException();
    }

    /**
     * Used to check if a location is within the square of two positions.
     * @param pos1 - First position.
     * @param pos2 - Second position.
     * @param checkLocation - The location to check.
     * @return
     */
    public static boolean collides(Location pos1, Location pos2, Location checkLocation)
    {
       Vector min = Vector.getMinimum(pos1.toVector(), pos2.toVector());
       Vector max = Vector.getMaximum(pos1.toVector(), pos2.toVector());

       if ((min.getBlockX() < checkLocation.getBlockX()) || (max.getBlockX() > checkLocation.getBlockX())) {
           if ((min.getBlockY() < checkLocation.getBlockY()) || (max.getBlockY() > checkLocation.getBlockY())) {
               return (min.getBlockZ() < checkLocation.getBlockZ()) || (max.getBlockZ() > checkLocation.getBlockZ());
           }
       }

       return false;
    }
}
