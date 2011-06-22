import com.griefcraft.logging.Logger;
import com.griefcraft.sql.MemDB;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.Config;
import java.sql.Connection;
import java.util.Random;

public class SQLTest
{
  private Logger logger = Logger.getLogger("SQLTest");
  private PhysDB phys;
  private MemDB mem;

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    new SQLTest();
  }

  public SQLTest()
    throws Exception
  {
    Config.init();
    Config.getInstance().setProperty("db-path", "E:\\Java\\LWC\\lwc.db");
    this.phys = new PhysDB();
    this.mem = new MemDB();
    this.phys.connect();
    this.phys.load();
    this.mem.connect();
    this.mem.load();
    speedTest();
  }

  private void createChests(int paramInt)
    throws Exception
  {
    Random localRandom = new Random();
    this.phys.connection.setAutoCommit(false);
    while (paramInt > 0)
    {
      this.phys.registerProtectedEntity("NORMAL",localRandom.nextInt(1), "Hidendra", "", localRandom.nextInt(255), localRandom.nextInt(255), localRandom.nextInt(255));
      paramInt--;
    }
    this.logger.info("Commiting");
    this.phys.connection.commit();
    this.phys.connection.setAutoCommit(true);
    this.logger.info("Done. Count : " + this.phys.entityCount());
  }

  private void speedTest()
    throws Exception
  {
    Random localRandom = new Random();
    long l1 = System.currentTimeMillis();
    int i = 0;
    int j = 0;
    while (true)
    {
      long l2 = System.currentTimeMillis() - l1;
      int k = (int)(l2 / 1000L);
      int m = k > 0 ? i / k : 0;
      if ((j != k) && (l2 % 1000L == 0L))
      {
        this.logger.info(String.format("[%ds] [Q:%d] QUERIES/SEC:%d", new Object[] { Integer.valueOf(k), Integer.valueOf(i), Integer.valueOf(m) }));
        j = k;
      }
      this.mem.getModeData("Hidendra", "m" + localRandom.nextInt(50000));
      i++;
    }
  }
}

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     SQLTest
 * JD-Core Version:    0.6.0
 */