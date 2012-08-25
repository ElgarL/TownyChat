package com.palmergames.bukkit.TownyChat.util;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.Towny;

public class TownyUtil {

	public TownyUtil() {
	}
	
	public static boolean removePlayerMode(Towny towny, Player player, String mode, boolean notify) {
		if (!towny.hasPlayerMode(player, mode)) {
			return false;
		}
		
		List<String> modes = towny.getPlayerMode(player);
		int newmodecount = modes.size();
		Iterator<String> iter = modes.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (mode.equalsIgnoreCase(s)) {
				iter.remove();
				towny.setPlayerMode(player, modes.toArray(new String[newmodecount-1]), notify);
				return true;
			}
		}
		return false;
	}

	public static boolean addPlayerMode(Towny towny, Player player, String mode, boolean notify) {
		if (towny.hasPlayerMode(player, mode)) {
			return false;
		}
		
		List<String> modes = towny.getPlayerMode(player);
		modes.add(mode);
		towny.setPlayerMode(player, modes.toArray(new String[modes.size()]), notify);
		return true;
	}

	public static boolean removeAndSetPlayerMode(Towny towny, Player player, String removeMode, String addMode, boolean notify) {
		List<String> modes = towny.getPlayerMode(player);
		boolean modesChanged = false;
		if (removeMode != null && towny.hasPlayerMode(player, removeMode)) {
			Iterator<String> iter = modes.iterator();
			while (iter.hasNext()) {
				String s = iter.next();
				if (s == null) continue;
				if (s.equalsIgnoreCase(removeMode)) {
					iter.remove();
					modesChanged = true;
				}
			}
		}
		if (addMode != null) {
			modes.add(addMode);
			modesChanged = true;
		}
		if (modesChanged) {
			towny.setPlayerMode(player, modes.toArray(new String[modes.size()]), notify);
			return true;
		}
		return false;
	}
}
