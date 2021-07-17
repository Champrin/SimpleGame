package net.createlight.champrin.simplegame;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;
import net.createlight.champrin.simplegame.games.*;
import net.createlight.champrin.simplegame.games.Games;
import net.createlight.champrin.simplegame.games2.*;
import net.createlight.champrin.simplegame.schedule.RoomSchedule_1;
import net.createlight.champrin.simplegame.schedule.RoomSchedule_2;
import net.createlight.champrin.simplegame.schedule.RoomSchedule_3;

import java.util.*;

public class Room implements Listener {

    public SimpleGame plugin;
    public Games GameType;
    public Task GameTask;
    public String gameType;
    public String roomId;
    public LinkedHashMap<String, Object> data;
    public int game = 0;

    public Level level;
    public int xi, xa, yi, ya, zi, za, S;

    public LinkedHashMap<String, Integer> rank = new LinkedHashMap<>();
    public ArrayList<Player> waitPlayer = new ArrayList<>();
    public ArrayList<Player> gamePlayer = new ArrayList<>();
    public ArrayList<Player> viewPlayer = new ArrayList<>();
    public ArrayList<Player> finishPlayer = new ArrayList<>();
    public int joinGamePlayer = 0;
    public LinkedHashMap<String, String> playerNameTag = new LinkedHashMap<>();
    public Vector3 WaitPos, ViewPos, LeavePos;

    public ArrayList<String> BreakGame = new ArrayList<>(Arrays.asList("OreRace", "OreRace_2", "SnowballWar", "SnowballWar_2", "BeFast_1", "BeFast_2", "BeFast_4", "Weeding", "MakeItem"));
    public ArrayList<String> PlaceGame = new ArrayList<>(Arrays.asList("BeFast_3", "SnowballWar"));
    public ArrayList<String> DamageGame = new ArrayList<>(Arrays.asList("KeepStanding", "KeepStanding_2", "SnowballWar", "FallingRun", "WatchingFeet", "AvoidPoison", "SurvivalWar"));

    private String quitItemName,ruleItemName;

