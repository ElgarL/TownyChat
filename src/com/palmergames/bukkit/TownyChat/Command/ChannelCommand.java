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
import com.palmergames.bukkit.util.ChatTools;

import java.util.Map;

public class ChannelCommand implements CommandExecutor {

	Chat plugin = null;

	public ChannelCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		if (!label.equalsIgnoreCase("ch") && !label.equalsIgnoreCase("channel")) {
			return false;
		}
		
		if (args.length == 0) {
			return false;
		}

		if (args[0].equalsIgnoreCase("join")) {
			return plugin.getCommand("join").getExecutor().onCommand(sender, cmd, label, args); 
		}

		if (args[0].equalsIgnoreCase("leave")) {
			return plugin.getCommand("leave").getExecutor().onCommand(sender, cmd, label, args); 
		}

		if (args[0].equalsIgnoreCase("mute")) {
			return plugin.getCommand("chmute").getExecutor().onCommand(sender, cmd, label, args); 
		}

		if (args[0].equalsIgnoreCase("unmute")) {
			return plugin.getCommand("chunmute").getExecutor().onCommand(sender, cmd, label, args); 
		}

		if (args[0].equalsIgnoreCase("mutelist")) {
			return plugin.getCommand("mutelist").getExecutor().onCommand(sender, cmd, label, args); 
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			return listChannels(sender, cmd, label, args); 
		}

		return false;
	}
	
	public boolean listChannels(CommandSender sender, Command cmd, String label, String[] args) {
		// If not our command
		if ((!(label.equalsIgnoreCase("ch") || label.equalsIgnoreCase("channel")) || args.length < 1 || !args[0].equalsIgnoreCase("list"))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: Invalid command!");
			return false;
		}
			
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}

		Player player = ((Player) sender);
		Map<String, Channel> chanList = plugin.getChannelsHandler().getAllChannels();
		
		TownyMessaging.sendMsg(sender, ChatTools.formatTitle("Channels"));
		TownyMessaging.sendMsg(sender, Colors.Gold + "Channel" + Colors.Gray + " - " + Colors.LightBlue + "(Status)");
		for (Map.Entry<String, Channel> channel : chanList.entrySet()) {
			if (player.hasPermission(channel.getValue().getPermission()))
				if (channel.getValue().isPresent(player.getName())) {
					TownyMessaging.sendMsg(sender, Colors.Gold + channel.getKey() + Colors.Gray + " - " + Colors.LightBlue + "In");
				} else {
					/*if (!plugin.getTowny().isPermissions()
						|| ( (plugin.getTowny().isPermissions()) 
						&& (TownyUniverse.getPermissionSource().has(player, channel.getValue().getPermission()))
						|| (channel.getValue().getPermission().isEmpty()))) {*/
						TownyMessaging.sendMsg(sender, Colors.Gold + channel.getKey() + Colors.Gray + " - " + Colors.LightBlue + "Out");
					//}
				}
		}
		
		return true;
	}
}
