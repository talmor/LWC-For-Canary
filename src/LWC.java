import com.griefcraft.logging.Logger;
import com.griefcraft.model.Entity;
import com.griefcraft.sql.Database;
import com.griefcraft.sql.MemDB;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.Config;
import com.griefcraft.util.ConfigValues;
import com.griefcraft.util.Performance;
import com.griefcraft.util.Updater;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class LWC extends Plugin {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());
    private LWCListener listener;
    private PhysDB physicalDatabase;
    private MemDB memoryDatabase;
    private Updater updater;
    private List<LWC_Command> commands;

    public boolean canAccessChest(Player player, Entity chest) {
        if (chest == null) {
            return true;
        }

        if (isAdmin(player)) {
            return true;
        }

        if (isMod(player)) {
            Player chestOwner = etc.getDataSource().getPlayer(chest.getOwner());

            if (chestOwner == null) {
                return true;
            }

            if (!isAdmin(chestOwner)) {
                return true;
            }
        }

        switch (chest.getType()) {
        case 0:
            return true;
        case 1:
            return this.memoryDatabase.hasAccess(player.getName(), chest);
        case 2:
            PhysDB instance = this.physicalDatabase;
            if (!player.getName().equalsIgnoreCase(chest.getOwner()))
                if ((instance.getPrivateAccess(1, chest.getID(), new String[] { player.getName() }) == -1)
                        && (instance.getPrivateAccess(0, chest.getID(), player.getGroups()) == -1))
                    return false;
            return true;
        }

        return false;
    }

    public boolean canAccessChest(Player player, int x, int y, int z) {
        String world = player.getWorld().getType().name();
        return canAccessChest(player, this.physicalDatabase.loadProtectedEntity(world, x, y, z));
    }

    public boolean canAdminChest(Player player, Entity chest) {
        if (chest == null) {
            return true;
        }

        if (isAdmin(player)) {
            return true;
        }

        switch (chest.getType()) {
        case 0:
            return player.getName().equalsIgnoreCase(chest.getOwner());
        case 1:
            return (player.getName().equalsIgnoreCase(chest.getOwner()))
                    && (this.memoryDatabase.hasAccess(player.getName(), chest));
        case 2:
            PhysDB instance = this.physicalDatabase;
            if (!player.getName().equalsIgnoreCase(chest.getOwner()))
                if ((instance.getPrivateAccess(1, chest.getID(), new String[] { player.getName() }) != 1)
                        && (instance.getPrivateAccess(0, chest.getID(), player.getGroups()) != 1))
                    return false;
            return true;
        }

        return false;
    }

    public void disable() {
        log("Stopping LWC");
        Config.getInstance().save();
        Config.destroy();

        etc.getInstance().removeCommand("/lwc");
        try {
            this.physicalDatabase.connection.close();
            this.memoryDatabase.connection.close();

            this.physicalDatabase = null;
            this.memoryDatabase = null;
        } catch (Exception localException) {
        }
    }

    public Updater getUpdater() {
        return this.updater;
    }

    public void enable() {
        try {
            log("Initializing LWC");

            Performance.init();

            this.commands = new ArrayList();
            this.physicalDatabase = new PhysDB();
            this.memoryDatabase = new MemDB();

            log("Binding commands");
            loadCommands();
            etc.getInstance().addCommand("/lwc", "- Chest/Furnace protection");

            Config.init();

            this.updater = new Updater();

            this.updater.check();
            this.updater.update();

            if ((ConfigValues.AUTO_UPDATE.getBool()) && (this.updater.checkDist())) {
                log("Reloading LWC");
                etc.getLoader().reloadPlugin("LWC");
                return;
            }

            log("LWC config:      lwc.properties");
            log("SQLite jar:      lib/sqlite.jar");
            log("SQLite library:  lib/" + this.updater.getOSSpecificFileName());
            log("DB location:     " + this.physicalDatabase.getDatabasePath());

            log("Opening sqlite databases");

            this.physicalDatabase.connect();
            this.memoryDatabase.connect();

            this.physicalDatabase.load();
            this.memoryDatabase.load();

            log("Protections:\t" + this.physicalDatabase.entityCount());
            log("Limits:\t\t" + this.physicalDatabase.limitCount());

            if (ConfigValues.CUBOID_SAFE_AREAS.getBool()) {
                log("Only allowing chests to be protected in Cuboid-protected zones that DO NOT have PvP toggled!");
            }

            Config.getInstance().save();
        } catch (Exception e) {
            log("Error occured while initializing LWC : " + e.getMessage());
            e.printStackTrace();
            log("LWC will now be disabled");
            etc.getLoader().disablePlugin("LWC");
        }
    }

    public String encrypt(String plaintext) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update(plaintext.getBytes("UTF-8"));

            byte[] raw = md.digest();
            return byteArray2Hex(raw);
        } catch (Exception localException) {
        }
        return "";
    }

    public boolean enforceChestLimits(Player player) {
        if (isAdmin(player)) {
            return false;
        }

        int userLimit = this.physicalDatabase.getUserLimit(player.getName());

        if (userLimit != -1) {
            int chests = this.physicalDatabase.getChestCount(player.getName());

            if (chests >= userLimit) {
                player.sendMessage("§4You have exceeded the amount of chests you can lock!");
                return true;
            }
        } else {
            List<String> inheritedGroups = new ArrayList<String>();
            String groupName = player.getGroups().length > 0 ? player.getGroups()[0] : etc.getInstance()
                    .getDefaultGroup().Name;

            inheritedGroups.add(groupName);
            String[] inherited;
            while (true) {
                Group group = etc.getDataSource().getGroup(groupName);

                if (group == null) {
                    break;
                }
                inherited = group.InheritedGroups;

                if ((inherited == null) || (inherited.length == 0)) {
                    break;
                }
                groupName = inherited[0];

                for (String _groupName : inherited) {
                    _groupName = _groupName.trim();

                    if (_groupName.isEmpty()) {
                        continue;
                    }
                    inheritedGroups.add(_groupName);
                }
            }

            for (String group : inheritedGroups) {
                int groupLimit = this.physicalDatabase.getGroupLimit(group);

                if (groupLimit != -1) {
                    int chests = this.physicalDatabase.getChestCount(player.getName());

                    if (chests >= groupLimit) {
                        player.sendMessage("§4You have exceeded the amount of chests you can lock!");
                        return true;
                    }

                    return false;
                }
            }
        }

        return false;
    }

    public List<LWC_Command> getCommands() {
        return this.commands;
    }

    public List<ComplexBlock> getEntitySet(World world, int x, int y, int z) {
        List<ComplexBlock> entities = new ArrayList<ComplexBlock>(2);

        // ComplexBlock baseBlock = etc.getServer().getComplexBlock(x, y, z);
        ComplexBlock baseBlock = world.getComplexBlock(x, y, z);
        int dev = -1;
        boolean isXDir = true;

        entities = _validateChest(entities, baseBlock);
        while (true) {
            // ComplexBlock block = etc.getServer().getComplexBlock(x + (isXDir
            // ? dev : 0), y, z + (isXDir ? 0 : dev));
            ComplexBlock block = world.getComplexBlock(x + (isXDir ? dev : 0), y, z + (isXDir ? 0 : dev));
            entities = _validateChest(entities, block);

            if (dev == 1) {
                if (!isXDir)
                    break;
                isXDir = false;
                dev = -1;
                continue;
            }

            dev = 1;
        }

        return entities;
    }

    public MemDB getMemoryDatabase() {
        return this.memoryDatabase;
    }

    public PhysDB getPhysicalDatabase() {
        return this.physicalDatabase;
    }

    public int getPlayerDropTransferTarget(String player) {
        String rawTarget = this.memoryDatabase.getModeData(player, "dropTransfer");
        try {
            int ret = Integer.parseInt(rawTarget.substring(1));
            return ret;
        } catch (Throwable localThrowable) {
        }
        return -1;
    }

    public void initialize() {
        if (!Database.isConnected()) {
            return;
        }

        log("Registering hooks");

        this.listener = new LWCListener(this);

        registerHook(PluginLoader.Hook.DISCONNECT);
        registerHook(PluginLoader.Hook.COMMAND);

        registerHook(PluginLoader.Hook.BLOCK_BROKEN);
        registerHook(PluginLoader.Hook.BLOCK_DESTROYED);
        registerHook(PluginLoader.Hook.OPEN_INVENTORY);
        registerHook(PluginLoader.Hook.EXPLODE);
        registerHook(PluginLoader.Hook.ITEM_DROP);
    }

    public boolean isAdmin(Player player) {
        return player.canUseCommand("/lwcadmin");
    }

    public boolean isMod(Player player) {
        return player.canUseCommand("/lwcmod");
    }

    public boolean isModeBlacklisted(String mode) {
        String blacklistedModes = ConfigValues.BLACKLISTED_MODES.getString();

        if (blacklistedModes.isEmpty()) {
            return false;
        }

        String[] modes = blacklistedModes.split(",");

        for (String _mode : modes) {
            if (mode.equalsIgnoreCase(_mode)) {
                return true;
            }
        }

        return false;
    }

    public void log(String str) {
        this.logger.log(str);
    }

    public boolean notInPersistentMode(String player) {
        return !this.memoryDatabase.hasMode(player, "persist");
    }

    public boolean isPlayerDropTransferring(String player) {
        return (this.memoryDatabase.hasMode(player, "dropTransfer"))
                && (this.memoryDatabase.getModeData(player, "dropTransfer").startsWith("t"));
    }

    public void sendFullHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage("§2Welcome to LWC, a Protection mod");
        player.sendMessage(" ");
        player.sendMessage("§a/lwc -c - View creation help");
        player.sendMessage("§a/lwc -c <public|private|password>");
        player.sendMessage("§a/lwc -m - Modify an existing private protection");
        player.sendMessage("§a/lwc -u - Unlock a password protected entity");
        player.sendMessage("§a/lwc -i  - View information on a protected Chest or Furnace");
        player.sendMessage("§a/lwc -r <chest|furnace|modes>");

        player.sendMessage("§a/lwc -p <persist|droptransfer>");

        if (isAdmin(player)) {
            player.sendMessage("");
            player.sendMessage("§4/lwc admin - Admin functions");
        }
    }

    public boolean isInCuboidSafeZone(Player player) {
        if (!ConfigValues.CUBOID_SAFE_AREAS.getBool()) {
            return false;
        }

        try {
            Plugin cuboidPlugin = etc.getLoader().getPlugin("CuboidPlugin");

            if (cuboidPlugin == null) {
                player.sendMessage("CuboidPlugin is not activated");
                return false;
            }

            Class cuboidClass = cuboidPlugin.getClass().getClassLoader().loadClass("CuboidAreas");

            Method findCuboidArea = cuboidClass.getMethod("findCuboidArea", new Class[] { Integer.TYPE, Integer.TYPE,
                    Integer.TYPE });

            Object cuboidC = findCuboidArea.invoke(
                    null,
                    new Object[] { Integer.valueOf((int) player.getX()), Integer.valueOf((int) player.getY()),
                            Integer.valueOf((int) player.getZ()) });

            if (cuboidC != null) {
                Field pvp = cuboidC.getClass().getDeclaredField("PvP");
                pvp.setAccessible(true);

                boolean isPvP = pvp.getBoolean(cuboidC);

                return isPvP;
            }

            Class cuboidPluginClass = cuboidPlugin.getClass().getClassLoader().loadClass("CuboidPlugin");

            Field globalDisablePvP = cuboidPluginClass.getDeclaredField("globalDisablePvP");
            globalDisablePvP.setAccessible(true);

            boolean isPvP = !globalDisablePvP.getBoolean(null);

            return isPvP;
        } catch (Exception e) {
        }
        return false;
    }

    public void sendPendingRequest(Player player) {
        player.sendMessage("§4You already have a pending chest request.");
        player.sendMessage("§4To remove it, type /lwc free pending");
    }

    public void sendSimpleUsage(Player player, String command) {
        player.sendMessage("§4Usage:§6 " + command);
    }

    private List<ComplexBlock> _validateChest(List<ComplexBlock> entities, ComplexBlock block) {
        if (block == null) {
            return entities;
        }

        if (entities.size() > 2) {
            return entities;
        }

        if ((block instanceof Furnace)) {
            if (entities.size() == 0) {
                if (!entities.contains(block)) {
                    entities.add(block);
                }

            }

            return entities;
        }
        if (entities.size() == 1) {
            ComplexBlock other = (ComplexBlock) entities.get(0);

            if ((!(other instanceof Chest)) && (!(other instanceof DoubleChest))) {
                return entities;
            }
        }

        if (!entities.contains(block)) {
            entities.add(block);
        }

        return entities;
    }

    private String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", new Object[] { Byte.valueOf(b) });
        }
        return formatter.toString();
    }

    private void loadCommands() {
        registerCommand(Command_Admin.class);
        registerCommand(Command_Create.class);
        registerCommand(Command_Free.class);
        registerCommand(Command_Info.class);
        registerCommand(Command_Modes.class);
        registerCommand(Command_Modify.class);
        registerCommand(Command_Unlock.class);
    }

    private void registerCommand(Class<?> clazz) {
        try {
            LWC_Command command = (LWC_Command) clazz.newInstance();
            this.commands.add(command);
            log("Loaded command : " + clazz.getSimpleName());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerHook(PluginLoader.Hook hook) {
        registerHook(hook, PluginListener.Priority.MEDIUM);
    }

    private void registerHook(PluginLoader.Hook hook, PluginListener.Priority priority) {
        // log("LWCListener -> " + hook.toString());
        etc.getLoader().addListener(hook, this.listener, this, priority);
    }
}
