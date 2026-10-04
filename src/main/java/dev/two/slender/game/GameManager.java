// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import java.util.function.Consumer;
import java.util.Objects;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Optional;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.World;
import java.util.function.Supplier;
import dev.two.slender.map.ArenaMap;
import java.util.Map;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.Plugin;

public final class GameManager
{
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final Items items;
    private final HubAccess hub;
    private final Map<String, ArenaMap> maps;
    private final Supplier<World> world;
    private final Map<String, Game> games;
    private final Map<UUID, Game> byPlayer;
    
    public GameManager(final Plugin plugin, final SlenderConfig cfg, final Msg msg, final Items items, final HubAccess hub, final Map<String, ArenaMap> maps, final Supplier<World> world) {
        this.games = new HashMap<String, Game>();
        this.byPlayer = new HashMap<UUID, Game>();
        this.plugin = plugin;
        this.cfg = cfg;
        this.msg = msg;
        this.items = items;
        this.hub = hub;
        this.maps = maps;
        this.world = world;
    }
    
    public Optional<Game> gameOf(final UUID uuid) {
        return Optional.ofNullable(this.byPlayer.get(uuid));
    }
    
    public Optional<Game> game(final String s) {
        return Optional.ofNullable(this.games.get(s));
    }
    
    public String modeName(final String anObject) {
        return this.msg.raw("asim".equals(anObject) ? "mode-asim" : "mode-coop");
    }
    
