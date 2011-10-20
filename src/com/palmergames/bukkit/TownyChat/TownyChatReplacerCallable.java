package com.palmergames.bukkit.TownyChat;

import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.util.ReplacerCallable;

public abstract class TownyChatReplacerCallable implements ReplacerCallable<TownyChatEvent> {
	public TownyChatReplacerCallable() {
		
	}
	
	public abstract String call(String match, TownyChatEvent event) throws Exception;
}
