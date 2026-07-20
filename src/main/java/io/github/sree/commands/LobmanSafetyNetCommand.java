package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

public class LobmanSafetyNetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("safety_net")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("y-level", IntegerArgumentType.integer())
                                        .then(Commands.argument("pos-1", ArgumentTypes.columnBlockPosition())
                                                .then(Commands.argument("pos-2", ArgumentTypes.columnBlockPosition())
                                                        .executes(LobmanSafetyNetCommand::executeCreateSafetyNet)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("get")
                        .executes(LobmanSafetyNetCommand::getSafetyNets)
                );
    }

    private static int executeCreateSafetyNet(CommandContext<CommandSourceStack> ctx) {
        return Command.SINGLE_SUCCESS;
    }

    private static int getSafetyNets(CommandContext<CommandSourceStack> ctx) {
        return Command.SINGLE_SUCCESS;
    }
}
