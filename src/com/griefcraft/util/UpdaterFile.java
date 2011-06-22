/*    */ package com.griefcraft.util;
/*    */ 
/*    */ public class UpdaterFile
/*    */ {
/*    */   private String remoteLocation;
/*    */   private String localLocation;
/*    */ 
/*    */   public UpdaterFile(String location)
/*    */   {
/* 16 */     this.remoteLocation = location;
/* 17 */     this.localLocation = location;
/*    */   }
/*    */ 
/*    */   public String getRemoteLocation()
/*    */   {
/* 24 */     return this.remoteLocation;
/*    */   }
/*    */ 
/*    */   public String getLocalLocation()
/*    */   {
/* 31 */     return this.localLocation;
/*    */   }
/*    */ 
/*    */   public void setRemoteLocation(String remoteLocation)
/*    */   {
/* 40 */     this.remoteLocation = remoteLocation;
/*    */   }
/*    */ 
/*    */   public void setLocalLocation(String localLocation)
/*    */   {
/* 49 */     this.localLocation = localLocation;
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.util.UpdaterFile
 * JD-Core Version:    0.6.0
 */