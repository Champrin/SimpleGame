package net.createlight.champrin.simplegame;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.*;

public class SimpleGame extends PluginBase implements Listener {

    public Config config, player;

    public String COMMAND_NAME = "sgmap";
    public String PLUGIN_NAME = "SimpleGame";
    public String PLUGIN_No = "3";
    public String GAME_NAME = "小游戏";
    public String PREFIX = "§a§l==> §c" + GAME_NAME + "§a <==§r";

    public int GameMenuId = 100002;

    public LinkedHashMap<Integer, String> GameMap = new LinkedHashMap<Integer, String>() {{
        put(1, "BeFast_1");
        put(2, "BeFast_2");
        put(3, "BeFast_3");
        put(4, "BeFast_4");
        put(5, "HuntingDiamond");
        put(6, "KeepStanding");
        put(7, "KeepStanding_2");
        //put(8, "MakeItem");
        put(9, "MineRun");
        put(10, "OreRace");
        put(11, "OreRace_2");
        put(12, "Parkour");
        put(13, "SnowballWar");
        put(14, "SnowballWar_2");
        put(15, "Weeding");

        put(16, "AvoidPoison");
        put(17, "CollectOre");
        put(18, "FallingRun");
        put(19, "HelpHen");
        put(20, "RedAlert");
        put(21, "SnowSlide");
        put(22, "TrafficLight");
        put(23, "WatchingFeet");
        //put(24, "SurvivalWar");
        //put(25, "FightForColour");
        //put(26, "ColourShot");
    }};

    public LinkedHashMap<String, LinkedHashMap<String, Object>> roomInformation = new LinkedHashMap<>();//房间基本信息
    public LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();//开启的房间信息
    public ArrayList<Room> freeRooms = new ArrayList<>();
    public LinkedHashMap<String, LinkedHashMap<String, String>> setters = new LinkedHashMap<>();
    public LinkedHashMap<Player, Room> gamePlayer = new LinkedHashMap<>(), waitPlayer = new LinkedHashMap<>(), viewPlayer = new LinkedHashMap<>();

    //public int OwnPoint, TeamPoint, WinPoint;

    private static SimpleGame instance;

    public static SimpleGame getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        long start = new Date().getTime();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().info("§d加载中。。。§e|作者：Champrin");
        this.getLogger().info("§dLoading...§e|Author：Champrin");
        this.getLogger().info("§e==> Champrin的第§c" + PLUGIN_No + "§e款小游戏 " + GAME_NAME + " " + PLUGIN_NAME + "！");
        this.getLogger().info("§e==> Champrin's No.§c" + PLUGIN_No + "§e games " + GAME_NAME + " " + PLUGIN_NAME + "！");

        this.LoadConfig();
        this.LoadRoomConfig();

