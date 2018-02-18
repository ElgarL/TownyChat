package com.palmergames.bukkit.TownyChat.tasks;

import com.palmergames.bukkit.TownyChat.TownyChat;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.towny.Towny;

/*
 * 
 * Created by RocketRidah
 * 
 */

public class onPlayerJoinTask implements Runnable {
	
	TownyChat plugin;
	Towny towny;
	Player player;
	String mode;
	
	public onPlayerJoinTask(TownyChat plugin, Player player, Channel defaultChannel) {
        super();
        this.plugin = plugin;
        this.towny = plugin.getTowny();
        this.player = player;
        this.mode = defaultChannel.getName();
	}
	
	@Override
	public void run() {
		if (this.towny.isEnabled()) {
			if (!this.towny.isError()) {
				if (player != null) {
					towny.setPlayerMode(player, new String[] { mode }, true);
				}
				return;
			}
		}
	}
	
}