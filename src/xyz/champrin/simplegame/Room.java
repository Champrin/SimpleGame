package xyz.champrin.simplegame;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;
import xyz.champrin.simplegame.games.*;
import xyz.champrin.simplegame.schedule.RoomSchedule;

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
    public int xi, xa, yi, ya, zi, za;

    public LinkedHashMap<String, Integer> rank = new LinkedHashMap<>();
    public ArrayList<Player> waitPlayer = new ArrayList<>();
    public ArrayList<Player> gamePlayer = new ArrayList<>();
    public ArrayList<Player> viewPlayer = new ArrayList<>();
    public ArrayList<Player> finishPlayer = new ArrayList<>();
    public int joinGamePlayer = 0;
    public LinkedHashMap<String, String> playerNameTag = new LinkedHashMap<>();
    public Vector3 WaitPos, ViewPos, LeavePos;

    public ArrayList<String> BreakGame = new ArrayList<>(Arrays.asList("OreRace", "OreRace", "SnowballWar", "SnowballWar_2", "BeFast_1", "BeFast_2", "BeFast_4", "Weeding"));
    public ArrayList<String> PlaceGame = new ArrayList<>(Arrays.asList("BeFast_3", "KeepStanding_2", "SnowballWar"));
    public ArrayList<String> DamageGame = new ArrayList<>(Arrays.asList("KeepStanding", "KeepStanding_2", "SnowballWar", "FallingRun"));

    public Room(String roomId, SimpleGame plugin) {
        this.plugin = plugin;
        this.roomId = roomId;
        this.data = plugin.roomInformation.get(roomId);

        this.level = plugin.getServer().getLevelByName((String) data.get("room_world"));
        this.gameType = (String) data.get("gameName");
        switch (gameType) {
            case "OreRace":
                this.GameType = new OreRace(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "OreRace_2":
                this.GameType = new OreRace_2(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "KeepStanding":
            case "KeepStanding_2":
                this.GameType = new KeepStanding(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "SnowballWar":
                this.GameType = new SnowballWar(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "SnowballWar_2":
                this.GameType = new SnowballWar_2(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "Parkour":
                this.GameType = new Parkour(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "MineRun":
                this.GameType = new MineRun(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "BeFast_1":
                this.GameType = new BeFast_1(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "BeFast_2":
                this.GameType = new BeFast_2(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "BeFast_3":
                this.GameType = new BeFast_3(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "BeFast_4":
                this.GameType = new BeFast_4(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
            case "Weeding":
                this.GameType = new Weeding(this);
                GameTask = new RoomSchedule(this, GameType);
                break;
                /*case "MakeItem":
                this.GameType = new MakeItem(this);
               GameTask= new RoomSchedule(this, GameType);
                break;
            case "CollectOre":
                this.GameType = new CollectOre(this);
               GameTask= new RoomSchedule(this, GameType);
                break;
            case "CollectOre_2":
                this.GameType = new CollectOre_2(this);
               GameTask= new RoomSchedule(this, GameType);
                break;
            case "WatchingFeet":
                this.GameType = new WatchingFeet(this);
                GameTask = new RoomSchedule(this, GameType);
                break;

            case "FallingRun":
                this.GameType = new FallingRun(this);
                GameTask= new RoomSchedule_2(this, GameType);
                break;
            case "TrafficLight":
                this.GameType = new TrafficLight(this);
                GameTask= new RoomSchedule_2(this, GameType);
                break;
            case "RedAlert":
                this.GameType = new RedAlert(this);
                GameTask= new RoomSchedule_2(this, GameType);
                break;*/
        }
        this.plugin.getServer().getScheduler().scheduleRepeatingTask(GameTask, 20);

        String[] p1 = ((String) data.get("pos1")).split("\\+");
        String[] p2 = ((String) data.get("pos2")).split("\\+");
        this.xi = (Math.min(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.xa = (Math.max(Integer.parseInt(p1[0]), Integer.parseInt(p2[0])));
        this.yi = (Math.min(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.ya = (Math.max(Integer.parseInt(p1[1]), Integer.parseInt(p2[1])));
        this.zi = (Math.min(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));
        this.za = (Math.max(Integer.parseInt(p1[2]), Integer.parseInt(p2[2])));

        this.ViewPos = getVector3((String) data.get("view_pos"));
        this.WaitPos = getVector3((String) data.get("wait_pos"));
        this.LeavePos = getVector3((String) data.get("leave_pos"));
    }

    public Vector3 getVector3(String pos) {
        String[] p1 = pos.split("\\+");
        return new Vector3(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]) + 2, Integer.parseInt(p1[2]));
    }

    public Vector3 getRandPos(int num)//在游戏区域内随机获取坐标
    {
        int x = xi;
        int z = zi;
        int y = new Random().nextInt(ya - yi + 1 + num) + yi;

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

    public String getPlayerMode(Player p)//获取玩家当前状态
    {
        if (waitPlayer.contains(p)) {
            return "wait";
        } else if (gamePlayer.contains(p)) {
            return "game";
        } else if (viewPlayer.contains(p)) {
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
    public void onChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        if (getPlayerMode(p) != null) {
            event.setCancelled(true);
            arenaMsg(p.getNameTag() + " §e§l>§r " + event.getMessage());
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

    public int getLobbyPlayersNumber() {
        return waitPlayer.size();
    }

    public int getRemainPlayers() {
        return getMaxPlayers() - getLobbyPlayersNumber();
    }

    public void arenaMsg(String msg) {
        for (Player p : getAllPlayers()) {
            p.sendMessage(msg);
        }
    }

    public void arenaTiTle(String msg, String msg1) {
        for (Player p : getAllPlayers()) {
            p.sendTitle(msg, msg1, 2, 20 * 3, 2);
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
    public LinkedHashMap<Player, ArrayList<String>> playerBag = new LinkedHashMap<>();

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
        playerBag.put(gamePlayer, bag);
        gamePlayer.getInventory().clearAll();
    }

    public void loadBag(Player gamePlayer) {
        gamePlayer.getInventory().clearAll();
        ArrayList<String> bag = playerBag.get(gamePlayer);
        for (int i = 0; i < gamePlayer.getInventory().getSize() + 4; i++) {
            String[] a = bag.get(i).split("-");
            Item item = new Item(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
            if (!a[3].equals("null")) {
                CompoundTag tag = Item.parseCompoundTag(hexStringToBytes(a[3]));
                item.setNamedTag(tag);
            }
            gamePlayer.getInventory().setItem(i, item);
        }
        playerBag.remove(gamePlayer);
    }

    public void setToView(Player player) {
        gamePlayer.remove(player);
        viewPlayer.add(player);

        player.setGamemode(3);
        player.setHealth(20);
        player.getFoodData().setLevel(20);
        player.getInventory().clearAll();
        player.removeAllEffects();
        player.getInventory().setItem(8, (Item.get(64, 0, 1)).setCustomName("§c退出游戏"));
        player.teleport(ViewPos);
        player.setSpawn(ViewPos);
        player.sendMessage(">> 你进入观战模式");
    }

    public void joinToRoom(Player p) {
        if (getPlayerMode(p) != null) {
            p.sendMessage(">>  你已经加入一个房间了,请在聊天栏输入“@hub”重试");
            return;
        } else if (waitPlayer.size() >= getMaxPlayers()) {
            p.sendMessage(">>  房间已满");
            return;
        } else if (game == 1) {
            p.sendMessage(">>  房间已开始");
            return;
        }
        Position v3 = Position.fromObject(WaitPos, plugin.getServer().getLevelByName((String) data.get("room_world")));
        saveBag(p);
        p.setGamemode(2);
        p.teleport(v3);

        this.waitPlayer.add(p);
        this.rank.put(p.getName(), 0);

        p.getInventory().setItem(8, (Item.get(64, 0, 1)).setCustomName("§c退出游戏"));

        arenaMsg(">  §a" + p.getName() + "§f加入了房间");
        p.sendMessage("§c若想退出游戏请输入 @hub");
    }

    public void leaveRoom(Player p) {
        waitPlayer.remove(p);
        gamePlayer.remove(p);
        viewPlayer.remove(p);

        rank.remove(p.getName());

        Position v3 = Position.fromObject(LeavePos, plugin.getServer().getLevelByName((String) data.get("leave_pos")));
        p.teleport(v3);
        p.setSpawn(v3);
        p.removeAllEffects();
        this.joinGamePlayer = joinGamePlayer - 1;
        p.getInventory().clearAll();
        arenaMsg(">  §a" + p.getName() + "§f离开了房间");
    }

    public void checkTool() {
        switch (gameType) {
            case "OreRace":
            case "OreRace_2":
            case "BeFast_1":
            case "BeFast_2":
                for (Player player : gamePlayer) {
                    player.getInventory().setItem(0, Item.get(Item.DIAMOND_SHOVEL, 0, 1));
                    player.getInventory().setItem(1, Item.get(Item.DIAMOND_PICKAXE, 0, 1));
                    player.getInventory().setItem(2, Item.get(Item.DIAMOND_AXE, 0, 1));
                    player.getInventory().setItem(3, Item.get(Item.DIAMOND_SWORD, 0, 1));
                }
                break;
            case "SnowballWar":
            case "SnowballWar_2":
            case "Weeding":
                for (Player player : gamePlayer) {
                    player.getInventory().setItem(0, Item.get(Item.DIAMOND_SHOVEL, 0, 1));
                }
                break;
            case "BeFast_3":
                for (Player player : gamePlayer) {
                    player.getInventory().setItem(0, Item.get(Item.GLASS, 0, 64));
                    player.getInventory().setItem(1, Item.get(Item.GLASS, 0, 64));
                    player.getInventory().setItem(2, Item.get(Item.GLASS, 0, 64));
                }
                break;
            case "BeFast_4":
                for (Player player : gamePlayer) {
                    player.getInventory().setItem(0, Item.get(Item.STONE_PICKAXE, 0, 1));
                    player.getInventory().setItem(1, Item.get(Item.IRON_PICKAXE, 0, 1));
                    player.getInventory().setItem(2, Item.get(Item.GOLD_PICKAXE, 0, 1));
                    player.getInventory().setItem(3, Item.get(Item.DIAMOND_PICKAXE, 0, 1));
                }
                break;
            /*
            case "MakeItem":
            case "CollectOre":
            case "CollectOre_2":

            case "FallingRun":
            case "TrafficLight":
            case "RedAlert":
            */
        }
    }

    public void startGame() {
        this.joinGamePlayer = waitPlayer.size();
        for (Player p : waitPlayer) {
            p.getInventory().clearAll();
            p.setGamemode(0);
            gamePlayer.add(p);
            playerNameTag.put(p.getName(), p.getNameTag());
            p.setNameTag("[PLAYER] §f" + p.getName());
        }
        waitPlayer.clear();
        checkTool();
        this.game = 1;
        this.plugin.freeRooms.remove(this);
    }


    public void stopGame() {
        getRichList();
        for (Player p : gamePlayer) {
            p.sendMessage(getRank(p.getName()));
        }
        unsetAllPlayers();
        this.game = 0;
        this.plugin.freeRooms.add(this);
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
            for (Player p : gamePlayer) {
                p.sendMessage(r + " " + map.getKey() + " 分数:" + map.getValue());
            }
        }
    }

    public void severStop() {
        for (Player p : gamePlayer) {
            p.setHealth(20);
            p.getFoodData().sendFoodLevel(20);
            p.getFoodData().sendFoodLevel();
            p.setGamemode(2);
            p.removeAllEffects();
            loadBag(p);
            p.setNameTag(playerNameTag.get(p.getName()));
        }
        for (Player p : viewPlayer) {
            p.setHealth(20);
            p.getFoodData().sendFoodLevel(20);
            p.getFoodData().sendFoodLevel();
            p.setGamemode(2);
            p.removeAllEffects();
            loadBag(p);
            p.setNameTag(playerNameTag.get(p.getName()));
        }
        for (Player p : waitPlayer) {
            p.setHealth(20);
            p.getFoodData().sendFoodLevel(20);
            p.getFoodData().sendFoodLevel();
            p.setGamemode(2);
            p.removeAllEffects();
            loadBag(p);
            p.setNameTag(playerNameTag.get(p.getName()));
        }
        this.gamePlayer.clear();
        this.waitPlayer.clear();
        this.viewPlayer.clear();
        this.joinGamePlayer = 0;
    }

    public void unsetAllPlayers() {

        for (Player p : gamePlayer) {
            Position v3 = Position.fromObject(LeavePos, plugin.getServer().getLevelByName((String) data.get("leave_pos")));
            p.teleport(v3);
            p.setSpawn(v3);
            p.setHealth(20);
            p.getFoodData().sendFoodLevel(20);
            p.getFoodData().sendFoodLevel();
            p.setGamemode(2);
            p.removeAllEffects();
            loadBag(p);
            p.setNameTag(playerNameTag.get(p.getName()));
        }
        this.gamePlayer.clear();
        this.waitPlayer.clear();
        this.viewPlayer.clear();
        this.joinGamePlayer = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTouch(PlayerInteractEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            Player player = event.getPlayer();
            if ("§c退出游戏".equals(player.getInventory().getItemInHand().getCustomName())) {
                player.sendMessage("§c>  §f你已退出游戏！");
                leaveRoom(player);
            }
            event.setCancelled(true);
        }
    }

    /**
     * 玩家退出类事件
     **/
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            leaveRoom(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            leaveRoom(event.getPlayer());
        }
    }

    /**
     * 玩家受伤类事件
     **/
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (getPlayerMode(event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }


    private void recoverPlayer(Player player, String[] p1) {
        Position v3;
        player.setHealth(20);
        player.getFoodData().sendFoodLevel(20);
        player.removeAllEffects();
        v3 = new Position(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]), Integer.parseInt(p1[2]), level);
        player.teleport(v3);
        player.setSpawn(v3);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(EntityDamageEvent event) {
        Entity en = event.getEntity();
        if (en instanceof Player) {
            if (getPlayerMode((Player) en) != null) {
                if (getPlayerMode((Player) en).equals("game")) {
                    if (!DamageGame.contains(gameType)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            if (getPlayerMode(event.getPlayer()).equals("game")) {
                if (!BreakGame.contains(gameType)) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            if (getPlayerMode(event.getPlayer()).equals("game")) {
                if (!PlaceGame.contains(gameType)) {
                    event.setCancelled(true);
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {
        if (getPlayerMode(event.getPlayer()) != null) {
            if (!getPlayerMode(event.getPlayer()).equals("game")) {
                event.setCancelled(true);
            }
        }
    }


}
