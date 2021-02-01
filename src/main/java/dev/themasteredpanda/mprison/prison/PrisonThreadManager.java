package dev.themasteredpanda.mprison.prison;

import com.google.common.collect.Lists;
import dev.themasteredpanda.mprison.lib.config.ConfigPopulate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the execution of the main prison threads.
 *
 * This is used to synchronise prison threads resetting prisons  
 */
@Getter
@RequiredArgsConstructor
public class PrisonThreadManager
{
    public ExecutorService pool = Executors.newFixedThreadPool(10);
    public LinkedList<Integer> queue = Lists.newLinkedList();
    private final int resetLimit = -1;

    @ConfigPopulate(value = "messages.prison.reset_countdown", format = true)
    public String RESETTING_COUNTDOWN_MESSAGE;

    @ConfigPopulate(value = "messages.prison.reset", format = true)
    public String RESET_PRISONS_MESSAGE;

    public void queue(Integer prisonId)
    {

    }

    public class PrisonResetThread
    {

    }
}
