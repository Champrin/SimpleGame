package net.createlight.champrin.simplegame.games2;

import cn.nukkit.level.Level;
import net.createlight.champrin.simplegame.Room;

public abstract class Games extends net.createlight.champrin.simplegame.games.Games {

    public int mainTime, gameTime;

    public Games(Room room) {
        super(room);
        this.gameTime = (int) room.data.get("gameTime");
        this.mainTime = gameTime;
    }

    public abstract void eachTick();
}
