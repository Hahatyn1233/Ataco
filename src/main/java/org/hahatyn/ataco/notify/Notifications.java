package org.hahatyn.ataco.notify;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Notifications {

    public static void sendMessage(NotificationsType notificationsType, Player player){
        switch (notificationsType){
            case NotificationsType.NO_BREAK_IN_REGION -> {
                player.sendMessage("§cВам нельзя ломать блоки в чужом регионе.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            }
            case NotificationsType.NO_PLACE_IN_REGION -> {
                player.sendMessage("§cВам нельзя ставить блоки в чужом регионе.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            }
            case NotificationsType.NO_INTERACT_IN_REGION -> {
                player.sendMessage("§cВам нельзя взаимодействовать с предметами в чужом регионе.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            }
            case NotificationsType.MAX_REGIONS_PER_PLAYER -> {
                player.sendMessage("§cВы не можете создать больше 3-х регионов.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            }
            case NotificationsType.NO_REGION_NEAR -> {
                player.sendMessage("§cВы не можете создать регион который пересекается с чужим регионам.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1,1);
            }
        }
    }

}
