package org.hahatyn.ataco.rpg.attribute;


import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Random;

public class AttributeEvent implements Listener {

    private final AttributeData attributeData;
    private final Random random = new Random();
    private final JavaPlugin plugin;

    public AttributeEvent(JavaPlugin plugin, AttributeData attributeData) {
        this.plugin = plugin;
        this.attributeData = attributeData;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        // Загружаем атрибуты
        Map<AttributeType, Double> attackerAttributes = attributeData.getAttributesOrEmpty(attacker.getName());
        Map<AttributeType, Double> victimAttributes = null;
        if (victim instanceof Player victimPlayer) {
            victimAttributes = attributeData.getAttributesOrEmpty(victimPlayer.getName());
        }

        // === Уклонение ===
        if (victimAttributes != null) {
            double dodge = victimAttributes.getOrDefault(AttributeType.DODGE_CHANGE, 0.0);
            if (random.nextDouble() * 100 < dodge) {
                event.setCancelled(true);
                victim.sendMessage("§eВы увернулись от удара!");
                attacker.sendMessage("§cПротивник увернулся от вашей атаки!");
                return;
            }
        }

        // === Физический урон ===
        double baseDamage = attackerAttributes.getOrDefault(AttributeType.PHYSICAL_DAMAGE, 0.0);

        // === Критический удар ===
        double critChance = attackerAttributes.getOrDefault(AttributeType.CRITICAL_CHANGE, 0.0);
        double critDamagePercent = attackerAttributes.getOrDefault(AttributeType.CRITICAL_DAMAGE, 0.0);
        boolean isCrit = random.nextDouble() * 100 < critChance;
        if (isCrit) {
            double bonusCrit = baseDamage * (critDamagePercent / 100.0);
            baseDamage += bonusCrit;
            attacker.sendMessage("§cКритический удар! +" + bonusCrit + " урона");
        }

        // === Физическая броня цели ===
        if (victimAttributes != null) {
            double physicalArmor = victimAttributes.getOrDefault(AttributeType.PHYSICAL_ARMOR, 0.0);
            double reduction = Math.min(physicalArmor, 100.0);
            baseDamage = baseDamage * (1 - reduction / 100.0);
        }

        // Применяем урон
        event.setDamage(baseDamage);

        // === Вампиризм ===
        double vampirism = attackerAttributes.getOrDefault(AttributeType.VAMPIRISM, 0.0);
        if (vampirism > 0) {
            double healAmount = baseDamage * (vampirism / 100.0);
            attacker.setHealth(Math.min(attacker.getHealth() + healAmount, attacker.getMaxHealth()));
            attacker.sendMessage("§aВы восстановили §f" + String.format("%.1f", healAmount) + " §aздоровья благодаря вампиризму.");
        }
    }
}