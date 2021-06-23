package net.createlight.champrin.simplegame.schedule;


import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.simplegame.Room;
import net.createlight.champrin.simplegame.games2.Games;


public class RoomSchedule_2 extends Task {

    private int gameTime, startTime;
    private Room room;
    private Games game;

    public RoomSchedule_2(Room room, Games game) {
        this.room = room;
        this.startTime = (int) room.data.get("startTime");
        game.startTime = startTime;
        this.gameTime = (int) room.data.get("gameTime");
        game.mainTime = gameTime;
        this.game = game;
    }

    @Override
    public void onRun(int tick) {
        if (room.game == 0) {
            if (game.mainTime != gameTime) {
                this.game.mainTime = gameTime;
            }
            if (room.waitPlayer.size() < room.getMinPlayers()) {
                this.game.startTime = startTime;
                for (Player p : room.waitPlayer) {
                    p.sendPopup("> §r§l等待其他玩家加入...<");
                }
            } else if (room.waitPlayer.size() >= room.getMinPlayers()) {
                this.game.startTime = game.startTime - 1;
                if (room.waitPlayer.size() >= room.getMaxPlayers() - room.getMinPlayers()) {
                    this.game.startTime = 20;
                } else if (room.waitPlayer.size() >= room.getMaxPlayers()) {
                    this.game.startTime = 10;
                }
                for (Player p : room.waitPlayer) {
                    p.sendPopup(new Countdown().countDown(game.startTime));
                }
                if (this.game.startTime <= 0) {
                    if (room.waitPlayer.size() >= room.getMinPlayers()) {
                        room.startGame();
                    }
                    this.game.startTime = startTime;
                }
            }
        }

        if (room.game == 1) {
            this.game.mainTime = game.mainTime - 1;
            for (Player p : room.gamePlayer) {
                String msg = "§e剩余玩家: §f" + room.gamePlayer.size() + "\n";
                //msg = msg + "§e你的得分: §f" + room.rank.get(p.getName()) + "\n";
                msg = msg + "§d时间剩余: §b" + game.mainTime + "\n\n";
                p.sendTip(msg);
            }
            if (room.gamePlayer.size() <= 1 || game.mainTime < 0) {
                this.game.mainTime = gameTime;
                room.stopGame();
            }
            if (game.mainTime >= gameTime * 0.8) {
                game.eachTick();
            } else if (game.mainTime < 0.8 && game.mainTime >= 0.5) {
                game.eachTick();
                game.eachTick();
            } else if (game.mainTime < 0.5 && game.mainTime >= 0.3) {
                game.eachTick();
                game.eachTick();
                game.eachTick();
            } else if (game.mainTime < 0.2) {
                game.eachTick();
                game.eachTick();
                game.eachTick();
                game.eachTick();
            }
        }

    }


}