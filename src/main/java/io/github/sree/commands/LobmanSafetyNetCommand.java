package io.github.sree.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.sree.safetynet.SafetyNet;
import io.github.sree.safetynet.SafetyNetManager;

import io.github.sree.selection.RegionSelection;
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

import java.util.List;
import java.util.Map;

public class LobmanSafetyNetCommand {

    private final SafetyNetManager safetyNetManager;
    private final Map<String, SafetyNet> activeSafetyNets;
    private final Map<Player, RegionSelection> activeSelections;

    public LobmanSafetyNetCommand(SafetyNetManager safetyNetManager, Map<String, SafetyNet> activeSafetyNets, Map<Player, RegionSelection> activeSelections) {
        this.safetyNetManager = safetyNetManager;
        this.activeSafetyNets = activeSafetyNets;
        this.activeSelections = activeSelections;
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
        final Location tpLocation = new Location(source.getLocation().getWorld(), tpPosition.x(), tpPosition.y(), tpPosition.z(), tpRotation.yaw(), tpRotation.pitch());

        if (sender instanceof Player player) {
            // Checks if player has completed selection
            if (!activeSelections.containsKey(player)) {
                player.sendMessage(Component.text("Make a region selection first!", NamedTextColor.LIGHT_PURPLE));
                return 0;
            }

            if (!activeSelections.get(player).isComplete()) {
                player.sendMessage(Component.text("You are missing one or more selections.", NamedTextColor.LIGHT_PURPLE));
                return 0;
            }

            Location firstPosition = activeSelections.get(player).getFirstCorner();
            Location secondPosition = activeSelections.get(player).getSecondCorner();

            safetyNetManager.createAndRegisterSafetyNet(name, yLevel, firstPosition.getBlockX(), secondPosition.getBlockX(), firstPosition.getBlockZ(), secondPosition.getBlockZ(), tpLocation);
            activeSelections.remove(player);

            return Command.SINGLE_SUCCESS;
        }

        sender.sendPlainMessage("You must be a player to execute this command.");
        return 0;
    }

    private int removeSafetyNet(CommandContext<CommandSourceStack> ctx) {
        final String name = ctx.getArgument("name", String.class);

        safetyNetManager.deleteAndRemoveSafetyNet(name);
        return Command.SINGLE_SUCCESS;
    }

    private int getSafetyNets(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        for(String name : activeSafetyNets.keySet()) {
            sender.sendMessage(Component.text("Name: " + activeSafetyNets.get(name), NamedTextColor.GREEN));
            sender.sendMessage(Component.text("Y-Level: " + activeSafetyNets.get(name).getYLevel(), NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Teleport Location: " + activeSafetyNets.get(name).getTpLocation(), NamedTextColor.GOLD));
            sender.sendMessage(Component.text("------------------------------"));
        }

        return Command.SINGLE_SUCCESS;
    }
}
