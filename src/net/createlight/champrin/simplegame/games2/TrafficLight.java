package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import net.createlight.champrin.simplegame.Room;

import java.util.Random;

public class TrafficLight extends Games implements Listener {

    //红绿黄、灯种类
    private int r, g, y;
    private String type = "g";
    private int rt = 0, gt = 0, yt = 0;
    private String rc=room.plugin.config.getString("item-red-customName") ,gc=room.plugin.config.getString("item-green-customName"),yc=room.plugin.config.getString("item-yellow-customName");

    public TrafficLight(Room room) {
        super(room);
        this.r = new Random().nextInt(3) + 1;
        this.g = new Random().nextInt(4) + 3;
        this.y = new Random().nextInt(3) + 2;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("TrafficLight")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Level level = player.getLevel();
                int blockId = level.getBlock(player.floor().subtract(0, 1)).getId();
                Item hand = player.getInventory().getItemInHand();
                if (blockId != Block.IRON_BLOCK) {
                    if ((hand.getId() == 35 && hand.getDamage() == 14) || hand.getId() != 35) {
                        player.sendTitle("§c  红灯亮起时不能行走！！！", "§e重新开始吧！", 2, 20 * 2, 2);
                        player.teleport(room.getRandPos(2));
                    }
                }
                if (blockId == Block.PLANKS)//木板
                {
                    gameFinish(player);
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onPickup(EntityInventoryChangeEvent event) {
        if (room.gameType.equals("TrafficLight")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (room.gamePlayer.contains(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) return;
        switch (type) {
            case "r":
                this.rt++;
                if (this.rt == this.r) {
                    this.giveLight(5, gc);
                    this.type = "g";
                    this.rt = 0;
                    this.r = new Random().nextInt(3) + 1;
                }
                break;
            case "g":
                this.gt++;
                if (this.gt == this.g) {
                    this.giveLight(4, yc);
                    this.type = "y";
                    this.gt = 0;
                    this.g = new Random().nextInt(4) + 3;
                }
                break;
            case "y":
                this.yt++;
                if (this.yt == this.y) {
                    this.giveLight(14, rc);
                    this.type = "r";
                    this.yt = 0;
                    this.y = new Random().nextInt(3) + 2;
                }
                break;
        }
    }

    //35:14-5-4 r-g-y
    public void giveLight(int damage, String name) {
        for (Player p : room.gamePlayer) {
            p.getInventory().clearAll();
            p.getInventory().clearAll();
            p.getInventory().clearAll();
            for (int i = 0; i < 9; i++) {
                Item item = Item.get(35, damage, 1);
                item.setCustomName(name);
                p.getInventory().setItem(i, item);
            }
        }
    }
}
