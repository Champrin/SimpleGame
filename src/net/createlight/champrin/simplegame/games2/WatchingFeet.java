package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.simplegame.Room;


public class WatchingFeet extends Games implements Listener {

    public WatchingFeet(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR) @SuppressWarnings("unused")
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("WatchingFeet")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                player.getLevel().setBlock(new Vector3(player.getFloorX(), player.getFloorY() - 1, player.getFloorZ()), Block.get(0, 0));
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) return;
        for (Player player : room.gamePlayer) {
            player.getLevel().setBlock(new Vector3(player.getFloorX(), player.getFloorY() - 1, player.getFloorZ()), Block.get(0, 0));
            room.addPoint(player, 1);
            if (player.getY() < room.yi - 2) {
                gameFail(player);
            }
        }
    }

    @Override
    public void madeArena() {
        Level level = room.level;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                level.setBlock(new Vector3(x, room.yi, z), Block.get(Block.SANDSTONE, 0));
            }
        }
        finishBuild();
    }
}