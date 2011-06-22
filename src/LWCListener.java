/*     */ import com.griefcraft.model.Action;
/*     */ import com.griefcraft.model.Entity;
/*     */ import com.griefcraft.sql.MemDB;
/*     */ import com.griefcraft.sql.PhysDB;
/*     */ import com.griefcraft.util.ConfigValues;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
import java.util.List;
/*     */ 
/*     */ public class LWCListener extends PluginListener
/*     */ {
/*     */   private final LWC lwc;
/*     */   private static final int BLAST_RADIUS = 4;
/*  45 */   public boolean debugMode = false;
/*     */   private PhysDB physicalDatabase;
/*     */   private MemDB memoryDatabase;
/*     */ 
/*     */   public LWCListener(LWC paramLWC)
/*     */   {
/*  58 */     this.lwc = paramLWC;
/*  59 */     this.physicalDatabase = paramLWC.getPhysicalDatabase();
/*  60 */     this.memoryDatabase = paramLWC.getMemoryDatabase();
/*     */   }
/*     */ 
/*     */   public boolean onBlockBreak(Player paramPlayer, Block paramBlock)
/*     */   {
 			  paramBlock = paramPlayer.getWorld().getBlockAt(paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
			  if (paramPlayer.isAdmin()) {
				  paramPlayer.sendMessage("Tipo de bloque (break): "+String.valueOf(paramBlock.getType()));
			  }
			  
/*  65 */     if (!isProtectable(paramBlock)) {
/*  66 */       return false;
/*     */     }
/*     */ 
/*  69 */     List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(),paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
/*  70 */     boolean bool1 = true;
/*  71 */     boolean bool2 = true;
/*  72 */     Entity localEntity = null;
/*     */ 
/*  74 */     for (ComplexBlock localComplexBlock : localList) {
/*  75 */       if (localComplexBlock == null)
/*     */       {
/*     */         continue;
/*     */       }
				String world = paramPlayer.getWorld().getType().name();
/*  79 */       localEntity = this.physicalDatabase.loadProtectedEntity(world,localComplexBlock.getX(), localComplexBlock.getY(), localComplexBlock.getZ());
/*     */ 
/*  81 */       if (localEntity == null)
/*     */       {
/*     */         continue;
/*     */       }
/*  85 */       bool1 = this.lwc.canAccessChest(paramPlayer, localEntity);
/*  86 */       bool2 = this.lwc.canAdminChest(paramPlayer, localEntity);
/*     */     }
/*     */ 
/*  89 */     if ((bool1) && (localEntity != null) && 
/*  90 */       (bool2)) {
				String world = paramPlayer.getWorld().getType().name();
/*  91 */       this.physicalDatabase.unregisterProtectedEntity(world,localEntity.getX(), localEntity.getY(), localEntity.getZ());
/*  92 */       this.physicalDatabase.unregisterProtectionRights(localEntity.getID());
/*  93 */       paramPlayer.sendMessage("§4Chest unregistered.");
/*     */     }
/*     */ 
/*  97 */     return !bool2;
/*     */   }
/*     */ 
/*     */   public boolean onBlockDestroy(Player paramPlayer, Block paramBlock)
/*     */   {
			  // work around to crow problem
			  paramBlock = paramPlayer.getWorld().getBlockAt(paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
			  if (paramPlayer.isAdmin()) {
				  paramPlayer.sendMessage("Tipo de bloque: "+String.valueOf(paramBlock.getType()));
			  }
			  
/* 105 */     if (!isProtectable(paramBlock)) {
/* 106 */       return false;
/*     */     }				
/*     */ 
/* 109 */     List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(),paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
if (paramPlayer.isAdmin()) {
	  paramPlayer.sendMessage("Local list count: "+String.valueOf(localList.size()));
}

/* 110 */     boolean bool1 = true;
/* 111 */     Entity localEntity = null;
/* 112 */     int i = 1;
/*     */ 

/* 114 */     for (Iterator<ComplexBlock> iBlocks = localList.iterator(); iBlocks.hasNext(); ) { 
				ComplexBlock localComplexBlock = iBlocks.next();
/* 115 */       if (localComplexBlock == null)
/*     */       {
/*     */         continue;
/*     */       }
				String world = paramPlayer.getWorld().getType().name();
/* 119 */       localEntity = this.physicalDatabase.loadProtectedEntity(world,localComplexBlock.getX(), localComplexBlock.getY(), localComplexBlock.getZ());
/*     */ 
/* 121 */       if (localEntity == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 125 */       bool1 = this.lwc.canAccessChest(paramPlayer, localEntity);
/* 126 */       i = 0;
/*     */     }
/*     */ 
/* 130 */     if (paramBlock.getStatus() != 0) {
/* 131 */       return !bool1;
/*     */     }
/*     */ 
/* 134 */     List<String> localObject1 = this.memoryDatabase.getActions(paramPlayer.getName());
/*     */ 
/* 136 */     boolean bool2 = localObject1.contains("free");
/* 137 */     boolean bool3 = localObject1.contains("info");
/* 138 */     boolean bool4 = localObject1.contains("create");
/* 139 */     boolean bool5 = localObject1.contains("modify");
/* 140 */     boolean bool6 = localObject1.contains("dropTransferSelect");
/*     */     Object localObject2;
/*     */     Object localObject3;
/*     */     Action localAction;
/*     */     //Object localObject5;
/*     */     int n;
/*     */     int i1;
/* 142 */     if (localEntity != null) {
/* 143 */       i = 0;
/*     */ 
/* 145 */       if (bool3) {
/* 146 */         String str1 = "";
/*     */ 
/* 150 */         if (localEntity.getType() == 1) {
/* 151 */           localObject2 = this.memoryDatabase.getSessionUsers(localEntity.getID());
/*     */ 
/* 156 */           for (localObject3 = ((List)localObject2).iterator(); ((Iterator)localObject3).hasNext(); ) { String str2 = (String)((Iterator)localObject3).next();
/* 157 */             Player localPlayer = etc.getServer().getPlayer(str2);
/*     */ 
/* 159 */             if (localPlayer == null)
/*     */             {
/*     */               continue;
/*     */             }
/* 163 */             str1 = new StringBuilder().append(str1).append(localPlayer.getColor()).append(str2).append("§f").append(", ").toString();
/*     */           }
/*     */ 
/* 166 */           if (((List)localObject2).size() > 0) {
/* 167 */             str1 = str1.substring(0, str1.length() - 4);
/*     */           }
/*     */         }
/*     */ 
/* 171 */         localObject3 = " ";
/*     */ 
/* 173 */         switch (localEntity.getType()) {
/*     */         case 0:
/* 175 */           localObject3 = "Public";
/* 176 */           break;
/*     */         case 1:
/* 178 */           localObject3 = "Password";
/* 179 */           break;
/*     */         case 2:
/* 181 */           localObject3 = "Private";
/*     */         }
/*     */ 
/* 185 */         boolean bool8 = this.lwc.canAdminChest(paramPlayer, localEntity);
/*     */ 
/* 189 */         if (bool8) {
/* 190 */           paramPlayer.sendMessage(new StringBuilder().append("§2ID: §6").append(localEntity.getID()).toString());
/*     */         }
/*     */ 
/* 193 */         paramPlayer.sendMessage(new StringBuilder().append("§2Type: §6").append((String)localObject3).toString());
/* 194 */         paramPlayer.sendMessage(new StringBuilder().append("§2Owner: §6").append(localEntity.getOwner()).toString());
/*     */ 
/* 196 */         if ((localEntity.getType() == 1) && (bool8)) {
/* 197 */           paramPlayer.sendMessage(new StringBuilder().append("§2Authed players: ").append(str1).toString());
/*     */         }
/*     */ 
/* 200 */         if (bool8) {
					String world = com.griefcraft.util.StringUtils.capitalizeFirstLetter(localEntity.getWorld());
					paramPlayer.sendMessage(new StringBuilder().append("§2World: §6").append(world).toString());
/* 201 */           paramPlayer.sendMessage(new StringBuilder().append("§2Location: §6{").append(localEntity.getX()).append(", ").append(localEntity.getY()).append(", ").append(localEntity.getZ()).append("}").toString());
/* 202 */           paramPlayer.sendMessage(new StringBuilder().append("§2Date created: §6").append(localEntity.getDate()).toString());
/*     */         }
/*     */ 
/* 205 */         if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 206 */           this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */         }
/* 208 */         return false;
/* 209 */       }if (bool6) {
/* 210 */         boolean bool7 = this.lwc.canAccessChest(paramPlayer, localEntity);
/* 211 */         if (!bool7) {
/* 212 */           paramPlayer.sendMessage("§4You cannot use a chest that you cannot access as a drop transfer target.");
/* 213 */           paramPlayer.sendMessage("§4If this is a passworded chest, please unlock it before retrying.");
/* 214 */           paramPlayer.sendMessage("§4Use \"/lwc droptransfer select\" to try again.");
/*     */         } else {
/* 216 */           for (localObject2 = localList.iterator(); ((Iterator)localObject2).hasNext(); ) { localObject3 = (ComplexBlock)((Iterator)localObject2).next();
/* 217 */             if ((!(localObject3 instanceof Chest)) && (!(localObject3 instanceof DoubleChest))) {
/* 218 */               paramPlayer.sendMessage("§4You need to select a chest as the Drop Transfer target!");
/* 219 */               this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/* 220 */               return false;
/*     */             }
/*     */           }
/*     */ 
/* 224 */           this.memoryDatabase.registerMode(paramPlayer.getName(), "dropTransfer", new StringBuilder().append("f").append(localEntity.getID()).toString());
/* 225 */           paramPlayer.sendMessage("§2Successfully registered chest as drop transfer target.");
/*     */         }
/* 227 */         this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */ 
/* 229 */         return false;
/* 230 */       }if (bool2) {
/* 231 */         if ((this.lwc.isAdmin(paramPlayer)) || (localEntity.getOwner().equals(paramPlayer.getName()))) {
/* 232 */           paramPlayer.sendMessage("§aRemoved lock on the chest succesfully!");
					String world = paramPlayer.getWorld().getType().name();
/* 233 */           this.physicalDatabase.unregisterProtectedEntity(world,localEntity.getX(), localEntity.getY(), localEntity.getZ());
/* 234 */           this.physicalDatabase.unregisterProtectionRights(localEntity.getID());
/* 235 */           if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 236 */             this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */           }
/* 238 */           return false;
/*     */         }
/* 240 */         paramPlayer.sendMessage("§4You do not own that chest!");
/* 241 */         if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 242 */           this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */         }
/* 244 */         return true;
/*     */       }
/* 246 */       if (bool5) {
/* 247 */         if (this.lwc.canAdminChest(paramPlayer, localEntity)) {
/* 248 */           localAction = this.memoryDatabase.getAction("modify", paramPlayer.getName());
/*     */ 
/* 250 */           localObject2 = localAction.getData();
					String[] splits = {""};
/* 251 */           localObject3 = new String[0];
/*     */ 
/* 253 */           if (((String)localObject2).length() > 0) {
/* 254 */             splits= ((String)localObject2).split(" ");
/*     */           }
/*     */ 
/* 257 */           if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 258 */             this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */           }
/*     */ 
/* 261 */           for (String localObject5 : splits) {
/* 262 */             int m = 0;
/* 263 */             n = 0;
/* 264 */             i1 = 1;
/*     */ 
/* 266 */             if (((String)localObject5).startsWith("-")) {
/* 267 */               m = 1;
/* 268 */               localObject5 = ((String)localObject5).substring(1);
/*     */             }
/*     */ 
/* 271 */             if (((String)localObject5).startsWith("@")) {
/* 272 */               n = 1;
/* 273 */               localObject5 = ((String)localObject5).substring(1);
/*     */             }
/*     */ 
/* 276 */             if (((String)localObject5).toLowerCase().startsWith("g:")) {
/* 277 */               i1 = 0;
/* 278 */               localObject5 = ((String)localObject5).substring(2);
/*     */             }
/*     */ 			
					  String world = paramPlayer.getWorld().getType().name();
/* 281 */             int i2 = this.physicalDatabase.loadProtectedEntity(world,paramBlock.getX(), paramBlock.getY(), paramBlock.getZ()).getID();
/*     */ 
/* 283 */             if (m == 0) {
/* 284 */               this.physicalDatabase.unregisterProtectionRights(i2, (String)localObject5);
/* 285 */               this.physicalDatabase.registerProtectionRights(i2, (String)localObject5, n != 0 ? 1 : 0, i1);
/* 286 */               paramPlayer.sendMessage(new StringBuilder().append("§aRegistered rights for §6").append((String)localObject5).append("§2").append(" ").append(n != 0 ? "[§4ADMIN§6]" : "").append(" [").append(i1 == 1 ? "Player" : "Group").append("]").toString());
/*     */             } else {
/* 288 */               this.physicalDatabase.unregisterProtectionRights(i2, (String)localObject5);
/* 289 */               paramPlayer.sendMessage(new StringBuilder().append("§aRemoved rights for §6").append((String)localObject5).append("§2").append(" [").append(i1 == 1 ? "Player" : "Group").append("]").toString());
/*     */             }
/*     */           }
/*     */ 
/* 293 */           return false;
/*     */         }
/* 295 */         paramPlayer.sendMessage("§4You do not own that chest!");
/* 296 */         if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 297 */           this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */         }
/* 299 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 304 */     if (bool6) {
/* 305 */       paramPlayer.sendMessage("§4Cannot select unregistered chest as drop transfer target.");
/* 306 */       paramPlayer.sendMessage("§4Use \"/lwc droptransfer select\" to try again.");
/* 307 */       this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */ 
/* 310 */       return false;
/*     */     }
/*     */ 
/* 313 */     if ((bool3) || (bool2)) {
/* 314 */       paramPlayer.sendMessage("§4Chest is unregistered");
/* 315 */       if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 316 */         this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */       }
/* 318 */       return false;
/*     */     }
/*     */ 
/* 321 */     if (((bool4) || (bool5)) && (i == 0)) {
/* 322 */       if (!this.lwc.canAdminChest(paramPlayer, localEntity))
/* 323 */         paramPlayer.sendMessage("§4You do not own that chest!");
/*     */       else {
/* 325 */         paramPlayer.sendMessage("§4You have already registered that chest!");
/*     */       }
/* 327 */       if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 328 */         this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */       }
/* 330 */       return true;
/*     */     }
/*     */ 
/* 333 */     if ((i != 0) && (bool4)) {
/* 334 */       localAction = this.memoryDatabase.getAction("create", paramPlayer.getName());
/*     */ 
/* 336 */       localObject2 = localAction.getData();
/* 337 */       String[] split = ((String)localObject2).split(" ");
/* 338 */       String cmd = split[0].toLowerCase();
/* 339 */       String str3 = "";
/*     */ 
/* 341 */       if (split.length > 1) {
/* 342 */         for (int i2 = 1; i2 < split.length; i2++) {
/* 343 */           str3 = new StringBuilder().append(str3).append(split[i2]).append(" ").toString();
/*     */         }
/*     */       }
/*     */ 
/* 347 */       if (this.lwc.enforceChestLimits(paramPlayer)) {
/* 348 */         if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 349 */           this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */         }
/* 351 */         return false;
/*     */       }
/*     */ 
/* 354 */       if ((!this.lwc.isAdmin(paramPlayer)) && (this.lwc.isInCuboidSafeZone(paramPlayer))) {
/* 355 */         paramPlayer.sendMessage("§4You need to be in a Cuboid-protected safe zone to do that!");
/* 356 */         this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/* 357 */         return false;
/*     */       }
/*     */ 
/* 360 */       if (cmd.equals("public")) {
				  String world = paramPlayer.getWorld().getType().name();
/* 361 */         this.physicalDatabase.registerProtectedEntity(world,0, paramPlayer.getName(), "", paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
/* 362 */         paramPlayer.sendMessage("§2Created public protection successfully");
/*     */       }
/*     */       else
/*     */       {
/*     */         String str4;
/*     */         Object localObject6;
/* 363 */         if (cmd.equals("password")) {
/* 364 */           str4 = localAction.getData().substring("password ".length());
/* 365 */           str4 = this.lwc.encrypt(str4);
/*     */ 
					String world = paramPlayer.getWorld().getType().name();
/* 367 */           this.physicalDatabase.registerProtectedEntity(world,1, paramPlayer.getName(), str4, paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
/* 368 */           this.memoryDatabase.registerPlayer(paramPlayer.getName(), this.physicalDatabase.loadProtectedEntity(world,paramBlock.getX(), paramBlock.getY(), paramBlock.getZ()).getID());
/* 369 */           paramPlayer.sendMessage("§2Created password protection successfully");
/* 370 */           paramPlayer.sendMessage("§aFor convenience, you don't have to enter your password until");
/* 371 */           paramPlayer.sendMessage("§ayou next log in");
/*     */ 

/* 373 */           for (Iterator it = localList.iterator(); it.hasNext(); ) { 
						localObject6 = (ComplexBlock)it.next();
/* 374 */             if (localObject6 != null) {
/* 375 */               ((ComplexBlock)localObject6).update();
/*     */             }
/*     */           }
/*     */         }
/* 379 */         else if (cmd.equals("private")) {
/* 380 */           str4 = localAction.getData();
/* 381 */           String[] split2 = new String[0];
/*     */ 
/* 383 */           if (str4.length() > "private ".length()) {
/* 384 */             str4 = str4.substring("private ".length());
/* 385 */             split2 = str4.split(" ");
/*     */           }
/*     */ 
					String world = paramPlayer.getWorld().getType().name();
/* 388 */           this.physicalDatabase.registerProtectedEntity(world,2, paramPlayer.getName(), "", paramBlock.getX(), paramBlock.getY(), paramBlock.getZ());
/*     */ 
/* 390 */           paramPlayer.sendMessage("§2Created private protection successfully");
/*     */ 
/* 392 */           for (String str5 : split2) {
/* 393 */             int i3 = 0;
/* 394 */             int i4 = 1;
/*     */ 
/* 396 */             if (str5.startsWith("@")) {
/* 397 */               i3 = 1;
/* 398 */               str5 = str5.substring(1);
/*     */             }
/*     */ 
/* 401 */             if (str5.toLowerCase().startsWith("g:")) {
/* 402 */               i4 = 0;
/* 403 */               str5 = str5.substring(2);
/*     */             }
/*     */ 
/* 406 */             this.physicalDatabase.registerProtectionRights(this.physicalDatabase.loadProtectedEntity(world,paramBlock.getX(), paramBlock.getY(), paramBlock.getZ()).getID(), str5, i3 != 0 ? 1 : 0, i4);
/* 407 */             paramPlayer.sendMessage(new StringBuilder().append("§aRegistered rights for §6").append(str5).append(": ").append(i3 != 0 ? "[§4ADMIN§6]" : "").append(" [").append(i4 == 1 ? "Player" : "Group").append("]").toString());
/*     */           }
/*     */         }
/*     */       }
/* 411 */       if (this.lwc.notInPersistentMode(paramPlayer.getName())) {
/* 412 */         this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */       }
/*     */     }
/*     */ 
/* 416 */     return !bool1;
/*     */   }
/*     */ 
/*     */   public boolean onCommand(Player paramPlayer, String[] paramArrayOfString)
/*     */   {
/* 421 */     String str1 = paramArrayOfString[0].substring(1);
/* 422 */     String str2 = "";
/* 423 */     String[] arrayOfString = paramArrayOfString.length > 1 ? new String[paramArrayOfString.length - 1] : new String[0];
/*     */ 
/* 426 */     if (paramArrayOfString.length > 1) {
/* 427 */       for (int i = 1; i < paramArrayOfString.length; i++) {
/* 428 */         paramArrayOfString[i] = paramArrayOfString[i].trim();
/*     */ 
/* 430 */         if (paramArrayOfString[i].isEmpty())
/*     */         {
/*     */           continue;
/*     */         }
/* 434 */         arrayOfString[(i - 1)] = paramArrayOfString[i];
/* 435 */         str2 = new StringBuilder().append(str2).append(paramArrayOfString[i]).append(" ").toString();
/*     */       }
/*     */     }
/*     */ 
/* 439 */     if (str1.equals("cpublic")) {
/* 440 */       return onCommand(paramPlayer, "/lwc -c public".split(" "));
/*     */     }
/* 442 */     if (str1.equals("cpassword")) {
/* 443 */       return onCommand(paramPlayer, new StringBuilder().append("/lwc -c password ").append(str2).toString().split(" "));
/*     */     }
/* 445 */     if (str1.equals("cprivate")) {
/* 446 */       return onCommand(paramPlayer, "/lwc -c private".split(" "));
/*     */     }
/* 448 */     if (str1.equals("cinfo")) {
/* 449 */       return onCommand(paramPlayer, "/lwc -i".split(" "));
/*     */     }
/* 451 */     if (str1.equals("cunlock")) {
/* 452 */       return onCommand(paramPlayer, "/lwc -u".split(" "));
/*     */     }
/*     */ 
/* 455 */     if (!paramPlayer.canUseCommand(paramArrayOfString[0])) {
/* 456 */       return false;
/*     */     }
/*     */ 
/* 459 */     if (!"lwc".equalsIgnoreCase(str1)) {
/* 460 */       return false;
/*     */     }
/*     */ 
/* 463 */     if (arrayOfString.length == 0) {
/* 464 */       this.lwc.sendFullHelp(paramPlayer);
/* 465 */       return true;
/*     */     }
/*     */ 
/* 468 */     for (LWC_Command localCommand : this.lwc.getCommands()) {
/* 469 */       if (!localCommand.validate(this.lwc, paramPlayer, arrayOfString))
/*     */       {
/*     */         continue;
/*     */       }
/* 473 */       localCommand.execute(this.lwc, paramPlayer, arrayOfString);
/* 474 */       return true;
/*     */     }
/*     */ 
/* 477 */     return false;
/*     */   }
/*     */ 
/*     */   public void onDisconnect(Player paramPlayer)
/*     */   {
/* 482 */     this.memoryDatabase.unregisterPlayer(paramPlayer.getName());
/* 483 */     this.memoryDatabase.unregisterUnlock(paramPlayer.getName());
/* 484 */     this.memoryDatabase.unregisterChest(paramPlayer.getName());
/* 485 */     this.memoryDatabase.unregisterAllActions(paramPlayer.getName());
/*     */   }

