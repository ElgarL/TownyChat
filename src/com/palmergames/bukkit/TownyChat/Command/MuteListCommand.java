package com.palmergames.bukkit.TownyChat.Command;

import java.util.Iterator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.Colors;

public class MuteListCommand implements CommandExecutor {

	Chat plugin = null;

	public MuteListCommand(Chat instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,	String[] args) {
		// If not our command
		if ((!label.equalsIgnoreCase("mutelist") || args.length != 1) && 
			(!label.equalsIgnoreCase("ch") || args.length != 2 || !args[0].equalsIgnoreCase("mutelist"))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] Error: Invalid command!");
			return false;
		}
			
		if (!(sender instanceof Player)) {
			return false; // Don't think it can happen but ...
		}

		Player player = ((Player)sender);
		
		String name = null;
		if (label.equalsIgnoreCase("mutelist")){
			name = args[0];
		} else {
			name = args[1];
		}
		
		String mutePerm = plugin.getChannelsHandler().getMutePermission();
		String unmutePerm = plugin.getChannelsHandler().getUnmutePermission();
		if ((mutePerm == null && unmutePerm == null) || 
			(mutePerm != null && (plugin.getTowny().isPermissions() && !TownyUniverse.getPermissionSource().has(player, mutePerm))) ||
			(unmutePerm != null && (plugin.getTowny().isPermissions() && !TownyUniverse.getPermissionSource().has(player, unmutePerm)))) {
			TownyMessaging.sendErrorMsg(sender, "[TownyChat] You don't have permissions to see mute list");
			return true;
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

		name = chan.getName();
		
		// TODO Support paging through this list
		int count = 0;
		String players = "";

		if (chan.hasMuteList()) {
			Iterator<String> iter = chan.getMuteList().iterator();
			boolean first = true;
			while(iter.hasNext()) {
				if (!first) {
					players += Colors.Green + ", "+ Colors.White;
				}
				players += iter.next();
				count++;
			}
		}

		if (count == 0) {
			TownyMessaging.sendMsg(sender, "[TownyChat] There are no muted playeers in " + Colors.White + chan.getName());
			return true;
		}
		
		String msg = "[TownyChat] " + Colors.White + count + Colors.Green + " players muted in " + Colors.White + chan.getName() + Colors.Green + ": " + Colors.White + players;

		TownyMessaging.sendMsg(sender, msg);
		return true;
	}
}
