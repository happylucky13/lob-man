package io.github.sree.lobby.listeners;

import io.github.sree.lobby.LobbiesManager;
import org.bukkit.event.Listener;

public abstract class LobbiesListener implements Listener {

    protected final LobbiesManager lobbiesManager;

    protected LobbiesListener(LobbiesManager lobbiesManager) {
        this.lobbiesManager = lobbiesManager;
    }
}