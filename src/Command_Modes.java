 import com.griefcraft.sql.MemDB;
 import com.griefcraft.util.StringUtils;
 
 public class Command_Modes
   implements LWC_Command
 {
   public void execute(LWC lwc, Player player, String[] args)
   {
     if (args.length < 2) {
       lwc.sendSimpleUsage(player, "/lwc -p <persist|droptransfer>");
       return;
     }
 
     String mode = args[1].toLowerCase();
 
     if (mode.equals("persist")) {
       if ((!lwc.isAdmin(player)) && (lwc.isModeBlacklisted(mode))) {
         player.sendMessage("§4That mode is currently disabled");
         return;
       }
 
       lwc.getMemoryDatabase().registerMode(player.getName(), mode);
       player.sendMessage("§2Persistance mode activated");
       player.sendMessage("§2Type §6/lwc -r modes§2 to undo (or logout)");
     }
     else if (mode.equals("droptransfer")) {
       mode = "dropTransfer";
 
       if ((!lwc.isAdmin(player)) && (lwc.isModeBlacklisted(mode))) {
         player.sendMessage("§4That mode is currently disabled");
         return;
       }
 
       if (args.length < 3) {
         player.sendMessage("§2LWC Drop Transfer");
         player.sendMessage("");
         player.sendMessage("§a/lwc -p droptransfer select - Select a chest to drop transfer to");
         player.sendMessage("§a/lwc -p droptransfer on - Turn on drop transferring");
         player.sendMessage("§a/lwc -p droptransfer off - Turn off drop transferring");
         player.sendMessage("§a/lwc -p droptransfer status - Check the status of drop transferring");
         return;
       }
 
       String action = args[2].toLowerCase();
       String playerName = player.getName();
 
       if (action.equals("select")) {
         if (lwc.isPlayerDropTransferring(playerName)) {
           player.sendMessage("§4Please turn off drop transfer before reselecting a chest.");
           return;
         }
 
         lwc.getMemoryDatabase().unregisterMode(playerName, mode);
         lwc.getMemoryDatabase().registerAction("dropTransferSelect", playerName, "");
 
         player.sendMessage("§2Please left-click a registered chest to set as your transfer target.");
       } else if (action.equals("on")) {
         int target = lwc.getPlayerDropTransferTarget(playerName);
 
         if (target == -1) {
           player.sendMessage("§4Please register a chest before turning drop transfer on.");
           return;
         }
 
         lwc.getMemoryDatabase().unregisterMode(playerName, "dropTransfer");
         lwc.getMemoryDatabase().registerMode(playerName, "dropTransfer", "t" + target);
         player.sendMessage("§2Drop transfer is now on.");
         player.sendMessage("§2Any items dropped will be transferred to your chest.");
       } else if (action.equals("off")) {
         int target = lwc.getPlayerDropTransferTarget(playerName);
 
         if (target == -1) {
           player.sendMessage("§4Please register a chest before turning drop transfer off.");
           return;
         }
 
         lwc.getMemoryDatabase().unregisterMode(playerName, "dropTransfer");
         lwc.getMemoryDatabase().registerMode(playerName, "dropTransfer", "f" + target);
 
         player.sendMessage("§2Drop transfer is now off.");
       } else if (action.equals("status")) {
         if (lwc.getPlayerDropTransferTarget(playerName) == -1) {
           player.sendMessage("§2You have not registered a drop transfer target.");
         }
         else if (lwc.isPlayerDropTransferring(playerName))
           player.sendMessage("§2Drop transfer is currently active.");
         else
           player.sendMessage("§2Drop transfer is currently inactive.");
       }
     }
   }
 
   public boolean validate(LWC lwc, Player player, String[] args)
   {
     return StringUtils.hasFlag(args, "p");
   }
 }

