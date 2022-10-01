package com.palmergames.bukkit.TownyChat;

import java.util.function.Function;

import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;

public class TownyChatReplacer {
	private final Function<LocalTownyChatEvent, String> func;
	
	public TownyChatReplacer(Function<LocalTownyChatEvent, String> func) {
		this.func = func;
	}
	
	public String getWith(LocalTownyChatEvent event) {
		return func.apply(event);
	}
}
