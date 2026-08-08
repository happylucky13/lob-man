package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.LobmanLobbyCommand;
import io.github.sree.commands.LobmanSafetyNetCommand;
import io.github.sree.lobby.LobbiesManager;
import io.github.sree.lobby.listeners.DamageEventListener;
import io.github.sree.lobby.listeners.FoodLevelChangeListener;
import io.github.sree.lobby.listeners.LobbiesListener;
import io.github.sree.safetynet.SafetyNet;
import io.github.sree.safetynet.SafetyNetManager;
import io.github.sree.selection.SelectionManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public class LobmanPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        SafetyNetManager safetyNetManager = new SafetyNetManager(this);
        SelectionManager selectionManager = new SelectionManager();
        LobbiesManager lobbiesManager = new LobbiesManager(this);

        ConfigurationSerialization.registerClass(SafetyNet.class);
        getServer().getPluginManager().registerEvents(selectionManager, this);

        List<LobbiesListener> lobbiesListeners = List.of(new DamageEventListener(lobbiesManager), new FoodLevelChangeListener(lobbiesManager));
        lobbiesListeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        // Register lobman command
        LobmanSafetyNetCommand safetyNetCommand = new LobmanSafetyNetCommand(safetyNetManager, selectionManager);
        LobmanLobbyCommand lobbyCommand = new LobmanLobbyCommand(lobbiesManager);

        LiteralCommandNode<CommandSourceStack> lobmanCommand = Commands.literal("lobman")
                .then(safetyNetCommand.createCommand())
                .then(lobbyCommand.createCommand())
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(lobmanCommand);
        });

        // Load safety nets from disk and start scheduler
        safetyNetManager.loadSafetyNetsFromConfig();
        safetyNetManager.scheduleSafetyNets();
        lobbiesManager.loadLobbyWorldsFromConfig();
    }
}
