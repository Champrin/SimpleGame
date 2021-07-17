package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.item.Item;
import net.createlight.champrin.simplegame.Room;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

public class MakeItem extends Games implements Listener {

    public LinkedHashMap<String, ArrayList<Integer>> project = new LinkedHashMap<>();
    private int type;
    private String item_repeat_make;

    public MakeItem(Room room) {
        super(room);
        this.type = new Random().nextInt(3)+1;
        this.item_repeat_make = room.plugin.config.getString("item-repeat-make");
    }

    public boolean isCraft(String name, int itemId) {
        return project.get(name).contains(itemId);
    }

    //Material:干草块，铁块，原石，木头，红石块，金块
    //Target:面包 铁砧 熔炉 音乐盒 充能铁轨
    public void MakeItem_1(Player player, int item) {
        String name = player.getName();
        if (isCraft(name, item)) {
            player.sendMessage(item_repeat_make);
            return;
        }
        if (item == Item.BREAD || item == Item.ANVIL || item == Item.FURNACE || item == Item.NOTEBLOCK || item == Item.POWERED_RAIL) {
            room.addPoint(player, 1);
            project.get(name).add(item);
        }
    }

    //Material:金块 钻石块 南瓜 木头 煤块 铁块 红石块 原石
    //Target:金剑 钻石甲 南瓜灯 活塞 运输矿车
    public void MakeItem_2(Player player, int item) {
        String name = player.getName();
        if (isCraft(name, item)) {
            player.sendMessage(item_repeat_make);
            return;
        }
        if (item == Item.GOLD_SWORD || item == Item.DIAMOND_CHESTPLATE || item == Item.JACK_O_LANTERN || item == Item.PISTON || item == Item.MINECART_WITH_CHEST) {
            room.addPoint(player, 1);
            project.get(name).add(item);
        }
    }

    //Material:木头 干草块 铁块 原石 粘液块
    //Target:门 漏斗矿车 面包 剪刀 粘性活塞
    public void MakeItem_3(Player player, int item) {
        String name = player.getName();
        if (isCraft(name, item)) {
            player.sendMessage(item_repeat_make);
            return;
        }
        if (item == Item.DOOR_BLOCK || item == Item.MINECART_WITH_HOPPER || item == Item.BREAD || item == Item.SHEARS || item == Item.STICKY_PISTON) {
            room.addPoint(player, 1);
            project.get(name).add(item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(EntityInventoryChangeEvent event) {
        if (room.gameType.equals("MakeItem")) {
            Entity player = event.getEntity();
            if (player instanceof Player) {
                if (room.gamePlayer.contains((Player) player)) {
                    if (this.type == 1) {
                        this.MakeItem_1((Player) player, event.getNewItem().getId());
                    } else if (this.type == 2) {
                        this.MakeItem_2((Player) player, event.getNewItem().getId());
                    } else if (this.type == 3) {
                        this.MakeItem_3((Player) player, event.getNewItem().getId());
                    }
                    if (this.room.rank.get(((Player) player).getName()) == 5) {
                        gameFinish((Player) player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("MakeItem")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Block block = event.getBlock();
                event.setDrops(null);
                player.getInventory().addItem(Item.get(block.getId(), block.getDamage()));
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void madeArena() {
        this.type = new Random().nextInt(3)+1;
    }
}