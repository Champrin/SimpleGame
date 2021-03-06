package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.simplegame.Room;


public class SnowballWar_2 extends Games implements Listener {

    public SnowballWar_2(Room room) {
        super(room);
        this.tools = new Item[1];
        tools[0] = Item.get(Item.DIAMOND_SHOVEL, 0, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("SnowballWar_2")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                if (event.getBlock().getId() != Block.SNOW_BLOCK) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("SnowballWar_2")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                room.addPoint(player, 1);
                if (player.getY() < room.yi - 2) {
                    room.setToView(player);
                }
            }
        }
    }

    @Override
    public void madeArena() {
        Level level = room.level;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                level.setBlock(new Vector3(x, room.yi, z), Block.get(Block.SNOW_BLOCK, 0));
            }
        }
        finishBuild();
    }
}