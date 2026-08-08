package io.github.sree.lobby.listeners;

import io.github.sree.lobby.LobbiesManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener extends LobbiesListener {

    public FoodLevelChangeListener(LobbiesManager lobbiesManager) {
        super(lobbiesManager);
    }

    @EventHandler
    public void onFoodLevelChange (FoodLevelChangeEvent event) {

        if (event.getEntity() instanceof Player player) {

            if (lobbiesManager.getLobbies().contains(player.getWorld().getName())) {
                event.setCancelled(true);
            }
        }
    }
}