/*     */ 
/*     */   public boolean onExplode(Block paramBlock)
/*     */   {
			  String world = "NORMAL";
/* 492 */     int i = this.physicalDatabase.loadProtectedEntities(world,paramBlock.getX(), paramBlock.getY(), paramBlock.getZ(), 4).size() > 0 ? 1 : 0;
/*     */ 
/* 498 */     return i != 0;
/*     */   }
/*     */ 
/*     */   public boolean onItemDrop(Player paramPlayer, Item paramItem)
/*     */   {
/* 509 */     String str = paramPlayer.getName();
/* 510 */     int i = this.lwc.getPlayerDropTransferTarget(str);
/*     */ 
/* 512 */     if ((i == -1) || (!this.lwc.isPlayerDropTransferring(str))) {
/* 513 */       return false;
/*     */     }
/*     */ 
/* 516 */     if (!this.physicalDatabase.doesChestExist(i)) {
/* 517 */       paramPlayer.sendMessage("§4Your drop transfer target was unregistered and/or destroyed.");
/* 518 */       paramPlayer.sendMessage("§4Please re-register a target chest. Drop transfer will be deactivated.");
/*     */ 
/* 520 */       this.memoryDatabase.unregisterMode(str, "dropTransfer");
/* 521 */       return false;
/*     */     }
/*     */ 
/* 524 */     Entity localEntity = this.physicalDatabase.loadProtectedEntity(i);
/*     */ 
/* 526 */     if (localEntity == null) {
/* 527 */       paramPlayer.sendMessage("§4An unknown error occured. Drop transfer will be deactivated.");
/*     */ 
/* 529 */       this.memoryDatabase.unregisterMode(str, "dropTransfer");
/* 530 */       return false;
/*     */     }
/*     */ 
/* 533 */     if (!this.lwc.canAccessChest(paramPlayer, localEntity)) {
/* 534 */       paramPlayer.sendMessage("§4You have lost access to your target chest.");
/* 535 */       paramPlayer.sendMessage("§4Please re-register a target chest. Drop transfer will be deactivated.");
/*     */ 
/* 537 */       this.memoryDatabase.unregisterMode(str, "dropTransfer");
/* 538 */       return false;
/*     */     }
/*     */ 
/* 541 */     List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(),localEntity.getX(), localEntity.getY(), localEntity.getZ());
/* 542 */     int j = paramItem.getAmount();
/*     */ 
/* 544 */     for (ComplexBlock localComplexBlock : localList) {
/* 545 */       Inventory localInventory = (Inventory)localComplexBlock;
/*     */ 
/* 548 */       for (Item localItem2 : localInventory.getContents())
/* 549 */         System.out.println(new StringBuilder().append("").append(localItem2.getItemId()).append(":").append(localItem2.getAmount()).append(" ").append(localItem2.getSlot()).toString());
/*     */       Item localItem1;
/* 552 */       while ((((localItem1 = localInventory.getItemFromId(paramItem.getItemId(), 63)) != null) || (localInventory.getEmptySlot() != -1)) && (j > 0)) {
/* 553 */         if (localItem1 != null) {
/* 554 */           int k = Math.min(64 - localItem1.getAmount(), paramItem.getAmount());
/* 555 */           localInventory.setSlot(paramItem.getItemId(), localItem1.getAmount() + k, localItem1.getSlot());
/* 556 */           j -= k;
/* 557 */           continue;
/* 558 */         }localInventory.addItem(new Item(paramItem.getItemId(), j));
/* 559 */         j = 0;
/*     */       }
/*     */ 
/* 563 */       localComplexBlock.update();
/*     */ 
/* 565 */       if (j == 0)
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/* 570 */     if (j > 0) {
/* 571 */       paramPlayer.sendMessage("§4Your chest is full. Drop transfer will be deactivated.");
/* 572 */       paramPlayer.sendMessage("§4Any remaining quantity that could not be stored will be returned.");
/* 573 */       this.memoryDatabase.unregisterMode(str, "dropTransfer");
/* 574 */       this.memoryDatabase.registerMode(str, "dropTransfer", new StringBuilder().append("f").append(i).toString());
/* 575 */       paramPlayer.getInventory().addItem(paramItem);
/* 576 */       paramPlayer.getInventory().update();
/*     */     }
/*     */ 
/* 579 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean onOpenInventory(Player paramPlayer, Inventory paramInventory)
/*     */   {
/* 584 */     if ((this.lwc.isAdmin(paramPlayer)) && (!this.debugMode)) {
/* 585 */       return false;
/*     */     }
/*     */ 
/* 588 */     if ((paramInventory instanceof Workbench)) {
/* 589 */       return false;
/*     */     }
/*     */ 
/*
			  if ((paramInventory instanceof Dispenser)) {
				  return false;
			  }
*/
/*     */ 
/* 596 */     ComplexBlock localComplexBlock1 = (ComplexBlock)paramInventory;
/*     */ 
/* 598 */     if (!isProtectable(localComplexBlock1.getBlock())) {
/* 599 */       return false;
/*     */     }
/*     */ 
/* 602 */     List<ComplexBlock> localList = this.lwc.getEntitySet(paramPlayer.getWorld(),localComplexBlock1.getX(), localComplexBlock1.getY(), localComplexBlock1.getZ());
/* 603 */     boolean bool = true;
/*     */ 
/* 605 */     for (ComplexBlock localComplexBlock2 : localList) {
/* 606 */       if (localComplexBlock2 == null)
/*     */       {
/*     */         continue;
/*     */       }
				String world = paramPlayer.getWorld().getType().name();
/* 610 */       Entity localEntity = this.physicalDatabase.loadProtectedEntity(world,localComplexBlock2.getX(), localComplexBlock2.getY(), localComplexBlock2.getZ());
/*     */ 
/* 612 */       if (localEntity == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 616 */       bool = this.lwc.canAccessChest(paramPlayer, localEntity);
/*     */ 
/* 618 */       switch (localEntity.getType()) {
/*     */       case 1:
/* 620 */         if (bool) break;
/* 621 */         this.memoryDatabase.unregisterUnlock(paramPlayer.getName());
/* 622 */         this.memoryDatabase.registerUnlock(paramPlayer.getName(), localEntity.getID());
/*     */ 
/* 624 */         paramPlayer.sendMessage("§4This chest is locked.");
/* 625 */         paramPlayer.sendMessage("§4Type §6/lwc -u <password>§4 to unlock it"); break;
/*     */       case 2:
/* 631 */         if (bool) break;
/* 632 */         paramPlayer.sendMessage("§4This chest is locked with a magical spell.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 639 */     return !bool;
/*     */   }
/*     */ 

/*     */   private boolean isProtectable(Block paramBlock)
/*     */   {
/* 649 */     switch (paramBlock.getType())
/*     */     {
			  case 23: /* Dispensers */
				return true;
/*     */     case 54:
/* 652 */       return true;
/*     */     case 61:
/*     */     case 62:
/* 657 */       return true;
/*     */     }
/*     */ 
/* 664 */     return false;
/*     */   }
/*     */ }

/* Location:           D:\dev\Minecraft Mods\server_1.6.Crow_b1.1.7\plugins\LWC.jar
 * Qualified Name:     LWCListener
 * JD-Core Version:    0.6.0
 */