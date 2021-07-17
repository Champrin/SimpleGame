package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import net.createlight.champrin.simplegame.Room;

import java.util.Random;

public class HuntingDiamond extends Games implements Listener {

    private String item_nameTag;
    private int maxPlayers = (int) room.data.get("maxPlayers");
    private String tip;

    public HuntingDiamond(Room room) {
        super(room);
        this.item_nameTag = room.plugin.config.getString("HuntingDiamond-item-nameTag");
        this.eachTick = new Random().nextInt(mainTime / maxPlayers) + 1;
        this.tip = room.plugin.config.getString("HuntingDiamond-tip");
    }

    //关闭清除掉落物
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(EntityInventoryChangeEvent event) {
        if (room.gameType.equals("HuntingDiamond")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (room.gamePlayer.contains(player)) {
                    if (event.getNewItem().getId() == Item.DIAMOND) {
                        gameFinish(player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            if (room.gameType.equals("HuntingDiamond")) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (room.gamePlayer.contains(player)) {
                        if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
                            Player damager = (Player) ((EntityDamageByEntityEvent) event).getDamager();
                            if (room.gamePlayer.contains(damager)) {
                                event.setCancelled(true);
                                double yaw = Math.atan2((player.x - damager.x), (player.z - damager.z));
                                player.knockBack(damager, 0, Math.sin(yaw), Math.cos(yaw), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private int eachTick;

    @Override
    public void eachTick() {
        if (gameTime <= 1) {
            room.cleanDrop();
        }
        if (room.gamePlayer.size() == 0) {
            room.cleanDrop();
            return;
        }
        this.eachTick = eachTick - 1;
        for (Player player : room.gamePlayer) {
            player.sendPopup(tip.replaceAll("%REMAINTIME%", String.valueOf(eachTick)));
        }
        if (eachTick <= 0) {
            dropItem();
            this.eachTick = new Random().nextInt(mainTime / maxPlayers) + 1;
        }
    }

    private void dropItem() {
        Vector3 v3 = room.getRandPos(19);
        CompoundTag nbt = Entity.getDefaultNBT(v3, new Vector3(0.04, 0.4, 0.04), 0f, 0f);

        nbt.putShort("Health", 5);
        nbt.putCompound("Item", NBTIO.putItemHelper(Item.get(Item.DIAMOND, 0), -1));
        nbt.putShort("PickupDelay", 10);

        EntityItem entity = new EntityItem(new Position(v3.x, v3.y, v3.z, room.level).getChunk(), nbt);
        entity.setDataFlag(0, 48, true);
        entity.setNameTag(item_nameTag);
        entity.setNameTagVisible(true);
        entity.setNameTagAlwaysVisible(true);
        room.plugin.getServer().getPluginManager().callEvent(new ItemSpawnEvent(entity));
        entity.spawnToAll();
    }

    @Override
    public void madeArena() {
        //不用生成建筑
    }
}