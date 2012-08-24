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

public class UnmuteCommand implements CommandExecutor {

	Chat plugin = null;

	public UnmuteCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		// If not our command
		if ((!label.equalsIgnoreCase("unmute") || args.length != 2) && 
			(!label.equalsIgnoreCase("ch") || args.length != 3 || !args[0].equalsIgnoreCase("unmute"))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: Invalid command!");
			return false;
		}
			
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}

		Player player = ((Player)sender);
		
		String name = null;
		String mutee = null;
		if (label.equalsIgnoreCase("mute")){
			name = args[0];
			mutee = args[1];
		} else {
			name = args[1];
			mutee = args[1];
		}
		
		String unmutePerm = plugin.getChannelsHandler().getUnmutePermission();
		if ((unmutePerm == null) || (unmutePerm != null && (plugin.getTowny().isPermissions() && !TownyUniverse.getPermissionSource().has(player, unmutePerm)))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] You don't have unmute permissions");
			return true;
		}

		Player muteePlayer = plugin.getServer().getPlayer(mutee);
		if (muteePlayer == null) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] There are no online players with name " + Colors.White + mutee);
			return true;
		}

		mutee = muteePlayer.getName();

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

		
		if (!chan.mute(name)) {
			TownyMessaging.sendMsg(sender, "[TownyChat] Player is not muted in "+ Colors.White + chan.getName());
			return true;
		}
		
		TownyMessaging.sendMsg(sender, "[TownyChat] " + Colors.White + name + Colors.Green +" is now muted in "+ Colors.White + chan.getName());
		return true;
	}
}
