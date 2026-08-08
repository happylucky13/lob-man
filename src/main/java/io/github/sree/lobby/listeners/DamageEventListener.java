package io.github.sree.lobby.listeners;

import io.github.sree.lobby.LobbiesManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEventListener extends LobbiesListener {

    public DamageEventListener(LobbiesManager lobbiesManager) {
        super(lobbiesManager);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player player) {

            if (lobbiesManager.getLobbies().contains(player.getWorld().getName())) {
                event.setCancelled(true);
            }
        }
    }
}
