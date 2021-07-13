package net.createlight.champrin.simplegame;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
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
import net.createlight.champrin.simplegame.schedule.RoomSchedule;
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

    public Room(String roomId, SimpleGame plugin) {
        this.plugin = plugin;
        this.roomId = roomId;
        this.data = plugin.roomInformation.get(roomId);

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
                this.GameTask = new RoomSchedule(this);
                break;
            case "OreRace_2":
                this.GameType = new OreRace_2(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "KeepStanding":
            case "KeepStanding_2":
                this.GameType = new KeepStanding(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "SnowballWar":
                this.GameType = new SnowballWar(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "SnowballWar_2":
                this.GameType = new SnowballWar_2(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "Parkour":
                this.GameType = new Parkour(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "MineRun":
                this.GameType = new MineRun(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "BeFast_1":
                this.GameType = new BeFast_1(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "BeFast_2":
                this.GameType = new BeFast_2(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "BeFast_3":
                this.GameType = new BeFast_3(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "BeFast_4":
                this.GameType = new BeFast_4(this);
                this.GameTask = new RoomSchedule(this);
                break;
            case "Weeding":
                this.GameType = new Weeding(this);
                this.GameTask = new RoomSchedule(this);
                break;
            /*case "MakeItem":
                this.GameType = new MakeItem(this);
                this.GameTask = new RoomSchedule(this);
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
            y = new Random().nextInt(num+1) + yi;
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

    public String getRank(String name) {
        int a = 0;
        for (Map.Entry<String, Integer> map : rank.entrySet()) {
            a = a + 1;
            if (map.getKey().equals(name)) {
                return "§a你的排名: §e[" + a + "] §a你的分数: §e[" + map.getValue() + "]";
            }
        }
        return "无数据";
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (getPlayerMode(player) != null) {
            event.setCancelled(true);
            arenaMsg(player.getNameTag() + " §e§l>§r " + event.getMessage());
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
        player.sendMessage(">> 你进入观战模式");
    }

    public void joinToRoom(Player player) {
        if (getPlayerMode(player) != null) {
            player.sendMessage(">>  你已经加入一个房间了,请在聊天栏输入“@hub”重试");
            return;
        } else if (waitPlayer.size() >= getMaxPlayers()) {
            player.sendMessage(">>  房间已满");
            return;
        } else if (game == 1) {
            player.sendMessage(">>  房间已开始");
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

        arenaMsg(">  §a" + player.getName() + "§f加入了房间");
        player.sendMessage("§c若想退出游戏请输入 @hub");
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
        arenaMsg(">  §a" + player.getName() + "§f离开了房间");
    }

    public void startGame() {
        this.joinGamePlayer = waitPlayer.size();
        for (Player player : waitPlayer) {
            player.getInventory().clearAll();
            player.setGamemode(0);
            gamePlayer.add(player);
            playerNameTag.put(player.getName(), player.getNameTag());
            player.setNameTag("[PLAYER] §f" + player.getName());
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
        getRichList();
        for (Player player : getAllPlayers()) {
            player.sendMessage(getRank(player.getName()));
        }
        unsetAllPlayers();
        this.game = 0;
        this.plugin.freeRooms.add(this);
        GameType.madeArena();
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
                player.sendMessage(r + " " + map.getKey() + " 分数:" + map.getValue());
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
            if ("§c退出游戏".equals(player.getInventory().getItemInHand().getCustomName())) {
                player.sendMessage("§c>  §f你已退出游戏！");
                leaveRoom(player);
            } else if ("§a游戏介绍".equals(player.getInventory().getItemInHand().getCustomName())) {
                FormWindowSimple window = new FormWindowSimple(gameType + "§6游戏玩法介绍", getGameRule());
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
        String rule;
        switch (gameType) {
            case "BeFast_1":
                rule = "    §r§l§f游戏玩法：§r在游戏区域内，隐藏了钻石矿，玩家需找到它并挖掉，视为完成游戏。\n开始游戏时玩家玩家会获得《镐子 斧头 铲子 剪刀》工具，在木头石头泥土里，玩家需快速挖除这些木头石头泥土，找到钻石矿，完成游戏。\n" +
                        "    §l§f得分条件：§r完成游戏越快得分越高。";
                break;
            case "BeFast_2":
                rule = "    §l§f游戏玩法：§r玩家需正确使用工具来挖除方块，如斧子是用来挖木头类，镐子是用来挖石头类等，\n" +
                        "    §l§f得分条件：§r情况玩家需快速反应，反应越快，方块破坏的就越多，挖掉的越多得分越高";

                break;
            case "KeepStanding":
                rule = "    §l§f游戏玩法：§r玩家需在高地中保持站立，玩家可使用手中木棒来击退其他玩家。\n" +
                        "    §l§f得分条件：§r在高地中站立得越久，得分越高。";
                break;
            case "KeepStanding_2":
                rule = "    §l§f游戏玩法：§r玩家使用手中木棒来击退其他玩家,使其他玩家击退至虚空\n" +
                        "    §l§f得分条件：§r存活得越久，得分越高。";
                break;
            case "SnowballWar":
                rule = "    §l§f游戏玩法：§r玩家需挖取雪块来获得雪球，并投掷其他玩家。玩家若被击中，直接淘汰。\n玩家不需要去拾取雪球，获得的雪球直接发送至背包，且被挖的雪块不会被破坏。（实质就是雪球大战）\n" +
                        "    §l§f得分条件：§r存活得越久，得分越高。";
                break;
            case "SnowballWar_2":
                rule = "    §l§f游戏玩法：§r本体：掘战游戏（掘一死战），玩家需用铲子挖雪块,来使玩家掉落至虚空。\n" +
                        "    §l§f得分条件：§r存活得越久，得分越高。";
                break;
            case "Parkour":
                rule = "    §l§f游戏玩法：§r跑酷，不用多介绍了吧！\n" +
                        "    §l§f得分条件：§r到达终点越快得分越高。";
                break;
            case "MineRun":
                rule = "    §l§f游戏玩法：§r玩家在行走中不能踩到地雷《即为石制压力板》，踩到则游戏失败，传送至开始点重新开始。\n" +
                        "    §l§f得分条件：§r到达终点越快得分越高。";
                break;
            case "OreRace":
                rule = "    §l§f游戏玩法：§r三维收集矿物，场地为xyz的空间\n" +
                        "    §l§f得分条件：§r玩家需收集矿物来得分，收集矿物越多得分越高，矿物越稀有得分越高。";
                break;
            case "OreRace_2":
                rule = "    §f§l游戏玩法：§r二维收集矿物，场地为xy的空间，并及时提交收集到的物品\n" +
                        "    §l§f得分条件：§r玩家需收集矿物来得分，收集矿物越多得分越高，矿物越稀有得分越高。\n§c请在游戏结束前，§a点击出发地的钻石§c块完成提交，只有提交的玩家才有得分";
                break;
            case "BeFast_3":
                rule = "    §l§f游戏玩法：§r玩家需用手中的方块快速搭桥至终点区，玩家可攻击他人，使他人踏入虚空重新进行游戏。\n" +
                        "    §l§f得分条件：§r到达终点越快得分越高。";
                break;
            case "BeFast_4":
                rule = "    游戏玩法&§l§f得分条件：§r玩家需挖取矿石需要用同物质的镐子挖，如铁矿石要用铁镐挖，钻石矿要用钻石镐挖等";
                break;
            case "Weeding":
                rule = "    §l§f游戏玩法：§r除草，玩家需快速挖掉杂草方块。\n" +
                        "    §l§f得分条件：§r挖掉越多者得分越高。";
                break;
            case "MakeItem":
                rule = "    §l§f游戏玩法：§r按照目标物品，并自行获得原料，最后制作目标物品\n" +
                        "    §l§f得分条件：§r制作目标物品越快得分越高。";
                break;
            case "WatchingFeet":
                rule = "    §l§f游戏玩法：§rTNTRUN 保持行走，不要跌落\n" +
                        "    §l§f得分条件：§r存活得越久得分越高";
                break;
            case "FallingRun":
                rule = "    §l§f游戏玩法：§r塌方跑酷 小心，不要被高空生成的方块砸到\n" +
                        "    §l§f得分条件：§r存活得越久得分越高";
                break;
            case "TrafficLight":
                rule = "    §l§f游戏玩法：§r玩家需注意手中的方块颜色变化，绿色羊毛为绿灯，黄色为黄灯，红色为红灯。\n绿灯，黄灯可行走，若红灯还在行走，则游戏失败，传送至开始点重新开始。\n" +
                        "    §l§f得分条件：§r到达终点越快得分越高，奖励也越丰富。\n" +
                        "    §c§l##提醒：黄灯时要停止一切动作";
                break;
            case "RedAlert":
                rule = "    §l§f游戏玩法：§r游戏场地中的方块会按照 [白->黄->橙->红->消失] 变化，玩家须尽可能的让自己不向下掉落\n" +
                        "    §l§f得分条件：§r存活得越久得分越高";
                break;
            case "SnowSlide":
                rule = "    §l§f游戏玩法：§r不惜一切代价！不要被雪球砸到！及时躲避天上掉下来的雪球！\n" +
                        "    §l§f得分条件：§r存活得越久得分越高";
                break;
            case "AvoidPoison":
                rule = "    §l§f游戏玩法：§r不要被天上掉下来的药水砸到！及时使用你的治疗药水，注意治疗药水是有限的哦！\n" +
                        "    §l§f得分条件：§r存活得越久得分越高";
                break;
            case "CollectOre":
                rule = "    §l§f游戏玩法：§r天上掉馅饼啦！快去收集掉下来的矿石吧！\n" +
                        "    §l§f得分条件：§r矿石越稀有得分越高";
                break;
            case "HelpHen":
                rule = "    §l§f游戏玩法：§rSTEVE的母鸡正在下蛋，请帮助母鸡接下她生的蛋吧！\n" +
                        "    §l§f得分条件：§r收集的蛋越多得分越高";
                break;
            case "HuntingDiamond":
                rule = "    §l§f游戏玩法：§r地图中藏了一些稀有的钻石，请把它找出来吧！（钻石是以掉落物的形式）\n" +
                        "    §l§f得分条件：§r收集钻石越快得分越高";
                break;
            default:
                rule = "";
                break;
        }
        return rule;
    }
}
