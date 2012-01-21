package com.palmergames.bukkit.TownyChat;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.channels.ChannelsHolder;
import com.palmergames.bukkit.TownyChat.config.ConfigurationHandler;
import com.palmergames.bukkit.TownyChat.event.TownyPlayerHighestListener;
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

	private Logger logger = Logger.getLogger("com.palmergames.bukkit.TownyChat");
	private TownyPlayerHighestListener TownyPlayerListener;
	private ChannelsHolder channels = new ChannelsHolder(this);
	private ConfigurationHandler configuration = new ConfigurationHandler(this);

	protected PluginManager pm;
	private Towny towny = null;
	private CraftIRC craftIRC = null;
	private DynmapAPI dynMap = null;
	
	private CraftIRCHandler irc = null;

	@Override
	public void onEnable() {

		pm = getServer().getPluginManager();

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
			logger.severe("Could not schedule onLoadedTask.");
			logger.severe("disabling TownyChat");
			pm.disablePlugin(this);
			return;
		}
		
	}
	
	private boolean load() {
		FileMgmt.checkFolders(new String[] { getRootPath(), getChannelsPath() });
		return configuration.loadChannels(getChannelsPath(), "Channels.yml");
	}

	@Override
	public void onDisable() {
		// reset any handles
		towny = null;
		pm = null;
		logger = null;
	}

	private void checkPlugins() {
		Plugin test;

		test = pm.getPlugin("Towny");
		if (test != null && test instanceof Towny)
			towny = (Towny) test;
		
		test = pm.getPlugin("CraftIRC");
		if (test != null) {
			try {
				if (Double.valueOf(test.getDescription().getVersion()) >= 3.1) {
					craftIRC = (CraftIRC) test;
					irc = new CraftIRCHandler(towny, craftIRC, "towny");
				} else
					logger.warning("TownyChat requires CraftIRC version 3.1 or higher to relay chat.");
			} catch (NumberFormatException e) {
				logger.warning("Non number format found for craftIRC version string!");
			}
		}
		
		test = pm.getPlugin("dynmap");
		if (test != null) {
			dynMap = (DynmapAPI) test;
		}

	}

	public void registerEvents() {
		TownyPlayerListener = new TownyPlayerHighestListener(this, irc, dynMap);

		pm.registerEvents(TownyPlayerListener, this);
		
	}
	
	public void registerPermissions() {
		// Register all Permissions.
		for (String perm : getChannelsHandler().getAllPermissions()) {
			pm.addPermission(new Permission(perm, new HashMap<String, Boolean>()));
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

	public Logger getLogger() {
		return logger;
	}

	public Towny getTowny() {
		return towny;
	}

}