package org.hahatyn.ataco.rpg.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hahatyn.ataco.Ataco;

public class ClassesListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClassesData data = Ataco.getInstance().getClassesData();
            data.initPlayer(player.getName(), ClassesType.WARRIOR);
    }
}
