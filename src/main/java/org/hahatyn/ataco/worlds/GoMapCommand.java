package org.hahatyn.ataco.worlds;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hahatyn.ataco.worlds.WorldManager;

public class GoMapCommand implements CommandExecutor {

    private final WorldManager worldManager;

    public GoMapCommand(WorldManager manager) {
        this.worldManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько игрок может использовать эту команду.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§7Использование: §e/gomap <название_карты>");
            return true;
        }

        String worldName = args[0];
        worldManager.teleportToWorld(player, worldName);
        return true;
    }
}