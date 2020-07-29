package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import xyz.champrin.simplegame.Room;


public class SnowballWar_2 extends Games implements Listener {

    public SnowballWar_2(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
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

}