        this.getLogger().info("§d已加载完毕。。。");
        this.getLogger().info("§dFinished loading...");
        this.getLogger().info("§e加载耗时" + (new Date().getTime() - start) + "毫秒");
        this.getLogger().info("§eLoading time" + (new Date().getTime() - start) + "millisecond");
    }

    public void setConfigData() {
        /*if (this.config.get("占领加分") == null) {
            this.config.set("占领加分", 10);
            this.config.set("团队加分", 2);
            this.config.set("最终胜利加分", 10);
            this.config.save();
        }
        this.OwnPoint = (int) this.config.get("占领加分");
        this.TeamPoint = (int) this.config.get("团队加分");
        this.WinPoint = (int) this.config.get("最终胜利加分");*/

    }

    public String getGameName() {
        return this.GAME_NAME;
    }

    public Room getPlayerRoom(Player p) {
        for (Map.Entry<String, Room> map : this.rooms.entrySet()) {
            Room room = map.getValue();
            if (room.gamePlayer.contains(p) || room.waitPlayer.contains(p) || room.viewPlayer.contains(p)) {
                return room;
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        //给每个房间结算结果
        if (!this.rooms.isEmpty()) {
            for (Map.Entry<String, Room> map : rooms.entrySet()) {
                map.getValue().severStop();
            }
        }
    }

    //判断房间是否存在
    public boolean isRoomSet(String roomName) {
        return this.rooms.containsKey(roomName);
    }

    public Room getRoom(String roomName) {
        return this.rooms.getOrDefault(roomName, null);
    }

    public int getAllPlayerCount() {
        return this.gamePlayer.size() + this.waitPlayer.size() + this.viewPlayer.size();
    }

    public int getAllRoomCount() {
        return this.rooms.size();
    }

    public int getAllFreeRoomCount() {
        return this.freeRooms.size();
    }

    public String getGAME_NAME() {
        return this.GAME_NAME;
    }


    public void LoadConfig() {
        this.getLogger().info("-配置文件加载中...");
        this.getLogger().info("-Configuration file loading...");

        if (!new File(this.getDataFolder() + "/config.yml").exists()) {
            this.saveResource("config.yml", false);
        }
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
        this.gui_title=config.getString("gui-title");
        this.random_join =config.getString("random-join");
        this.each_room=config.getString("each-room");
        this.room_state_wait =config.getString("room-state-wait");
        this.room_state_start=config.getString("room-state-start");


        if (!new File(this.getDataFolder() + "/player.yml").exists()) {
            this.saveResource("player.yml", false);
        }
        this.player = new Config(this.getDataFolder() + "/player.yml", Config.YAML);

        this.setConfigData();
        File file = new File(this.getDataFolder() + "/Room/");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                this.getServer().getLogger().info("文件夹创建失败");
                this.getServer().getLogger().info("Folder creation failed");
            }
        }
    }

    public void LoadRoomConfig() {
        this.getLogger().info("-房间信息加载中...");
        this.getLogger().info("-Room information loading...");
        File file = new File(this.getDataFolder() + "/Room/");
        File[] files = file.listFiles();
        if (files != null) {
            for (File FILE : files) {
                if (FILE.isFile()) {
                    Config room = new Config(FILE, Config.YAML);
                    String FileName = FILE.getName().substring(0, FILE.getName().lastIndexOf("."));
                    this.setRoomData(room, FileName);
                }
            }
        }
        this.getLogger().info("-房间信息加载完毕...");
        this.getLogger().info("-Room information loaded...");
    }

    public void setRoomData(Config room, String FileName) {
        this.roomInformation.put(FileName, new LinkedHashMap<>(room.getAll()));
        if ((Boolean) room.get("state")) {
            Room game = new Room(FileName, this);
            this.rooms.put(FileName, game);
            this.freeRooms.add(game);
            this.getServer().getPluginManager().registerEvents(game, this);
            if (!((Boolean) game.data.get("arena"))) {
                game.GameType.madeArena();
            }
            this.getLogger().info("   房间§b" + FileName + "§r加载完成");
            this.getLogger().info("   Room§b" + FileName + "§rload complete");
        }
    }

    public Config getRoomConfig(String roomName) {
        return new Config(this.getDataFolder() + "/Room/" + roomName + ".yml", Config.YAML);
    }

    //判断房间是否存在
    public boolean RoomExist(String roomName) {
        return this.roomInformation.containsKey(roomName);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(PlayerJoinEvent event) {
        setters.remove(event.getPlayer().getName());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        if (getPlayerRoom(p) == null) return;
        if (event.getMessage().contains("@hub")) {
            event.setCancelled(true);
            p.sendMessage(config.getString("quit-game-player"));
            this.getPlayerRoom(p).leaveRoom(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        String name = p.getName();
        if (setters.containsKey(name)) {
            event.setCancelled(true);
            Block b = event.getBlock();

            String room_name = setters.get(name).get("room_name");
            Config room = this.getRoomConfig(room_name);

            String xyz = Math.round(Math.floor(b.x)) + "+" + Math.round(Math.floor(b.y)) + "+" + Math.round(Math.floor(b.z));
            if (setters.get(name).containsKey("step")) {
                int step = Integer.parseInt(setters.get(name).get("step"));
                switch (step) {
                    case 1:
                        room.set("wait_pos", xyz);//等待点
                        setters.get(name).put("step", step + 1 + "");
                        p.sendMessage("§c>  §f请设置§a区域要求点1");
                        p.sendMessage("§7>  §fPlease set the §a area requirement point 1");
                        break;
                    case 2:
                        room.set("pos1", xyz);
                        setters.get(name).put("step", step + 1 + "");
                        p.sendMessage("§c>  §f请设置§a区域要求点2");
                        p.sendMessage("§7>  Please set the area requirement point 2");
                        break;
                    case 3:
                        room.set("pos2", xyz);
                        String[] p1 = ((String) room.get("pos1")).split("\\+");
                        room.set("center_pos",
                                (Integer.parseInt(p1[0]) + Math.round(Math.floor(b.x))) / 2 + "+"
                                        + (Integer.parseInt(p1[1]) + Math.round(Math.floor(b.y))) / 2 + "+"
                                        + (Integer.parseInt(p1[2]) + Math.round(Math.floor(b.z))) / 2);
                        room.set("room_world", b.level.getFolderName());
                        setters.get(name).put("step", step + 1 + "");
                        p.sendMessage("§c>  §f请设置§a观战点");
                        p.sendMessage("§7>  Please set viewing point");
                        break;
                    case 4:
                        room.set("view_pos", xyz);
                        setters.get(name).put("step", step + 1 + "");
                        p.sendMessage("§c>  §c请设置§a离开点");
                        p.sendMessage("§7>  Please set departure point");
                        break;
                    case 5:
                        room.set("leave_pos", xyz);
                        room.set("leave_world", b.level.getFolderName());
                        room.set("arena", false);
                        room.set("state", true);
                        setRoomData(room, room_name);
                        setters.remove(name);
                        p.sendMessage("§a>>  §f房间设置已完成");
                        p.sendMessage("§7>>  Room setup is complete");
                        break;
                }
            } else {
                String index = setters.get(name).get("setc");
                room.set(index, xyz);
                switch (index) {
                    case "wait_pos":
                    case "pos1":
                    case "pos2":
                        room.set("room_world", b.level.getFolderName());
                        break;
                    case "leave_pos":
                        room.set("leave_world", b.level.getFolderName());
                        break;
                }
                room.set("arena", false);
                room.set("state", true);
                rooms.remove(room_name);
                roomInformation.remove(room_name);
                setRoomData(room, room_name);
                setters.remove(name);
                p.sendMessage("§a>>  §f设置成功");
                p.sendMessage("§7>>  Set successfully");
            }
            room.save();
        }
    }

    public String getStringFormList(LinkedHashMap<Integer, String> MAP) {
        String allGameNameType = "";
        int i = 0;
        for (Map.Entry<Integer, String> map : MAP.entrySet()) {
            allGameNameType = allGameNameType + "[" + map.getKey() + "]" + map.getValue() + ",";
            i = i + 1;
            if (i == 3) {
                i = 0;
                allGameNameType = allGameNameType + "\n";
            }
        }
        return allGameNameType;
    }

    public void Op_CHelpMessage(CommandSender sender) {
        sender.sendMessage(">  /" + COMMAND_NAME + " add [房间名RoomName] [游戏类型GameType] ------ §d创建一个新房间(Create a new room)");
        sender.sendMessage(">  游戏类型(Game type): " + getStringFormList(GameMap));
        sender.sendMessage(">  /" + COMMAND_NAME + " set [房间名RoomName] ------ §d设置一个房间(Set up a room)");
        sender.sendMessage(">  /" + COMMAND_NAME + " del [房间名RoomName] ------ §d删除一个房间(Delete a room)");
        sender.sendMessage(">  /" + COMMAND_NAME + " start [房间名RoomName] ------ §d强行开始一个房间的游戏(Forcibly start a room game)");
        sender.sendMessage(">  /" + COMMAND_NAME + " stop [房间名RoomName] ------ §d强行停止一个房间的游戏(Forcibly stop a room game)");
        sender.sendMessage(">  /" + COMMAND_NAME + " setc [房间名RoomName] [参数序号Parameter serial number] *[设置值Set value]------ §d设置一个房间的配置文件的信息(Set the information of a room's profile)");
        sender.sendMessage(">  参数序号(Parameter serial number): " + getStringFormList(new LinkedHashMap<Integer, String>() {{
            put(1, "gameTime");
            put(2, "startTime");
            put(3, "maxPlayers");
            put(4, "minPlayers");
            put(5, "wait_pos");
            put(6, "pos1");
            put(7, "pos2");
            put(8, "view_pos");
            put(9, "leave_pos");
        }}));

    }

    public void CHelpMessage(CommandSender sender) {
        sender.sendMessage(">  §f==========" + PLUGIN_NAME + "§f==========§r");
        sender.sendMessage(">  /" + COMMAND_NAME + " join ------ §d加入游戏(Join the game)");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (COMMAND_NAME.equals(command.getName())) {
            if (args.length < 1) {
                this.CHelpMessage(sender);
                if (sender.isOp()) {
                    this.Op_CHelpMessage(sender);
                }
            } else {
                switch (args[0]) {
                    case "join":
                        if (sender instanceof Player) {
                            sendMenu((Player) sender);
                        }
                        break;
                    case "set":
                        if (sender instanceof Player) {
                            if (args.length < 2) {
                                sender.sendMessage(">  参数不足");
                                sender.sendMessage(">  Insufficient parameters");
                                break;
                            }
                            if (!this.RoomExist(args[1])) {
                                sender.sendMessage(">  房间不存在");
                                sender.sendMessage(">  Room does not exist");
                                break;
                            }
                            if (this.isRoomSet(args[1])) {
                                Room a = this.rooms.get(args[1]);
                                if (a.game != 0 || !a.gamePlayer.isEmpty()) {
                                    sender.sendMessage(">  房间正在游戏中");
                                    sender.sendMessage(">  The room is playing");
                                    break;
                                }
                            }
                            LinkedHashMap<String, String> list = new LinkedHashMap<>();
                            list.put("room_name", args[1]);
                            list.put("step", 1 + "");
                            setters.put(sender.getName(), list);
                            sender.sendMessage("房间" + args[1] + "正在设置 \n§c>  §f请破坏方块设置§a等待点");
                            sender.sendMessage("Room" + args[1] + "is setting \n§c>  §fPlease destroy the box settings§aWaiting point");
                        } else {
                            sender.sendMessage(">  请在游戏中运行");
                            sender.sendMessage(">  Please run in the game");
                        }
                        break;
                    case "setc":
                        if (sender instanceof Player) {
                            if (args.length < 2) {
                                sender.sendMessage(">  参数不足");
                                sender.sendMessage(">  Insufficient parameters");
                                break;
                            }
                            if (!this.isRoomSet(args[1])) {
                                sender.sendMessage(">  房间不存在或未开启");
                                sender.sendMessage(">  The room does not exist or is not opened");
                                break;
                            }
                            if (this.isRoomSet(args[1])) {
                                Room a = this.rooms.get(args[1]);
                                if (a.game != 0 || !a.gamePlayer.isEmpty()) {
                                    sender.sendMessage(">  房间正在游戏中");
                                    sender.sendMessage(">  The room is playing");
                                    break;
                                }
                            }
                            LinkedHashMap<Integer, String> typeMap = new LinkedHashMap<Integer, String>() {{
                                put(1, "gameTime");
                                put(2, "startTime");
                                put(3, "maxPlayers");
                                put(4, "minPlayers");
                                put(5, "wait_pos");
                                put(6, "pos1");
                                put(7, "pos2");
                                put(8, "view_pos");
                                put(9, "leave_pos");
                            }};
                            int type = Integer.parseInt(args[2]);
                            if (!typeMap.containsKey(type)) {
                                sender.sendMessage(">  序号输入错误");
                                sender.sendMessage(">  Serial number input error");
                                break;
                            }
                            if (type <= 4) {
                                Config c = getRoomConfig(args[1]);
                                c.set(typeMap.get(type), Integer.parseInt(args[3]));
                                c.save();
                                rooms.remove(args[1]);
                                roomInformation.remove(args[1]);
                                setRoomData(c, args[1]);
                                break;
                            } else {
                                LinkedHashMap<String, String> list = new LinkedHashMap<>();
                                list.put("room_name", args[1]);
                                list.put("setc", typeMap.get(type));
                                setters.put(sender.getName(), list);
                                sender.sendMessage("房间" + args[1] + "正在设置 \n§c>  §f请破坏方块设置此点");
                                sender.sendMessage("Room" + args[1] + "is setting \n§c>  §fPlease set this point");
                            }
                        } else {
                            sender.sendMessage(">  请在游戏中运行");
                            sender.sendMessage(">  Please run in the game");
                        }
                        break;
                    case "add":
                        if (args.length < 3) {
                            sender.sendMessage(">  参数不足");
                            sender.sendMessage(">  Insufficient parameters");
                            break;
                        }
                        if (this.RoomExist(args[1])) {
                            sender.sendMessage(">  房间已存在");
                            sender.sendMessage(">  Room already exists");
                            break;
                        }
                        Config a = new Config(this.getDataFolder() + "/Room/" + args[1] + ".yml", Config.YAML);
                        a.set("state", false);
                        a.set("room_world", null);
                        a.set("gameTime", 120);
                        a.set("startTime", 30);
                        a.set("maxPlayers", 6);
                        a.set("minPlayers", 2);
                        a.set("gameName", GameMap.get(Integer.parseInt(args[2])));
                        a.save();
                        roomInformation.put(args[1], (LinkedHashMap<String, Object>) a.getAll());
                        sender.sendMessage(">  房间" + args[1] + "成功创建");
                        sender.sendMessage(">  Room" + args[1] + "successfully created");
                        break;
                    case "del":
                        if (args.length < 2) {
                            sender.sendMessage(">  参数不足");
                            sender.sendMessage(">  Insufficient parameters");
                            break;
                        }
                        if (!this.RoomExist(args[1])) {
                            sender.sendMessage(">  房间不存在");
                            sender.sendMessage(">  Room does not exist");
                            break;
                        }
                        boolean file = new File(this.getDataFolder() + "/Room/" + args[1] + ".yml").delete();
                        if (file) {
                            if (rooms.containsKey(args[1])) {
                                rooms.get(args[1]).stopGame();
                                rooms.remove(args[1]);
                            }
                            this.setters.remove(sender.getName());
                            roomInformation.remove(args[1]);
                            sender.sendMessage(">  房间" + args[1] + "已成功删除");
                            sender.sendMessage(">  Room" + args[1] + "successfully deleted");
                        } else {
                            sender.sendMessage(">  房间" + args[1] + "删除失败");
                            sender.sendMessage(">  Room" + args[1] + "deletion failed");
                        }
                        break;
                    case "start":
                        if (args.length < 2) {
                            sender.sendMessage(">  参数不足");
                            sender.sendMessage(">  Insufficient parameters");
                            break;
                        }
                        if (!this.isRoomSet(args[1])) {
                            sender.sendMessage(">  房间不存在或未开启");
                            sender.sendMessage(">  The room does not exist or is not opened");
                            break;
                        }
                        rooms.get(args[1]).startGame();
                        break;
                    case "stop":
                        if (args.length < 2) {
                            sender.sendMessage(">  参数不足");
                            sender.sendMessage(">  Insufficient parameters");
                            break;
                        }
                        if (!this.isRoomSet(args[1])) {
                            sender.sendMessage(">  房间不存在或未开启");
                            sender.sendMessage(">  The room does not exist or is not opened");
                            break;
                        }
                        rooms.get(args[1]).stopGame();
                        break;
                    case "help":
                    default:
                        this.CHelpMessage(sender);
                        if (sender.isOp()) {
                            this.Op_CHelpMessage(sender);
                        }
                        break;
                }
            }
        }
        return true;
    }

    private String random_join,each_room,room_state_wait,room_state_start,gui_title;

    public void sendMenu(Player player) {
        FormWindowSimple form = new FormWindowSimple("§c§fSimpleGame", gui_title);
        form.addButton(new ElementButton(random_join, new ElementButtonImageData("path", "textures/blocks/bedrock.png")));
        for (Room room : rooms.values()) {
            String state = each_room.replaceAll("%ROOMID%",room.roomId).replaceAll("%GAMETYPE%",room.gameType);
            if (room.game == 0) {
                state = state + room_state_wait.replaceAll("%JOINED%",String.valueOf(room.getWaitingPlayersNumber())).replaceAll("%MAXPLAYER%",String.valueOf(room.getMaxPlayers()));
            } else {
                state = state + room_state_start;
            }
            form.addButton(new ElementButton(state));
        }
        player.showFormWindow(form, GameMenuId);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player p = event.getPlayer();
        if (GameMenuId == event.getFormID()) {
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if (response == null) return;
            int clickedButtonId = response.getClickedButtonId();
            if (clickedButtonId != 0) {
                Room room = getRoomByIndex(clickedButtonId);
                if (room != null) {
                    room.joinToRoom(p);
                }
            } else {
                if (this.rooms.size() <= 0) {
                    p.sendMessage(config.getString("no-room"));
                    return;
                } else if (this.freeRooms.size() <= 0) {
                    p.sendMessage(config.getString("no-freeroom"));
                    return;
                }
                int a = new Random().nextInt(freeRooms.size());
                Room room = freeRooms.get(a);
                if (room != null) {
                    room.joinToRoom(p);
                }
            }
        }
    }

    public Room getRoomByIndex(int index) {
        int i = 1;
        for (Room room : rooms.values()) {
            if (i == index) {
                return room;
            }
            i = i + 1;
        }
        return null;
    }

}
