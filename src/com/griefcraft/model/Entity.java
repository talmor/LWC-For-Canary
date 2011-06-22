package com.griefcraft.model;

public class Entity
{
  private int id;
  private int type;
  private String owner;
  private String password;
  private int x;
  private int y;
  private int z;
  private String date;
  private String world;

  public String getWorld() {
	return world;
}

public void setWorld(String world) {
	this.world = world;
}

public String getDate()
  {
    return this.date;
  }

  public int getID()
  {
    return this.id;
  }

  public String getOwner()
  {
    return this.owner;
  }

  public String getPassword()
  {
    return this.password;
  }

  public int getType()
  {
    return this.type;
  }

  public int getX()
  {
    return this.x;
  }

  public int getY()
  {
    return this.y;
  }

  public int getZ()
  {
    return this.z;
  }

  public void setDate(String paramString)
  {
    this.date = paramString;
  }

  public void setID(int paramInt)
  {
    this.id = paramInt;
  }

  public void setOwner(String paramString)
  {
    this.owner = paramString;
  }

  public void setPassword(String paramString)
  {
    this.password = paramString;
  }

  public void setType(int paramInt)
  {
    this.type = paramInt;
  }

  public void setX(int paramInt)
  {
    this.x = paramInt;
  }

  public void setY(int paramInt)
  {
    this.y = paramInt;
  }

  public void setZ(int paramInt)
  {
    this.z = paramInt;
  }
}

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.model.Entity
 * JD-Core Version:    0.6.0
 */