package com.palmergames.bukkit.TownyChat.event;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

import com.earth2me.essentials.User;
import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyException;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.util.StringMgmt;

public class TownyPlayerHighestListener extends PlayerListener {
	private final Towny plugin;
	private CraftIRCHandler ircHander;

	public TownyPlayerHighestListener(Towny instance, CraftIRCHandler irc) {
		this.plugin = instance;
		this.ircHander = irc;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();

		// Test if this player is registered with Towny.
		try {
			plugin.getTownyUniverse().getResident(player.getName());
		} catch (NotRegisteredException e) {
			return;
		}
		String split[] = event.getMessage().split("\\ ");
		String command = split[0].trim().toLowerCase();
		String message = "";

		if (split.length > 1)
			message = StringMgmt.join(StringMgmt.remFirstArg(split), " ");

		if (TownySettings.chatChannelExists(command)) {
			event.setMessage(message);

			if (!plugin.isPermissions() || (plugin.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(player, TownySettings.getChatChannelPermission(command)))) {
				
				// Check if essentials has this player muted.
				if (plugin.isEssentials()) {
					try {
						if (plugin.getEssentials().getUser(player).isMuted()) {
							TownyMessaging.sendErrorMsg(player, "Unable to talk...You are currently muted!");
							return;
						}
					} catch (TownyException e) {
						// Get essentials failed
					}
				}
				
				// Deal with special cases
				if (command.equalsIgnoreCase("/tc")) {

					if (message.isEmpty())
						plugin.setPlayerChatMode(player, "tc");
					else {
						// Town Chat
						parseTownChatCommand(event, command, player);
					}
					event.setCancelled(true);

				} else if (command.equalsIgnoreCase("/nc")) {

					if (message.isEmpty())
						plugin.setPlayerChatMode(player, "nc");
					else {
						// Nation Chat
						parseNationChatCommand(event, command, player);
					}
					event.setCancelled(true);

				} else if (command.equalsIgnoreCase("/g")) {

					if (message.isEmpty())
						plugin.setPlayerChatMode(player, "g");
					else {
						// Global Chat
						parseGlobalChannelChatCommand(event, command, player);
					}
					event.setCancelled(true);

				} else {

					if (message.isEmpty())
						plugin.setPlayerChatMode(player, command.replace("/", ""));
					else {
						// Custom channel Chat
						parseDefaultChannelChatCommand(event, command, player);
					}
					event.setCancelled(true);
				}
			} else {
				TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_command_disable"));
				event.setCancelled(true);
			}

		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {

		Player player = event.getPlayer();
		Resident resident;
		
		// Check if essentials has this player muted.
		if (plugin.isEssentials()) {
			try {
				if (plugin.getEssentials().getUser(player).isMuted()) {
					TownyMessaging.sendErrorMsg(player, "Unable to talk...You are currently muted!");
					return;
				}
			} catch (TownyException e) {
				// Get essentials failed
			}
		}
		
		try {
			resident = plugin.getTownyUniverse().getResident(player.getName());
		} catch (NotRegisteredException e) {
			return;
		}

		if (plugin.hasPlayerMode(player, "tc")) {
			// Town Chat
			parseTownChatCommand(event, "/tc", player);
		} else if (plugin.hasPlayerMode(player, "nc")) {
			// Nation chat
			parseNationChatCommand(event, "/nc", player);
		} else if (plugin.hasPlayerMode(player, "g")) {
			// Global chat
			//parseGlobalChannelChatCommand(event, "/g", player);
			
			// This is Global chat.
			if (TownySettings.isUsingModifyChat()) {
				event.setFormat(TownySettings.getModifyChatFormat());
			}

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			
			if (ircHander != null) {
				parseGlobalChannelChatCommand(event, "/g", player);
			} else
				return;
			
		} else {
			
			for (String channel : TownySettings.getChatChannels()) {
				if (plugin.hasPlayerMode(player, channel.replace("/", ""))) {
					// Custom channel Chat
					parseDefaultChannelChatCommand(event, channel, player);
					event.setCancelled(true);
					return;
				}
			}
			
			// All chat modes are disabled, or this is Global chat.
			if (TownySettings.isUsingModifyChat()) {
				event.setFormat(TownySettings.getModifyChatFormat());
			}

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));

			if (ircHander != null) {
				parseGlobalChannelChatCommand(event, "/g", player);
			} else
				return;
		}
		event.setCancelled(true);

	}

	private void parseTownChatCommand(PlayerChatEvent event, String command, Player player) {
		try {
			Resident resident = plugin.getTownyUniverse().getResident(player.getName());
			Town town = resident.getTown();

			event.setFormat(TownySettings.getChatTownChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);

			TownyMessaging.sendTownMessage(town, msg);
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}

	private void parseNationChatCommand(PlayerChatEvent event, String command, Player player) {
		try {
			Resident resident = plugin.getTownyUniverse().getResident(player.getName());
			Nation nation = resident.getTown().getNation();

			event.setFormat(TownySettings.getChatNationChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			TownyMessaging.sendNationMessage(nation, msg);
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}

	private void parseDefaultChannelChatCommand(PlayerChatEvent event, String command, Player player) {
		try {
			Resident resident = plugin.getTownyUniverse().getResident(player.getName());
			Boolean bEssentials = plugin.isEssentials();
			
			event.setFormat(TownySettings.getChatDefaultChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			TownyUniverse.plugin.log(msg);
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			for (Player test : plugin.getTownyUniverse().getOnlinePlayers()) {
				if (!plugin.isPermissions() || (plugin.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, TownySettings.getChatChannelPermission(command)))) {
					
					if (bEssentials) {
						try {
							User targetUser = plugin.getEssentials().getUser(test);
							// Don't send this message if the user is ignored
							if (targetUser.isIgnoredPlayer(player.getName()))
								continue;
						} catch (TownyException e) {
							// Failed to fetch user so ignore.
						}
					}
					TownyMessaging.sendMessage(test, msg);
				}
			}

		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}
	
	private void parseGlobalChannelChatCommand(PlayerChatEvent event, String command, Player player) {
		try {
			Resident resident = plugin.getTownyUniverse().getResident(player.getName());
			Boolean bEssentials = plugin.isEssentials();

			if (TownySettings.isUsingModifyChat()) {
				event.setFormat(TownySettings.getModifyChatFormat());
			}

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			TownyUniverse.plugin.log(msg);
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			for (Player test : plugin.getTownyUniverse().getOnlinePlayers()) {
				if (!plugin.isPermissions() || (plugin.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, TownySettings.getChatChannelPermission(command)))) {
					
					if (bEssentials) {
						try {
							User targetUser = plugin.getEssentials().getUser(test);
							// Don't send this message if the user is ignored
							if (targetUser.isIgnoredPlayer(player.getName()))
								continue;
						} catch (TownyException e) {
							// Failed to fetch user so ignore.
						}
					}
					TownyMessaging.sendMessage(test, msg);
				}
			}

			// TownyMessaging.sendNationMessage(nation, chatEvent.getFormat());
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}
}
