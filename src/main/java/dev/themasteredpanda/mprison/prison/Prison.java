package dev.themasteredpanda.mprison.prison;

import com.google.common.collect.Lists;
import dev.themasteredpanda.mprison.MPrison;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Object representation of a minecraft Prison.
 */
@Getter
public class Prison
{
    private final MPrison instance;
    private final String prisonName;
    private final int id;
    private final int rankId;
    private final Location firstCorner;
    private final Location secondCorner;
    private final List<Material> blockTypes;
    private int blocksBroken = 0;
    private int blocks = 0;
    private final Vector min;
    private final Vector max;

    public Prison(MPrison instance, String prisonName, int id, int rankId, Location firstCorner, Location secondCorner, Material... blockTypes)
    {
        this.instance = instance;
        this.prisonName = prisonName;
        this.id = id;
        this.rankId = rankId;
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;
        this.blockTypes = Arrays.asList(blockTypes);
        min = Vector.getMinimum(firstCorner.toVector(), secondCorner.toVector());
        max = Vector.getMaximum(firstCorner.toVector(), secondCorner.toVector());

        for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
               blocks++;
            }
        }
    }

    public void reset()
    {
        for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                   Location cursor = new Location(firstCorner.getWorld(), x, y, z);
                   cursor.getBlock().setType(getMaterial());
                }
            }
        }
    }

    public void clear()
    {
        for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                   Location cursor = new Location(firstCorner.getWorld(), x, y, z);
                   cursor.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public List<UUID> getPlayers()
    {
        Vector max = Vector.getMaximum(firstCorner.toVector(), secondCorner.toVector());
        Vector min = Vector.getMinimum(firstCorner.toVector(), secondCorner.toVector());
        ArrayList<UUID> players = Lists.newArrayList();

        for (PrisonPlayer p : instance.getPrisonManager().getPrisonPlayers(rankId)) {
            Location location = p.getPlayer().getLocation();
            if ((min.getBlockY() < location.getBlockY()) || (max.getBlockY() < location.getBlockY())) {
                if ((min.getBlockX() < location.getBlockX()) || (max.getBlockX() < location.getBlockX())) {
                   players.add(p.getPlayer().getUniqueId());
                }
            }
        }

        return players;
    }

    private Material getMaterial()
    {
        int position = ThreadLocalRandom.current().nextInt(0, blockTypes.size() - 1);
        return blockTypes.get(position);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    Block block = e.getBlock();
                    if (y == block.getY() && x == block.getX() && z == block.getZ()) {
                        blocksBroken++;

                        //TODO: calculate the percentage of blocks broken, if it meets or exceeds the threshold add it to the reset queue in the prison thread manager.
                    }
                }
            }
        }
    }
}
