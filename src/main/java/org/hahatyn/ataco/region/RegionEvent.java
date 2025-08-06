package org.hahatyn.ataco.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.hahatyn.ataco.Ataco;
import org.hahatyn.ataco.notify.Notifications;
import org.hahatyn.ataco.notify.NotificationsType;
import org.hahatyn.ataco.utils.Cuboid;

import java.util.ArrayList;

public class RegionEvent implements Listener {

    private final RegionManager regionManager;

    public RegionEvent(RegionManager regionManager) {
        this.regionManager = regionManager;
    }
    /**
     * Создание региона с помощью бумаги
     */
    @EventHandler
    public void onCreateRegion(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!regionManager.isCreateRegionPaper(item)) return;

        // Кол-во регионов игрока
        long playerRegions = Ataco.getInstance().getRegionData().getAllRegions().values().stream()
                .filter(r -> r.getOwnerName().equalsIgnoreCase(player.getName()))
                .count();

        if (playerRegions >= 3) {
            Notifications.sendMessage(NotificationsType.MAX_REGIONS_PER_PLAYER, player);
            return;
        }

        Location center = clickedBlock.getLocation().add(0.5, 1, 0.5);
        RegionType type = RegionType.SMALL;
        Cuboid newCuboid = new Cuboid(center, type.getSize());

        for (Region existing : Ataco.getInstance().getRegionData().getAllRegions().values()) {
            if (existing.getOwnerName().equalsIgnoreCase(player.getName())) continue;

            if (regionManager.isIntersecting(newCuboid, existing.getCuboid())) {
                Notifications.sendMessage(NotificationsType.NO_REGION_NEAR, player);
                return;
            }
        }

        String id = Ataco.getInstance().getRegionData().generateRegionId(player.getName());
        Region region = new Region(id, player.getName(), type, new ArrayList<>(), center, 100.0, 100.0);

        // Показать границы
        regionManager.showRegionBorder(player, newCuboid);

        // Удалить бумагу
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        player.sendMessage("§aРегион создан! ID: §f" + id);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);

        // Сохранить в БД асинхронно
        new BukkitRunnable() {
            @Override
            public void run() {
                Ataco.getInstance().getRegionData().saveRegion(region);
            }
        }.runTaskAsynchronously(Ataco.getInstance());

        event.setCancelled(true);
    }

    /**
     * Запретить ставить блоки в чужом регионе
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!regionManager.canInteract(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            Notifications.sendMessage(NotificationsType.NO_PLACE_IN_REGION, event.getPlayer());
        }
    }

    /**
     * Запретить ломать блоки в чужом регионе
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!regionManager.canInteract(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            Notifications.sendMessage(NotificationsType.NO_BREAK_IN_REGION, event.getPlayer());
        }
    }
    /**
     * Запретить открывать двери, сундуки в чужом регионе
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!regionManager.canInteract(event.getPlayer(), event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
            Notifications.sendMessage(NotificationsType.NO_INTERACT_IN_REGION, event.getPlayer());
        }
    }

    /**
     * Запретить наносить урон сущностям в чужом регионе
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!regionManager.canInteract(player, event.getEntity().getLocation())) {
            event.setCancelled(true);
            Notifications.sendMessage(NotificationsType.NO_DAMAGE_ENTITY_REGION, (Player) event.getDamager());
        }
    }

}
