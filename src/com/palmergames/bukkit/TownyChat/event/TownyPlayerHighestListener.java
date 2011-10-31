package com.palmergames.bukkit.TownyChat.event;


import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.util.StringMgmt;

public class TownyPlayerHighestListener extends PlayerListener {
	private final Towny plugin;

	public TownyPlayerHighestListener(Towny instance) {
		this.plugin = instance;
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

		if (split.length > 1) {
			String command = split[0].trim().toLowerCase();
			String message = StringMgmt.join(StringMgmt.remFirstArg(split), " ");

			if (TownySettings.chatChannelExists(command)) {
				event.setMessage(message);

				if (!plugin.isPermissions() || (plugin.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(player, TownySettings.getChatChannelPermission(command)))) {
					// Deal with special cases
					if (command.equalsIgnoreCase("/tc")) {

						// Town Chat
						parseTownChatCommand(event, command, player);
						event.setCancelled(true);

					} else if (command.equalsIgnoreCase("/nc")) {

						// Nation Chat
						parseNationChatCommand(event, command, player);
						event.setCancelled(true);

					} else {

						// Custom channel Chat
						parseDefaultChannelChatCommand(event, command, player);
						event.setCancelled(true);
					}
				} else {
					TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_command_disable"));
					event.setCancelled(true);
				}

			}// channel doesn't exist

		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {

		Player player = event.getPlayer();
		Resident resident;
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
		} else {
			// All chat modes are disabled, or this is open chat.
			if (TownySettings.isUsingModifyChat()) {
				event.setFormat(TownySettings.getModifyChatFormat());
			}

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
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

			TownyMessaging.sendTownMessage(town, chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage()));
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
			TownyMessaging.sendNationMessage(nation, chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage()));
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}

	private void parseDefaultChannelChatCommand(PlayerChatEvent event, String command, Player player) {
		try {
			Resident resident = plugin.getTownyUniverse().getResident(player.getName());

			event.setFormat(TownySettings.getChatDefaultChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));

			TownyUniverse.plugin.log(chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage()));
			for (Player test : plugin.getTownyUniverse().getOnlinePlayers()) {
				if (!plugin.isPermissions() || (plugin.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, TownySettings.getChatChannelPermission(command))))
					TownyMessaging.sendMessage(test, chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage()));
			}

			// TownyMessaging.sendNationMessage(nation, chatEvent.getFormat());
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}
}
