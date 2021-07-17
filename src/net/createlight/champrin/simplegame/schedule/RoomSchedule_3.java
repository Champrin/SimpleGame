package net.createlight.champrin.simplegame.schedule;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.simplegame.Room;
import net.createlight.champrin.simplegame.games2.Games;

public class RoomSchedule_3 extends Task {

    private int startTime;
    private Room room;
    private Games game;
    private String waiting_tip;

    public RoomSchedule_3(Room room, Games game) {
        this.room = room;
        this.startTime = (int) room.data.get("startTime");
        this.game = game;

        this.waiting_tip = room.plugin.config.getString("waiting-tip");
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
        } else if (room.game == 1) {
            this.game.gameTime = game.gameTime - 1;
            game.eachTick();
            if (room.gamePlayer.size() <= 1 || game.gameTime < 0) {
                this.game.gameTime = game.mainTime;
                room.stopGame();
            }
        }
    }

}