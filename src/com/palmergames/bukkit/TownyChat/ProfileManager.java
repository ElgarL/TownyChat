package com.palmergames.bukkit.TownyChat;

import java.util.HashMap;

public class ProfileManager {
	private static HashMap<String, Profiles> profiles=new HashMap<String, Profiles>();
	
	public static void PlayerJoin(String Player){
		String key = Player.toLowerCase();
		if(profiles.containsKey(key)) return;
		profiles.put(key, new Profiles(key));
	}
	
	public static void PlayerQuit(String Player){
		String key = Player.toLowerCase();
		if (!profiles.containsKey(key)) return;
		profiles.get(key).Save();
		profiles.remove(key);
	}
	
	public static Profiles getPlayerProfile(String Player){
		String key = Player.toLowerCase();
		if (!profiles.containsKey(key)) profiles.put(key, new Profiles(key));
		return profiles.get(Player.toLowerCase());
	}
	
	public static void DisableMod(){
		for (Profiles profile : profiles.values()) {
			profile.Save();
		}
	}
}
