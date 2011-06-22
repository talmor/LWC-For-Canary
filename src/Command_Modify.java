/*    */ import com.griefcraft.sql.MemDB;
/*    */ import com.griefcraft.util.StringUtils;
/*    */ 
/*    */ public class Command_Modify
/*    */   implements LWC_Command
/*    */ {
/*    */   public void execute(LWC lwc, Player player, String[] args)
/*    */   {
/* 25 */     if (args.length < 2) {
/* 26 */       sendHelp(player);
/* 27 */       return;
/*    */     }
/*    */ 
/* 30 */     String full = StringUtils.join(args, 1);
/*    */ 
/* 32 */     lwc.getMemoryDatabase().unregisterAllActions(player.getName());
/* 33 */     lwc.getMemoryDatabase().registerAction("modify", player.getName(), full);
/* 34 */     player.sendMessage("§2Please left click your Chest/Furnace to complete modifications");
/*    */   }
/*    */ 
/*    */   public boolean validate(LWC lwc, Player player, String[] args)
/*    */   {
/* 39 */     return (StringUtils.hasFlag(args, "m")) || (StringUtils.hasFlag(args, "modify"));
/*    */   }
/*    */ 
/*    */   private void sendHelp(Player player) {
/* 43 */     player.sendMessage(" ");
/* 44 */     player.sendMessage("§2LWC Protection");
/* 45 */     player.sendMessage(" ");
/*    */ 
/* 47 */     player.sendMessage("/lwc -m <users/groups> §6Modify an existing protection, adding or");
/* 48 */     player.sendMessage("§6removing users and/or groups");
/* 49 */     player.sendMessage("§2See: §6/lwc -c§2, the example for private protections");
/* 50 */     player.sendMessage(" ");
/*    */ 
/* 52 */     player.sendMessage("§aAdditional prefixes for users/groups:");
/* 53 */     player.sendMessage("§4-§a: Remove a user/group from protection");
/* 54 */     player.sendMessage("§4@§a: The user/group will be able to modify the chest");
/* 55 */     player.sendMessage("§6note: chest admins cannot remove the owner from access");
/* 56 */     player.sendMessage(" ");
/*    */ 
/* 58 */     player.sendMessage("Examples");
/* 59 */     player.sendMessage("§6Remove a group from access: §3/lwc -m -g:name");
/* 60 */     player.sendMessage("§6Remove a user + add an admin: §3/lwc -m -name @OtherName");
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     Command_Modify
 * JD-Core Version:    0.6.0
 */