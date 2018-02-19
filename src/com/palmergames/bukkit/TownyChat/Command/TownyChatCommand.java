package com.palmergames.bukkit.TownyChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.palmergames.bukkit.TownyChat.TownyChat;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;

/**
 * @author ElgarL
 *
 */
public class TownyChatCommand implements CommandExecutor {

	private TownyChat plugin;
	/**
	 * 
	 */
	public TownyChatCommand(TownyChat instance) {
		this.plugin = instance;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		
		if (label.equalsIgnoreCase("townychat")) {
			
			if (args.length != 1) {
				TownyMessaging.sendErrorMsg(sender, TownySettings.getLangString("tc_err_invalid_command"));
				return false;
			}
			
			if (args[0].equalsIgnoreCase("reload")) {
				plugin.reload();
				TownyMessaging.sendMsg(sender, TownySettings.getLangString("tc_settings_reloaded"));
				return true;
			}
			
			
		}

		TownyMessaging.sendErrorMsg(sender, TownySettings.getLangString("tc_err_unrecognized_command_format"));
		return false;
	}

}
