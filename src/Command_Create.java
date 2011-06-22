/*    */ import com.griefcraft.sql.MemDB;
/*    */ import com.griefcraft.util.StringUtils;
/*    */ 
/*    */ public class Command_Create
/*    */   implements LWC_Command
/*    */ {
/*    */   public void execute(LWC lwc, Player player, String[] args)
/*    */   {
/* 27 */     if (args.length == 1) {
/* 28 */       sendHelp(player);
/* 29 */       return;
/*    */     }
/*    */ 
/* 32 */     String type = args[1].toLowerCase();
/* 33 */     String full = StringUtils.join(args, 1);
/*    */ 
/* 35 */     if (type.equals("password")) {
/* 36 */       if (args.length < 3) {
/* 37 */         lwc.sendSimpleUsage(player, "/lwc -c password <Password>");
/* 38 */         return;
/*    */       }
/*    */ 
/* 41 */       String password = StringUtils.join(args, 2);
/* 42 */       String hiddenPass = StringUtils.transform(password, '*');
/*    */ 
/* 44 */       player.sendMessage("§aUsing password: §e" + hiddenPass);
/*    */     }
/* 47 */     else if ((!type.equals("public")) && (!type.equals("private"))) {
/* 48 */       sendHelp(player);
/* 49 */       return;
/*    */     }
/*    */ 
/* 52 */     lwc.getMemoryDatabase().unregisterAllActions(player.getName());
/* 53 */     lwc.getMemoryDatabase().registerAction("create", player.getName(), full);
/*    */ 
/* 55 */     player.sendMessage("§aLock type: §2" + StringUtils.capitalizeFirstLetter(type));
/* 56 */     player.sendMessage("§2Please left click your Chest or Furnace to lock it.");
/*    */   }
/*    */ 
/*    */   public boolean validate(LWC lwc, Player player, String[] args)
/*    */   {
/* 61 */     return (StringUtils.hasFlag(args, "c")) || (StringUtils.hasFlag(args, "create"));
/*    */   }
/*    */ 
/*    */   public void sendHelp(Player player) {
/* 65 */     player.sendMessage(" ");
/* 66 */     player.sendMessage("§2LWC Protection");
/* 67 */     player.sendMessage(" ");
/*    */ 
/* 69 */     player.sendMessage("/lwc -c public §6Create a public protection");
/* 70 */     player.sendMessage("§aAnyone can access a Public chest, but no one can protect it");
/* 71 */     player.sendMessage(" ");
/*    */ 
/* 73 */     player.sendMessage("/lwc -c password <password> §6Create a password-protected");
/* 74 */     player.sendMessage("§6Chest or Furnace");
/* 75 */     player.sendMessage("§aEach time you login you need to enter the password to access");
/* 76 */     player.sendMessage("§athe chest (if someone knows the pass, they can use it too!)");
/* 77 */     player.sendMessage(" ");
/*    */ 
/* 79 */     player.sendMessage("/lwc -c private §6Create a private protection");
/* 80 */     player.sendMessage("§aPrivate means private. You can also allow other users or");
/* 81 */     player.sendMessage("§agroups to access the chest or furnace. This is done by");
/* 82 */     player.sendMessage("§aadding them after \"private\".");
/* 83 */     player.sendMessage(" ");
/* 84 */     player.sendMessage("Example:");
/* 85 */     player.sendMessage("§3/lwc -c private UserName g:GroupName OtherGuy");
/* 86 */     player.sendMessage(" ");
/* 87 */     player.sendMessage("§aYou can specify more than 1 group and/or user per command!");
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     Command_Create
 * JD-Core Version:    0.6.0
 */