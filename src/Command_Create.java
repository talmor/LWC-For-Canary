import com.griefcraft.sql.MemDB;
import com.griefcraft.util.StringUtils;

public class Command_Create implements LWC_Command {
    public void execute(LWC lwc, Player player, String[] args) {
        if (args.length == 1) {
            sendHelp(player);
            return;
        }

        String type = args[1].toLowerCase();
        String full = StringUtils.join(args, 1);

        if (type.equals("password")) {
            if (args.length < 3) {
                lwc.sendSimpleUsage(player, "/lwc -c password <Password>");
                return;
            }

            String password = StringUtils.join(args, 2);
            String hiddenPass = StringUtils.transform(password, '*');

            player.sendMessage("§aUsing password: §e" + hiddenPass);
        } else if ((!type.equals("public")) && (!type.equals("private"))) {
            sendHelp(player);
            return;
        }

        lwc.getMemoryDatabase().unregisterAllActions(player.getName());
        lwc.getMemoryDatabase().registerAction("create", player.getName(), full);

        player.sendMessage("§aLock type: §2" + StringUtils.capitalizeFirstLetter(type));
        player.sendMessage("§2Please left click your Chest or Furnace to lock it.");
    }

    public boolean validate(LWC lwc, Player player, String[] args) {
        return (StringUtils.hasFlag(args, "c")) || (StringUtils.hasFlag(args, "create"));
    }

    public void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage("§2LWC Protection");
        player.sendMessage(" ");

        player.sendMessage("/lwc -c public §6Create a public protection");
        player.sendMessage("§aAnyone can access a Public chest, but no one can protect it");
        player.sendMessage(" ");

        player.sendMessage("/lwc -c password <password> §6Create a password-protected");
        player.sendMessage("§6Chest or Furnace");
        player.sendMessage("§aEach time you login you need to enter the password to access");
        player.sendMessage("§athe chest (if someone knows the pass, they can use it too!)");
        player.sendMessage(" ");

        player.sendMessage("/lwc -c private §6Create a private protection");
        player.sendMessage("§aPrivate means private. You can also allow other users or");
        player.sendMessage("§agroups to access the chest or furnace. This is done by");
        player.sendMessage("§aadding them after \"private\".");
        player.sendMessage(" ");
        player.sendMessage("Example:");
        player.sendMessage("§3/lwc -c private UserName g:GroupName OtherGuy");
        player.sendMessage(" ");
        player.sendMessage("§aYou can specify more than 1 group and/or user per command!");
    }
}
