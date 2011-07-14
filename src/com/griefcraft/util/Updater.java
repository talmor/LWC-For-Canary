/*     */ package com.griefcraft.util;
/*     */ 
/*     */ import com.griefcraft.logging.Logger;
///*     */ import com.sun.net.ssl.internal.ssl.Provider;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.security.Security;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Updater
/*     */ {
/*  42 */   private Logger logger = Logger.getLogger(getClass().getSimpleName());
/*     */   //private static final String UPDATE_SITE = "https://github.com/Hidendra/LWC/raw/master/";
            private static final String UPDATE_SITE = "https://github.com/Hidendra/LWC/raw/master/";
/*     */   private static final String VERSION_FILE = "VERSION";
/*     */   private static final String DIST_FILE = "dist/LWC.jar";
/*  62 */   private List<UpdaterFile> needsUpdating = new ArrayList();
/*     */ 
/*     */   public Updater() {
/*  65 */     //enableSSL();
/*     */   }
/*     */ 
/*     */   public void check()
/*     */   {
/*  74 */     String[] paths = { "lib/sqlite.jar", "lib/" + getOSSpecificFileName() };
/*     */ 
/*  76 */     for (String path : paths) {
/*  77 */       File file = new File(path);
/*     */ 
/*  79 */       if ((file != null) && (!file.exists()) && (!file.isDirectory())) {
/*  80 */         UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + path);
/*  81 */         updaterFile.setLocalLocation(path);
/*     */ 
/*  83 */         this.needsUpdating.add(updaterFile);
/*     */       }
/*     */     }
/*     */ 
/*  87 */     double latestVersion = getLatestVersion();
/*     */ 
/*  89 */     if (latestVersion > 1.44D) {
/*  90 */       this.logger.info("Update detected for LWC");
/*  91 */       this.logger.info("Latest version: " + latestVersion);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean checkDist()
/*     */   {
/* 102 */     double latestVersion = getLatestVersion();
/*     */ 
/* 104 */     if (latestVersion > 1.44D) {
/* 105 */       UpdaterFile updaterFile = new UpdaterFile(UPDATE_SITE + DIST_FILE);
/* 106 */       updaterFile.setLocalLocation("plugins/LWC.jar");
/*     */ 
/* 108 */       this.needsUpdating.add(updaterFile);
/*     */       try
/*     */       {
/* 111 */         update();
/* 112 */         this.logger.info("Updated successful");
/* 113 */         return true;
/*     */       } catch (Exception e) {
/* 115 */         this.logger.info("Update failed: " + e.getMessage());
/* 116 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 120 */     return false;
/*     */   }
/*     */ 
/*     */   public double getLatestVersion()
/*     */   {
/*     */     try
/*     */     {
/* 130 */       URL url = new URL(UPDATE_SITE+VERSION_FILE);
/*     */ 
/* 132 */       InputStream inputStream = url.openStream();
/* 133 */       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
/*     */ 
/* 135 */       double version = Double.parseDouble(bufferedReader.readLine());
/*     */ 
/* 137 */       bufferedReader.close();
/*     */ 
/* 139 */       return version;
/*     */     } catch (Exception e) {
/* 141 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 144 */     return 0.0D;
/*     */   }
/*     */ 
///*     */   private void enableSSL()
///*     */   {
///* 151 */     Security.addProvider(new Provider());
///* 152 */     System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
///*     */   }
/*     */ 
/*     */   public String getOSSpecificFileName()
/*     */   {
/* 161 */     String osname = System.getProperty("os.name").toLowerCase();
/* 162 */     String arch = System.getProperty("os.arch");
/*     */ 
/* 164 */     if (osname.contains("windows")) {
/* 165 */       osname = "win";
/* 166 */       arch = "x86";
/* 167 */     } else if (osname.contains("mac")) {
/* 168 */       osname = "mac";
/* 169 */       arch = "universal";
/* 170 */     } else if (osname.contains("nix")) {
/* 171 */       osname = "linux";
/* 172 */     } else if (osname.equals("sunos")) {
/* 173 */       osname = "linux";
/*     */     }
/*     */ 
/* 176 */     if ((arch.startsWith("i")) && (arch.endsWith("86"))) {
/* 177 */       arch = "x86";
/*     */     }
/*     */ 
/* 180 */     return osname + "-" + arch + ".lib";
/*     */   }
/*     */ 
/*     */   public void update()
/*     */     throws Exception
/*     */   {
/* 187 */     if (this.needsUpdating.size() == 0) {
/* 188 */       return;
/*     */     }
/*     */ 
/* 191 */     File folder = new File("lib");
/*     */ 
/* 193 */     if ((folder.exists()) && (!folder.isDirectory()))
/* 194 */       throw new Exception("Folder \"lib\" cannot be created ! It is a file!");
/* 195 */     if (!folder.exists()) {
/* 196 */       this.logger.info("Creating folder : lib");
/* 197 */       folder.mkdir();
/*     */     }
/*     */ 
/* 200 */     this.logger.info("Need to download " + this.needsUpdating.size() + " object(s)");
/*     */ 
/* 202 */     for (UpdaterFile item : this.needsUpdating) {
/* 203 */       this.logger.info(" - Downloading file : " + item.getRemoteLocation());
/*     */ 
/* 205 */       URL url = new URL(item.getRemoteLocation());
/* 206 */       File file = new File(item.getLocalLocation());
/*     */ 
/* 208 */       if (file.exists()) {
/* 209 */         file.delete();
/*     */       }
/*     */ 
/* 212 */       InputStream inputStream = url.openStream();
/* 213 */       OutputStream outputStream = new FileOutputStream(file);
/*     */ 
/* 215 */       saveTo(inputStream, outputStream);
/*     */ 
/* 217 */       inputStream.close();
/* 218 */       outputStream.close();
/*     */ 
/* 220 */       this.logger.info("  + Download complete");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveTo(InputStream inputStream, OutputStream outputStream)
/*     */     throws IOException
/*     */   {
/* 231 */     byte[] buffer = new byte[1024];
/* 232 */     int len = 0;
/*     */ 
/* 234 */     while ((len = inputStream.read(buffer)) > 0)
/* 235 */       outputStream.write(buffer, 0, len);
/*     */   }
/*     */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.util.Updater
 * JD-Core Version:    0.6.0
 */