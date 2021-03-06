 import com.griefcraft.sql.MemDB;
 import com.griefcraft.util.StringUtils;
 
 public class Command_Free
   implements LWC_Command
 {
   public void execute(LWC lwc, Player player, String[] args)
   {
     if (args.length < 2) {
       lwc.sendSimpleUsage(player, "/lwc -r <chest|furnace|modes>");
       return;
     }
 
     String type = args[1].toLowerCase();
 
     if ((type.equals("chest")) || (type.equals("furnace"))) {
       if (lwc.getMemoryDatabase().hasPendingChest(player.getName())) {
         player.sendMessage("§4You already have a pending action.");
         return;
       }
 
       lwc.getMemoryDatabase().unregisterAllActions(player.getName());
       lwc.getMemoryDatabase().registerAction("free", player.getName());
       player.sendMessage("§aLeft click your Chest or Furnace to remove the lock");
     }
     else if (type.equals("modes")) {
       lwc.getMemoryDatabase().unregisterAllModes(player.getName());
 
       player.sendMessage("§2Successfully removed all set modes.");
     }
     else
     {
       lwc.sendSimpleUsage(player, "/lwc -r <chest|furnace|modes>");
       return;
     }
   }
 
   public boolean validate(LWC lwc, Player player, String[] args)
   {
     return (StringUtils.hasFlag(args, "r")) || (StringUtils.hasFlag(args, "free")) || (StringUtils.hasFlag(args, "remove"));
   }
 }
