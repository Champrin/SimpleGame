package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Potion;
import net.createlight.champrin.simplegame.Room;

public class AvoidPoison extends Games implements Listener {

    public AvoidPoison(Room room) {
        super(room);
        this.tools = new Item[1];
        tools[0] = Item.get(Item.POTION, 22, 6);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageEvent event) {
        if (room.gameType.equals("AvoidPoison")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (room.gamePlayer.contains(player)) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                        if (gameTime <= mainTime * 0.3) {
                            if (damager instanceof Player) {
                                if (room.gamePlayer.contains((Player) damager)) {
                                    event.setCancelled(true);
                                    event.setDamage(0);
                                    double yaw = Math.atan2((player.x - damager.x), (player.z - damager.z));
                                    player.knockBack(damager, 0, Math.sin(yaw), Math.cos(yaw), 1);
                                }
                            }
                        }
                    }
                    if (player.getHealth() - event.getDamage() <= 0) {
                        event.setCancelled(true);
                        gameFail(player);
                    }
                }
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) return;
        if (gameTime % 6 != 0) return;
        for (Player player : room.gamePlayer) {
            room.addPoint(player, 1);
            CompoundTag nbt = Entity.getDefaultNBT(new Vector3(player.x, player.y + 19, player.z), new Vector3(0, 0, 0), 0.0F, 0.0F);
            nbt.putShort("PotionId", Potion.HARMING);
            Entity potion = Entity.createEntity(86, new Position(player.x, player.y + 19, player.z, level).getChunk(), nbt);
            potion.setMotion(potion.getMotion().multiply(10));
            potion.spawnToAll();
        }
    }

    @Override
    public void madeArena() {
        //不用生成建筑
    }
}
