package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import xyz.champrin.simplegame.Room;

import java.util.ArrayList;

public class BeFast_3 extends Games implements Listener {

    private ArrayList<Block> blockPlace = new ArrayList<>();

    public BeFast_3(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        if (room.gameType.equals("BeFast_3")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Block block = event.getBlock();
                if (block.getId() != Block.GLASS) {
                    event.setCancelled(true);
                    return;
                }
                blockPlace.add(block);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("BeFast_3")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                if (player.getLevel().getBlock(player.floor().subtract(0, 1)).getId() == Block.PLANKS)//木板
                {
                    gameFinish(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (room.gameType.equals("BeFast_3")) {
            Entity player = event.getEntity();
            if (player instanceof Player) {
                if (room.gamePlayer.contains((Player) player)) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                        if (damager instanceof Player) {
                            if (room.gamePlayer.contains((Player) damager)) {
                                event.setCancelled(true);
                                double yaw = Math.atan2((player.x - damager.x), (player.z - damager.z));
                                ((Player) player).knockBack(damager, 0, Math.sin(yaw), Math.cos(yaw), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void madeArena() {
        for (Block block : blockPlace) {
            block.level.setBlock(block, Block.get(0, 0));
        }
        finishBuild();
    }
}