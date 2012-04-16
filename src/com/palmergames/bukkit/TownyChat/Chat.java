package com.palmergames.bukkit.TownyChat;

import java.util.HashMap;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.Command.TownyChatCommand;
import com.palmergames.bukkit.TownyChat.channels.ChannelsHolder;
import com.palmergames.bukkit.TownyChat.config.ConfigurationHandler;
import com.palmergames.bukkit.TownyChat.listener.HeroicDeathForwarder;
import com.palmergames.bukkit.TownyChat.listener.TownyChatPlayerListener;
import com.palmergames.bukkit.TownyChat.tasks.onLoadedTask;
import com.palmergames.bukkit.TownyChat.util.FileMgmt;
import com.palmergames.bukkit.towny.Towny;

/**
 * TownyChat plugin to manage all Towny chat
 * 
 * Website: http://code.google.com/a/eclipselabs.org/p/towny/
 * 
 * @author ElgarL
 */

public class Chat extends JavaPlugin {

	private TownyChatPlayerListener TownyPlayerListener;
	private ChannelsHolder channels;
	private ConfigurationHandler configuration;

	protected PluginManager pm;
	private Towny towny = null;
	private CraftIRC craftIRC = null;
	private DynmapAPI dynMap = null;
	
	private CraftIRCHandler irc = null;
	private HeroicDeathForwarder heroicDeathListener = null;


	@Override
	public void onEnable() {

		pm = getServer().getPluginManager();
		configuration = new ConfigurationHandler(this);
		channels = new ChannelsHolder(this);

		checkPlugins();

		/*
		 * This executes the task with a 1 tick delay avoiding the bukkit
		 * depends bug.
		 */
		if ((towny == null) || (getServer().getScheduler().scheduleSyncDelayedTask(this, new onLoadedTask(this), 1) == -1)
			|| (!load())) {
			/*
			 * We either failed to find Towny or the Scheduler failed to
			 * register the task.
			 */
			getLogger().severe("Could not schedule onLoadedTask.");
			getLogger().severe("disabling TownyChat");
			pm.disablePlugin(this);
			return;
		}

		getCommand("townychat").setExecutor(new TownyChatCommand(this));
		
	}
	
	private boolean load() {
		FileMgmt.checkFolders(new String[] { getRootPath(), getChannelsPath() });
		return configuration.loadChannels(getChannelsPath(), "Channels.yml");
	}

	@Override
	public void onDisable() {
		unregisterPermissions();
		// reset any handles
		irc = null;
		if (craftIRC != null) {
			craftIRC.unregisterEndPoint("towny");
			craftIRC = null;
		}
		dynMap = null;
		heroicDeathListener= null;
		towny = null;
		pm = null;
	}

	private void checkPlugins() {
		Plugin test;

		test = pm.getPlugin("Towny");
		if (test != null && test instanceof Towny)
			towny = (Towny) test;
		/**
		 * Hook craftIRC
		 */
		test = pm.getPlugin("CraftIRC");
		if (test != null) {
			try {
				if (Double.valueOf(test.getDescription().getVersion()) >= 3.1) {
					craftIRC = (CraftIRC) test;
					irc = new CraftIRCHandler(towny, craftIRC, "towny");
				} else
					getLogger().warning("TownyChat requires CraftIRC version 3.1 or higher to relay chat.");
			} catch (NumberFormatException e) {
				getLogger().warning("Non number format found for craftIRC version string!");
			}
		}
		
		/**
		 * If we found craftIRC check for HeroicDeath
		 */
		if (irc != null) {
			test = pm.getPlugin("HeroicDeath");
			if (test != null) {
				heroicDeathListener = new HeroicDeathForwarder(irc);
				getLogger().info("[TownyChat] Found and attempting to relay Heroic Death messages to craftIRC.");
			}
		}
		
		test = pm.getPlugin("dynmap");
		if (test != null) {
			dynMap = (DynmapAPI) test;
		}

	}

	public void registerEvents() {
		TownyPlayerListener = new TownyChatPlayerListener(this);

		if (TownyPlayerListener != null)
			pm.registerEvents(TownyPlayerListener, this);
		if (heroicDeathListener != null)
			pm.registerEvents(heroicDeathListener, this);
		
	}
	
	public void registerPermissions() {
		// Register all Permissions.
		for (String perm : getChannelsHandler().getAllPermissions()) {
			try {
				pm.addPermission(new Permission(perm, new HashMap<String, Boolean>()));
			} catch (IllegalArgumentException e) {
				//permission already registered.
			}
		}
	}
	
	public void unregisterPermissions() {
		// Register all Permissions.
		for (String perm : getChannelsHandler().getAllPermissions()) {
			pm.removePermission(new Permission(perm, new HashMap<String, Boolean>()));
		}
	}
	
	
	public String getRootPath() {
		return getTowny().getDataFolder().getPath();
	}

	public String getChannelsPath() {
		return getRootPath() + FileMgmt.fileSeparator() + "settings";
	}
	
	/**
	 * @return the channels
	 */
	public ChannelsHolder getChannelsHandler() {
		return channels;
	}

	/**
	 * @return the data
	 */
	public ConfigurationHandler getConfigurationHandler() {
		return configuration;
	}

	public Towny getTowny() {
		return towny;
	}
	
	public CraftIRCHandler getIRC() {
		return irc;
	}
	
	public DynmapAPI getDynmap() {
		return dynMap;
	}

	public HeroicDeathForwarder getHeroicDeath() {
		return heroicDeathListener;
	}

}