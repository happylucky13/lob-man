package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.LobmanPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class LobmanLobbyCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("lobby")
                .then(Commands.literal("set")
                        .then(Commands.argument("name", ArgumentTypes.world())
                                .executes(LobmanLobbyCommand::setLobby)
                        )
                )
                .executes(LobmanLobbyCommand::getLobby);
    }

    private static int setLobby(CommandContext<CommandSourceStack> ctx) {
        final World lobbyWorld = ctx.getArgument("name", World.class);
        CommandSender sender = ctx.getSource().getSender();
        var plugin = LobmanPlugin.getInstance();

        plugin.getConfig().set("lobby-world", lobbyWorld.getName());
        plugin.saveConfig();
        plugin.setLobbyWorld(lobbyWorld);
        sender.sendMessage(Component.text("Lobby world set to " + plugin.getLobbyWorld().getName()));

        return Command.SINGLE_SUCCESS;
    }

    private static int getLobby(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        var plugin = LobmanPlugin.getInstance();

        if (plugin.getLobbyWorld() == null) {
            sender.sendMessage(Component.text("Lobby world is not yet set."));
            return 0;
        }

        sender.sendMessage(Component.text("The current Lobby World is " + plugin.getLobbyWorld()));
        return Command.SINGLE_SUCCESS;
    }
}
