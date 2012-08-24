package com.palmergames.bukkit.TownyChat.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.Colors;

public class JoinCommand implements CommandExecutor {

	Chat plugin = null;

	public JoinCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		// If not our command
		if ((!label.equalsIgnoreCase("join") || args.length != 1) && 
			(!label.equalsIgnoreCase("ch") || args.length != 2 || !args[0].equalsIgnoreCase("join"))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: Invalid command!");
			return false;
		}
			
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}

		Player player = ((Player)sender);
		
		String name = null;
		if (label.equalsIgnoreCase("join")){
			name = args[0];
		} else {
			name = args[1];
		}
		
		Channel chan = plugin.getChannelsHandler().getChannel(name);

		// If we can't find the channel by name, look up all the channel commands for an alias
		if (chan == null) {
			for(Channel chan2 : plugin.getChannelsHandler().getAllChannels().values()) {
				for (String command : chan2.getCommands()) {
					if (command.equalsIgnoreCase(name)) {
						chan = chan2;
						break;
					}
				}
				if (chan != null) {
					break;
				}
			}
		}
		
		if (chan == null) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] There is no channel called " + Colors.White + name);
			return true;
		}
				
		// You can join if:
		// - Towny doesn't recognize your permissions plugin
		// - channel has no permission set OR  [by default they don't]
		//   - channel has permission set AND:
		//     - player has channel permission
		String joinPerm = chan.getPermission();
		if ((joinPerm != null && (plugin.getTowny().isPermissions() && !TownyUniverse.getPermissionSource().has(player, joinPerm)))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] You cannot join " + Colors.White + chan.getName());
			return true;
		}
	
		if (!chan.join(sender.getName())){
			TownyMessaging.sendMsg(sender, "[TownyChat] You are already in " + Colors.White + chan.getName());
			return true;
		}
		
		TownyMessaging.sendMsg(sender, "[TownyChat] You joined " + Colors.White + chan.getName());
		return true;
	}
}
