package com.palmergames.bukkit.TownyChat.util;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;

public class EssentialsUtil {

	static Chat plugin;
	public EssentialsUtil(Chat chat) {
		plugin = chat;
	}
	public static boolean isUserIgnoringUser(Player sender, Player receiver) {
		if (plugin.getEssentials().getUser(receiver).isIgnoredPlayer(plugin.getEssentials().getUser(sender)))
			return true;
		return false;
	}

}
