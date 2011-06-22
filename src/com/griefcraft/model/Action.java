package com.griefcraft.model;

public class Action {
    public int id;
    private String action;
    private String player;
    private int chestID;
    private String data;

    public String getAction() {
        return this.action;
    }

    public int getChestID() {
        return this.chestID;
    }

    public String getData() {
        return this.data;
    }

    public int getID() {
        return this.id;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setAction(String paramString) {
        this.action = paramString;
    }

    public void setChestID(int paramInt) {
        this.chestID = paramInt;
    }

    public void setData(String paramString) {
        this.data = paramString;
    }

    public void setID(int paramInt) {
        this.id = paramInt;
    }

    public void setPlayer(String paramString) {
        this.player = paramString;
    }
}
