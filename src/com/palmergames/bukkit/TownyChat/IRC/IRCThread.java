package com.palmergames.bukkit.TownyChat.IRC;

import com.palmergames.bukkit.TownyChat.Chat;

public class IRCThread extends Thread {
	
	Chat plugin;
	
	public IRCThread(Chat plugin) {
		this.plugin = plugin;
		this.start();
		this.setName("TownyChatIRCThread");
		this.setPriority(NORM_PRIORITY);
	}
	
	@Override
	public void run() {
		new IRCMain(this.plugin).run();
	}
	
	
}
