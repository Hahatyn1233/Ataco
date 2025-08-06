package org.hahatyn.ataco.worlds;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoMapCommand implements CommandExecutor {

    private final WorldManager worldManager;

    public GoMapCommand(WorldManager manager) {
        this.worldManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§7Использование: §e/gomap <название_карты>");
            return true;
        }

        worldManager.teleportToWorld(player, args[0]);
        return true;
    }
}