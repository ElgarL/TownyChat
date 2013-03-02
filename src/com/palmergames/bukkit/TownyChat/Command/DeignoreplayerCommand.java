package com.palmergames.bukkit.TownyChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.ProfileManager;

public class DeignoreplayerCommand implements CommandExecutor {
	Chat plugin = null;

	public DeignoreplayerCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}
		if (args.length<1) return false;
		if (ProfileManager.getPlayerProfile(sender.getName()).removeFromIgnored(args[0])){
			sender.sendMessage("OK");
		}
		return true;
	}
}
