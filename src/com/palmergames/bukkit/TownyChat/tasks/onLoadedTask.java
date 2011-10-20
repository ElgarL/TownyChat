package com.palmergames.bukkit.TownyChat.tasks;

import com.palmergames.bukkit.TownyChat.Chat;
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

		plugin.getLogger().info("-******* TownyChat enabled *******-");
		plugin.registerEvents();
		
	}
	
}