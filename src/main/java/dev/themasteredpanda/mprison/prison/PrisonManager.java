package dev.themasteredpanda.mprison.prison;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import dev.themasteredpanda.mprison.MPrison;
import dev.themasteredpanda.mprison.exception.PrisonCollisionException;
import dev.themasteredpanda.mprison.exception.PrisonDoesNotExistException;
import dev.themasteredpanda.mprison.lib.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PrisonManager
{
    private HashMap<Integer, Prison> prisons = Maps.newHashMap();
    private HashMultimap<Integer, PrisonPlayer> prisonPlayers = HashMultimap.create();
    private PrisonThreadManager threadManager;
    private MPrison instance;

    public PrisonManager(MPrison instance)
    {
        threadManager = new PrisonThreadManager();
        this.instance = instance;
    }

    /**
     * Creates a prison. However before creating a new Prison instance it first checks that the positions provided
     * do not overlap or 'collide' with other prisons. If the prison attempting to be made does collide it will
     * throw a PrisonCollisionException, containing all relevant information on which prison it collided with.
     *
     * @param name - The name of the prison.
     * @param level - The rank the prison is assigned to.
     * @param pos1 - The uppermost corner of the prison.
     * @param pos2 - The lowermost corner of the prison.
     * @param types - The types of materials that will be spawned in the prison mining area.
     * @throws PrisonCollisionException - only thrown when a collision between an existing prison and this one is found.
     */
    public void createPrison(String name, int level, Location pos1, Location pos2, Material... types) throws PrisonCollisionException
    {
        Optional<Integer> collisions = checkForCollisions(pos1, pos2);

        if (collisions.isPresent()) {
            throw new PrisonCollisionException(name, pos1, pos2, collisions.get());
        }

       Prison prison = new Prison(instance, name, randomID(), level, pos1, pos2, types);
       prison.reset();
       prisons.put(prison.getId(), prison);
    }

    private Optional<Integer> checkForCollisions(Location pos1, Location pos2) {
        Vector min = Vector.getMinimum(pos1.toVector(), pos2.toVector());
        Vector max = Vector.getMaximum(pos1.toVector(), pos2.toVector());

        for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    for (Prison prison : prisons.values()) {
                        if (!Objects.requireNonNull(prison.getFirstCorner().getWorld()).getName().equals(Objects.requireNonNull(pos1.getWorld()).getName())) continue;
                        if (LocationUtil.collides(prison.getFirstCorner(), prison.getSecondCorner(), new Location(pos1.getWorld(), x, y, z))) {
                            return Optional.of(prison.getId());
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

    private int randomID()
    {
        int randomId = 0;

        do {
           randomId = ThreadLocalRandom.current().nextInt(0, 1000);

        } while (prisons.values().stream().map(Prison::getId).anyMatch(id -> id == randomID()));

        return randomId;
    }

    public void deletePrison(Integer prisonId) throws PrisonDoesNotExistException
    {
        Optional<Prison> prison = getPrison(prisonId);

        if (prison.isEmpty()) throw new PrisonDoesNotExistException(prisonId);
        prison.get().clear();

    }

    public Optional<Prison> getPrison(Integer prisonId)
    {
        return Optional.ofNullable(prisons.get(prisonId));
    }

    public Set<PrisonPlayer> getPrisonPlayers(Integer rankId)
    {
        return prisonPlayers.get(rankId);
    }

    public HashMultimap<Integer, PrisonPlayer> getAllPrisonPlayers()
    {
        return prisonPlayers;
    }
}
