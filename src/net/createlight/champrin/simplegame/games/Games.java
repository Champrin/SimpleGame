package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import net.createlight.champrin.simplegame.SimpleGame;
import net.createlight.champrin.simplegame.Room;

import java.io.File;

public abstract class Games {

    public SimpleGame plugin = SimpleGame.getInstance();

    public Room room;
    public Level level;

    public Item[] tools;

    public Games(Room room) {
        this.room = room;
        this.level = room.level;
    }

    public void giveTools() {
        if (tools == null) return;
        if (room.gamePlayer.size() == 0) return;
        for (Player player : room.gamePlayer) {
            for (Item item : tools) {
                player.getInventory().addItem(item);
            }
        }
    }

    public void madeArena() {
        Level level = room.level;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                level.setBlock(new Vector3(x, room.yi, z), Block.get(1, 0));
            }
        }
        finishBuild();
    }

    public void finishBuild() {
        File file = new File(this.plugin.getDataFolder() + "/Room/" + room.roomId + ".yml");
        if (!file.exists()) return;
        Config c = plugin.getRoomConfig(room.roomId);
        c.set("arena", true);
        c.save();
        room.data.put("arena", true);
    }

    public void useBuild() {
        File file = new File(this.plugin.getDataFolder() + "/Room/" + room.roomId + ".yml");
        if (!file.exists()) return;
        Config c = plugin.getRoomConfig(room.roomId);
        c.set("arena", false);
        c.save();
        room.data.put("arena", false);
    }

    public void gameFinish(Player player) {
        if (room.finishPlayer.contains(player)) return;
        if (room.finishPlayer.size() < room.gamePlayer.size()) {
            player.sendMessage(plugin.config.getString("finish-game-person-1"));
            room.arenaMsg(plugin.config.getString("finish-game-room").replaceAll("%PLAYER%", player.getName()));
            room.addPoint(player, 66 - room.finishPlayer.size());
            player.sendMessage(plugin.config.getString("finish-game-person-2").replaceAll("%RANK%", String.valueOf(room.finishPlayer.size() + 1)));
            room.finishPlayer.add(player);
            room.setToView(player);
            player.sendMessage(plugin.config.getString("finish-game-tip-1"));
            player.sendMessage(plugin.config.getString("finish-game-tip-2"));
        } else {
            room.stopGame();
        }
    }

    public void gameFail(Player player) {
        if (room.finishPlayer.contains(player)) return;
        if (room.gamePlayer.size() > 1) {
            player.sendMessage(plugin.config.getString("fail-game-person"));
            room.arenaMsg(plugin.config.getString("fail-game-room").replaceAll("%PLAYER%", player.getName()));
            room.finishPlayer.add(player);
            room.addPoint(player, room.finishPlayer.size());
            room.setToView(player);
            player.sendMessage(plugin.config.getString("fail-game-tip"));
            if (room.gamePlayer.size() <= 1) {
                gameSucceed(room.gamePlayer.get(0));
            }
        } else {
            gameSucceed(room.gamePlayer.get(0));
        }
    }

    public void gameSucceed(Player player) {
        if (room.finishPlayer.contains(player)) return;
        room.finishPlayer.add(player);
        player.sendMessage(plugin.config.getString("succeed-game-person"));
        room.arenaMsg(plugin.config.getString("succeed-game-room").replaceAll("%PLAYER%", player.getName()));
        room.addPoint(player, 66);
        room.stopGame();
    }
}
