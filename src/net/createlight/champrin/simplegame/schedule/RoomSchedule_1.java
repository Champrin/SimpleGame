package net.createlight.champrin.simplegame.schedule;


import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.simplegame.Room;


public class RoomSchedule_1 extends Task {

    private int mainTime;
    private int gameTime, startTime;
    private Room room;
    private String tip;
    private String waiting_tip;

    public RoomSchedule_1(Room room) {
        this.room = room;
        this.startTime = (int) room.data.get("startTime");
        this.mainTime = (int) room.data.get("gameTime");
        this.gameTime = mainTime;

        this.waiting_tip = room.plugin.config.getString("waiting-tip");
        StringBuilder tip = new StringBuilder();
        for (String string : room.plugin.config.getStringList("gaming-tip-1")) {
            tip.append(string).append("\n");
        }
        this.tip = tip.toString();
    }

    @Override
    public void onRun(int tick) {
        if (room.game == 0) {
            if (this.gameTime != mainTime) {
                this.gameTime = mainTime;
            }
            if (room.waitPlayer.size() < room.getMinPlayers()) {
                this.startTime = (int) room.data.get("startTime");
                for (Player p : room.waitPlayer) {
                    p.sendPopup(waiting_tip);
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
            this.gameTime = gameTime - 1;
            for (Player p : room.gamePlayer) {
                p.sendTip(tip.replaceAll("%GAMETYPE%",room.gameType).replaceAll("%POINTS%", String.valueOf(room.rank.get(p.getName()))).replaceAll("%REMAINTIME%", String.valueOf(gameTime)));
            }
            if (room.gamePlayer.size() <= 1 || this.gameTime <= 0) {
                this.gameTime = mainTime;
                room.stopGame();
            }
        }

    }


}