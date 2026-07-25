package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.LobmanSafetyNetCommand;
import io.github.sree.safetynet.SafetyNet;
import io.github.sree.safetynet.SafetyNetManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LobmanPlugin extends JavaPlugin {

    private final List<SafetyNet> activeSafetyNets = new ArrayList<>();

    @Override
    public void onEnable() {
        SafetyNetManager safetyNetManager = new SafetyNetManager(this);

        ConfigurationSerialization.registerClass(SafetyNet.class);

        // Register lobman command
        LobmanSafetyNetCommand safetyNetCommand = new LobmanSafetyNetCommand(safetyNetManager, activeSafetyNets);

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

    public List<SafetyNet> getActiveSafetyNets() {
        return activeSafetyNets;
    }
}
