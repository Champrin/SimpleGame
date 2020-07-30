package xyz.champrin.simplegame.games;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import xyz.champrin.simplegame.Room;
import xyz.champrin.simplegame.SimpleGame;

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
                level.setBlock(new Vector3(x, room.yi, z), Block.get(80, 0));//STONE
            }
        }
        finishBuild();
    }

    public void finishBuild() {
        Config c = plugin.getRoomConfig(room.roomId);
        c.set("Arena", true);
        c.save();
        room.data.put("Arena", true);
    }

    public void gameFinish(Player player) {
        if (room.finishPlayer.size() < room.gamePlayer.size()) {
            room.finishPlayer.add(player);
            player.sendMessage("§a 你完成游戏了！！！");
            room.arenaMsg("  " + player.getName() + "§e完成了游戏");
            int rank = room.finishPlayer.size() + 1;
            room.addPoint(player, room.gamePlayer.size());
            player.sendMessage("§d   你是第§c  " + rank + " §d个完成的！");
            room.setToView(player);
            player.sendMessage("§e  先已将你切换成观战模式，耐心的等待别人完成比赛吧！");
            player.sendMessage("§e     若退出将不会记录成绩！");
        } else {
            room.stopGame();
        }
    }

    public void gameFail(Player player) {
        if (room.gamePlayer.size() > 1) {
            room.finishPlayer.add(player);
            room.addPoint(player,room.finishPlayer.size());
            room.setToView(player);
            player.sendMessage("§e  耐心的等待别人完成比赛吧！");
            if (room.gamePlayer.size() <= 1) {
                room.addPoint(player,room.finishPlayer.size());
                room.arenaMsg("  " +room.gamePlayer.get(0)+"§e活到了最后！");
                room.stopGame();
            }
        } else {
            room.addPoint(player,room.finishPlayer.size());
            room.arenaMsg("  " +room.gamePlayer.get(0)+"§e活到了最后！");
            room.stopGame();
        }
    }
}
