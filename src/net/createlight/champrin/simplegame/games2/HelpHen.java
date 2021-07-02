package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import net.createlight.champrin.simplegame.Room;

public class HelpHen extends Games implements Listener {

    public HelpHen(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            if (room.gameType.equals("HelpHen")) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (room.gamePlayer.contains(player)) {
                        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                        if (gameTime <= mainTime * 0.5) {
                            if (damager instanceof Player) {
                                if (room.gamePlayer.contains((Player) damager)) {
                                    event.setCancelled(true);
                                    double yaw = Math.atan2((player.x - damager.x), (player.z - damager.z));
                                    player.knockBack(damager, 0, Math.sin(yaw), Math.cos(yaw), 1);
                                }
                            }
                        }
                        if (damager instanceof EntityEgg) {
                            room.addPoint(player, gameTime);
                            player.getInventory().addItem(Item.get(Item.EGG, 0, 1));
                            player.getLevel().addSound(player, Sound.CAULDRON_EXPLODE);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) return;
        spawnEgg();
        spawnEgg();
        spawnEgg();
    }

    private void spawnEgg(){
        Vector3 v3 = room.getRandPos(19);
        CompoundTag nbt = Entity.getDefaultNBT(v3, new Vector3(0, 0, 0), 0.0F, 0.0F);
        Entity egg = Entity.createEntity(82, new Position(v3.x, v3.y, v3.z, level).getChunk(), nbt);
        egg.setMotion(egg.getMotion().multiply(10));
        egg.spawnToAll();
    }
    @Override
    public void madeArena() {

    }
}
