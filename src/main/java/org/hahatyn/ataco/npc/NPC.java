package org.hahatyn.ataco.npc;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NPC extends Villager {

    private final Location location;

    public NPC(String id, String name, Location location) {
        super(EntityType.VILLAGER, ((CraftWorld) location.getWorld()).getHandle());
        this.location = location;
        this.setPos(location.getX(), location.getY(), location.getZ());
        this.setCustomNameVisible(true);
        this.setCustomName(Component.nullToEmpty(name));
    }

    public void spawnFor(Player player) {
        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
        ServerEntity serverEntity = new ServerEntity(serverLevel, this, 0, false, null, null, null);
        sendPacket(new ClientboundAddEntityPacket(this, serverEntity), player);
        sendPacket(new ClientboundSetEntityDataPacket(this.getId(), this.getEntityData().packDirty()), player);
    }


    private static void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}
