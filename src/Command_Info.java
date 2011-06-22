import com.griefcraft.sql.MemDB;
import com.griefcraft.util.StringUtils;

public class Command_Info implements LWC_Command {
    public void execute(LWC lwc, Player player, String[] args) {
        lwc.getMemoryDatabase().unregisterAllActions(player.getName());
        lwc.getMemoryDatabase().registerAction("info", player.getName());
        player.sendMessage("§aLeft click a Chest or Furnace to see information about it");
    }

    public boolean validate(LWC lwc, Player player, String[] args) {
        return (StringUtils.hasFlag(args, "i")) || (StringUtils.hasFlag(args, "info"));
    }
}
