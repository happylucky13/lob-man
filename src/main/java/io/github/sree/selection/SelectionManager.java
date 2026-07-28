package io.github.sree.selection;

import io.github.sree.DisplayFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager implements Listener {

    private final Map<Player, RegionSelection> activeSelections = new HashMap<>();

    // Getter
    public Map<Player, RegionSelection> getActiveSelections() {
        return activeSelections;
    }

    public void beginSelection(Player player) {
        activeSelections.put(player, new RegionSelection());
    }

    public void setFirstCorner(Player player, Location location) {
        activeSelections.get(player).setFirstCorner(location);
        player.sendMessage(Component.text("First position set to (" + DisplayFormatter.format2DLocation(location) + ")", NamedTextColor.LIGHT_PURPLE));
    }

    public void setSecondCorner(Player player, Location location) {
        activeSelections.get(player).setSecondCorner(location);
        player.sendMessage(Component.text("Second position set to (" + DisplayFormatter.format2DLocation(location) + ")", NamedTextColor.LIGHT_PURPLE));
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Checks if player is op, if they have left-clicked a block, and are holding a wooden hoe.
        if (!(player.isOp() && player.getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE)) {
            return;
        }

        // Creates RegionSelection object if one is not already present
        if (!(activeSelections.containsKey(player))) {
            beginSelection(player);
        }

        // Checks whether if player left-clicked or right-clicked
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            this.setFirstCorner(player, event.getClickedBlock().getLocation());
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            this.setSecondCorner(player, event.getClickedBlock().getLocation());
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        activeSelections.remove(player);
    }
}
