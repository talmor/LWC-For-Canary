import com.griefcraft.model.Action;
import com.griefcraft.model.Entity;
import com.griefcraft.sql.MemDB;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.ConfigValues;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class LWCListener extends PluginListener {
    private final LWC lwc;
    private static final int BLAST_RADIUS = 4;
    public boolean debugMode = false;
    private PhysDB physicalDatabase;
    private MemDB memoryDatabase;

    public LWCListener(LWC paramLWC) {
        this.lwc = paramLWC;
        this.physicalDatabase = paramLWC.getPhysicalDatabase();
        this.memoryDatabase = paramLWC.getMemoryDatabase();
    }

    public boolean onBlockBreak(Player paramPlayer, Block paramBlock) {

        if (!isProtectable(paramBlock)) {
            return false;
        }
        int worldID = paramPlayer.getWorld().getType().getId();

        List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(), paramBlock.getX(),
                paramBlock.getY(), paramBlock.getZ());
        boolean bool1 = true;
        boolean bool2 = true;
        Entity localEntity = null;

        for (ComplexBlock localComplexBlock : localList) {
            if (localComplexBlock == null) {
                continue;
            }
            Entity entity = this.physicalDatabase.loadProtectedEntity(worldID, localComplexBlock.getX(),
                    localComplexBlock.getY(), localComplexBlock.getZ());

            if (entity == null) {
                continue;
            }
            localEntity = entity;
            bool1 = this.lwc.canAccessChest(paramPlayer, localEntity);
            bool2 = this.lwc.canAdminChest(paramPlayer, localEntity);
        }

        if ((bool1) && (localEntity != null) && (bool2)) {
            this.physicalDatabase.unregisterProtectedEntity(worldID, localEntity.getX(), localEntity.getY(),
                    localEntity.getZ());
            this.physicalDatabase.unregisterProtectionRights(localEntity.getID());
            paramPlayer.sendMessage("§4Chest unregistered.");
        }

        return !bool2;
    }

    public boolean onBlockDestroy(Player paramPlayer, Block paramBlock) {
        // work around to crow problem
        // paramBlock = paramPlayer.getWorld().getBlockAt(paramBlock.getX(),
        // paramBlock.getY(), paramBlock.getZ());
        int worldID = paramPlayer.getWorld().getType().getId();

        if (!isProtectable(paramBlock)) {
            return false;
        }

        List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(), paramBlock.getX(),
                paramBlock.getY(), paramBlock.getZ());
        
      
        boolean bool1 = true;
        Entity localEntity = null;
        int i = 1;
                
        for (Iterator<ComplexBlock> iBlocks = localList.iterator(); iBlocks.hasNext();) {
            ComplexBlock localComplexBlock = iBlocks.next();
            if (localComplexBlock == null) {
                continue;
            }
            Entity entity = this.physicalDatabase.loadProtectedEntity(worldID, localComplexBlock.getX(),
                    localComplexBlock.getY(), localComplexBlock.getZ());
            if (entity == null) {
                continue;
            }
            localEntity = entity;
            bool1 = this.lwc.canAccessChest(paramPlayer, localEntity);
            i = 0;
        }
        
        
        if (paramBlock.getStatus() != 0) {
            return !bool1;
        }
        String name = paramPlayer.getName();
        List<String> localObject1 = this.memoryDatabase.getActions(name);

        boolean op_free = localObject1.contains("free");
        boolean op_info = localObject1.contains("info");
        boolean op_create = localObject1.contains("create");
        boolean op_modify = localObject1.contains("modify");
        boolean op_drop = localObject1.contains("dropTransferSelect");
        Object localObject2;
        Object localObject3;
        Action localAction;
        // Object localObject5;
        int n;
        int i1;
        if (localEntity != null) {
            i = 0;

            if (op_info) {
                String str1 = "";

                if (localEntity.getType() == 1) {
                    localObject2 = this.memoryDatabase.getSessionUsers(localEntity.getID());

                    for (localObject3 = ((List) localObject2).iterator(); ((Iterator) localObject3).hasNext();) {
                        String str2 = (String) ((Iterator) localObject3).next();
                        Player localPlayer = etc.getServer().getPlayer(str2);

                        if (localPlayer == null) {
                            continue;
                        }
                        str1 = new StringBuilder().append(str1).append(localPlayer.getColor()).append(str2)
                                .append("§f").append(", ").toString();
                    }

                    if (((List) localObject2).size() > 0) {
                        str1 = str1.substring(0, str1.length() - 4);
                    }
                }

                localObject3 = " ";

                switch (localEntity.getType()) {
                case 0:
                    localObject3 = "Public";
                    break;
                case 1:
                    localObject3 = "Password";
                    break;
                case 2:
                    localObject3 = "Private";
                }

                boolean bool8 = this.lwc.canAdminChest(paramPlayer, localEntity);

                if (bool8) {
                    paramPlayer.sendMessage(new StringBuilder().append("§2ID: §6").append(localEntity.getID())
                            .toString());
                }

                paramPlayer.sendMessage(new StringBuilder().append("§2Type: §6").append((String) localObject3)
                        .toString());
                paramPlayer.sendMessage(new StringBuilder().append("§2Owner: §6").append(localEntity.getOwner())
                        .toString());

                if ((localEntity.getType() == 1) && (bool8)) {
                    paramPlayer.sendMessage(new StringBuilder().append("§2Authed players: ").append(str1).toString());
                }

                if (bool8) {
                    String world = com.griefcraft.util.StringUtils.capitalizeFirstLetter(localEntity.getWorldName());
                    paramPlayer.sendMessage(new StringBuilder().append("§2World: §6").append(world).toString());
                    paramPlayer.sendMessage(new StringBuilder().append("§2Location: §6{").append(localEntity.getX())
                            .append(", ").append(localEntity.getY()).append(", ").append(localEntity.getZ())
                            .append("}").toString());
                    paramPlayer.sendMessage(new StringBuilder().append("§2Date created: §6")
                            .append(localEntity.getDate()).toString());
                }

                if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                    this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                }
                return false;
            }
            if (op_drop) {
                boolean bool7 = this.lwc.canAccessChest(paramPlayer, localEntity);
                if (!bool7) {
                    paramPlayer
                            .sendMessage("§4You cannot use a chest that you cannot access as a drop transfer target.");
                    paramPlayer.sendMessage("§4If this is a passworded chest, please unlock it before retrying.");
                    paramPlayer.sendMessage("§4Use \"/lwc droptransfer select\" to try again.");
                } else {
                    for (localObject2 = localList.iterator(); ((Iterator) localObject2).hasNext();) {
                        localObject3 = (ComplexBlock) ((Iterator) localObject2).next();
                        if ((!(localObject3 instanceof Chest)) && (!(localObject3 instanceof DoubleChest))) {
                            paramPlayer.sendMessage("§4You need to select a chest as the Drop Transfer target!");
                            this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                            return false;
                        }
                    }

                    this.memoryDatabase.registerMode(paramPlayer.getName(), "dropTransfer",
                            new StringBuilder().append("f").append(localEntity.getID()).toString());
                    paramPlayer.sendMessage("§2Successfully registered chest as drop transfer target.");
                }
                this.memoryDatabase.unregisterAllActions(paramPlayer.getName());

                return false;
            }
            if (op_free) {
                if ((this.lwc.isAdmin(paramPlayer)) || (localEntity.getOwner().equals(paramPlayer.getName()))) {
                    paramPlayer.sendMessage("§aRemoved lock on the chest succesfully!");
                    this.physicalDatabase.unregisterProtectedEntity(worldID, localEntity.getX(), localEntity.getY(),
                            localEntity.getZ());
                    this.physicalDatabase.unregisterProtectionRights(localEntity.getID());
                    if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                        this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                    }
                    return false;
                }
                paramPlayer.sendMessage("§4You do not own that chest!");
                if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                    this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                }
                return true;
            }
            if (op_modify) {
                if (this.lwc.canAdminChest(paramPlayer, localEntity)) {
                    localAction = this.memoryDatabase.getAction("modify", paramPlayer.getName());

                    localObject2 = localAction.getData();
                    String[] splits = { "" };
                    localObject3 = new String[0];

                    if (((String) localObject2).length() > 0) {
                        splits = ((String) localObject2).split(" ");
                    }

                    if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                        this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                    }

                    for (String localObject5 : splits) {
                        int m = 0;
                        n = 0;
                        i1 = 1;

                        if (((String) localObject5).startsWith("-")) {
                            m = 1;
                            localObject5 = ((String) localObject5).substring(1);
                        }

                        if (((String) localObject5).startsWith("@")) {
                            n = 1;
                            localObject5 = ((String) localObject5).substring(1);
                        }

                        if (((String) localObject5).toLowerCase().startsWith("g:")) {
                            i1 = 0;
                            localObject5 = ((String) localObject5).substring(2);
                        }

                        int i2 = this.physicalDatabase.loadProtectedEntity(worldID, paramBlock.getX(), paramBlock.getY(),
                                paramBlock.getZ()).getID();

                        if (m == 0) {
                            this.physicalDatabase.unregisterProtectionRights(i2, (String) localObject5);
                            this.physicalDatabase.registerProtectionRights(i2, (String) localObject5, n != 0 ? 1 : 0,
                                    i1);
                            paramPlayer.sendMessage(new StringBuilder().append("§aRegistered rights for §6")
                                    .append((String) localObject5).append("§2").append(" ")
                                    .append(n != 0 ? "[§4ADMIN§6]" : "").append(" [")
                                    .append(i1 == 1 ? "Player" : "Group").append("]").toString());
                        } else {
                            this.physicalDatabase.unregisterProtectionRights(i2, (String) localObject5);
                            paramPlayer.sendMessage(new StringBuilder().append("§aRemoved rights for §6")
                                    .append((String) localObject5).append("§2").append(" [")
                                    .append(i1 == 1 ? "Player" : "Group").append("]").toString());
                        }
                    }

                    return false;
                }
                paramPlayer.sendMessage("§4You do not own that chest!");
                if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                    this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                }
                return true;
            }

        }

        if (op_drop) {
            paramPlayer.sendMessage("§4Cannot select unregistered chest as drop transfer target.");
            paramPlayer.sendMessage("§4Use \"/lwc droptransfer select\" to try again.");
            this.memoryDatabase.unregisterAllActions(paramPlayer.getName());

            return false;
        }

        if ((op_info) || (op_free)) {
            paramPlayer.sendMessage("§4Chest is unregistered");
            if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
            }
            return false;
        }

        if (((op_create) || (op_modify)) && (i == 0)) {
            if (!this.lwc.canAdminChest(paramPlayer, localEntity))
                paramPlayer.sendMessage("§4You do not own that chest!");
            else {
                paramPlayer.sendMessage("§4You have already registered that chest!");
            }
            if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
            }
            return true;
        }

        if ((i != 0) && (op_create)) {
            localAction = this.memoryDatabase.getAction("create", paramPlayer.getName());

            localObject2 = localAction.getData();
            String[] split = ((String) localObject2).split(" ");
            String cmd = split[0].toLowerCase();
            String str3 = "";

            if (split.length > 1) {
                for (int i2 = 1; i2 < split.length; i2++) {
                    str3 = new StringBuilder().append(str3).append(split[i2]).append(" ").toString();
                }
            }

            if (this.lwc.enforceChestLimits(paramPlayer)) {
                if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                    this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                }
                return false;
            }

            if ((!this.lwc.isAdmin(paramPlayer)) && (this.lwc.isInCuboidSafeZone(paramPlayer))) {
                paramPlayer.sendMessage("§4You need to be in a Cuboid-protected safe zone to do that!");
                this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
                return false;
            }

            if (cmd.equals("public")) {
                this.physicalDatabase.registerProtectedEntity(worldID, 0, paramPlayer.getName(), "", paramBlock.getX(),
                        paramBlock.getY(), paramBlock.getZ());
                paramPlayer.sendMessage("§2Created public protection successfully");
            } else {
                String str4;
                Object localObject6;
                if (cmd.equals("password")) {
                    str4 = localAction.getData().substring("password ".length());
                    str4 = this.lwc.encrypt(str4);

                    this.physicalDatabase.registerProtectedEntity(worldID, 1, paramPlayer.getName(), str4,
                            paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
                    this.memoryDatabase.registerPlayer(paramPlayer.getName(), this.physicalDatabase
                            .loadProtectedEntity(worldID, paramBlock.getX(), paramBlock.getY(), paramBlock.getZ())
                            .getID());
                    paramPlayer.sendMessage("§2Created password protection successfully");
                    paramPlayer.sendMessage("§aFor convenience, you don't have to enter your password until");
                    paramPlayer.sendMessage("§ayou next log in");

                    for (Iterator it = localList.iterator(); it.hasNext();) {
                        localObject6 = (ComplexBlock) it.next();
                        if (localObject6 != null) {
                            ((ComplexBlock) localObject6).update();
                        }
                    }
                } else if (cmd.equals("private")) {
                    str4 = localAction.getData();
                    String[] split2 = new String[0];

                    if (str4.length() > "private ".length()) {
                        str4 = str4.substring("private ".length());
                        split2 = str4.split(" ");
                    }

                    this.physicalDatabase.registerProtectedEntity(worldID, 2, paramPlayer.getName(), "",
                            paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());

                    paramPlayer.sendMessage("§2Created private protection successfully");

                    for (String str5 : split2) {
                        int i3 = 0;
                        int i4 = 1;

                        if (str5.startsWith("@")) {
                            i3 = 1;
                            str5 = str5.substring(1);
                        }

                        if (str5.toLowerCase().startsWith("g:")) {
                            i4 = 0;
                            str5 = str5.substring(2);
                        }

                        this.physicalDatabase.registerProtectionRights(
                                this.physicalDatabase.loadProtectedEntity(worldID, paramBlock.getX(), paramBlock.getY(),
                                        paramBlock.getZ()).getID(), str5, i3 != 0 ? 1 : 0, i4);
                        paramPlayer.sendMessage(new StringBuilder().append("§aRegistered rights for §6").append(str5)
                                .append(": ").append(i3 != 0 ? "[§4ADMIN§6]" : "").append(" [")
                                .append(i4 == 1 ? "Player" : "Group").append("]").toString());
                    }
                }
            }
            if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
                this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
            }
        }

        return !bool1;
    }

    public boolean onCommand(Player paramPlayer, String[] paramArrayOfString) {
        String str1 = paramArrayOfString[0].substring(1);
        String str2 = "";
        String[] arrayOfString = paramArrayOfString.length > 1 ? new String[paramArrayOfString.length - 1]
                : new String[0];

        if (paramArrayOfString.length > 1) {
            for (int i = 1; i < paramArrayOfString.length; i++) {
                paramArrayOfString[i] = paramArrayOfString[i].trim();

                if (paramArrayOfString[i].isEmpty()) {
                    continue;
                }
                arrayOfString[(i - 1)] = paramArrayOfString[i];
                str2 = new StringBuilder().append(str2).append(paramArrayOfString[i]).append(" ").toString();
            }
        }

        if (str1.equals("cpublic")) {
            return onCommand(paramPlayer, "/lwc -c public".split(" "));
        }
        if (str1.equals("cpassword")) {
            return onCommand(paramPlayer, new StringBuilder().append("/lwc -c password ").append(str2).toString()
                    .split(" "));
        }
        if (str1.equals("cprivate")) {
            return onCommand(paramPlayer, "/lwc -c private".split(" "));
        }
        if (str1.equals("cinfo")) {
            return onCommand(paramPlayer, "/lwc -i".split(" "));
        }
        if (str1.equals("cunlock")) {
            return onCommand(paramPlayer, "/lwc -u".split(" "));
        }

        if (!paramPlayer.canUseCommand(paramArrayOfString[0])) {
            return false;
        }

        if (!"lwc".equalsIgnoreCase(str1)) {
            return false;
        }

        if (arrayOfString.length == 0) {
            this.lwc.sendFullHelp(paramPlayer);
            return true;
        }

        for (LWC_Command localCommand : this.lwc.getCommands()) {
            if (!localCommand.validate(this.lwc, paramPlayer, arrayOfString)) {
                continue;
            }
            localCommand.execute(this.lwc, paramPlayer, arrayOfString);
            return true;
        }

        return false;
    }

    public void onDisconnect(Player paramPlayer) {
        this.memoryDatabase.unregisterPlayer(paramPlayer.getName());
        this.memoryDatabase.unregisterUnlock(paramPlayer.getName());
        this.memoryDatabase.unregisterChest(paramPlayer.getName());
        this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
    }

    public boolean onExplode(Block paramBlock) {
        int worldID = paramBlock.getWorld().getType().getId();
        int i = this.physicalDatabase.loadProtectedEntities(worldID, paramBlock.getX(), paramBlock.getY(),
                paramBlock.getZ(), 4).size() > 0 ? 1 : 0;
        return i != 0;
    }

    public boolean onItemDrop(Player player, Item dropItem) {       
        String playerName = player.getName();
        int i = this.lwc.getPlayerDropTransferTarget(playerName);

        if ((i == -1) || (!this.lwc.isPlayerDropTransferring(playerName))) {
            return false;
        }

        if (!this.physicalDatabase.doesChestExist(i)) {
            player.sendMessage("§4Your drop transfer target was unregistered and/or destroyed.");
            player.sendMessage("§4Please re-register a target chest. Drop transfer will be deactivated.");

            this.memoryDatabase.unregisterMode(playerName, "dropTransfer");
            return false;
        }

        Entity localEntity = this.physicalDatabase.loadProtectedEntity(i);

        if (localEntity == null) {
            player.sendMessage("§4An unknown error occured. Drop transfer will be deactivated.");

            this.memoryDatabase.unregisterMode(playerName, "dropTransfer");
            return false;
        }

        if (!this.lwc.canAccessChest(player, localEntity)) {
            player.sendMessage("§4You have lost access to your target chest.");
            player.sendMessage("§4Please re-register a target chest. Drop transfer will be deactivated.");

            this.memoryDatabase.unregisterMode(playerName, "dropTransfer");
            return false;
        }
        World world = etc.getServer().getWorld(localEntity.getWorldID());
        List<ComplexBlock> chestList = this.lwc.getEntitySet(world, localEntity.getX(),
                localEntity.getY(), localEntity.getZ());
        int amount = dropItem.getAmount();
        
        for (ComplexBlock localComplexBlock : chestList) {
            Inventory chest = (Inventory) localComplexBlock;
            Item item;
            while ((((item = chest.getItemFromId(dropItem.getItemId(), 63)) != null) || (chest
                    .getEmptySlot() != -1)) && (amount > 0)) {
                if (item != null) {
                    int k = Math.min(64 - item.getAmount(), dropItem.getAmount());
                    chest.setSlot(dropItem.getItemId(), item.getAmount() + k, item.getSlot());
                    amount -= k;
                    continue;
                }
                if (amount > 0) {
                    chest.addItem(new Item(dropItem.getItemId(), amount));
                    amount = 0;
                }
            }
            localComplexBlock.update();

            if (amount == 0) {
                break;
            }
        }

        player.sendMessage(String.format("Amount dropped: %d", dropItem.getAmount()));
        player.sendMessage(String.format("Amount left: %d", amount));
        if (dropItem.getAmount()-amount > 0 ) {
            player.getInventory().removeItem(dropItem.getItemId(), dropItem.getAmount()-amount);
        }


        if (amount > 0) {
            player.sendMessage("§4Your chest is full. Drop transfer will be deactivated.");
            player.sendMessage("§4Any remaining quantity that could not be stored will be returned.");
            this.memoryDatabase.unregisterMode(playerName, "dropTransfer");
            this.memoryDatabase.registerMode(playerName, "dropTransfer", new StringBuilder().append("f").append(i).toString());
            dropItem.setAmount(amount);
            player.getInventory().addItem(dropItem);
        }
        player.getInventory().update();

        return true;
    }

    public boolean onOpenInventory(Player paramPlayer, Inventory paramInventory) {
        if ((this.lwc.isAdmin(paramPlayer)) && (!this.debugMode)) {
            return false;
        }

        if ((paramInventory instanceof Workbench)) {
            return false;
        }

        /*
         * if ((paramInventory instanceof Dispenser)) { return false; }
         */

        ComplexBlock localComplexBlock1 = (ComplexBlock) paramInventory;

        if (!isProtectable(localComplexBlock1.getBlock())) {
            return false;
        }

        List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(), localComplexBlock1.getX(),
                localComplexBlock1.getY(), localComplexBlock1.getZ());
        boolean bool = true;

        for (ComplexBlock localComplexBlock2 : localList) {
            if (localComplexBlock2 == null) {
                continue;
            }
            int worldID = paramPlayer.getWorld().getType().getId();
            Entity localEntity = this.physicalDatabase.loadProtectedEntity(worldID, localComplexBlock2.getX(),
                    localComplexBlock2.getY(), localComplexBlock2.getZ());

            if (localEntity == null) {
                continue;
            }
            bool = this.lwc.canAccessChest(paramPlayer, localEntity);

            switch (localEntity.getType()) {
            case 1:
                if (bool)
                    break;
                this.memoryDatabase.unregisterUnlock(paramPlayer.getName());
                this.memoryDatabase.registerUnlock(paramPlayer.getName(), localEntity.getID());

                paramPlayer.sendMessage("§4This chest is locked.");
                paramPlayer.sendMessage("§4Type §6/lwc -u <password>§4 to unlock it");
                break;
            case 2:
                if (bool)
                    break;
                paramPlayer.sendMessage("§4This chest is locked with a magical spell.");
            }

        }

        return !bool;
    }

    private boolean isProtectable(Block paramBlock) {
        switch (paramBlock.getType()) {
        case 23: /* Dispensers */
            return true;
        case 54:
            return true;
        case 61:
        case 62:
            return true;
        }

        return false;
    }
}
