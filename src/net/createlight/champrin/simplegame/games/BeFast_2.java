package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.simplegame.Room;

public class BeFast_2 extends Games implements Listener {

    public BeFast_2(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("BeFast_2")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                int blockId = event.getBlock().getId();
                int inHandId = player.getInventory().getItemInHand().getId();
                //铲 镐 斧 剑
                if (blockId == Block.SAND || blockId == Block.DIRT) {
                    if (inHandId == Item.DIAMOND_SHOVEL) {
                        room.addPoint(player, 1);
                    } else {
                        event.setCancelled(true);
                    }
                } else if (blockId == Block.STONE) {
                    if (inHandId == Item.DIAMOND_PICKAXE) {
                        room.addPoint(player, 1);
                    } else {
                        event.setCancelled(true);
                    }
                } else if (blockId == Block.WOOD) {
                    if (inHandId == Item.DIAMOND_AXE) {
                        room.addPoint(player, 1);
                    } else {
                        event.setCancelled(true);
                    }
                } else if (blockId == Block.LEAVES) {
                    if (inHandId == Item.DIAMOND_SWORD) {
                        room.addPoint(player, 1);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void madeArena() {
        Level level = room.level;
        int v = room.S*12;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                for (int y = room.yi; y < room.yi + 13; y++) {
                    level.setBlock(new Vector3(x, y+1, z), Block.get(1, 0));//石头
                }
            }
        }
        for (int i = 0; i <= v / 5; i++) {
            level.setBlock(room.getRandPos(13), Block.get(12, 0));//沙子
        }
        for (int i = 0; i <= v / 5; i++) {
            level.setBlock(room.getRandPos(13), Block.get(17, 0));//木头
        }
        for (int i = 0; i <= v / 5; i++) {
            level.setBlock(room.getRandPos(13), Block.get(3, 0));//泥土
        }
        for (int i = 0; i <= v / 5; i++) {
            level.setBlock(room.getRandPos(13), Block.get(18, 0));//叶子
        }
        finishBuild();
    }
}