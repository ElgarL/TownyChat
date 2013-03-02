package com.palmergames.bukkit.TownyChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.ProfileManager;
import com.palmergames.bukkit.towny.TownyMessaging;

public class IgnorepmeCommand implements CommandExecutor {
	Chat plugin = null;

	public IgnorepmeCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}
		if (args.length<1) return false;
		if (ProfileManager.getPlayerProfile(args[0]).isIgnored(sender.getName())){
			TownyMessaging.sendMsg(sender,"You are ignored "+args[0]);
		}else{
			TownyMessaging.sendMsg(sender,"You are not ignored "+args[0]);
		}
		return true;
	}
}
