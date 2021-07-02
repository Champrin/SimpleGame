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

import java.util.LinkedHashMap;

public class OreRace extends Games implements Listener {

    public OreRace(Room room) {
        super(room);
        this.tools = new Item[4];
        tools[0] = Item.get(Item.DIAMOND_SHOVEL, 0, 1);
        tools[1] = Item.get(Item.DIAMOND_PICKAXE, 0, 1);
        tools[2] = Item.get(Item.DIAMOND_AXE, 0, 1);
        tools[3] = Item.get(Item.DIAMOND_SWORD, 0, 1);
    }

    private LinkedHashMap<Integer, Integer> BlockPoint = new LinkedHashMap<Integer, Integer>() {{
        put(Block.COAL_ORE, 3);
        put(Block.GOLD_ORE, 3);
        put(Block.IRON_ORE, 5);
        put(Block.LAPIS_ORE, 8);
        put(Block.REDSTONE_ORE, 10);
        put(Block.DIAMOND_ORE, 20);
        put(Block.EMERALD_ORE, 30);
        put(Block.STONE, 1);
    }};

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
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

    @Override
    public void madeArena() {
        Level level = room.level;
        //一定是三维 不可能是二维
        int v = room.S * 12;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                for (int y = room.yi; y < room.yi + 13; y++) {
                    level.setBlock(new Vector3(x, y, z), Block.get(1, 0));//STONE
                }
            }
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(16, 0));//煤
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(14, 0));//GOLD
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(15, 0));//IRON
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(21, 0));//LAPIS
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(73, 0));//RedStone
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(56, 0));//钻石
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(129, 0));//绿宝石
        }
        finishBuild();
    }
}