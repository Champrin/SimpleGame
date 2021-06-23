package xyz.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import xyz.champrin.simplegame.Room;
import xyz.champrin.simplegame.SimpleGame;

public abstract class Games extends xyz.champrin.simplegame.games.Games {

    public int mainTime, startTime;

    public Games(Room room) {
        super(room);
    }

    public abstract void eachTick();
}
