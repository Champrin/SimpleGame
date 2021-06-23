package net.createlight.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import net.createlight.champrin.simplegame.SimpleGame;
import net.createlight.champrin.simplegame.Room;

public abstract class Games {

    public SimpleGame plugin = SimpleGame.getInstance();

    public Room room;

    public Games(Room room) {
        this.room = room;
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
        Config c = plugin.getRoomConfig(room.roomId);
        c.set("arena", true);
        c.save();
        room.data.put("arena", true);
    }

    public void useBuild() {
        Config c = plugin.getRoomConfig(room.roomId);
        c.set("arena", false);
        c.save();
        room.data.put("arena", false);
    }

    public void gameFinish(Player player) {
        if (room.finishPlayer.contains(player)) return;
        if (room.finishPlayer.size() < room.gamePlayer.size()) {
            player.sendMessage("§a 你完成游戏了！！！");
            room.arenaMsg(player.getName() + "§e完成了游戏");
            room.addPoint(player, 66 - room.finishPlayer.size());
            player.sendMessage("§d  你是第§c  " + room.finishPlayer.size() + 1 + " §d个完成的！");
            room.finishPlayer.add(player);
            room.setToView(player);
            player.sendMessage("§e  先已将你切换成观战模式，耐心的等待别人完成比赛吧！");
            player.sendMessage("§e  若退出将不会记录成绩！");
        } else {
            room.stopGame();
        }
    }

    public void gameFail(Player player) {
        if (room.finishPlayer.contains(player)) return;
        if (room.gamePlayer.size() > 1) {
            player.sendMessage(player.getName() + "§f游戏失败");
            player.sendMessage(">> §c你游戏失败了！");
            room.finishPlayer.add(player);
            room.addPoint(player, room.finishPlayer.size());
            room.setToView(player);
            player.sendMessage(">> §e耐心的等待别人完成比赛吧！");
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
        room.addPoint(player, room.finishPlayer.size());
        room.arenaMsg(player.getName() + "§e活到了最后！");
        room.stopGame();
    }
}
