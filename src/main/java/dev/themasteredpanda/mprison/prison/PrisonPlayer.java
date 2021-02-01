package dev.themasteredpanda.mprison.prison;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class PrisonPlayer
{
    private Player player;
    private PrisonRank rank;


    public void setRank(PrisonRank rank)
    {
        this.rank = rank;
    }

}
