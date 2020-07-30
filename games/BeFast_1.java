package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.champrin.simplegame.Room;

import java.util.ArrayList;
import java.util.Arrays;

public class BeFast_1 extends Games implements Listener {

    public BeFast_1(Room room) {
        super(room);
    }

    private ArrayList<Integer> Block_Array = new ArrayList<>(Arrays.asList(Block.WOOD, Block.WOOD2, Block.STONE, Block.DIRT));

    @EventHandler(priority = EventPriority.MONITOR) @SuppressWarnings("unused")
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("BeFast_1")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Block block = event.getBlock();
                if (!Block_Array.contains(block.getId())) {
                    event.setCancelled(true);
                } else if (block.getId() == Block.DIAMOND_BLOCK) {
                    gameFinish(player);
                }
            }
        }
    }

    //TODO 边界y直接是设置的就行 不用+1
    public void madeArena() {
        Level level = room.level;
        int v = Math.abs(room.xa - room.xi) * 12 * (Math.abs(room.ya - room.yi));
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                for (int y = room.yi; y < room.yi + 13; y++) {
                    level.setBlock(new Vector3(x, y+1, z), Block.get(3, 0));//泥土
                }
            }
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(1, 0));//石头
        }
        for (int i = 0; i <= v / 3; i++) {
            level.setBlock(room.getRandPos(13), Block.get(17, 0));//木头
        }
        for (int i = 0; i <= room.getMaxPlayers(); i++) {
            level.setBlock(room.getRandPos(13), Block.get(56, 0));//钻石矿
        }
        finishBuild();
    }
}