package com.palmergames.bukkit.TownyChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.towny.TownyMessaging;

/**
 * @author ElgarL
 *
 */
public class TownyChatCommand implements CommandExecutor {

	private Chat plugin;
	/**
	 * 
	 */
	public TownyChatCommand(Chat instance) {
		// TODO Auto-generated constructor stub
		this.plugin = instance;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		
		if (label.equalsIgnoreCase("townychat")) {
			
			if (args.length != 1) {
				TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: Invalid command!");
				return false;
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				plugin.reload();
				TownyMessaging.sendMsg(sender, "[TownyChat] Settings reloaded!");
				return true;
			}
			
			
		}

		TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: unrecognised command format!");
		return false;
	}

}