    public void join(final Player p0, final String p1, final String p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokeinterface org/bukkit/entity/Player.getUniqueId:()Ljava/util/UUID;
        //     6: astore          4
        //     8: aload_0        
        //     9: getfield        dev/two/slender/game/GameManager.byPlayer:Ljava/util/Map;
        //    12: aload           4
        //    14: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    19: checkcast       Ldev/two/slender/game/Game;
        //    22: astore          5
        //    24: aload           5
        //    26: ifnull          67
        //    29: aload           5
        //    31: invokevirtual   dev/two/slender/game/Game.map:()Ldev/two/slender/map/ArenaMap;
        //    34: invokevirtual   dev/two/slender/map/ArenaMap.id:()Ljava/lang/String;
        //    37: aload_2        
        //    38: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    41: ifne            52
        //    44: aload           5
        //    46: invokevirtual   dev/two/slender/game/Game.acceptingPlayers:()Z
        //    49: ifne            67
        //    52: aload_0        
        //    53: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //    56: aload_1        
        //    57: ldc             "already-in-game"
        //    59: iconst_0       
        //    60: anewarray       Ljava/lang/String;
        //    63: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //    66: return         
        //    67: aload_0        
        //    68: getfield        dev/two/slender/game/GameManager.maps:Ljava/util/Map;
        //    71: aload_2        
        //    72: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    77: checkcast       Ldev/two/slender/map/ArenaMap;
        //    80: astore          6
        //    82: aload_0        
        //    83: getfield        dev/two/slender/game/GameManager.world:Ljava/util/function/Supplier;
        //    86: invokeinterface java/util/function/Supplier.get:()Ljava/lang/Object;
        //    91: checkcast       Lorg/bukkit/World;
        //    94: astore          7
        //    96: aload           6
        //    98: ifnull          106
        //   101: aload           7
        //   103: ifnonnull       130
        //   106: aload_0        
        //   107: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //   110: aload_1        
        //   111: ldc             "map-not-built"
        //   113: iconst_2       
        //   114: anewarray       Ljava/lang/String;
        //   117: dup            
        //   118: iconst_0       
        //   119: ldc             "map"
        //   121: aastore        
        //   122: dup            
        //   123: iconst_1       
        //   124: aload_2        
        //   125: aastore        
        //   126: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //   129: return         
        //   130: aload           6
        //   132: aload_0        
        //   133: getfield        dev/two/slender/game/GameManager.cfg:Ldev/two/slender/config/SlenderConfig;
        //   136: getfield        dev/two/slender/config/SlenderConfig.pages:I
        //   139: invokestatic    dev/two/slender/map/MapValidator.playable:(Ldev/two/slender/map/ArenaMap;I)Z
        //   142: ifne            182
        //   145: aload_0        
        //   146: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //   149: aload_1        
        //   150: ldc             "map-incomplete"
        //   152: iconst_4       
        //   153: anewarray       Ljava/lang/String;
        //   156: dup            
        //   157: iconst_0       
        //   158: ldc             "map"
        //   160: aastore        
        //   161: dup            
        //   162: iconst_1       
        //   163: aload           6
        //   165: invokevirtual   dev/two/slender/map/ArenaMap.displayName:()Ljava/lang/String;
        //   168: aastore        
        //   169: dup            
        //   170: iconst_2       
        //   171: ldc             "id"
        //   173: aastore        
        //   174: dup            
        //   175: iconst_3       
        //   176: aload_2        
        //   177: aastore        
        //   178: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //   181: return         
        //   182: aload_0        
        //   183: getfield        dev/two/slender/game/GameManager.games:Ljava/util/Map;
        //   186: aload_2        
        //   187: aload_0        
        //   188: aload           6
        //   190: aload           7
        //   192: invokedynamic   BootstrapMethod #0, apply:(Ldev/two/slender/game/GameManager;Ldev/two/slender/map/ArenaMap;Lorg/bukkit/World;)Ljava/util/function/Function;
        //   197: invokeinterface java/util/Map.computeIfAbsent:(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;
        //   202: checkcast       Ldev/two/slender/game/Game;
        //   205: astore          8
        //   207: aload           8
        //   209: invokevirtual   dev/two/slender/game/Game.size:()I
        //   212: ifeq            223
        //   215: aload           8
        //   217: invokevirtual   dev/two/slender/game/Game.mode:()Ldev/two/slender/game/Mode;
        //   220: ifnonnull       227
        //   223: aconst_null    
        //   224: goto            235
        //   227: aload           8
        //   229: invokevirtual   dev/two/slender/game/Game.mode:()Ldev/two/slender/game/Mode;
        //   232: invokevirtual   dev/two/slender/game/Mode.id:()Ljava/lang/String;
        //   235: astore          9
        //   237: aload           9
        //   239: aload_3        
        //   240: invokestatic    dev/two/slender/logic/JoinRules.resolveMode:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   243: astore          10
        //   245: getstatic       dev/two/slender/game/GameManager$1.$SwitchMap$dev$two$slender$logic$JoinRules$Result:[I
        //   248: aload           9
        //   250: aload_3        
        //   251: aload           8
        //   253: invokevirtual   dev/two/slender/game/Game.acceptingPlayers:()Z
        //   256: aload           8
        //   258: invokevirtual   dev/two/slender/game/Game.size:()I
        //   261: aload_0        
        //   262: getfield        dev/two/slender/game/GameManager.cfg:Ldev/two/slender/config/SlenderConfig;
        //   265: aload           10
        //   267: invokevirtual   dev/two/slender/config/SlenderConfig.maxPlayers:(Ljava/lang/String;)I
        //   270: invokestatic    dev/two/slender/logic/JoinRules.check:(Ljava/lang/String;Ljava/lang/String;ZII)Ldev/two/slender/logic/JoinRules$Result;
        //   273: invokevirtual   dev/two/slender/logic/JoinRules$Result.ordinal:()I
        //   276: iaload         
        //   277: tableswitch {
        //                2: 308
        //                3: 336
        //                4: 364
        //                5: 395
        //          default: 395
        //        }
        //   308: aload_0        
        //   309: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //   312: aload_1        
        //   313: ldc             "game-running"
        //   315: iconst_2       
        //   316: anewarray       Ljava/lang/String;
        //   319: dup            
        //   320: iconst_0       
        //   321: ldc             "map"
        //   323: aastore        
        //   324: dup            
        //   325: iconst_1       
        //   326: aload           6
        //   328: invokevirtual   dev/two/slender/map/ArenaMap.displayName:()Ljava/lang/String;
        //   331: aastore        
        //   332: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //   335: return         
        //   336: aload_0        
        //   337: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //   340: aload_1        
        //   341: ldc             "game-full"
        //   343: iconst_2       
        //   344: anewarray       Ljava/lang/String;
        //   347: dup            
        //   348: iconst_0       
        //   349: ldc             "map"
        //   351: aastore        
        //   352: dup            
        //   353: iconst_1       
        //   354: aload           6
        //   356: invokevirtual   dev/two/slender/map/ArenaMap.displayName:()Ljava/lang/String;
        //   359: aastore        
        //   360: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //   363: return         
        //   364: aload_0        
        //   365: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //   368: aload_1        
        //   369: ldc             "mode-forced"
        //   371: iconst_2       
        //   372: anewarray       Ljava/lang/String;
        //   375: dup            
        //   376: iconst_0       
        //   377: ldc             "mode"
        //   379: aastore        
        //   380: dup            
        //   381: iconst_1       
        //   382: aload_0        
        //   383: aload           10
        //   385: invokevirtual   dev/two/slender/game/GameManager.modeName:(Ljava/lang/String;)Ljava/lang/String;
        //   388: aastore        
        //   389: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //   392: goto            395
        //   395: aload           5
        //   397: ifnull          410
        //   400: aload           5
        //   402: aload           4
        //   404: getstatic       dev/two/slender/game/LeaveReason.COMMAND:Ldev/two/slender/game/LeaveReason;
        //   407: invokevirtual   dev/two/slender/game/Game.leave:(Ljava/util/UUID;Ldev/two/slender/game/LeaveReason;)V
        //   410: aload_0        
        //   411: getfield        dev/two/slender/game/GameManager.byPlayer:Ljava/util/Map;
        //   414: aload           4
        //   416: aload           8
        //   418: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   423: pop            
        //   424: aload           8
        //   426: aload_1        
        //   427: aload           10
        //   429: invokestatic    dev/two/slender/game/Mode.of:(Ljava/lang/String;)Ldev/two/slender/game/Mode;
        //   432: invokevirtual   dev/two/slender/game/Game.join:(Lorg/bukkit/entity/Player;Ldev/two/slender/game/Mode;)V
        //   435: aload_0        
        //   436: getfield        dev/two/slender/game/GameManager.msg:Ldev/two/slender/config/Msg;
        //   439: aload_1        
        //   440: ldc             "joined"
        //   442: iconst_4       
        //   443: anewarray       Ljava/lang/String;
        //   446: dup            
        //   447: iconst_0       
        //   448: ldc             "map"
        //   450: aastore        
        //   451: dup            
        //   452: iconst_1       
        //   453: aload           6
        //   455: invokevirtual   dev/two/slender/map/ArenaMap.displayName:()Ljava/lang/String;
        //   458: aastore        
        //   459: dup            
        //   460: iconst_2       
        //   461: ldc             "mode"
        //   463: aastore        
        //   464: dup            
        //   465: iconst_3       
        //   466: aload_0        
        //   467: aload           10
        //   469: invokevirtual   dev/two/slender/game/GameManager.modeName:(Ljava/lang/String;)Ljava/lang/String;
        //   472: aastore        
        //   473: invokevirtual   dev/two/slender/config/Msg.send:(Lorg/bukkit/command/CommandSender;Ljava/lang/String;[Ljava/lang/String;)V
        //   476: return         
        //    StackMapTable: 00 0D FD 00 34 07 01 17 07 00 34 0E FD 00 26 07 00 59 07 00 6D 17 33 FC 00 28 07 00 34 03 47 07 00 3F FD 00 48 07 00 3F 07 00 3F 1B 1B 1E 0E
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Could not infer any expression.
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:382)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:334)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:255)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:145)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void leave(final Player player) {
        final Game game = this.byPlayer.get(player.getUniqueId());
        if (game == null) {
            this.msg.send((CommandSender)player, "not-in-game", new String[0]);
            return;
        }
        game.leave(player.getUniqueId(), LeaveReason.COMMAND);
        this.msg.send((CommandSender)player, "left", new String[0]);
    }
    
    public void quit(final Player player) {
        final Game game = this.byPlayer.get(player.getUniqueId());
        if (game != null) {
            game.leave(player.getUniqueId(), LeaveReason.QUIT);
        }
    }
    
    public void worldChanged(final Player player) {
        final Game game = this.byPlayer.get(player.getUniqueId());
        if (game != null) {
            game.leave(player.getUniqueId(), LeaveReason.WORLD_CHANGE);
        }
    }
    
    public void stop(final String s) {
        final Game game = this.games.get(s);
        if (game != null) {
            game.stop();
        }
    }
    
    public void drop(final String s) {
        final Game game = this.games.remove(s);
        if (game != null) {
            game.stop();
        }
    }
    
    public void shutdown() {
        final Iterator iterator = new ArrayList(this.games.values()).iterator();
        while (iterator.hasNext()) {
            ((Game)iterator.next()).stop();
        }
        this.games.clear();
        this.byPlayer.clear();
    }
}
