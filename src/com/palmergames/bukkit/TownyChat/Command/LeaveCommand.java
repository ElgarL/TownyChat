package com.palmergames.bukkit.TownyChat.Command;

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.util.TownyUtil;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.Colors;

public class LeaveCommand implements CommandExecutor {

	Chat plugin = null;

	public LeaveCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		// If not our command
		if ((!label.equalsIgnoreCase("leave") || args.length != 1) && 
			(!label.equalsIgnoreCase("ch") || args.length != 2 || !args[0].equalsIgnoreCase("leave"))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: Invalid command!");
			return false;
		}
			
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}

		Player player = ((Player)sender);
		
		String name = null;
		if (label.equalsIgnoreCase("leave")){
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

		// You can leave if:
		// - Towny recognizes your permissions plugin AND
		// - channel has leaving permission set AND [by default they always do and you can provide your own permission name]
		// - player has leaving permission set      [by default they don't]
		String leavePerm = chan.getLeavePermission();
		if ( leavePerm == null || 
		   ( leavePerm != null && ( !plugin.getTowny().isPermissions() || 
								  ( plugin.getTowny().isPermissions() && !TownyUniverse.getPermissionSource().has( player, leavePerm ) )))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] You cannot leave " + Colors.White + chan.getName());
			return true;
		}

		// If we fail you weren't in there to start with
		if (!chan.leave(sender.getName())){
			TownyMessaging.sendMsg(sender, "[TownyChat] You already left " + Colors.White + chan.getName());
			return true;
		}
					
		// Announce it
		TownyMessaging.sendMsg(sender, "[TownyChat] You left " + Colors.White + chan.getName());

		// Remove this channel from player modes
		if (TownyUtil.removePlayerMode(plugin.getTowny(), player, chan.getName(), false)) {
			
		}
		
		Channel newchannel = null;
		// Find us a new channel to speak on
		newchannel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.GLOBAL);
		if (newchannel == null) {
			newchannel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.NATION);
			if (newchannel == null) {
				newchannel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.TOWN);
				if (newchannel == null) {
					newchannel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.DEFAULT);
					if (newchannel == null) {
						newchannel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.PRIVATE);
					}
				}
			}
		}

		if (newchannel != null) {
			TownyUtil.addPlayerMode(plugin.getTowny(), player, newchannel.getName(), true);
			TownyMessaging.sendMsg(sender, "[TownyChat] You are now talking in " + Colors.White + newchannel.getName());
		} else {
			TownyMessaging.sendMsg(sender, "[TownyChat] You are now in blissful silence.");
		}

		return true;
	}

}
