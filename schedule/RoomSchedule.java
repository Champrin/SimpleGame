package xyz.champrin.simplegame.schedule;


import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import xyz.champrin.simplegame.Room;
import xyz.champrin.simplegame.games.Games;


public class RoomSchedule extends Task {

    private int mainTime, StartTime;
    private int maxTime, startTime;
    private Room room;
    private Games game;
    public RoomSchedule(Room room, Games game) {
        this.room = room;
        this.StartTime = (int) room.data.get("startTime");

        this.startTime = StartTime;
        this.maxTime = (int) room.data.get("gameTime");
        this.mainTime = maxTime;
        this.game = game;
    }

    @Override
    public void onRun(int tick) {
        if (room.game == 0) {
            if (this.mainTime != maxTime) {
                this.mainTime = maxTime;
            }
            if (room.waitPlayer.size() < room.getMinPlayers()) {
                this.startTime = StartTime;
                for (Player p : room.waitPlayer) {
                    p.sendPopup("§f> §l等待其他玩家加入...<\n");
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
                    this.startTime = StartTime;
                }
            }
        }

        if (room.game == 1) {
            this.mainTime = mainTime - 1;
            for (Player p : room.gamePlayer) {
                String msg = "§e你的得分: §f" + room.rank.get(p.getName()) + "\n";
                msg = msg + "§d时间剩余: §b" + mainTime + "\n\n";
                p.sendTip(msg);
            }
            if (room.gamePlayer.size() <= 1 || this.mainTime <= 0) {
                this.mainTime = maxTime;
                room.stopGame();
                game.madeArena();
            }
        }

    }


}