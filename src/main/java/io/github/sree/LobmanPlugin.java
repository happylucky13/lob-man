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
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class LobmanPlugin extends JavaPlugin {

    private static LobmanPlugin instance;
    private final String lobbyName = this.getConfig().getString("lobby-world");
    private final List<SafetyNet> safetyNets = new ArrayList<>();
    private final SafetyNetManager safetyNetManager = new SafetyNetManager(this);

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Plugin started!");

        ConfigurationSerialization.registerClass(SafetyNet.class);

        LiteralCommandNode<CommandSourceStack> lobmanCommand = Commands.literal("lobman")
                .then(LobmanSafetyNetCommand.createCommand(safetyNetManager))
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(lobmanCommand);
        });

        BukkitScheduler scheduler = getServer().getScheduler();

        scheduler.runTaskTimer(this, task -> {
            for(SafetyNet safetyNet : safetyNets) {
                safetyNet.teleportPlayers();
            }
        }, 0L, 1L);
    }

    public static LobmanPlugin getInstance() {
        return instance;
    }

    public List<SafetyNet> getSafetyNets() {
        return safetyNets;
    }
}
