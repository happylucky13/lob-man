package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.LobmanSafetyNetCommand;
import io.github.sree.safetynet.SafetyNet;
import io.github.sree.safetynet.SafetyNetManager;
import io.github.sree.selection.SelectionManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobmanPlugin extends JavaPlugin {

    private final Map<String, SafetyNet> activeSafetyNets = new HashMap<>();

    @Override
    public void onEnable() {
        SafetyNetManager safetyNetManager = new SafetyNetManager(this);
        SelectionManager selectionManager = new SelectionManager();

        ConfigurationSerialization.registerClass(SafetyNet.class);
        getServer().getPluginManager().registerEvents(selectionManager, this);

        // Register lobman command
        LobmanSafetyNetCommand safetyNetCommand = new LobmanSafetyNetCommand(safetyNetManager, activeSafetyNets, selectionManager.getActiveSelections());

        LiteralCommandNode<CommandSourceStack> lobmanCommand = Commands.literal("lobman")
                .then(safetyNetCommand.createCommand())
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(lobmanCommand);
        });

        // Load safety nets from disk and start scheduler
        safetyNetManager.loadSafetyNetsFromConfig();
        safetyNetManager.scheduleSafetyNets();
    }

    public Map<String, SafetyNet> getActiveSafetyNets() {
        return activeSafetyNets;
    }
}
