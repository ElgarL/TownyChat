package com.palmergames.bukkit.TownyChat.tasks;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.IRCHandler;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.towny.Towny;

/*
 * 
 * Created by ElgarL
 * 
 */

public class onLoadedTask implements Runnable {
	
	Chat plugin;
	Towny towny;
	
	public onLoadedTask(Chat plugin) {
        super();
        this.plugin = plugin;
        this.towny = plugin.getTowny();
	}
	
	@Override
	public void run() {
		
		if (this.towny.isEnabled()) {
			// Setup our config here as we now have Towny loaded.
			if (this.towny.isError() || (!plugin.getConfigurationHandler().loadConfig(plugin.getChannelsPath(), "ChatConfig.yml"))) {
				
				plugin.getLogger().severe("disabling TownyChat");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}

			/*
			 * Start PIRC
			 */
			if (ChatSettings.getIRCEnabled()) {
				plugin.setIrc(new IRCHandler(plugin));
				System.out.println("IRC has started!");
				for (String name : plugin.getChannels().getAllChannels().keySet()) {
					plugin.getIRC().connectToChannels(plugin.getChannelsHandler().getChannel(name).getIRCChannels());
				}
			}
			
			plugin.getLogger().info("-******* TownyChat enabled *******-");
			plugin.registerPermissions();
			plugin.registerEvents();
			
		}
		
	}
	
}