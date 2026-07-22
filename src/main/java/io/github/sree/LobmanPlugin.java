package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.LobmanLobbyCommand;
import io.github.sree.commands.LobmanSafetyNetCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class LobmanPlugin extends JavaPlugin {

    private static LobmanPlugin instance;
    private World lobbyWorld;
    private String lobbyName = this.getConfig().getString("lobby-world");

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Plugin started!");

        LiteralCommandNode<CommandSourceStack> lobmanCommand = Commands.literal("lobman")
                .then(LobmanSafetyNetCommand.createCommand())
                .then(LobmanLobbyCommand.createCommand())
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(lobmanCommand);
        });

        if (lobbyName != null) {
            lobbyWorld = Bukkit.getWorld(lobbyName);
        }
    }

    public static LobmanPlugin getInstance() {
        return instance;
    }

    public World getLobbyWorld() {
        return lobbyWorld;
    }

    public void setLobbyWorld(World lobbyWorld) {
        this.lobbyWorld = lobbyWorld;
    }
}
