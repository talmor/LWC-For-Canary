/*    */ import com.griefcraft.sql.MemDB;
/*    */ import com.griefcraft.util.StringUtils;
/*    */ 
/*    */ public class Command_Modes
/*    */   implements LWC_Command
/*    */ {
/*    */   public void execute(LWC lwc, Player player, String[] args)
/*    */   {
/*  8 */     if (args.length < 2) {
/*  9 */       lwc.sendSimpleUsage(player, "/lwc -p <persist|droptransfer>");
/* 10 */       return;
/*    */     }
/*    */ 
/* 13 */     String mode = args[1].toLowerCase();
/*    */ 
/* 15 */     if (mode.equals("persist")) {
/* 16 */       if ((!lwc.isAdmin(player)) && (lwc.isModeBlacklisted(mode))) {
/* 17 */         player.sendMessage("§4That mode is currently disabled");
/* 18 */         return;
/*    */       }
/*    */ 
/* 21 */       lwc.getMemoryDatabase().registerMode(player.getName(), mode);
/* 22 */       player.sendMessage("§2Persistance mode activated");
/* 23 */       player.sendMessage("§2Type §6/lwc -r modes§2 to undo (or logout)");
/*    */     }
/* 26 */     else if (mode.equals("droptransfer")) {
/* 27 */       mode = "dropTransfer";
/*    */ 
/* 29 */       if ((!lwc.isAdmin(player)) && (lwc.isModeBlacklisted(mode))) {
/* 30 */         player.sendMessage("§4That mode is currently disabled");
/* 31 */         return;
/*    */       }
/*    */ 
/* 34 */       if (args.length < 3) {
/* 35 */         player.sendMessage("§2LWC Drop Transfer");
/* 36 */         player.sendMessage("");
/* 37 */         player.sendMessage("§a/lwc -p droptransfer select - Select a chest to drop transfer to");
/* 38 */         player.sendMessage("§a/lwc -p droptransfer on - Turn on drop transferring");
/* 39 */         player.sendMessage("§a/lwc -p droptransfer off - Turn off drop transferring");
/* 40 */         player.sendMessage("§a/lwc -p droptransfer status - Check the status of drop transferring");
/* 41 */         return;
/*    */       }
/*    */ 
/* 44 */       String action = args[2].toLowerCase();
/* 45 */       String playerName = player.getName();
/*    */ 
/* 47 */       if (action.equals("select")) {
/* 48 */         if (lwc.isPlayerDropTransferring(playerName)) {
/* 49 */           player.sendMessage("§4Please turn off drop transfer before reselecting a chest.");
/* 50 */           return;
/*    */         }
/*    */ 
/* 53 */         lwc.getMemoryDatabase().unregisterMode(playerName, mode);
/* 54 */         lwc.getMemoryDatabase().registerAction("dropTransferSelect", playerName, "");
/*    */ 
/* 56 */         player.sendMessage("§2Please left-click a registered chest to set as your transfer target.");
/* 57 */       } else if (action.equals("on")) {
/* 58 */         int target = lwc.getPlayerDropTransferTarget(playerName);
/*    */ 
/* 60 */         if (target == -1) {
/* 61 */           player.sendMessage("§4Please register a chest before turning drop transfer on.");
/* 62 */           return;
/*    */         }
/*    */ 
/* 65 */         lwc.getMemoryDatabase().unregisterMode(playerName, "dropTransfer");
/* 66 */         lwc.getMemoryDatabase().registerMode(playerName, "dropTransfer", "t" + target);
/* 67 */         player.sendMessage("§2Drop transfer is now on.");
/* 68 */         player.sendMessage("§2Any items dropped will be transferred to your chest.");
/* 69 */       } else if (action.equals("off")) {
/* 70 */         int target = lwc.getPlayerDropTransferTarget(playerName);
/*    */ 
/* 72 */         if (target == -1) {
/* 73 */           player.sendMessage("§4Please register a chest before turning drop transfer off.");
/* 74 */           return;
/*    */         }
/*    */ 
/* 77 */         lwc.getMemoryDatabase().unregisterMode(playerName, "dropTransfer");
/* 78 */         lwc.getMemoryDatabase().registerMode(playerName, "dropTransfer", "f" + target);
/*    */ 
/* 80 */         player.sendMessage("§2Drop transfer is now off.");
/* 81 */       } else if (action.equals("status")) {
/* 82 */         if (lwc.getPlayerDropTransferTarget(playerName) == -1) {
/* 83 */           player.sendMessage("§2You have not registered a drop transfer target.");
/*    */         }
/* 85 */         else if (lwc.isPlayerDropTransferring(playerName))
/* 86 */           player.sendMessage("§2Drop transfer is currently active.");
/*    */         else
/* 88 */           player.sendMessage("§2Drop transfer is currently inactive.");
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean validate(LWC lwc, Player player, String[] args)
/*    */   {
/* 98 */     return StringUtils.hasFlag(args, "p");
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     Command_Modes
 * JD-Core Version:    0.6.0
 */