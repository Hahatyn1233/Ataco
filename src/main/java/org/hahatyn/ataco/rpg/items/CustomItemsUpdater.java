package org.hahatyn.ataco.rpg.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.hahatyn.ataco.rpg.attribute.AttributeType;

import java.util.*;

public class CustomItemsUpdater {

    private final JavaPlugin plugin;
    private final Map<String, ItemTemplate> templates = new HashMap<>();
    private final Random random = new Random();

    public CustomItemsUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
        loadTemplates();
        startTask();
    }

    private void loadTemplates() {
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);
            if (section == null) continue;

            String name = section.getString("name", "§fБезымянный");
            Material material = Material.valueOf(section.getString("material", "STONE"));
            int chance = section.getInt("change", 100);

            Map<AttributeType, Double> attributes = new HashMap<>();
            for (AttributeType type : AttributeType.values()) {
                if (section.contains(type.name())) {
                    attributes.put(type, section.getDouble(type.name()));
                }
            }

            ItemTemplate template = new ItemTemplate(name, material, attributes, chance);
            templates.put(key, template);
        }
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> checkInventory(player)));
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void checkInventory(Player player) {
        boolean changed = false;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore()) continue;

            List<ItemTemplate> matchingTemplates = new ArrayList<>();
            for (ItemTemplate template : templates.values()) {
                if (template.getMaterial() == item.getType()) {
                    matchingTemplates.add(template);
                }
            }

            if (matchingTemplates.isEmpty()) continue;

            for (ItemTemplate template : matchingTemplates) {
                if (random.nextInt(100) < template.getChance()) {
                    ItemStack newItem = createItem(template);
                    contents[i] = newItem;
                    changed = true;
                    break;
                }
            }
        }

        if (changed) {
            Bukkit.getScheduler().runTask(plugin, () -> player.getInventory().setContents(contents));
        }
    }

    private ItemStack createItem(ItemTemplate template) {
        ItemStack item = new ItemStack(template.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(template.getName());

            List<String> lore = new ArrayList<>();
            for (Map.Entry<AttributeType, Double> entry : template.getAttributes().entrySet()) {
                String color = entry.getValue() < 0 ? "§c" : "§a";
                String suffix = entry.getKey().name().contains("CHANGE") || entry.getKey().name().contains("BONUS") || entry.getKey().name().contains("MOVESPEED") ? "%" : "";
                lore.add(entry.getKey().getDisplayName() + " " + color + entry.getValue() + suffix);
            }

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            meta.removeAttributeModifier(Attribute.ATTACK_SPEED);
            item.setItemMeta(meta);
        }

        return item;
    }

    private static class ItemTemplate {
        private final String name;
        private final Material material;
        private final Map<AttributeType, Double> attributes;
        private final int chance;

        public ItemTemplate(String name, Material material, Map<AttributeType, Double> attributes, int chance) {
            this.name = name;
            this.material = material;
            this.attributes = attributes;
            this.chance = chance;
        }

        public String getName() {
            return name;
        }

        public Material getMaterial() {
            return material;
        }

        public Map<AttributeType, Double> getAttributes() {
            return attributes;
        }

        public int getChance() {
            return chance;
        }
    }
}