package net.createlight.champrin.simplegame.games2;

import net.createlight.champrin.simplegame.Room;

public abstract class Games extends net.createlight.champrin.simplegame.games.Games {

    public int mainTime, startTime;

    public Games(Room room) {
        super(room);
    }

    public abstract void eachTick();
}
