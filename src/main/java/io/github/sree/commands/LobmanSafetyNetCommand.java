package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sree.LobmanPlugin;
import io.github.sree.safetynet.SafetyNet;
import io.github.sree.safetynet.SafetyNetManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.ColumnBlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class LobmanSafetyNetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand(SafetyNetManager safetyNetManager) {
        return Commands.literal("safety_net")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("y-level", IntegerArgumentType.integer())
                                        .then(Commands.argument("pos-1", ArgumentTypes.columnBlockPosition())
                                                .then(Commands.argument("pos-2", ArgumentTypes.columnBlockPosition())
                                                        .then(Commands.argument("tp-location", ArgumentTypes.finePosition())
                                                                .executes(ctx -> createSafetyNet(ctx, safetyNetManager))
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("get")
                        .executes(LobmanSafetyNetCommand::getSafetyNets)
                );
    }

    private static int createSafetyNet(CommandContext<CommandSourceStack> ctx, SafetyNetManager safetyNetManager) throws CommandSyntaxException {
        final String name = ctx.getArgument("name", String.class);
        final int yLevel = ctx.getArgument("y-level", int.class);
        final ColumnBlockPosition firstPosition = ctx.getArgument("pos-1", ColumnBlockPositionResolver.class).resolve(ctx.getSource());
        final ColumnBlockPosition secondPosition = ctx.getArgument("pos-2", ColumnBlockPositionResolver.class).resolve(ctx.getSource());
        final Location tpLocation = ctx.getArgument("tp-location", Location.class);

        safetyNetManager.createAndRegisterSafetyNet(name, yLevel, firstPosition.blockX(), secondPosition.blockX(), firstPosition.blockZ(), secondPosition.blockZ(), tpLocation);

        return Command.SINGLE_SUCCESS;
    }

    private static int getSafetyNets(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        var plugin = LobmanPlugin.getInstance();

        for(SafetyNet safetyNet : plugin.getSafetyNets()) {
            sender.sendMessage(Component.text("Name: " + safetyNet.getName(), NamedTextColor.GREEN));
            sender.sendMessage(Component.text("Y-Level: " + safetyNet.getYLevel(), NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Teleport Location: " + safetyNet.getTpLocation(), NamedTextColor.GOLD));
            sender.sendMessage(Component.text("------------------------------"));
        }

        return Command.SINGLE_SUCCESS;
    }
}
