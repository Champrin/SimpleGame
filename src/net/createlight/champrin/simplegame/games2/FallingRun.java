package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.potion.Effect;
import net.createlight.champrin.simplegame.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FallingRun extends Games implements Listener {

    private ArrayList<Integer> blockId = new ArrayList<>(Arrays.asList(3, 5, 12, 42, 20, 145));

    public FallingRun(Room room) {
        super(room);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (room.gameType.equals("FallingRun")) {
            Entity player = event.getEntity();
            if (player instanceof Player) {
                if (room.gamePlayer.contains(player)) {
                    if (player.getHealth() - event.getDamage() <= 0) {
                        gameFail((Player) player);
                    }
                }
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) return;
        for (Player player : room.gamePlayer) {
            room.addPoint(player, 1);
            int id = blockId.get(new Random().nextInt(blockId.size()));
            CompoundTag nbt = Entity.getDefaultNBT(new Vector3(
                    player.getFloorX() + 0.5, player.getFloorY() + 9, player.getFloorZ() + 0.5), null, 0.0F, 0.0F);

            nbt.putInt("TileID", id);
            nbt.putByte("Data", 0);

            EntityFallingBlock target = (EntityFallingBlock) Entity.createEntity("EntityFallingBlock", player, nbt);
            target.setDataProperty(new IntEntityData(2, GlobalBlockPalette.getOrCreateRuntimeId(id, 0)));
            target.spawnToAll();

            if (this.mainTime <= 50 && this.mainTime >= 30) {//随机药水
                player.addEffect(Effect.getEffect(new Random().nextInt(blockId.size() + 24)).setAmplifier(1).setDuration(200).setVisible(false));
            } else if (this.mainTime < 30 && this.mainTime >= 15) {//失明
                player.addEffect(Effect.getEffect(15).setAmplifier(1).setDuration(6000).setVisible(false));
            } /*else if (this.mainTime < 15 && this.mainTime >= 1) {//TODO TNT
                CompoundTag TNT = Entity.getDefaultNBT(new Vector3(
                        player.getFloorX() + 0.5, player.getFloorY() + 9, player.getFloorZ() + 0.5), null, 0.0F, 0.0F);
                Entity snowball = Entity.createEntity(65, (FullChunk) player.level, TNT);
                snowball.setMotion(snowball.getMotion().multiply(2));
                snowball.spawnToAll();
            }*/
        }
    }

}
