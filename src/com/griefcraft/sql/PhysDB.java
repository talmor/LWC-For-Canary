package com.griefcraft.sql;

import com.griefcraft.model.Entity;
import com.griefcraft.util.Performance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhysDB extends Database {
    private boolean loaded = false;
    private PreparedStatement _select_protectedEntity_ID;
    private PreparedStatement _select_chestExist_ID;
    private PreparedStatement _select_chestCount_user;
    private PreparedStatement _select_limit_type_entity;
    private PreparedStatement _select_privateAccess_type_ID_entities;
    private PreparedStatement _select_protectedEntity_x_y_z_radius;
    private PreparedStatement _select_protectedEntity_x_y_z;
    private PreparedStatement _insert_protectedEntity_type_player_password_x_y_z;
    private PreparedStatement _insert_protectedLimit_type_amount_entity;
    private PreparedStatement _insert_rights_ID_entity_rights_type;
    private PreparedStatement _delete_protectedEntity_ID;
    private PreparedStatement _delete_protectedEntity_x_y_z;
    private PreparedStatement _delete_limit_type_entity;
    private PreparedStatement _delete_rights_ID;
    private PreparedStatement _delete_rights_ID_entity;

    public boolean doesChestExist(int paramInt) {
        int i = 0;
        try {
            this._select_chestExist_ID.setInt(1, paramInt);
            ResultSet localResultSet = this._select_chestExist_ID.executeQuery();
            i = localResultSet.getInt("count") > 0 ? 1 : 0;
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i == 1;
    }

    public void doUpdate100() {
        try {
            Statement localStatement1 = this.connection.createStatement();
            localStatement1.executeQuery("SELECT `type` FROM `protections`");
            localStatement1.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException1) {
            log("Outdated database!");
            log("UPGRADING FROM 1.00 TO 1.10");
            log("ALTERING TABLE `protections` AND FILLING WITH DEFAULT DATA");
            try {
                Statement localStatement2 = this.connection.createStatement();
                localStatement2.addBatch("ALTER TABLE `protections` ADD `type` INTEGER");
                localStatement2.addBatch("UPDATE `protections` SET `type`='1'");
                localStatement2.executeBatch();
                localStatement2.close();
                Performance.addPhysDBQuery();
            } catch (Exception localException2) {
                log("Oops! Something went wrong: ");
                localException1.printStackTrace();
                System.exit(0);
            }
            log("Update completed!");
        }
    }

    public void doUpdate130() {
        int i = 1;
        try {
            Statement localStatement1 = this.connection.createStatement();
            ResultSet localResultSet = localStatement1.executeQuery("PRAGMA INDEX_LIST('protections')");
            while (localResultSet.next())
                i = 0;
            localStatement1.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException1) {
        }
        if (i == 0)
            return;
        log("Outdated database!");
        log("UPGRADING FROM 1.10 TO 1.30");
        log("CREATING INDEXES!");
        try {
            Statement localStatement2 = this.connection.createStatement();
            localStatement2.addBatch("BEGIN TRANSACTION");
            localStatement2.addBatch("CREATE INDEX in1 ON `protections` (owner, x, y, z)");
            localStatement2.addBatch("CREATE INDEX in2 ON `limits` (type, entity)");
            localStatement2.addBatch("CREATE INDEX in3 ON `rights` (chest, entity)");
            localStatement2.addBatch("END TRANSACTION");
            localStatement2.executeBatch();
            localStatement2.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException2) {
            log("Oops! Something went wrong: ");
            localException2.printStackTrace();
        }
        log("Update complete!");
    }

    public int entityCount() {
        int i = 0;
        try {
            Statement localStatement = this.connection.createStatement();
            ResultSet localResultSet = localStatement.executeQuery("SELECT `id` FROM `protections`");
            while (localResultSet.next())
                i++;
            localStatement.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i;
    }

    public int getChestCount(String paramString) {
        int i = 0;
        try {
            this._select_chestCount_user.setString(1, paramString);
            ResultSet localResultSet = this._select_chestCount_user.executeQuery();
            while (localResultSet.next())
                i++;
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i;
    }

    public int getGroupLimit(String paramString) {
        return getLimit(0, paramString);
    }

    public int getLimit(int paramInt, String paramString) {
        int i = -1;
        try {
            this._select_limit_type_entity.setInt(1, paramInt);
            this._select_limit_type_entity.setString(2, paramString.toLowerCase());
            ResultSet localResultSet = this._select_limit_type_entity.executeQuery();
            while (localResultSet.next())
                i = localResultSet.getInt("amount");
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i;
    }

    public int getPrivateAccess(int paramInt1, int paramInt2, String[] paramArrayOfString) {
        int i = -1;
        try {
            this._select_privateAccess_type_ID_entities.setInt(1, paramInt1);
            this._select_privateAccess_type_ID_entities.setInt(2, paramInt2);
            ResultSet localResultSet = this._select_privateAccess_type_ID_entities.executeQuery();
            label115: while (localResultSet.next()) {
                String str1 = localResultSet.getString("entity");
                for (String str2 : paramArrayOfString) {
                    if (!str2.equalsIgnoreCase(str1))
                        continue;
                    i = localResultSet.getInt("rights");
                    break label115;
                }
            }
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i;
    }

    public int getUserLimit(String paramString) {
        return getLimit(1, paramString);
    }

    public int limitCount() {
        int i = 0;
        try {
            Statement localStatement = this.connection.createStatement();
            ResultSet localResultSet = localStatement.executeQuery("SELECT `id` FROM `limits`");
            while (localResultSet.next())
                i++;
            localStatement.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return i;
    }

    public void load() {
        if (this.loaded)
            return;
        doUpdate140();
        try {
            Statement localStatement = this.connection.createStatement();
            this.connection.setAutoCommit(false);
            log("Creating physical tables if needed");
            localStatement
                    .executeUpdate("CREATE TABLE IF NOT EXISTS 'protections' (id INTEGER PRIMARY KEY,type INTEGER,owner TEXT,password TEXT,x INTEGER,y INTEGER,z INTEGER,date TEXT,world TEXT);");
            localStatement
                    .executeUpdate("CREATE TABLE IF NOT EXISTS 'limits' (id INTEGER PRIMARY KEY,type INTEGER,amount INTEGER,entity TEXT);");
            localStatement
                    .executeUpdate("CREATE TABLE IF NOT EXISTS 'rights' (id INTEGER PRIMARY KEY,chest INTEGER,entity TEXT,rights INTEGER,type INTEGER);");
            this.connection.commit();
            this.connection.setAutoCommit(true);
            localStatement.close();
            Performance.addPhysDBQuery();
            loadPreparedStatements();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        doUpdate100();
        doUpdate130();
        doUpdate150();
        this.loaded = true;
    }

    public List<Entity> loadProtectedEntities(String world, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        ArrayList localArrayList = new ArrayList();
        try {
            this._select_protectedEntity_x_y_z_radius.setInt(1, paramInt1 - paramInt4);
            this._select_protectedEntity_x_y_z_radius.setInt(2, paramInt1 + paramInt4);
            this._select_protectedEntity_x_y_z_radius.setInt(3, paramInt2 - paramInt4);
            this._select_protectedEntity_x_y_z_radius.setInt(4, paramInt2 + paramInt4);
            this._select_protectedEntity_x_y_z_radius.setInt(5, paramInt3 - paramInt4);
            this._select_protectedEntity_x_y_z_radius.setInt(6, paramInt3 + paramInt4);
            this._select_protectedEntity_x_y_z_radius.setString(7, world);
            ResultSet localResultSet = this._select_protectedEntity_x_y_z_radius.executeQuery();
            while (localResultSet.next()) {
                int i = localResultSet.getInt("id");
                int j = localResultSet.getInt("type");
                String str1 = localResultSet.getString("owner");
                String str2 = localResultSet.getString("password");
                int k = localResultSet.getInt("x");
                int m = localResultSet.getInt("y");
                int n = localResultSet.getInt("z");
                String str3 = localResultSet.getString("date");
                Entity localEntity = new Entity();
                localEntity.setID(i);
                localEntity.setType(j);
                localEntity.setOwner(str1);
                localEntity.setPassword(str2);
                localEntity.setX(k);
                localEntity.setY(m);
                localEntity.setZ(n);
                localEntity.setDate(str3);
                localArrayList.add(localEntity);
            }
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return localArrayList;
    }

    public Entity loadProtectedEntity(int paramInt) {
        try {
            this._select_protectedEntity_ID.setInt(1, paramInt);
            ResultSet localResultSet = this._select_protectedEntity_ID.executeQuery();
            if (localResultSet.next()) {
                int i = localResultSet.getInt("id");
                int j = localResultSet.getInt("type");
                String str1 = localResultSet.getString("owner");
                String str2 = localResultSet.getString("password");
                int k = localResultSet.getInt("x");
                int m = localResultSet.getInt("y");
                int n = localResultSet.getInt("z");
                String world = localResultSet.getString("world");
                String str3 = localResultSet.getString("date");
                Entity localEntity = new Entity();
                localEntity.setID(i);
                localEntity.setType(j);
                localEntity.setOwner(str1);
                localEntity.setPassword(str2);
                localEntity.setX(k);
                localEntity.setY(m);
                localEntity.setZ(n);
                localEntity.setWorld(world);
                localEntity.setDate(str3);
                localResultSet.close();
                Performance.addPhysDBQuery();
                return localEntity;
            }
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public Entity loadProtectedEntity(String world, int paramInt1, int paramInt2, int paramInt3) {
        try {
            this._select_protectedEntity_x_y_z.setInt(1, paramInt1);
            this._select_protectedEntity_x_y_z.setInt(2, paramInt2);
            this._select_protectedEntity_x_y_z.setInt(3, paramInt3);
            this._select_protectedEntity_x_y_z.setString(4, world);
            ResultSet localResultSet = this._select_protectedEntity_x_y_z.executeQuery();
            if (localResultSet.next()) {
                int i = localResultSet.getInt("id");
                int j = localResultSet.getInt("type");
                String str1 = localResultSet.getString("owner");
                String str2 = localResultSet.getString("password");
                String str3 = localResultSet.getString("date");
                Entity localEntity = new Entity();
                localEntity.setID(i);
                localEntity.setType(j);
                localEntity.setOwner(str1);
                localEntity.setPassword(str2);
                localEntity.setX(paramInt1);
                localEntity.setY(paramInt2);
                localEntity.setZ(paramInt3);
                localEntity.setWorld(world);
                localEntity.setDate(str3);
                localResultSet.close();
                Performance.addPhysDBQuery();
                return localEntity;
            }
            localResultSet.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public void registerProtectedEntity(String world, int paramInt1, String paramString1, String paramString2,
            int paramInt2, int paramInt3, int paramInt4) {
        try {
            this._insert_protectedEntity_type_player_password_x_y_z.setInt(1, paramInt1);
            this._insert_protectedEntity_type_player_password_x_y_z.setString(2, paramString1);
            this._insert_protectedEntity_type_player_password_x_y_z.setString(3, paramString2);
            this._insert_protectedEntity_type_player_password_x_y_z.setInt(4, paramInt2);
            this._insert_protectedEntity_type_player_password_x_y_z.setInt(5, paramInt3);
            this._insert_protectedEntity_type_player_password_x_y_z.setInt(6, paramInt4);
            this._insert_protectedEntity_type_player_password_x_y_z.setString(7,
                    new Timestamp(new Date().getTime()).toString());
            this._insert_protectedEntity_type_player_password_x_y_z.setString(8, world);
            this._insert_protectedEntity_type_player_password_x_y_z.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void registerProtectionLimit(int paramInt1, int paramInt2, String paramString) {
        try {
            unregisterProtectionLimit(paramInt1, paramString.toLowerCase());
            this._insert_protectedLimit_type_amount_entity.setInt(1, paramInt1);
            this._insert_protectedLimit_type_amount_entity.setInt(2, paramInt2);
            this._insert_protectedLimit_type_amount_entity.setString(3, paramString.toLowerCase());
            this._insert_protectedLimit_type_amount_entity.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void registerProtectionRights(int paramInt1, String paramString, int paramInt2, int paramInt3) {
        try {
            this._insert_rights_ID_entity_rights_type.setInt(1, paramInt1);
            this._insert_rights_ID_entity_rights_type.setString(2, paramString.toLowerCase());
            this._insert_rights_ID_entity_rights_type.setInt(3, paramInt2);
            this._insert_rights_ID_entity_rights_type.setInt(4, paramInt3);
            this._insert_rights_ID_entity_rights_type.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectedEntity(int paramInt) {
        try {
            this._delete_protectedEntity_ID.setInt(1, paramInt);
            this._delete_protectedEntity_ID.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        unregisterProtectionRights(paramInt);
    }

    public void unregisterProtectedEntity(String world, int paramInt1, int paramInt2, int paramInt3) {
        try {
            this._delete_protectedEntity_x_y_z.setInt(1, paramInt1);
            this._delete_protectedEntity_x_y_z.setInt(2, paramInt2);
            this._delete_protectedEntity_x_y_z.setInt(3, paramInt3);
            this._delete_protectedEntity_x_y_z.setString(4, world);
            this._delete_protectedEntity_x_y_z.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectionEntities() {
        try {
            Statement localStatement = this.connection.createStatement();
            localStatement.executeUpdate("DELETE FROM `protections`");
            localStatement.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectionLimit(int paramInt, String paramString) {
        try {
            this._delete_limit_type_entity.setInt(1, paramInt);
            this._delete_limit_type_entity.setString(2, paramString.toLowerCase());
            this._delete_limit_type_entity.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectionLimits() {
        try {
            Statement localStatement = this.connection.createStatement();
            localStatement.executeUpdate("DELETE FROM `limits`");
            localStatement.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectionRights() {
        try {
            Statement localStatement = this.connection.createStatement();
            localStatement.executeUpdate("DELETE FROM `rights`");
            localStatement.close();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectionRights(int paramInt) {
        try {
            this._delete_rights_ID.setInt(1, paramInt);
            this._delete_rights_ID.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void unregisterProtectionRights(int paramInt, String paramString) {
        try {
            this._delete_rights_ID_entity.setInt(1, paramInt);
            this._delete_rights_ID_entity.setString(2, paramString.toLowerCase());
            this._delete_rights_ID_entity.executeUpdate();
            Performance.addPhysDBQuery();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void doUpdate140() {
        try {
            Statement localStatement1 = this.connection.createStatement();
            localStatement1.executeQuery("SELECT `id` FROM `protections`");
            localStatement1.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException1) {
            log("Outdated database!");
            log("UPGRADING FROM 1.30 TO 1.40");
            log("Renaming table `chests` to `protections`");
            try {
                Statement localStatement2 = this.connection.createStatement();
                localStatement2.executeUpdate("ALTER TABLE `chests` RENAME TO `protections`");
                localStatement2.close();
                Performance.addPhysDBQuery();
            } catch (Exception localException2) {
                localException2.printStackTrace();
            }
        }
    }

    private void doUpdate150() {
        try {
            Statement localStatement1 = this.connection.createStatement();
            localStatement1.executeQuery("SELECT `world` FROM `protections`");
            localStatement1.close();
            Performance.addPhysDBQuery();
        } catch (Exception localException1) {
            log("Outdated database!");
            log("UPGRADING FROM 1.40 TO 1.50");
            log("ALTERING TABLE `protections` AND FILLING WITH DEFAULT DATA");
            try {
                Statement localStatement2 = this.connection.createStatement();
                localStatement2.addBatch("ALTER TABLE `protections` ADD `world` TEXT");
                localStatement2.addBatch("UPDATE `protections` SET `world`='NORMAL'");
                localStatement2.executeBatch();
                localStatement2.close();
                Performance.addPhysDBQuery();
            } catch (Exception localException2) {
                log("Oops! Something went wrong: ");
                localException1.printStackTrace();
                System.exit(0);
            }
            log("Update completed!");
        }
    }

    private void loadPreparedStatements() throws SQLException {
        this._select_protectedEntity_ID = this.connection
                .prepareStatement("SELECT * FROM `protections` WHERE `id` = ?");
        this._select_chestExist_ID = this.connection
                .prepareStatement("SELECT COUNT(*) AS count FROM `protections` WHERE `id` = ?");
        this._select_chestCount_user = this.connection
                .prepareStatement("SELECT `id` FROM `protections` WHERE `owner` = ?");
        this._select_limit_type_entity = this.connection
                .prepareStatement("SELECT `amount` FROM `limits` WHERE `type` = ? AND `entity` = ?");
        this._select_privateAccess_type_ID_entities = this.connection
                .prepareStatement("SELECT `entity`, `rights` FROM `rights` WHERE `type` = ? AND `chest` = ?");
        this._select_protectedEntity_x_y_z_radius = this.connection
                .prepareStatement("SELECT * FROM `protections` WHERE x >= ? AND x <= ? AND y >= ? AND y <= ? AND z >= ? AND z <= ? AND `world` = ?");
        this._select_protectedEntity_x_y_z = this.connection
                .prepareStatement("SELECT `id`, `type`, `owner`, `password`, `date` FROM `protections` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?");
        this._insert_protectedEntity_type_player_password_x_y_z = this.connection
                .prepareStatement("INSERT INTO `protections` (type, owner, password, x, y, z, date,world) VALUES(?, ?, ?, ?, ?, ?, ?,?)");
        this._insert_protectedLimit_type_amount_entity = this.connection
                .prepareStatement("INSERT INTO `limits` (type, amount, entity) VALUES(?, ?, ?)");
        this._insert_rights_ID_entity_rights_type = this.connection
                .prepareStatement("INSERT INTO `rights` (chest, entity, rights, type) VALUES (?, ?, ?, ?)");
        this._delete_protectedEntity_ID = this.connection.prepareStatement("DELETE FROM `protections` WHERE `id` = ?");
        this._delete_protectedEntity_x_y_z = this.connection
                .prepareStatement("DELETE FROM `protections` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?");
        this._delete_limit_type_entity = this.connection
                .prepareStatement("DELETE FROM `limits` WHERE `type` = ? AND `entity` = ?");
        this._delete_rights_ID = this.connection.prepareStatement("DELETE FROM `rights` WHERE `chest` = ?");
        this._delete_rights_ID_entity = this.connection
                .prepareStatement("DELETE FROM `rights` WHERE `chest` = ? AND `entity` = ?");
    }
}
