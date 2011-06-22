import com.griefcraft.model.Entity;
import com.griefcraft.sql.PhysDB;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;

public class CPConverter implements Runnable {
    private String[] CHESTS_FILES = { "../lockedChests.txt", "lockedChests.txt" };
    private int converted = 0;
    private Player player;
    private PhysDB physicalDatabase;

    public static void main(String[] paramArrayOfString) throws Exception {
        new CPConverter();
    }

    public CPConverter() {
        new Thread(this).start();
        this.physicalDatabase = new PhysDB();
    }

    public CPConverter(Player paramPlayer) {
        this();
        this.player = paramPlayer;
    }

    public void convertChests() throws FileNotFoundException, IOException {
        File localFile = null;
        for (String localObject2 : this.CHESTS_FILES) {
            localFile = new File((String) localObject2);
            if ((localFile != null) && (localFile.exists()))
                break;
        }
        if ((localFile == null) || (!localFile.exists()))
            throw new FileNotFoundException("No Chest Protect chest database found");
        int count = 0;
        BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));
        String line = "";
        while ((line = localBufferedReader.readLine()) != null) {
            line = line.trim();
            count++;
            if (line.startsWith("#"))
                continue;
            String[] split = line.split(",");
            if (split.length < 5)
                continue;
            String str1 = split[0];
            int k = Integer.parseInt(split[1]);
            int m = Integer.parseInt(split[2]);
            int n = Integer.parseInt(split[3]);
            int i1 = Integer.parseInt(split[4]);
            int i2 = -1;
            String str2 = "";
            if (i1 == 1) {
                i1 = 0;
            } else if (i1 > 1) {
                if (i1 == 3)
                    i2 = 0;
                else if (i1 == 4)
                    i2 = 1;
                i1 = 2;
            }
            if (split.length > 5)
                str2 = split[5].trim();
            log(String.format("Registering chest to %s at location {%d,%d,%d}", new Object[] { str1,
                    Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(n) }));
            this.physicalDatabase.registerProtectedEntity("NORMAL", i1, str1, "", k, m, n);
            this.converted += 1;
            if (i2 == -1)
                continue;
            int i3 = this.physicalDatabase.loadProtectedEntity("NORMAL", k, m, n).getID();
            String[] arrayOfString1 = str2.split(";");
            for (String str3 : arrayOfString1) {
                this.physicalDatabase.registerProtectionRights(i3, str3, 0, i2);
                log(String.format("  -> Registering rights to %s on chest %d",
                        new Object[] { str3, Integer.valueOf(i3) }));
            }
        }
    }

    public void log(String paramString) {
        System.out.println(paramString);
        if (this.player != null)
            this.player.sendMessage(paramString);
    }

    public void run() {
        try {
            log("LWC Conversion tool for Chest Protect chests");
            log("");
            log("Initializing sqlite");
            boolean bool = this.physicalDatabase.connect();
            if (!bool)
                throw new ConnectException("Failed to connect to the sqlite database");
            this.physicalDatabase.load();
            log("Done.");
            log("Starting conversion of Chest Protect chests");
            log("");
            convertChests();
            log("Done.");
            log("");
            log("Converted >" + this.converted + "< Chest Protect chests to LWC");
            log("LWC database now holds " + this.physicalDatabase.entityCount() + " protected chests!");
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}

/*
 * Location: D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name: CPConverter JD-Core Version: 0.6.0
 */