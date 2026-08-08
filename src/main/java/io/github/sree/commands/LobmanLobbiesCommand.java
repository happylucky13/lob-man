package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.lobby.LobbiesManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobmanLobbiesCommand {

    private final LobbiesManager lobbiesManager;

    public LobmanLobbiesCommand(LobbiesManager lobbiesManager) {
        this.lobbiesManager = lobbiesManager;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("lobbies")
                .then(Commands.literal("add")
                        .then(Commands.argument("world_name", StringArgumentType.word())
                                .executes(this::addLobbyWorld)
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("world_name", StringArgumentType.word())
                                .executes(this::removeLobbyWorld)
                        )
                )
                .then(Commands.literal("get")
                        .executes(this::getLobbyWorlds)
                );
    }

    private int addLobbyWorld(CommandContext<CommandSourceStack> ctx) {
        String worldName = ctx.getArgument("world_name", String.class);
        CommandSender sender = ctx.getSource().getSender();
        lobbiesManager.addAndRegisterLobbyWorld(worldName);

        if (lobbiesManager.getLobbies().contains(worldName) && sender instanceof Player) {
            sender.sendMessage(Component.text("World ", NamedTextColor.GOLD)
                    .append(Component.text(worldName, NamedTextColor.WHITE))
                    .append(Component.text(" successfully registered!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private int removeLobbyWorld(CommandContext<CommandSourceStack> ctx) {
        String worldName = ctx.getArgument("world_name", String.class);
        CommandSender sender = ctx.getSource().getSender();
        lobbiesManager.removeAndUnregisterLobbyWorld(worldName);

        if (!lobbiesManager.getLobbies().contains(worldName) && sender instanceof Player) {
            sender.sendMessage(Component.text("World ", NamedTextColor.GOLD)
                    .append(Component.text(worldName, NamedTextColor.WHITE))
                    .append(Component.text(" successfully removed!"))
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private int getLobbyWorlds(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Component message = Component.text("Current lobby worlds:", NamedTextColor.GOLD);

        for (String lobby : lobbiesManager.getLobbies()) {
            message = message.append(Component.newline()).append(Component.text(lobby, NamedTextColor.WHITE));
        }

        sender.sendMessage(message);

        return Command.SINGLE_SUCCESS;
    }
}
