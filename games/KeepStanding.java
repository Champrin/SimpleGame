package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import xyz.champrin.simplegame.Room;
import cn.nukkit.event.Listener;


public class KeepStanding extends Games implements Listener {

    public KeepStanding(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            if (room.gameType.equals("KeepStanding") || room.gameType.equals("KeepStanding_2")) {
                Entity player = event.getEntity();
                Entity damage = ((EntityDamageByEntityEvent) event).getDamager();
                if (player instanceof Player && damage instanceof Player) {
                    if (room.gamePlayer.contains((Player) player) && room.gamePlayer.contains((Player) damage)) {
                        event.setCancelled(true);
                        if (((Player) damage).getInventory().getItemInHand().getId() == 280) {
                            double yaw = Math.atan2((player.x - damage.x), (player.z - damage.z));
                            ((Player) player).knockBack(damage, 0, Math.sin(yaw), Math.cos(yaw), 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (room.gameType.equals("KeepStanding")) {
            if (room.gamePlayer.contains(player)) {
                Level level = player.getLevel();
                if (level.getBlock(player.floor().subtract(0, 1)).getId() == Block.WOOL) {
                    room.addPoint(player, 1);
                }
            }
        }
        else if (room.gameType.equals("KeepStanding_2")) {
            if (player.getY() < room.yi - 2) {
                room.setToView(player);
            }
        }
    }
}