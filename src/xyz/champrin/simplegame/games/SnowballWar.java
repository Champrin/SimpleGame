package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import xyz.champrin.simplegame.Room;

public class SnowballWar extends Games implements Listener {

    public SnowballWar(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (room.gameType.equals("SnowballWar")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                if (event.getBlock().getId() != Block.SNOW_BLOCK) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageEvent event) {
        if (room.gameType.equals("SnowballWar")) {
            Entity player = event.getEntity();
            if (player instanceof Player) {
                if (room.gamePlayer.contains((Player) player)) {
                    if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                        room.setToView((Player) player);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}