package net.createlight.champrin.simplegame.games2;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import net.createlight.champrin.simplegame.Room;

import java.util.ArrayList;
import java.util.Random;

public class SnowSlide extends Games implements Listener {

    private int round;
    private int roundTime;
    private ArrayList<Vector3> blockPlace = new ArrayList<>();
    private String tip, remain_time_tip, remain_players_tip,new_round_title;

    public SnowSlide(Room room) {
        super(room);
        this.round = 1;
        this.roundTime = getTimeByRound();

        this.remain_time_tip = room.plugin.config.getString("SnowSlide-round-remain-time");
        this.remain_players_tip = room.plugin.config.getString("SnowSlide-round-remain-players");
        this.new_round_title = room.plugin.config.getString("SnowSlide-new-round-title");
        StringBuilder tip = new StringBuilder();
        for (String string : room.plugin.config.getStringList("SnowSlide-tip")) {
            tip.append(string).append("\n");
        }
        this.tip = tip.toString();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageEvent event) {
        if (room.gameType.equals("SnowSlide")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (room.gamePlayer.contains(player)) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                        if (this.round >= 10) {
                            if (damager instanceof Player) {
                                if (room.gamePlayer.contains((Player) damager)) {
                                    event.setCancelled(true);
                                    double yaw = Math.atan2((player.x - damager.x), (player.z - damager.z));
                                    player.knockBack(damager, 0, Math.sin(yaw), Math.cos(yaw), 1);
                                }
                            }
                        }
                        if (damager instanceof EntityProjectile) {
                            gameFail(player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void eachTick() {
        if (room.gamePlayer.size() == 0) {
            cleanArena();
            return;
        }
        this.gameTime = mainTime;
        this.roundTime = roundTime - 1;
        StringBuilder pop = new StringBuilder();
        for (Player player : room.gamePlayer) {
            pop.append(tip.replaceAll("%ROUND%", String.valueOf(round)).replaceAll("%PLAYERS%", String.valueOf(room.gamePlayer.size())));
            room.addPoint(player, 1);
            if (this.roundTime > 0) {
                pop.append(remain_time_tip.replaceAll("%ROUNDTIME%", String.valueOf(roundTime))).append("\n");
                if (this.roundTime < 5) {
                    player.getLevel().addSound(player, Sound.RANDOM_ANVIL_LAND);
                }
            }
            if (this.roundTime == 5) {
                player.getLevel().addSound(player, Sound.CAULDRON_EXPLODE);
                this.spawnBlocks();
            } else if (this.roundTime == 0) {
                player.getLevel().addSound(player, Sound.BLOCK_BAMBOO_HIT);
                this.spawnSnowballs();
            } else {
                pop.append(remain_players_tip.replaceAll("%PLAYERS%", String.valueOf(room.gamePlayer.size()))).append("\n");
            }
            if (this.roundTime == -5) {
                this.cleanArena();
                room.addPoint(player, round);
                this.round = round + 1;
                player.sendTitle(new_round_title.replaceAll("%ROUND%", String.valueOf(round)), "", 2, 20 * 2, 2);
                this.roundTime = this.getTimeByRound();
            }
            player.sendPopup(pop.toString());
            pop = new StringBuilder();
        }
    }

    public void spawnBlocks() {
        if (round > 0 && round <= 5) {
            this.spawnBlock();
            this.spawnBlock();
            this.spawnBlock();
            this.spawnBlock();
        } else if (round > 5 && round <= 10) {
            this.spawnBlock();
            this.spawnBlock();
            this.spawnBlock();
        } else if (round > 10 && round <= 15) {
            this.spawnBlock();
            this.spawnBlock();
        } else if (round > 15) {
            this.spawnBlock();
        }
    }

    public void spawnBlock() {
        Vector3 v3 = new Vector3((new Random().nextInt(room.xa - room.xi + 1) + room.xi), room.yi + 3, (new Random().nextInt(room.za - room.zi + 1) + room.zi));
        level.setBlock(v3, Block.get(44, 2));
        blockPlace.add(v3);
    }

    public void cleanArena() {
        for (Vector3 v3 : this.blockPlace) {
            level.setBlock(v3, Block.get(0, 0));
        }
        this.blockPlace.clear();
    }

    public void spawnSnowballs() {
        int y = room.ya + 8;
        for (int x = room.xi; x <= room.xa; x++) {
            for (int z = room.zi; z <= room.za; z++) {
                CompoundTag nbt = Entity.getDefaultNBT(new Vector3(x, y, z), new Vector3(0, 0, 0), 0.0F, 0.0F);
                Entity snowball = Entity.createEntity(81, new Position(x, y, z, level).getChunk(), nbt);
                snowball.setMotion(snowball.getMotion().multiply(10));
                snowball.spawnToAll();
            }
        }
    }

    public int getTimeByRound() {
        if (round > 0 && round <= 5) return 25;
        else if (round > 5 && round <= 10) return 20;
        else if (round > 10 && round <= 15) return 15;
        else if (round > 15) return 10;
        return -1;
    }

    @Override
    public void madeArena() {
        cleanArena();
        this.round = 1;
        this.roundTime = getTimeByRound();
    }
}
