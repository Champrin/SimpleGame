package net.createlight.champrin.simplegame.schedule;


import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.simplegame.Room;
import net.createlight.champrin.simplegame.games2.Games;


public class RoomSchedule_2 extends Task {

    private int startTime;
    private Room room;
    private Games game;

    public RoomSchedule_2(Room room, Games game) {
        this.room = room;
        this.startTime = (int) room.data.get("startTime");
        this.game = game;
    }

    @Override
    public void onRun(int tick) {
        if (room.game == 0) {
            if (game.gameTime != game.mainTime) {
                this.game.gameTime = game.mainTime;
            }
            if (room.waitPlayer.size() < room.getMinPlayers()) {
                this.startTime = (int) room.data.get("startTime");
                for (Player p : room.waitPlayer) {
                    p.sendPopup("> §r§l等待其他玩家加入...<");
                }
            } else if (room.waitPlayer.size() >= room.getMinPlayers()) {
                this.startTime = startTime - 1;
                if (room.waitPlayer.size() >= room.getMaxPlayers() - room.getMinPlayers()) {
                    this.startTime = 20;
                } else if (room.waitPlayer.size() >= room.getMaxPlayers()) {
                    this.startTime = 10;
                }
                for (Player p : room.waitPlayer) {
                    p.sendPopup(new Countdown().countDown(startTime));
                }
                if (this.startTime <= 0) {
                    if (room.waitPlayer.size() >= room.getMinPlayers()) {
                        room.startGame();
                    }
                    this.startTime = (int) room.data.get("startTime");
                }
            }
        }

        if (room.game == 1) {
            this.game.gameTime = game.gameTime - 1;
            for (Player p : room.gamePlayer) {
                String msg = " §e§l==§f" + room.gameType + "§e==\n";
                msg = msg + "§e剩余玩家: §f" + room.gamePlayer.size() + "\n";
                //msg = msg + "§e你的得分: §f" + room.rank.get(p.getName()) + "\n";
                msg = msg + "§d时间剩余: §b" + game.gameTime + "\n\n";
                p.sendTip(msg);
            }
            if (room.gamePlayer.size() <= 1 || game.gameTime <= 0) {
                this.game.gameTime = game.mainTime;
                room.stopGame();
            }
            if (game.gameTime >= game.mainTime * 0.8) {
                game.eachTick();
            } else if (game.gameTime < game.mainTime * 0.8 && game.gameTime >= game.mainTime * 0.6) {
                game.eachTick();
                game.eachTick();
            } else if (game.gameTime < game.mainTime * 0.6 && game.gameTime >= game.mainTime * 0.4) {
                game.eachTick();
                game.eachTick();
                game.eachTick();
            } else if (game.gameTime < game.mainTime * 0.4 && game.gameTime >= game.mainTime * 0.2) {
                game.eachTick();
                game.eachTick();
                game.eachTick();
                game.eachTick();
            } else if (game.gameTime < game.mainTime * 0.2) {
                game.eachTick();
                game.eachTick();
                game.eachTick();
                game.eachTick();
                game.eachTick();
            }
        }

    }


}