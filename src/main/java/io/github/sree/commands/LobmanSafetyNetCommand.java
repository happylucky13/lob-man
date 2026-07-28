package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.sree.DisplayFormatter;
import io.github.sree.safetynet.SafetyNet;
import io.github.sree.safetynet.SafetyNetManager;

import io.github.sree.selection.RegionSelection;
import io.github.sree.selection.SelectionManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Rotation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class LobmanSafetyNetCommand {

    private final SafetyNetManager safetyNetManager;
    private final SelectionManager selectionManager;

    public LobmanSafetyNetCommand(SafetyNetManager safetyNetManager, SelectionManager selectionManager) {
        this.safetyNetManager = safetyNetManager;
        this.selectionManager = selectionManager;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("safety_net")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .then(Commands.argument("y-level", IntegerArgumentType.integer())
                                        .then(Commands.argument("tp-position", ArgumentTypes.finePosition(true))
                                                .then(Commands.argument("tp-rotation", ArgumentTypes.rotation())
                                                        .executes(this::createSafetyNet)
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(this::removeSafetyNet)
                        )
                )
                .then(Commands.literal("get")
                        .executes(this::getSafetyNets)
                );
    }

    private int createSafetyNet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        CommandSender sender = source.getSender();

        final String name = ctx.getArgument("name", String.class);
        final int yLevel = ctx.getArgument("y-level", int.class);

        final FinePosition tpPosition = ctx.getArgument("tp-position", FinePositionResolver.class).resolve(ctx.getSource());
        final Rotation tpRotation = ctx.getArgument("tp-rotation", RotationResolver.class).resolve(ctx.getSource());

        if (sender instanceof Player player) {
            final Location tpLocation = new Location(source.getLocation().getWorld(), tpPosition.x(), tpPosition.y(), tpPosition.z(), tpRotation.yaw(), tpRotation.pitch());
            RegionSelection selection = selectionManager.getActiveSelections().get(player);

            // Checks if player has completed selection
            if (selection == null) {
                player.sendMessage(Component.text("Make a region selection first!", NamedTextColor.LIGHT_PURPLE));
                return 0;
            }

            if (!selection.isComplete()) {
                player.sendMessage(Component.text("You are missing one or more selections.", NamedTextColor.LIGHT_PURPLE));
                return 0;
            }

            Location firstPosition = selection.getFirstCorner();
            Location secondPosition = selection.getSecondCorner();

            safetyNetManager.createAndRegisterSafetyNet(name, yLevel, firstPosition.getBlockX(), secondPosition.getBlockX(), firstPosition.getBlockZ(), secondPosition.getBlockZ(), tpLocation);
            selectionManager.getActiveSelections().remove(player);
            player.sendMessage(Component.text("Created safety net '" + name + "' (" + DisplayFormatter.format2DLocation(firstPosition) + " to " + DisplayFormatter.format2DLocation(secondPosition) + ")", NamedTextColor.GREEN));

            return Command.SINGLE_SUCCESS;
        }

        sender.sendPlainMessage("You must be a player to execute this command.");
        return 0;
    }

    private int removeSafetyNet(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        final String name = ctx.getArgument("name", String.class);
        final SafetyNet safetyNet = safetyNetManager.getActiveSafetyNets().get(name);

        sender.sendMessage(Component.text("Safety net '" + name + "' (" + safetyNet.getFirstPosition() + " to " + safetyNet.getSecondPosition() + ") " + "has been removed.", NamedTextColor.GREEN));

        safetyNetManager.deleteAndRemoveSafetyNet(name);
        return Command.SINGLE_SUCCESS;
    }

    private int getSafetyNets(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Map<String, SafetyNet> activeSafetyNets = safetyNetManager.getActiveSafetyNets();

        for(String name : activeSafetyNets.keySet()) {
            sender.sendMessage(Component.text("------------------------------"));
            sender.sendMessage(Component.text("Name: " + name, NamedTextColor.GREEN));
            sender.sendMessage(Component.text("Y-Level: " + activeSafetyNets.get(name).getYLevel(), NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Teleport Location: " + DisplayFormatter.formatLocation(activeSafetyNets.get(name).getTpLocation()), NamedTextColor.GOLD));
        }
        sender.sendMessage(Component.text("------------------------------"));

        return Command.SINGLE_SUCCESS;
    }
}