package xyz.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import xyz.champrin.simplegame.Room;

public class RedAlert extends Games {

    private int area;

    public RedAlert(Room room) {
        super(room);
        this.area = Math.abs(room.xa - room.xi) * (Math.abs(room.za - room.zi));
    }

    @Override
    public void eachTick() {
        //顺序 0 4 1 14 air
        for (int i = 1; i <= area / 8; i++) {
            Level level = room.level;
            Block block = level.getBlock(room.getRandPos(0));
            if (block.getId() == 35) {
                int damage = block.getDamage();
                if (damage == 0) {
                    level.setBlock(block, Block.get(35, 4));
                } else if (damage == 4) {
                    level.setBlock(block, Block.get(35, 1));
                } else if (damage == 1) {
                    level.setBlock(block, Block.get(35, 14));
                } else if (damage == 14) {
                    level.setBlock(block, Block.get(0, 0));
                }
            }
        }
        for (Player p : room.gamePlayer) {
            if (p.getY() <= room.yi - 2) {
                gameFail(p);
            }
        }
    }

    public void madeArena() {
        Level level = room.level;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                level.setBlock(new Vector3(x, room.yi, z), Block.get(Block.WOOL, 0));
            }
        }
        finishBuild();
    }
}
