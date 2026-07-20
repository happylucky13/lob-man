package io.github.sree;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.sree.commands.LobmanSafetyNetCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class LobmanPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");

        LiteralCommandNode<CommandSourceStack> lobmanCommand = Commands.literal("lobman")
                .then(LobmanSafetyNetCommand.createCommand())
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(lobmanCommand);
        });
    }
}
