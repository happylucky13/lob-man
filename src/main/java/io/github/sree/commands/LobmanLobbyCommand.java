package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class LobmanLobbyCommand {

    public LobmanLobbyCommand() {

    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("lobby")
                .then(Commands.literal("add")
                        .then(Commands.argument("world_name", StringArgumentType.word())
                                .executes(this::addLobbyWorld)
                        )
                );
    }

    private int addLobbyWorld(CommandContext<CommandSourceStack> ctx) {
        return Command.SINGLE_SUCCESS;
    }
}
