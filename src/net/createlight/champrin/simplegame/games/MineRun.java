package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import net.createlight.champrin.simplegame.Room;

public class MineRun extends Games implements Listener {

    private String stepping_on_mine;

    public MineRun(Room room) {
        super(room);
        this.stepping_on_mine = room.plugin.config.getString("stepping-on-mine");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("MineRun")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Level level = player.getLevel();
                if (level.getBlock(player.floor().subtract(0, 1)).getId() == Block.PLANKS)//木板
                {
                    gameFinish(player);
                } else if (level.getBlock(player.floor()).getId() == Block.STONE_PRESSURE_PLATE) {
                    player.sendMessage(stepping_on_mine);
                    player.teleport(room.getRandPos(2));
                }
            }
        }
    }
}