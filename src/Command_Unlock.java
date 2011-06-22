import com.griefcraft.model.Entity;
import com.griefcraft.sql.MemDB;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.StringUtils;

public class Command_Unlock implements LWC_Command {
    public void execute(LWC lwc, Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§4Usage: §6/lwc -u <Password>");
            return;
        }

        String password = StringUtils.join(args, 1);
        password = lwc.encrypt(password);

        if (!lwc.getMemoryDatabase().hasPendingUnlock(player.getName())) {
            player.sendMessage("§4Nothing selected. Open a locked Chest/Furnace first.");
            return;
        }

        int chestID = lwc.getMemoryDatabase().getUnlockID(player.getName());

        if (chestID == -1) {
            player.sendMessage("§4[lwc] Internal error. [ulock]");
            return;
        }

        Entity entity = lwc.getPhysicalDatabase().loadProtectedEntity(chestID);

        if (entity.getType() != 1) {
            player.sendMessage("§4That is not password protected!");
            return;
        }

        if (entity.getPassword().equals(password)) {
            player.sendMessage("§2Password accepted.");
            lwc.getMemoryDatabase().unregisterUnlock(player.getName());
            lwc.getMemoryDatabase().registerPlayer(player.getName(), chestID);

            for (ComplexBlock entity_ : lwc
                    .getEntitySet(player.getWorld(), entity.getX(), entity.getY(), entity.getZ()))
                if (entity_ != null)
                    entity_.update();
        } else {
            player.sendMessage("§4Invalid password.");
        }
    }

    public boolean validate(LWC lwc, Player player, String[] args) {
        return (StringUtils.hasFlag(args, "u")) || (StringUtils.hasFlag(args, "unlock"));
    }
}
