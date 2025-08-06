package org.hahatyn.ataco.rpg.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hahatyn.ataco.rpg.attribute.AttributeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItems extends ItemStack {

    private final Map<AttributeType, Double> attributes = new HashMap<>();

    public CustomItems(ItemStack item) {
        super(item);
        parseAttributes();
    }

    private void parseAttributes() {
        ItemMeta meta = getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        for (String line : lore) {
            for (AttributeType type : AttributeType.values()) {
                if (line.startsWith(type.getDisplayName())) {
                    String[] parts = line.split(":");
                    if (parts.length < 2) continue;
                    String valuePart = parts[1].replaceAll("§.", "").trim(); // удаляем цвет
                    valuePart = valuePart.replace("%", "").replace("+", "");

                    try {
                        double value = Double.parseDouble(valuePart);
                        attributes.put(type, value);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
    }

    public double getAttribute(AttributeType type) {
        return attributes.getOrDefault(type, 0.0);
    }

}