    public Room(String roomId, SimpleGame plugin) {
        this.plugin = plugin;
        this.roomId = roomId;
        this.data = plugin.roomInformation.get(roomId);

        this.quitItemName = plugin.config.getString("quit-game-item");
        this.ruleItemName = plugin.config.getString("game-rule-item");

        this.level = plugin.getServer().getLevelByName((String) data.get("room_world"));
        this.gameType = (String) data.get("gameName");
        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        this.xi = Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0]));
        this.xa = Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0]));
        this.yi = Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1]));
        this.ya = Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1]));
        this.zi = Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2]));
        this.za = Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2]));
        this.S = Math.abs(xa - xi) * Math.abs(za - zi);

        this.ViewPos = getVector3((String) data.get("view_pos"));
        this.WaitPos = getVector3((String) data.get("wait_pos"));
        this.LeavePos = getVector3((String) data.get("leave_pos"));
        switch (gameType) {
            case "OreRace":
                this.GameType = new OreRace(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "OreRace_2":
                this.GameType = new OreRace_2(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "KeepStanding":
            case "KeepStanding_2":
                this.GameType = new KeepStanding(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "SnowballWar":
                this.GameType = new SnowballWar(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "SnowballWar_2":
                this.GameType = new SnowballWar_2(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "Parkour":
                this.GameType = new Parkour(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "MineRun":
                this.GameType = new MineRun(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "BeFast_1":
                this.GameType = new BeFast_1(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "BeFast_2":
                this.GameType = new BeFast_2(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "BeFast_3":
                this.GameType = new BeFast_3(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "BeFast_4":
                this.GameType = new BeFast_4(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            case "Weeding":
                this.GameType = new Weeding(this);
                this.GameTask = new RoomSchedule_1(this);
                break;
            /*case "MakeItem":
                this.GameType = new MakeItem(this);
                this.GameTask = new RoomSchedule_1(this);
                break;*/
            case "WatchingFeet":
                this.GameType = new WatchingFeet(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "FallingRun":
                this.GameType = new FallingRun(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "TrafficLight":
                this.GameType = new TrafficLight(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "RedAlert":
                this.GameType = new RedAlert(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "HelpHen":
                this.GameType = new HelpHen(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "CollectOre":
                this.GameType = new CollectOre(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "SnowSlide":
                this.GameType = new SnowSlide(this);
                this.GameTask = new RoomSchedule_3(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "AvoidPoison":
                this.GameType = new AvoidPoison(this);
                this.GameTask = new RoomSchedule_2(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
            case "HuntingDiamond":
                this.GameType = new HuntingDiamond(this);
                this.GameTask = new RoomSchedule_3(this, (net.createlight.champrin.simplegame.games2.Games) GameType);
                break;
        }
        this.plugin.getServer().getScheduler().scheduleRepeatingTask(GameTask, 20);
        assert GameType != null;//断言
        this.plugin.getServer().getPluginManager().registerEvents((Listener) GameType, this.plugin);
    }

    public Vector3 getVector3(String pos) {
        String[] p1 = pos.split("\\+");
        return new Vector3(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]) + 2, Integer.parseInt(p1[2]));
    }

    public Vector3 getCenterPosVector3(int y) {
        String[] p1 = ((String) data.get("center_pos")).split("\\+");
        return new Vector3(Integer.parseInt(p1[0]) + 0.5, Integer.parseInt(p1[1]) + y, Integer.parseInt(p1[2]) + 0.5);
    }

    public Vector3 getRandPos(int num)//在游戏区域内随机获取坐标
    {
        int x = xi;
        int z = zi;
        int y = yi;
        if (num != 0) {
            y = new Random().nextInt(num + 1) + yi;
        }
        if (zi - za != 0) {
            z = new Random().nextInt(za - zi + 1) + zi;
        }
        if (xi - xa != 0) {
            x = new Random().nextInt(xa - xi + 1) + xi;
        }
        return new Vector3(x, y, z);
    }

    public void addPoint(Player player, int point) {
        String name = player.getName();
        rank.put(name, rank.get(name) + point);
    }

    public String getStatus() {
        if (game == 0) return "wait";
        if (game == 1) return "game";
        return null;
    }

    public String getPlayerMode(Player player)//获取玩家当前状态
    {
        if (waitPlayer.contains(player)) {
            return "wait";
        } else if (gamePlayer.contains(player)) {
            return "game";
        } else if (viewPlayer.contains(player)) {
            return "view";
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (getPlayerMode(player) != null) {
            event.setCancelled(true);
            arenaMsg(plugin.config.getString("room-chat-tag").replaceAll("%PLAYER%", player.getName()).replaceAll("%MSG%", event.getMessage()));
        }
    }

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> a = new ArrayList<>();
        a.addAll(gamePlayer);
        a.addAll(waitPlayer);
        a.addAll(viewPlayer);
        return a;
    }

    public int getMaxPlayers() {
        return (int) data.get("maxPlayers");
    }

    public int getMinPlayers() {
        return (int) data.get("minPlayers");
    }

    public int getWaitingPlayersNumber() {
        return waitPlayer.size();
    }

    public int getRemainPlayers() {
        return getMaxPlayers() - getWaitingPlayersNumber();
    }

    public void arenaMsg(String msg) {
        for (Player player : getAllPlayers()) {
            player.sendMessage(msg);
        }
    }

    public void arenaTiTle(String msg, String msg1) {
        for (Player player : getAllPlayers()) {
            player.sendTitle(msg, msg1, 2, 20 * 3, 2);
        }
    }

    public Map<String, Integer> sortByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        //升序排序
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        map.clear();

        for (Map.Entry<String, Integer> s : list) {
            map.put(s.getKey(), s.getValue());
        }

        return map;
    }

    //这里使用了若水的保存物品NBT的方法
    public LinkedHashMap<String, ArrayList<String>> playerBag = new LinkedHashMap<>();

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void saveBag(Player gamePlayer) {
        ArrayList<String> bag = new ArrayList<>();
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            Item item = gamePlayer.getInventory().getItem(i);
            String nbt = "null";
            if (item.hasCompoundTag()) {
                nbt = bytesToHexString(item.getCompoundTag());
            }
            bag.add(item.getId() + "-" + item.getDamage() + "-" + item.getCount() + "-" + nbt);
        }
        playerBag.put(gamePlayer.getName(), bag);
        gamePlayer.getInventory().clearAll();
    }

    public void loadBag(Player gamePlayer) {
        gamePlayer.getInventory().clearAll();
        if (!playerBag.containsKey(gamePlayer.getName())) return;
        ArrayList<String> bag = playerBag.get(gamePlayer.getName());
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            String[] a = bag.get(i).split("-");
            Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
            if (!a[3].equals("null")) {
                CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
                item.setNamedTag(tag);
            }
            gamePlayer.getInventory().setItem(i, item);
        }
        playerBag.remove(gamePlayer.getName());
    }

    public void setToView(Player player) {
        gamePlayer.remove(player);
        viewPlayer.add(player);

        player.setGamemode(3);
        player.getInventory().clearAll();
        recoverPlayer(player);
        player.getInventory().setItem(8, (Item.get(64, 0, 1)).setCustomName("§c退出游戏"));
        player.teleport(ViewPos);
        player.setSpawn(ViewPos);
        player.sendMessage(plugin.config.getString("view-game"));
    }

    public void joinToRoom(Player player) {
        if (getPlayerMode(player) != null) {
            player.sendMessage(plugin.config.getString("join-game-PlayerHasJoined"));
            return;
        } else if (waitPlayer.size() >= getMaxPlayers()) {
            player.sendMessage(plugin.config.getString("join-game-RoomFull"));
            return;
        } else if (game == 1) {
            player.sendMessage(plugin.config.getString("join-game-RoomHasStarted"));
            return;
        }
        Position v3 = Position.fromObject(WaitPos, plugin.getServer().getLevelByName((String) data.get("room_world")));
        saveBag(player);
        player.setGamemode(2);
        player.teleport(v3);

        this.waitPlayer.add(player);
        this.rank.put(player.getName(), 0);

        player.getInventory().setItem(8, (Item.get(64, 0, 1)).setCustomName("§c退出游戏"));
        player.getInventory().setItem(0, (Item.get(64, 0, 1)).setCustomName("§a游戏介绍"));

        arenaMsg(plugin.config.getString("join-game-Succeed").replaceAll("%PLAYER%", player.getName()));
        player.sendMessage(plugin.config.getString("join-game-TipAboutHowToQuit"));
    }

    public void leaveRoom(Player player) {
        waitPlayer.remove(player);
        gamePlayer.remove(player);
        viewPlayer.remove(player);
        rank.remove(player.getName());

        player.getInventory().clearAll();
        recoverPlayer(player);
        player.setGamemode(0);

        loadBag(player);
        if (playerNameTag.containsKey(player.getName())) {
            player.setNameTag(playerNameTag.get(player.getName()));
        }
        Position v3 = Position.fromObject(LeavePos, plugin.getServer().getLevelByName((String) data.get("leave_pos")));
        player.teleport(v3);
        player.setSpawn(v3);

        this.joinGamePlayer = joinGamePlayer - 1;
        player.getInventory().clearAll();
        arenaMsg(plugin.config.getString("quit-game-room").replaceAll("%PLAYER%", player.getName()));
    }

    public void startGame() {
        this.joinGamePlayer = waitPlayer.size();
        for (Player player : waitPlayer) {
            player.getInventory().clearAll();
            player.setGamemode(0);
            gamePlayer.add(player);
            playerNameTag.put(player.getName(), player.getNameTag());
            player.setNameTag(plugin.config.getString("player-nameTag").replaceAll("%PLAYER%", player.getName()));
            player.teleport(getStartVector3());
            player.setSpawn(getStartVector3());
        }
        waitPlayer.clear();

        this.game = 1;
        this.plugin.freeRooms.remove(this);
        /*checkTool 检查游戏是否需要工具*/
        GameType.giveTools();

        GameType.useBuild();
    }


    public Vector3 getStartVector3() {//TODO
        switch (gameType) {
            case "OreRace_2":
                return getCenterPosVector3(15);
            case "OreRace":
            case "BeFast_1":
            case "BeFast_2":
            case "BeFast_4":
                return getCenterPosVector3(13);
            /*
            case "MakeItem":
            case "CollectOre":
            case "HuntingDiamond":

            case "FallingRun":
            case "TrafficLight":
            case "RedAlert":
            */
            default:
                return getCenterPosVector3(1);
        }
    }

    public void stopGame() {
        cleanDrop();
        getRichList();
        for (Player player : getAllPlayers()) {
            player.sendMessage(getRank(player.getName()));
        }
        unsetAllPlayers();
        this.game = 0;
        this.plugin.freeRooms.add(this);
        GameType.madeArena();
    }
    public void cleanDrop(){
        for (Entity entity : level.getEntities()) {
            if (entity instanceof EntityItem) {
                entity.kill();
                entity.close();
            }
        }
    }
    public String getRank(String name) {
        int a = 0;
        for (Map.Entry<String, Integer> map : rank.entrySet()) {
            a = a + 1;
            if (map.getKey().equals(name)) {
                return plugin.config.getString("person-rank").replaceAll("%RANK%", String.valueOf(a)).replaceAll("%POINTS%", String.valueOf(map.getValue()));
            }
        }
        return "This player's data is null.";
    }

    public void getRichList() {
        int num = 0;
        this.rank = (LinkedHashMap<String, Integer>) sortByValue(rank);
        String r;
        for (Map.Entry<String, Integer> map : rank.entrySet()) {
            num = num + 1;
            if (num == 1) {
                r = "[1]";
            } else if (num == 2) {
                r = "[2]";
            } else if (num == 3) {
                r = "[3]";
            } else {
                r = "[" + num + "]";
            }
            for (Player player : getAllPlayers()) {
                player.sendMessage(plugin.config.getString("all-rank").replaceAll("%RANK%", r).replaceAll("%POINTS%", String.valueOf(map.getValue())).replaceAll("%PLAYER%", String.valueOf(map.getKey())));
            }
        }
    }

    public void severStop() {
        for (Player player : getAllPlayers()) {
            player.getInventory().clearAll();
            recoverPlayer(player);
            player.setGamemode(0);
            loadBag(player);
            if (playerNameTag.containsKey(player.getName())) {
                player.setNameTag(playerNameTag.get(player.getName()));
            }
        }
        this.gamePlayer.clear();
        this.waitPlayer.clear();
        this.viewPlayer.clear();
        this.joinGamePlayer = 0;
    }

    private void recoverPlayer(Player player) {
        player.setHealth(20);
        player.getFoodData().sendFoodLevel(20);
        player.removeAllEffects();
    }

    public void unsetAllPlayers() {
        for (Player player : getAllPlayers()) {
            if (playerNameTag.containsKey(player.getName())) {
                player.setNameTag(playerNameTag.get(player.getName()));
            }
            player.getInventory().clearAll();
            Position v3 = Position.fromObject(LeavePos, plugin.getServer().getLevelByName((String) data.get("leave_pos")));
            player.teleport(v3);
            player.setSpawn(v3);

            recoverPlayer(player);
            player.setGamemode(0);

            loadBag(player);
        }
        this.gamePlayer.clear();
        this.waitPlayer.clear();
        this.viewPlayer.clear();
        this.joinGamePlayer = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onTouch(PlayerInteractEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            Player player = event.getPlayer();
            if (quitItemName.equals(player.getInventory().getItemInHand().getCustomName())) {
                player.sendMessage(plugin.config.getString("quit-game-player"));
                leaveRoom(player);
            } else if (ruleItemName.equals(player.getInventory().getItemInHand().getCustomName())) {
                FormWindowSimple window = new FormWindowSimple(plugin.config.getString("game-rule-title").replaceAll("%GAMETYPE%", gameType), getGameRule());
                player.showFormWindow(window);
            }
            event.setCancelled(true);
        }
    }

    /**
     * 玩家退出类事件
     **/
    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            leaveRoom(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onKick(PlayerKickEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            leaveRoom(event.getPlayer());
        }
    }

    /**
     * 玩家受伤类事件
     **/
    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (getPlayerMode(event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onHit(EntityDamageEvent event) {
        Entity en = event.getEntity();
        if (en instanceof Player) {
            if (getPlayerMode((Player) en) != null) {
                if (getPlayerMode((Player) en).equals("game")) {
                    if (!DamageGame.contains(gameType)) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockBreakEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            if (getPlayerMode(event.getPlayer()).equals("game")) {
                if (!BreakGame.contains(gameType)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onBlockPlace(BlockPlaceEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            if (getPlayerMode(event.getPlayer()).equals("game")) {
                if (!PlaceGame.contains(gameType)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onDrop(PlayerDropItemEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            if (!getPlayerMode(event.getPlayer()).equals("game")) {
                event.setCancelled(true);
            }
        }
    }

    private String getGameRule() {
        StringBuilder rules = new StringBuilder();
        for (String rule : plugin.config.getStringList(gameType)) {
            rules.append(rule).append("\n");
        }
        return rules.toString();
    }
}
