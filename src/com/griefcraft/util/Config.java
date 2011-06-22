/*    */ package com.griefcraft.util;
/*    */ 
/*    */ import com.griefcraft.logging.Logger;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class Config extends Properties
/*    */ {
/* 58 */   private Logger logger = Logger.getLogger(getClass().getSimpleName());
/*    */   private static Config instance;
/*    */ 
/*    */   public static void destroy()
/*    */   {
/* 36 */     instance = null;
/*    */   }
/*    */ 
/*    */   public static Config getInstance()
/*    */   {
/* 43 */     return instance;
/*    */   }
/*    */ 
/*    */   public static void init()
/*    */   {
/* 50 */     if (instance == null)
/* 51 */       instance = new Config();
/*    */   }
/*    */ 
/*    */   private Config()
/*    */   {
/* 69 */     for (ConfigValues value : ConfigValues.values()) {
/* 70 */       setProperty(value.getName(), value.getDefaultValue());
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 79 */       File conf = new File("lwc.properties");
/*    */ 
/* 81 */       if (!conf.exists()) {
/* 82 */         save();
/* 83 */         return;
/*    */       }
/*    */ 
/* 86 */       Object inputStream = new FileInputStream(conf);
/* 87 */       load((InputStream)inputStream);
/* 88 */       ((InputStream)inputStream).close();
/*    */ 
/* 90 */       this.logger.info("Loaded " + size() + " config entries");
/*    */     } catch (Exception e) {
/* 92 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void save() {
/*    */     try {
/* 98 */       File file = new File("lwc.properties");
/*    */ 
/* 100 */       if (!file.exists()) {
/* 101 */         file.createNewFile();
/*    */       }
/*    */ 
/* 104 */       OutputStream outputStream = new FileOutputStream(file);
/*    */ 
/* 106 */       store(outputStream, "# LWC configuration file\n\n# + Github project page: https://github.com/Hidendra/LWC\n# + hMod thread link: http://forum.hey0.net/showthread.php?tid=837\n");
/* 107 */       outputStream.close();
/*    */     } catch (Exception e) {
/* 109 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.util.Config
 * JD-Core Version:    0.6.0
 */