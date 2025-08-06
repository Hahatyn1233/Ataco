package org.hahatyn.ataco.rpg.attribute;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.Map;

public class AttributeUpdateTask {

    private final JavaPlugin plugin;
    private final AttributeData attributeData;

    public AttributeUpdateTask(JavaPlugin plugin, AttributeData attributeData) {
        this.plugin = plugin;
        this.attributeData = attributeData;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    // Выполняем асинхронно
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        attributeData.updatePlayerAttributes(player);
                        applyExtraAttributes(player);
                    });
                });
            }
        }.runTaskTimer(plugin, 20L, 20L); // старт через 1 секунду, повтор каждую секунду
    }

    private void applyExtraAttributes(Player player) {
        Map<AttributeType, Double> attrs = attributeData.getAttributesOrEmpty(player.getName());

        Bukkit.getScheduler().runTask(plugin, () -> {
            // MAX_HEALTH
            double bonusHealth = attrs.getOrDefault(AttributeType.MAX_HEALTH, 0.0);
            AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
            if (healthAttr != null) {
                healthAttr.setBaseValue(20 + bonusHealth); // 20 = базовое значение
            }

            // MOVEMENT SPEED
            double speedPercent = attrs.getOrDefault(AttributeType.MOVESPEED, 0.0);
            AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(0.1 + 0.1 * (speedPercent / 100.0)); // базовое = 0.1
            }

            // РЕГЕНЕРАЦИЯ здоровья (повышаем ХП вручную, если на земле и жив)
            double regenPercent = attrs.getOrDefault(AttributeType.REGENERATION_HEALTH, 0.0);
            if (regenPercent > 0 && player.isOnGround() && player.getHealth() < player.getMaxHealth()) {
                double regen = 0.5 * (regenPercent / 100.0); // 0.5 сердечка в секунду * %
                player.setHealth(Math.min(player.getHealth() + regen, player.getMaxHealth()));
            }
        });
    }
}