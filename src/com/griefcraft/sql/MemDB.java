package com.griefcraft.sql;

import com.griefcraft.model.Action;
import com.griefcraft.model.Entity;
import com.griefcraft.util.Performance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MemDB extends Database
{
  public Action getAction(String paramString1, String paramString2)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT * FROM `actions` WHERE `player` = ? AND `action` = ?");
      localPreparedStatement.setString(1, paramString2);
      localPreparedStatement.setString(2, paramString1);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        int i = localResultSet.getInt("id");
        String str1 = localResultSet.getString("action");
        String str2 = localResultSet.getString("player");
        int j = localResultSet.getInt("chest");
        String str3 = localResultSet.getString("data");
        Action localAction = new Action();
        localAction.setID(i);
        localAction.setAction(str1);
        localAction.setPlayer(str2);
        localAction.setChestID(j);
        localAction.setData(str3);
        return localAction;
      }
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }

  public int getActionID(String paramString1, String paramString2)
  {
    try
    {
      int i = -1;
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `chest` FROM `actions` WHERE `action` = ? AND `player` = ?");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
        i = localResultSet.getInt("chest");
      localPreparedStatement.close();
      Performance.addMemDBQuery();
      return i;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return -1;
  }

  public List<String> getActions(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `action` FROM `actions` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        String str = localResultSet.getString("action");
        localArrayList.add(str);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localArrayList;
  }

  public String getDatabasePath()
  {
    return ":memory:";
  }

  public String getLockPassword(String paramString)
  {
    try
    {
      String str = "";
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `password` FROM `locks` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
        str = localResultSet.getString("password");
      localPreparedStatement.close();
      Performance.addMemDBQuery();
      return str;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }

  public String getModeData(String paramString1, String paramString2)
  {
    String str = null;
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `data` from `modes` WHERE `player` = ? AND `mode` = ?");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
        str = localResultSet.getString("data");
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return str;
  }

  public List<String> getModes(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT * from `modes` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        String str = localResultSet.getString("mode");
        localArrayList.add(str);
      }
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localArrayList;
  }

  public List<String> getSessionUsers(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `player` FROM `sessions` WHERE `chest` = ?");
      localPreparedStatement.setInt(1, paramInt);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        String str = localResultSet.getString("player");
        localArrayList.add(str);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localArrayList;
  }

  public int getUnlockID(String paramString)
  {
    return getActionID("unlock", paramString);
  }

  public boolean hasAccess(String paramString, Entity paramEntity)
  {
    if (paramEntity == null)
      return true;
    return hasAccess(paramString, paramEntity.getID());
  }

  public boolean hasAccess(String paramString, int paramInt)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `player` FROM `sessions` WHERE `chest` = ?");
      localPreparedStatement.setInt(1, paramInt);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        String str = localResultSet.getString("player");
        if (paramString.equals(str))
        {
          localPreparedStatement.close();
          Performance.addMemDBQuery();
          return true;
        }
      }
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }

  public boolean hasMode(String paramString1, String paramString2)
  {
    List localList = getModes(paramString1);
    return (localList.size() > 0) && (localList.contains(paramString2));
  }

  public boolean hasPendingAction(String paramString1, String paramString2)
  {
    return getAction(paramString1, paramString2) != null;
  }

  public boolean hasPendingChest(String paramString)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT `id` FROM `locks` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      ResultSet localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        localPreparedStatement.close();
        Performance.addMemDBQuery();
        return true;
      }
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }

  public boolean hasPendingUnlock(String paramString)
  {
    return getUnlockID(paramString) != -1;
  }

  public void load()
  {
    try
    {
      Statement localStatement = this.connection.createStatement();
      log("Creating memory tables");
      localStatement.executeUpdate("CREATE TABLE IF NOT EXISTS 'sessions' (id INTEGER PRIMARY KEY,player TEXT,chest INTEGER);");
      localStatement.executeUpdate("CREATE TABLE IF NOT EXISTS 'locks' (id INTEGER PRIMARY KEY,player TEXT,password TEXT);");
      localStatement.executeUpdate("CREATE TABLE IF NOT EXISTS 'actions' (id INTEGER PRIMARY KEY,action TEXT,player TEXT,chest INTEGER,data TEXT);");
      localStatement.executeUpdate("CREATE TABLE IF NOT EXISTS 'modes' (id INTEGER PRIMARY KEY,player TEXT,mode TEXT,data TEXT);");
      localStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public int pendingCount()
  {
    int i = 0;
    try
    {
      Statement localStatement = this.connection.createStatement();
      ResultSet localResultSet = localStatement.executeQuery("SELECT `id` FROM `locks`");
      while (localResultSet.next())
        i++;
      localStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return i;
  }

  public void registerAction(String paramString1, String paramString2)
  {
    try
    {
      unregisterAction(paramString1, paramString2);
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `actions` (action, player, chest) VALUES (?, ?, ?)");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setInt(3, -1);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerAction(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      unregisterAction(paramString1, paramString2);
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `actions` (action, player, chest) VALUES (?, ?, ?)");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setInt(3, paramInt);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerAction(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      unregisterAction(paramString1, paramString2);
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `actions` (action, player, data) VALUES (?, ?, ?)");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, paramString3);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerChest(String paramString1, String paramString2)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `locks` (player, password) VALUES (?, ?)");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerMode(String paramString1, String paramString2)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `modes` (player, mode) VALUES (?, ?)");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerMode(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `modes` (player, mode, data) VALUES (?, ?, ?)");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.setString(3, paramString3);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerPlayer(String paramString, int paramInt)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("INSERT INTO `sessions` (player, chest) VALUES(?, ?)");
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.setInt(2, paramInt);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void registerUnlock(String paramString, int paramInt)
  {
    registerAction("unlock", paramString, paramInt);
  }

  public int sessionCount()
  {
    int i = 0;
    try
    {
      Statement localStatement = this.connection.createStatement();
      ResultSet localResultSet = localStatement.executeQuery("SELECT `id` FROM `sessions`");
      while (localResultSet.next())
        i++;
      localStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return i;
  }

  public void unregisterAction(String paramString1, String paramString2)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("DELETE FROM `actions` WHERE `action` = ? AND `player` = ?");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterAllActions(String paramString)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("DELETE FROM `actions` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterAllChests()
  {
    try
    {
      Statement localStatement = this.connection.createStatement();
      localStatement.executeUpdate("DELETE FROM `locks`");
      localStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterAllModes(String paramString)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("DELETE FROM `modes` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterChest(String paramString)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("DELETE FROM `locks` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterMode(String paramString1, String paramString2)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("DELETE FROM `modes` WHERE `player` = ? AND `mode` = ?");
      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, paramString2);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterPlayer(String paramString)
  {
    try
    {
      PreparedStatement localPreparedStatement = this.connection.prepareStatement("DELETE FROM `sessions` WHERE `player` = ?");
      localPreparedStatement.setString(1, paramString);
      localPreparedStatement.executeUpdate();
      localPreparedStatement.close();
      Performance.addMemDBQuery();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public void unregisterUnlock(String paramString)
  {
    unregisterAction("unlock", paramString);
  }
}

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.sql.MemDB
 * JD-Core Version:    0.6.0
 */