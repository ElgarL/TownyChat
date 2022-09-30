package com.palmergames.bukkit.TownyChat.util;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.TownyException;

public class EssentialsIntegration {

	public static boolean ignoredByEssentials(Player sender, Player player) {
		try {
			com.earth2me.essentials.User targetUser = Towny.getPlugin().getEssentials().getUser(player);
			com.earth2me.essentials.User senderUser = Towny.getPlugin().getEssentials().getUser(sender);
			return targetUser.isIgnoredPlayer(senderUser);
		} catch (TownyException ignored) {
			// Failed to fetch user so ignore.
		}
		return false;
	}
}
