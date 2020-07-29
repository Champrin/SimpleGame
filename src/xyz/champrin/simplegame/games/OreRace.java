package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.champrin.simplegame.Room;

import java.util.LinkedHashMap;

public class OreRace extends Games implements Listener {

    public OreRace(Room room) {
        super(room);
    }

    private LinkedHashMap<Integer, Integer> BlockPoint = new LinkedHashMap<Integer, Integer>() {{
        put(Block.COAL_ORE, 3);
        put(Block.GOLD_ORE, 3);
        put(Block.IRON_ORE, 5);
        put(Block.LAPIS_ORE, 8);
        put(Block.REDSTONE_ORE, 10);
        put(Block.DIAMOND_ORE, 20);
        put(Block.EMERALD_ORE, 30);
        put(Block.DIRT, 1);
    }};

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("OreRace")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                int blockId = event.getBlock().getId();
                if (BlockPoint.containsKey(blockId)) {
                    room.addPoint(player, BlockPoint.get(blockId));
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTouch(PlayerInteractEvent event) {
        if (room.gameType.equals("OreRace")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                int blockId = event.getBlock().getId();
                int inHandId = player.getInventory().getItemInHand().getId();
                if (blockId == Block.DIAMOND_BLOCK) {
                    gameFinish(player);
                } else if (inHandId == Item.ARROW) {
                    player.getInventory().removeItem(new Item(262, 0, 1));
                    String[] p = ((String) room.data.get("pos1")).split("\\+");
                    player.teleport(new Vector3(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                }
            }
        }
    }

    public void madeArena() {
        Level level = room.level;
        int v = 14;
        if ((room.xa - room.xi) != 0) {
            v = Math.abs(room.xa - room.xi) * 14;
        } else if ((room.yi - room.ya) != 0) {
            v = 14 * Math.abs(room.ya - room.yi);
        }
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                for (int y = room.yi; y < room.yi + 15; y++) {
                    level.setBlock(new Vector3(x, y+1, z), Block.get(1, 0));//STONE
                }
            }
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(16, 0));//煤
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(14, 0));//GOLD
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(15, 0));//IRON
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(21, 0));//LAPIS
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(73, 0));//REDSTONE
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(56, 0));//钻石
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(15), Block.get(129, 0));//绿宝石
        }
        finishBuild();
    }
}