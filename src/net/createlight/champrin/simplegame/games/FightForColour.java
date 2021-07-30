package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import net.createlight.champrin.simplegame.Room;

public class FightForColour extends Games implements Listener {

    public FightForColour(Room room) {
        super(room);
        this.tools = new Item[1];
        tools[0] = Item.get(Item.DIAMOND_HOE, 0, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onTouch(PlayerInteractEvent event) {
        if (room.gameType.equals("FightForColour")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Block block = event.getBlock();
                if (block.getId() == Block.GRASS && player.getInventory().getItemInHand().getId() == Item.DIAMOND_HOE) {
                    int id = room.gamePlayer.indexOf(player);
                    int x = block.getFloorX();
                    int y = block.getFloorY();
                    int z = block.getFloorZ();
                    if (checkAround(x + 1, y, z, id) || checkAround(x, y, z + 1, id) || checkAround(x - 1, y, z, id) || checkAround(x, y, z - 1, id) || (!checkAround(x + 1, y, z, id) && !checkAround(x, y, z + 1, id) && !checkAround(x - 1, y, z, id) && !checkAround(x, y, z - 1, id))) {
                        level.setBlock(block, Block.get(Block.CONCRETE, id));
                    }
                }
            }
        }
    }

    private Boolean checkAround(int x, int y, int z, int id) {
        return level.getBlock(x, y, z).getId() == id;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageEvent event) {
        if (room.gameType.equals("FightForColour")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (room.gamePlayer.contains(player)) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                        if (damager instanceof Player) {
                            if (room.gamePlayer.contains((Player) damager)) {
                                event.setCancelled(true);
                                event.setDamage(0);
                                double yaw = Math.atan2((player.x - damager.x), (player.z - damager.z));
                                player.knockBack(damager, 0, Math.sin(yaw), Math.cos(yaw), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void madeArena() {
        Level level = room.level;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                level.setBlock(new Vector3(x, room.yi, z), Block.get(Block.GRASS, 0));
            }
        }
        finishBuild();
    }
}