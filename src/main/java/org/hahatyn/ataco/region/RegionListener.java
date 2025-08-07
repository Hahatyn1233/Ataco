package org.hahatyn.ataco.region;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;

public class RegionListener implements Listener {

    private final RegionManager regionManager;

    public RegionListener(RegionManager regionManager) { this.regionManager = regionManager; }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!regionManager.canInteract(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!regionManager.canInteract(e.getPlayer(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock()!=null && !regionManager.canInteract(e.getPlayer(), e.getClickedBlock().getLocation()))
            e.setCancelled(true);
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && !regionManager.canInteract(p, e.getEntity().getLocation()))
            e.setCancelled(true);
    }
}