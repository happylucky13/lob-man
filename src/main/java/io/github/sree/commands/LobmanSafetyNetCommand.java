package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.LobmanPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class LobmanSafetyNetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("safety_net")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("y-level", IntegerArgumentType.integer())
                                        .then(Commands.argument("pos-1", ArgumentTypes.columnBlockPosition())
                                                .then(Commands.argument("pos-2", ArgumentTypes.columnBlockPosition())
                                                        .then(Commands.argument("tp-location", ArgumentTypes.blockPosition())
                                                                .executes(LobmanSafetyNetCommand::executeCreateSafetyNet)
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("get")
                        .executes(LobmanSafetyNetCommand::getSafetyNets)
                )
                .then(Commands.literal("lobby")
                        .then(Commands.literal("set")
                                .then(Commands.argument("name", ArgumentTypes.world())
                                        .executes(LobmanSafetyNetCommand::setLobby)
                                )
                        )
                        .executes(LobmanSafetyNetCommand::getLobby)

                );
    }

    private static int executeCreateSafetyNet(CommandContext<CommandSourceStack> ctx) {
        return Command.SINGLE_SUCCESS;
    }

    private static int getSafetyNets(CommandContext<CommandSourceStack> ctx) {
        return Command.SINGLE_SUCCESS;
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

        sender.sendMessage(Component.text("The current Lobby World is " + plugin.getLobbyWorld()));
        return Command.SINGLE_SUCCESS;
    }
}
