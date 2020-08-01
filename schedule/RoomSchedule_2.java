package xyz.champrin.simplegame.schedule;


import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import xyz.champrin.simplegame.Room;
import xyz.champrin.simplegame.games2.Games;


public class RoomSchedule_2 extends Task {

    private int maxTime, StartTime;
    private Room room;
    private Games game;

    public RoomSchedule_2(Room room, Games game) {
        this.room = room;
        this.StartTime = (int) room.data.get("game.startTime");
        game.startTime = StartTime;
        this.maxTime = (int) room.data.get("gameTime");
        game.mainTime = maxTime;
        this.game = game;
    }

    @Override
    public void onRun(int tick) {
        if (room.game == 0) {
            if (game.mainTime != maxTime) {
                this.game.mainTime = maxTime;
            }
            if (room.waitPlayer.size() < room.getMinPlayers()) {
                this.game.startTime = StartTime;
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
                    this.game.startTime = StartTime;
                }
            }
        }

        if (room.game == 1) {
            this.game.mainTime = game.mainTime - 1;
            game.eachTick();
            for (Player p : room.gamePlayer) {
                String msg = "§e剩余玩家: §f" + room.gamePlayer.size() + "\n";
                //msg = msg + "§e你的得分: §f" + room.rank.get(p.getName()) + "\n";
                msg = msg + "§d时间剩余: §b" + game.mainTime + "\n\n";
                p.sendTip(msg);
            }
            if (room.gamePlayer.size() <= 1 || game.mainTime < 0) {
                this.game.mainTime = maxTime;
                room.stopGame();
                game.madeArena();
            }
        }

    }


}