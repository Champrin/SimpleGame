package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.champrin.simplegame.Room;

public class BeFast_4 extends Games implements Listener {

    public BeFast_4(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("BeFast_4")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                int blockId = event.getBlock().getId();
                int inHandId = player.getInventory().getItemInHand().getId();
                if (blockId == Block.STONE && inHandId == Item.STONE_PICKAXE) {
                    room.addPoint(player, 1);
                } else if (blockId == Block.IRON_ORE && inHandId == Item.IRON_PICKAXE) {
                    room.addPoint(player, 5);
                } else if (blockId == Block.GOLD_ORE && inHandId == Item.GOLD_PICKAXE) {
                    room.addPoint(player, 10);
                } else if (blockId == Block.DIAMOND_ORE && inHandId == Item.DIAMOND_PICKAXE) {
                    room.addPoint(player, 20);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void madeArena() {
        Level level = room.level;
        int v = Math.abs(room.xa - room.xi) * 12 * (Math.abs(room.ya - room.yi));
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                for (int y = room.yi; y < room.yi + 13; y++) {
                    level.setBlock(new Vector3(x, y+1, z), Block.get(1, 0));//石头
                }
            }
        }
        for (int i = 0; i <= v / 4; i++) {
            level.setBlock(room.getRandPos(13), Block.get(14, 0));//GOLD
        }
        for (int i = 0; i <= v / 6; i++) {
            level.setBlock(room.getRandPos(13), Block.get(15, 0));//IRON
        }
        for (int i = 0; i <= v / 8; i++) {
            level.setBlock(room.getRandPos(13), Block.get(56, 0));//钻石
        }
        finishBuild();
    }
}