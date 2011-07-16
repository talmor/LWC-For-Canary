import com.griefcraft.util.Performance;
import com.griefcraft.util.StringUtils;

public class Command_Admin implements LWC_Command {
    public void execute(LWC lwc, Player player, String[] args) {
        if (args.length < 2) {
            sendHelp(player);
            return;
        }

        String action = args[1].toLowerCase();

        if (action.equals("report")) {
            Performance.setChestCount(lwc.getPhysicalDatabase().entityCount());
            Performance.setPlayersOnline(etc.getServer().getPlayerList().size());

            for (String line : Performance.getGeneratedReport()) {
                player.sendMessage("§2" + line);
            }

            Performance.clear();
        } else if (action.equals("update")) {
            boolean updated = lwc.getUpdater().checkDist();

            if (updated) {
                etc.getLoader().reloadPlugin("LWC");
                player.sendMessage("§2Updated LWC successfully to version: " + lwc.getUpdater().getLatestVersion());
            } else {
                player.sendMessage("§4No update found.");
            }

        } else if (action.equalsIgnoreCase("limits")) {
            if (args.length < 3) {
                lwc.sendSimpleUsage(player, "/lwc -admin limits <count> <Group/User>");
                return;
            }

            int limit = Integer.parseInt(args[2]);

            for (int i = 3; i < args.length; i++) {
                String entity = args[i];
                boolean isGroup = entity.startsWith("g:");

                if (isGroup) {
                    entity = entity.substring(2);
                }

                if (limit != -2) {
                    lwc.getPhysicalDatabase().registerProtectionLimit(isGroup ? 0 : 1, limit, entity);
                    player.sendMessage("§2Registered limit of §6" + limit + "§2" + " chests to the "
                            + (isGroup ? "group" : "user") + " " + "§6" + entity);
                } else {
                    lwc.getPhysicalDatabase().unregisterProtectionLimit(isGroup ? 0 : 1, entity);
                    player.sendMessage("§2Unregistered limit for §6" + entity);
                }
            }

        } else if (action.equals("convert")) {
            if (args.length < 2) {
                lwc.sendSimpleUsage(player, "/lwc -admin convert chestprotect");
                return;
            }

            String pluginToConvert = args[1].toLowerCase();

            if (pluginToConvert.equals("chestprotect")) {
                new CPConverter(player);
            }

        } else if (action.equals("clear")) {
            if (args.length < 3) {
                lwc.sendSimpleUsage(player, "/lwc -admin clear chests|limits|rights");
                return;
            }

            String toClear = args[2].toLowerCase();
            
            if (toClear.equals("protections")) {
                lwc.getPhysicalDatabase().unregisterProtectionEntities();
                player.sendMessage("§2Removed all protected chests and furnaces");
            } else if (toClear.equals("rights")) {
                lwc.getPhysicalDatabase().unregisterProtectionRights();

                player.sendMessage("§2Removed all protection rights");
            } else if (toClear.equals("limits")) {
                lwc.getPhysicalDatabase().unregisterProtectionLimits();

                player.sendMessage("§2Removed all protection limits");
            }
        }
    }

    public void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage("§2LWC Administration");
        player.sendMessage(" ");
        player.sendMessage("/lwc admin report - §6Generate a Performance report");
        player.sendMessage("/lwc admin update - §6Check for an update (if one, update)");
        player.sendMessage("/lwc admin convert §6<chestprotect> - Convert X to LWC");
        player.sendMessage(" ");
        player.sendMessage("/lwc admin clear - §4PERMANENTLY removes data");
        player.sendMessage("/lwc admin clear §6<protections|rights|limits>");
    }

    public boolean validate(LWC lwc, Player player, String[] args) {
        return (lwc.isAdmin(player)) && ((StringUtils.hasFlag(args, "a")) || (StringUtils.hasFlag(args, "admin")));
    }
}
