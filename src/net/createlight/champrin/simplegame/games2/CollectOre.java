package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import net.createlight.champrin.simplegame.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CollectOre extends Games implements Listener {
//TODO 清理未收集的ORE
    private ArrayList<Integer> oreId = new ArrayList<>(Arrays.asList(Item.DIAMOND, Item.EMERALD, Item.GOLD_INGOT, Item.COAL, Item.IRON_INGOT, Item.REDSTONE));

    public CollectOre(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(EntityInventoryChangeEvent event) {
        if (room.gameType.equals("CollectOre")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (room.gamePlayer.contains(player)) {
                    int item = event.getNewItem().getId();
                    if (item == Item.DIAMOND) {
                        room.addPoint(player, 20);
                    } else if (item == Item.EMERALD) {
                        room.addPoint(player, 30);
                    } else if (item == Item.GOLD_INGOT) {
                        room.addPoint(player, 5);
                    } else if (item == Item.COAL) {
                        room.addPoint(player, 2);
                    } else if (item == Item.IRON_INGOT) {
                        room.addPoint(player, 10);
                    } else if (item == Item.REDSTONE) {
                        room.addPoint(player, 20);
                    }
                }
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) return;
        dropItem();
        dropItem();
        dropItem();
        dropItem();
        dropItem();
        dropItem();
        if (gameTime <= 1){
            for (Entity entity : level.getEntities()) {
                if (entity instanceof EntityItem) {
                    entity.kill();
                    entity.close();
                }
            }
        }
    }

    private void dropItem() {
        int id = oreId.get(new Random().nextInt(oreId.size()));
        Vector3 v3 = room.getRandPos(29);
        CompoundTag nbt = Entity.getDefaultNBT(v3, new Vector3(0.04, 0.4, 0.04), 0f, 0f);

        nbt.putShort("Health", 5);
        nbt.putCompound("Item", NBTIO.putItemHelper(Item.get(id, 0), -1));
        nbt.putShort("PickupDelay", 10);

        EntityItem entity = new EntityItem(new Position(v3.x,v3.y,v3.z,room.level).getChunk(), nbt);
        entity.setDataFlag(0, 48, true);
        entity.setNameTag("<< Ore >>");
        entity.setNameTagVisible(true);
        entity.setNameTagAlwaysVisible(true);
        room.plugin.getServer().getPluginManager().callEvent(new ItemSpawnEvent(entity));
        entity.spawnToAll();
    }
    @Override
    public void madeArena() {

    }
}