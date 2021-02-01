package dev.themasteredpanda.mprison.prison;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class PrisonRank
{
    private final String rank;
    private final int level;
    private List<Integer> assignedPrisons = Lists.newArrayList();

    public PrisonRank(String rank, int level, Integer... prisons)
    {
       this.rank = rank;
       this.level = level;
       Collections.addAll(assignedPrisons, prisons);
    }
}
