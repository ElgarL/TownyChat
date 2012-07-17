package com.palmergames.bukkit.TownyChat;

import java.util.HashMap;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.palmergames.bukkit.TownyChat.Command.TownyChatCommand;
import com.palmergames.bukkit.TownyChat.channels.ChannelsHolder;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
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
	
	private IRCHandler irc = null;
	
	


	private HeroicDeathForwarder heroicDeathListener = null;


	@Override
	public void onEnable() {

		pm = getServer().getPluginManager();
		configuration = new ConfigurationHandler(this);
		setChannels(new ChannelsHolder(this));
		
		/*
		 * This executes the task with a 1 tick delay avoiding the bukkit
		 * depends bug.
		 */
		if ((!checkTowny()) || (!load()) || (getServer().getScheduler().scheduleSyncDelayedTask(this, new onLoadedTask(this), 1) == -1)) {
			/*
			 * We either failed to find Towny or the Scheduler failed to
			 * register the task.
			 */
			getLogger().severe("Could not schedule onLoadedTask.");
			getLogger().severe("disabling TownyChat");
			pm.disablePlugin(this);
			return;
		} 
		checkPlugins();
		


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
		
		if (craftIRC != null) {
			craftIRC.unregisterEndPoint("towny");
			craftIRC = null;
		}
		irc = null;
		
		dynMap = null;
		heroicDeathListener= null;
		towny = null;
		pm = null;
		
		configuration = null;
		setChannels(null);
	}
	
	/**
	 * Perform a reload of this plugin.
	 */
	public void reload() {
		onDisable();
		onEnable();
	}

	/**
	 * Attempt to hook any plugins we want to access.
	 */
	private void checkPlugins() {
		Plugin test;
		/**
		 * If we found craftIRC check for HeroicDeath
		 */
		if (ChatSettings.getIRCEnabled()) {
			test = pm.getPlugin("HeroicDeath");
			if (test != null) {
				heroicDeathListener = new HeroicDeathForwarder(irc);
				getLogger().info("[TownyChat] Found and attempting to relay Heroic Death messages to IRC.");
			}
		}
		
		test = pm.getPlugin("dynmap");
		if (test != null) {
			dynMap = (DynmapAPI) test;
		}
	}

	private boolean checkTowny() {
		Plugin test;

		test = pm.getPlugin("Towny");
		if (test != null && test instanceof Towny) {
			towny = (Towny) test;
		} else {
			return false;
		}
		return true;
	}
	
	public void registerEvents() {
		
		if (TownyPlayerListener == null) {
			TownyPlayerListener = new TownyChatPlayerListener(this);
	
			if (TownyPlayerListener != null)
				pm.registerEvents(TownyPlayerListener, this);
			if (heroicDeathListener != null)
				pm.registerEvents(heroicDeathListener, this);
		}
		
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
		return getChannels();
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
	
	public IRCHandler getIRC() {
		return irc;
	}
	
	public DynmapAPI getDynmap() {
		return dynMap;
	}

	public HeroicDeathForwarder getHeroicDeath() {
		return heroicDeathListener;
	}

	public ChannelsHolder getChannels() {
		return channels;
	}

	private void setChannels(ChannelsHolder channels) {
		this.channels = channels;
	}

	public IRCHandler getIrc() {
		return irc;
	}

	public void setIrc(IRCHandler irc) {
		this.irc = irc;
	}
}