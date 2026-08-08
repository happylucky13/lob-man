package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sree.lobby.LobbiesManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class LobmanLobbyCommand {

    private final LobbiesManager lobbiesManager;

    public LobmanLobbyCommand(LobbiesManager lobbiesManager) {
        this.lobbiesManager = lobbiesManager;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("lobby")
                .then(Commands.literal("add")
                        .then(Commands.argument("world_name", StringArgumentType.word())
                                .executes(this::addLobbyWorld)
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("world_name", StringArgumentType.word())
                                .executes(this::removeLobbyWorld)
                        )
                );
    }

    private int addLobbyWorld(CommandContext<CommandSourceStack> ctx) {
        String worldName = ctx.getArgument("world_name", String.class);
        lobbiesManager.addAndRegisterLobbyWorld(worldName);

        return Command.SINGLE_SUCCESS;
    }

    private int removeLobbyWorld(CommandContext<CommandSourceStack> ctx) {
        String worldName = ctx.getArgument("world_name", String.class);
        lobbiesManager.removeAndUnregisterLobbyWorld(worldName);

        return Command.SINGLE_SUCCESS;
    }
}
