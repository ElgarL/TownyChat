package com.palmergames.bukkit.TownyChat;

import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;
import com.palmergames.bukkit.TownyChat.util.ReplacerCallable;

public abstract class TownyChatReplacerCallable implements ReplacerCallable<LocalTownyChatEvent> {
	public TownyChatReplacerCallable() {
		
	}
	
	public abstract String call(String match, LocalTownyChatEvent event) throws Exception;
}
