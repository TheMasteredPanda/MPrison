package dev.themasteredpanda.mprison.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;


@Getter
@AllArgsConstructor
public class PrisonCollisionException extends Exception
{
    private String prisonName;
    private Location pos1;
    private Location pos2;
    private int collidedWith;
}
