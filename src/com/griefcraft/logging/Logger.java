/*    */ package com.griefcraft.logging;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class Logger
/*    */ {
/*    */   private String name;
/*    */ 
/*    */   public static Logger getLogger(String name)
/*    */   {
/* 14 */     return new Logger(name);
/*    */   }
/*    */ 
/*    */   private Logger(String name)
/*    */   {
/* 23 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public void info(String str)
/*    */   {
/* 32 */     log(str);
/*    */   }
/*    */ 
/*    */   public void log(String str)
/*    */   {
/* 41 */     System.out.println(format(str));
/*    */   }
/*    */ 
/*    */   private String format(String msg)
/*    */   {
/* 51 */     return String.format("%s\t[%s]\t%s", new Object[] { this.name, com.griefcraft.LWCInfo.VERSION, msg });
/*    */   }
/*    */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     com.griefcraft.logging.Logger
 * JD-Core Version:    0.6.0
 */