package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import xyz.champrin.simplegame.Room;

public class Parkour extends Games implements Listener {
    public Parkour(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("Parkour")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Level level = player.getLevel();
                Block block = level.getBlock(player.floor().subtract(0, 1));
                if (block.getId() == Block.IRON_BLOCK) {
                    gameFinish(player);
                }else if (player.getY() < room.yi - 2) {
                    player.teleport(room.getRandPos(2));
                }
            }
        }
    }
}
