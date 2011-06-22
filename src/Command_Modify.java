import com.griefcraft.sql.MemDB;
import com.griefcraft.util.StringUtils;

public class Command_Modify implements LWC_Command {
    public void execute(LWC lwc, Player player, String[] args) {
        if (args.length < 2) {
            sendHelp(player);
            return;
        }

        String full = StringUtils.join(args, 1);

        lwc.getMemoryDatabase().unregisterAllActions(player.getName());
        lwc.getMemoryDatabase().registerAction("modify", player.getName(), full);
        player.sendMessage("§2Please left click your Chest/Furnace to complete modifications");
    }

    public boolean validate(LWC lwc, Player player, String[] args) {
        return (StringUtils.hasFlag(args, "m")) || (StringUtils.hasFlag(args, "modify"));
    }

    private void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage("§2LWC Protection");
        player.sendMessage(" ");

        player.sendMessage("/lwc -m <users/groups> §6Modify an existing protection, adding or");
        player.sendMessage("§6removing users and/or groups");
        player.sendMessage("§2See: §6/lwc -c§2, the example for private protections");
        player.sendMessage(" ");

        player.sendMessage("§aAdditional prefixes for users/groups:");
        player.sendMessage("§4-§a: Remove a user/group from protection");
        player.sendMessage("§4@§a: The user/group will be able to modify the chest");
        player.sendMessage("§6note: chest admins cannot remove the owner from access");
        player.sendMessage(" ");

        player.sendMessage("Examples");
        player.sendMessage("§6Remove a group from access: §3/lwc -m -g:name");
        player.sendMessage("§6Remove a user + add an admin: §3/lwc -m -name @OtherName");
    }
}
