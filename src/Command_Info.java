/*    */ import com.griefcraft.sql.MemDB;
/*    */ import com.griefcraft.util.StringUtils;
/*    */ 
/*    */ public class Command_Info
/*    */   implements LWC_Command
/*    */ {
/*    */   public void execute(LWC lwc, Player player, String[] args)
/*    */   {
/* 24 */     lwc.getMemoryDatabase().unregisterAllActions(player.getName());
/* 25 */     lwc.getMemoryDatabase().registerAction("info", player.getName());
/* 26 */     player.sendMessage("§aLeft click a Chest or Furnace to see information about it");
/*    */   }
/*    */ 
/*    */   public boolean validate(LWC lwc, Player player, String[] args)
/*    */   {
/* 31 */     return (StringUtils.hasFlag(args, "i")) || (StringUtils.hasFlag(args, "info"));
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     Command_Info
 * JD-Core Version:    0.6.0
 */