package com.palmergames.bukkit.TownyChat.tasks;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.towny.Towny;

/*
 * 
 * Created by RocketRidah
 * 
 */

public class onPlayerJoinTask implements Runnable {
	
	Chat plugin;
	Towny towny;
	String playerName;
	String mode;
	
	public onPlayerJoinTask(Chat plugin, Player player, Channel defaultChannel) {
        super();
        this.plugin = plugin;
        this.towny = plugin.getTowny();
        this.playerName = player.getName();
        this.mode = defaultChannel.getName();
	}
	
	@Override
	public void run() {
		if (this.towny.isEnabled()) {
			if (!this.towny.isError()) {
				Player player = plugin.getServer().getPlayer(playerName);
				if (player != null) {
					towny.setPlayerMode(player, new String[] { mode }, true);
				}
				return;
			}
		}
	}
	
}