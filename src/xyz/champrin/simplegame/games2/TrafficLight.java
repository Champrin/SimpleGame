package xyz.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.champrin.simplegame.Room;

import java.util.Random;

public class TrafficLight extends Games {

    //红绿黄、灯种类
    private int r = 0, g = 0, y = 0;
    private String type = "g";
    private int rt = 0, gt = 0, yt = 0;

    public TrafficLight(Room room) {
        super(room);
        this.r = new Random().nextInt(3) + 1;
        this.g = new Random().nextInt(4) + 3;
        this.y = new Random().nextInt(3) + 2;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        if (room.gameType.equals("TrafficLight")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                Level level = player.getLevel();
                int blockId = level.getBlock(player.floor().subtract(0, 1)).getId();
                Item hand = player.getInventory().getItemInHand();
                if (blockId != Block.IRON_BLOCK) {
                    if (hand.getId() == 35 && hand.getDamage() == 14) {
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
    public void onHeld(PlayerItemHeldEvent event) {
        if (room.gameType.equals("TrafficLight")) {
            Player player = event.getPlayer();
            if (room.gamePlayer.contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void eachTick() {
        switch (type) {
            case "r":
                this.rt++;
                if (this.rt == this.r) {
                    this.giveLight(5, "§a绿灯");
                    this.type = "g";
                    this.rt = 0;
                    this.r = new Random().nextInt(3) + 1;
                }
                break;
            case "g":
                this.gt++;
                if (this.gt == this.g) {
                    this.giveLight(4, "§e黄灯");
                    this.type = "y";
                    this.gt = 0;
                    this.g = new Random().nextInt(4) + 3;
                }
                break;
            case "y":
                this.yt++;
                if (this.yt == this.y) {
                    this.giveLight(14, "§c红灯");
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

    public void madeArena() {
        Level level = room.level;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                level.setBlock(new Vector3(x, room.yi, z), Block.get(Block.IRON_BLOCK, 0));
            }
        }
        finishBuild();
    }
}
