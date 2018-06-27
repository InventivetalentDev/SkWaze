package fr.weefle.waze;

import fr.weefle.waze.discord.DiscordRegister;
import fr.weefle.waze.effects.*;
import fr.weefle.waze.nms.*;
import fr.weefle.waze.skwrapper.SkWrapperListener;
import fr.weefle.waze.skwrapper.WazeEffectCreateServer;
import fr.weefle.waze.utils.Metrics;
import fr.weefle.waze.utils.Updater;
import fr.weefle.waze.utils.UpdaterListener;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleEvent;
import fr.weefle.waze.events.PlayerJumpEvent;
import fr.weefle.waze.expressions.WazeExpressionPing;

public class Waze extends JavaPlugin {
	
	private static Waze instance;
	private ActionBar actionbar;
	private Title title;
	private BossBarAPI bossbar;
	private Ping ping;
	private Particles particles;
	private ScoreBoard scoreboard;
	private AutoRespawn autorespawn;
	
	@Override
	public void onEnable() {
		new DiscordRegister(this);
		getServer().getPluginManager().registerEvents(new SkWrapperListener(), this);
		/*IDiscordClient client = Register.createClient("NDU4NzQxMDk1MjU0MzI3MzA3.DgsFJw.Ldme_DoKgVEhOQisEAycyO-EQ5k", true);
		EventDispatcher dispatcher = client.getDispatcher(); 
        dispatcher.registerListener(new InterfaceListener()); 
        dispatcher.registerListener(new AnnotationListener()); */
		getServer().getPluginManager().registerEvents(new UpdaterListener(), this);
			new Metrics(this);
			getLogger().info("Metrics setup was successful!");
		try {
			new Updater(this, 49195);
			getLogger().info("Updater setup was successful!");
		} catch (IOException e) {
			getLogger().severe("Failed to setup Updater!");
			getLogger().severe("Verify the resource's link!");
			e.printStackTrace();
		}
		if (setupNMS()) {

			getLogger().info("NMS setup was successful!");
			getLogger().info("The plugin setup process is complete!");

		} else {

			getLogger().severe("Failed to setup NMS!");
			getLogger().severe("Your server version is not compatible with this plugin!");

			Bukkit.getPluginManager().disablePlugin(this);
		}
		instance = this;
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		PlayerJumpEvent.register(this);
		Skript.registerAddon(this);
        Skript.registerEffect(WazeEffectTitle.class, "[waze] (send|create) title %string% with [sub[title]] %string% (to|for) %players% (for|to) %integer% tick[s]");
		Skript.registerEffect(WazeEffectActionBar.class, "[waze] (send|create) action[bar] %string% (to|for) %players%");
		Skript.registerEffect(WazeEffectBungee.class, "[waze] (send|teleport) %players% to [bungee[cord]] server %string%");
		Skript.registerExpression(WazeExpressionPing.class, Integer.class, ExpressionType.PROPERTY, "[waze] %players%['s] ping", "[waze] ping of %players%");
		Skript.registerEffect(WazeEffectRecipe.class, "[waze] (create|register) [new] recipe[s] [for] %itemtype% with %itemtype%, %itemtype%, %itemtype%, %itemtype%, %itemtype%, %itemtype%, %itemtype%, %itemtype%, %itemtype%");
		Skript.registerEffect(WazeEffectClearRecipes.class, "[waze] (remove|clear|delete) [all] [craft[ing]] recipe[s]");
		Skript.registerEffect(WazeEffectBossBarCreate.class, "[waze] (create|send) [boss]bar %string% (with|at) %double% percent[s] (and|with) color %string% with id %string% (to|for) %player%");
		Skript.registerEffect(WazeEffectBossBarTimer.class, "[waze] (create|send) [boss]bar %string% (with|at) %double% percent[s] (and|with) color %string% with id %string% (for|and) %integer% tick[s] (to|for) %player%");
		Skript.registerEffect(WazeEffectBossBarRemove.class, "[waze] (remove|delete|clear) [boss]bar with id %string% (of|for) %player%");
        Skript.registerEffect(WazeEffectScoreBoard.class, "[waze] (create|make) scoreboard %string% of type %string% to [display]slot %string% (with|and) score %string% (at|for) line %integer% (to|for) %players%");
		Skript.registerEffect(WazeEffectRemoveScoreBoard.class, "[waze] (clear|remove) scoreboard %string% (of|for) %players%");
		Skript.registerEffect(WazeEffectAutoRespawn.class, "[waze] [auto]respawn %players%");
		Skript.registerEffect(WazeEffectParticles.class, "[waze] (spawn|create|summon) [a number of] %integer% [of] %string%['s] particle[s] (to|for) %players% (at|from) %locations% (and|with) offset %float%, %float%, %float% (and|with) data %float%");
		Skript.registerEffect(WazeEffectCreateServer.class, "[waze] (add|create) [[a] new] server named %string% (from|with) template %string%");
		Skript.registerEvent("Jump Event", SimpleEvent.class, PlayerJumpEvent.class, "[waze] jump[ing]");
        /*EventValues.registerEventValue(PlayerJumpEvent.class, Player.class, new Getter<Player, PlayerJumpEvent>() {
            @Override
            public Player get(PlayerJumpEvent playerJumpEvent) {
                return playerJumpEvent.getPlayer();
            }
        }, 0);*/
        }

	private boolean setupNMS() {

		String version;

		try {

			version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

		} catch (ArrayIndexOutOfBoundsException exception) {
			return false;
		}

		getLogger().info("Your server is running version " + version);
		if (version.equals("v1_12_R1")) {
			title = new Title();
			autorespawn = new AutoRespawnNew();
			scoreboard = new ScoreBoard();
			bossbar = new BossBarNew(this);
			actionbar = new ActionBarNew();
			ping = new Ping();
			particles = new Particles();

        } else if (version.equals("v1_8_R3")) {
        	title = new Title();
    		scoreboard = new ScoreBoard();
    		autorespawn = new AutoRespawnOld(this);
    		bossbar = new BossBarOld();
    		actionbar = new ActionBarOld();
    		ping = new Ping();
    		particles = new Particles();
        }else if (version.equals("v1_7_R4")){
    		scoreboard = new ScoreBoard();
    		autorespawn = new AutoRespawnOld(this);
    		ping = new Ping();
    		bossbar = new BossBarOld();
    		particles = new Particles();
    }else {
    	title = new Title();
    	autorespawn = new AutoRespawnNew();
		scoreboard = new ScoreBoard();
		bossbar = new BossBarNew(this);
		actionbar = new ActionBarOld();
		ping = new Ping();
		particles = new Particles();
    }
		return true;
	}
    public ActionBar getActionbar() {
        return actionbar;
    }
    public Title getTitle() {
        return title;
    }
    public static Waze getInstance(){
	    return instance;
    }
    public BossBarAPI getBossBar(){
        return bossbar;
    }
    public Ping getPing(){
        return ping;
    }
    public ScoreBoard getScoreBoard(){
	    return scoreboard;
    }
    
    public AutoRespawn getAutoRespawn(){
	    return autorespawn;
    }
    
    public Particles getParticles(){
	    return particles;
    }

}