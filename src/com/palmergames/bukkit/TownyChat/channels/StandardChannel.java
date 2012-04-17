package com.palmergames.bukkit.TownyChat.channels;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.dynmap.DynmapAPI;

import com.earth2me.essentials.User;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.ChatTools;

public class StandardChannel extends Channel {

	private Chat plugin;
	public StandardChannel(Chat instance, String name) {
		super(name);
		this.plugin = instance;
	}

	@Override
	public void chatProcess(PlayerChatEvent event) {
		channelTypes exec = channelTypes.valueOf(getType().name());
		
		switch (exec) {
		
		case TOWN:
			parseTownChatCommand(event);
			break;
		
		case NATION:
			parseNationChatCommand(event);			
			break;
			
		case DEFAULT:
			parseDefaultChannelChatCommand(event);
			
			break;
			
		case GLOBAL:
			parseGlobalChannelChatCommand(event);
			
			break;
			
		case PRIVATE:
			parseGlobalChannelChatCommand(event);
			
			break;
		}
	}

	private void parseTownChatCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Town town = resident.getTown();

			event.setFormat(ChatSettings.getRelevantFormatGroup(player).getTOWN().replace("{channelTag}", getChannelTag()).replace("{msgcolour}", getMessageColour()));

			LocalTownyChatEvent chatEvent = new LocalTownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			
			// Throw a "fake" chat event for other plugins to handle the chat after TownyChat has formatted it.
			event = new TownyChatEvent(player, event.getMessage(), event.getFormat());
            plugin.getServer().getPluginManager().callEvent(event);
            
            // If the event was canceled by anything other then TownyChat
            if (event.isCancelled() && !((TownyChatEvent)event).isCanceledByTownyChat())
                return;
            
            // Read the format after it's been updated by all other plugins.
            String msg = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour("[Town Msg] " + town.getName() + ": " + msg));
			
			CraftIRCHandler ircHander = plugin.getIRC();
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg, getCraftIRCTag());
			
			sendSpy(player, "[Town Msg] " + town.getName() + ": " + msg);

			// Send the town message
			int count = 0;
	        for (Player test : TownyUniverse.getOnlinePlayers(town))
	        	if ((testDistance(player, test, getRange())) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
	        		count += 1;
	                test.sendMessage(msg);
	        	}
	        
	        if (count <= 1)
				player.sendMessage(TownySettings.parseSingleLineString("&cYou feel so lonely."));
			
			
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}

	private void parseNationChatCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Nation nation = resident.getTown().getNation();

			event.setFormat(ChatSettings.getRelevantFormatGroup(player).getNATION().replace("{channelTag}", getChannelTag()).replace("{msgcolour}", getMessageColour()));

			LocalTownyChatEvent chatEvent = new LocalTownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			
			// Throw a "fake" chat event for other plugins to handle the chat after TownyChat has formatted it.
			event = new TownyChatEvent(player, event.getMessage(), event.getFormat());
            plugin.getServer().getPluginManager().callEvent(event);
            
            // If the event was canceled by anything other then TownyChat
            if (event.isCancelled() && !((TownyChatEvent)event).isCanceledByTownyChat())
                return;
            
            // Read the format after it's been updated by all other plugins.
            String msg = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour("[Nation Msg] " + nation.getName() + ": " + msg));
			
			CraftIRCHandler ircHander = plugin.getIRC();
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg, getCraftIRCTag());
			
			sendSpy(player, "[Nation Msg] " + nation.getName() + ": " + msg);
			
			// Send the town message
			int count = 0;
	        for (Player test : TownyUniverse.getOnlinePlayers(nation))
	        	if ((testDistance(player, test, getRange())) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
	        		count += 1;
	                test.sendMessage(msg);
	        	}
	        
	        if (count <= 1)
	        	player.sendMessage(TownySettings.parseSingleLineString("&cYou feel so lonely."));
	        	
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}

	private void parseDefaultChannelChatCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Boolean bEssentials = plugin.getTowny().isEssentials();
			
			event.setFormat(ChatSettings.getRelevantFormatGroup(player).getDEFAULT().replace("{channelTag}", getChannelTag()).replace("{msgcolour}", getMessageColour()));

			LocalTownyChatEvent chatEvent = new LocalTownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			
			// Throw a "fake" chat event for other plugins to handle the chat after TownyChat has formatted it.
			event = new TownyChatEvent(player, event.getMessage(), event.getFormat());
            plugin.getServer().getPluginManager().callEvent(event);
            
            // If the event was canceled by anything other then TownyChat
            if (event.isCancelled() && !((TownyChatEvent)event).isCanceledByTownyChat())
                return;
            
            // Read the format after it's been updated by all other plugins.
            String msg = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour(msg));
			
			CraftIRCHandler ircHander = plugin.getIRC();
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg, getCraftIRCTag());
			
			sendSpy(player, msg);
			
			int count = 0;
			for (Player test : TownyUniverse.getOnlinePlayers()) {
				if (!plugin.getTowny().isPermissions() || (plugin.getTowny().isPermissions() && TownyUniverse.getPermissionSource().has(test, getPermission()))) {
					
					if (bEssentials) {
						try {
							User targetUser = plugin.getTowny().getEssentials().getUser(test);
							// Don't send this message if the user is ignored
							if (targetUser.isIgnoredPlayer(player.getName()))
								continue;
						} catch (TownyException e) {
							// Failed to fetch user so ignore.
						}
					}
					if ((testDistance(player, test, getRange())) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
						count += 1;
						TownyMessaging.sendMessage(test, msg);
					}
				}				
			}
			if (count <= 1)
				player.sendMessage(TownySettings.parseSingleLineString("&cYou feel so lonely."));

		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}
	
	private void parseGlobalChannelChatCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Boolean bEssentials = plugin.getTowny().isEssentials();

			if (ChatSettings.isModify_chat())
				event.setFormat(ChatSettings.getRelevantFormatGroup(player).getGLOBAL().replace("{channelTag}", getChannelTag()).replace("{msgcolour}", getMessageColour()));

			LocalTownyChatEvent chatEvent = new LocalTownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			
			// Throw a "fake" chat event for other plugins to handle the chat after TownyChat has formatted it.
			event = new TownyChatEvent(player, event.getMessage(), event.getFormat());
            plugin.getServer().getPluginManager().callEvent(event);
            
            // If the event was canceled by anything other then TownyChat
            if (event.isCancelled() && !((TownyChatEvent)event).isCanceledByTownyChat())
                return;
            
            // Read the format after it's been updated by all other plugins.
            String msg = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour(msg));
			
			CraftIRCHandler ircHander = plugin.getIRC();
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg, getCraftIRCTag());
			
			sendSpy(player, msg);
			
			DynmapAPI dynMap = plugin.getDynmap();
			
			if (dynMap != null)
				dynMap.postPlayerMessageToWeb(player, event.getMessage());
			
			int count = 0;
			for (Player test : TownyUniverse.getOnlinePlayers()) {
				if (!plugin.getTowny().isPermissions() || (plugin.getTowny().isPermissions() && TownyUniverse.getPermissionSource().has(test, getPermission()))) {
					
					if (bEssentials) {
						try {
							User targetUser = plugin.getTowny().getEssentials().getUser(test);
							// Don't send this message if the user is ignored
							if (targetUser.isIgnoredPlayer(player.getName()))
								continue;
						} catch (TownyException e) {
							// Failed to fetch user so ignore.
						}
					}
					if (testDistance(player, test, getRange()) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
						count += 1;
						TownyMessaging.sendMessage(test, msg);
					}
				}
			}
			if (count <= 1)
				player.sendMessage(TownySettings.parseSingleLineString("&cYou feel so lonely."));

			// TownyMessaging.sendNationMessage(nation, chatEvent.getFormat());
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}
	
	private void sendSpy(Player player, String msg) {
		
		for (Player test : TownyUniverse.getOnlinePlayers()) {
			if (plugin.getTowny().hasPlayerMode(test, "spy")) {
				TownyMessaging.sendMessage(test, msg);
			}
		}
			
	}
	
	/**
	 * Check the distance between players and return a result based upon the range setting
	 * -1 = no limit
	 * 0 = same world
	 * any positive value = distance in blocks
	 * 
	 * @param player1
	 * @param player2
	 * @param range
	 * @return true if in range
	 */
	private boolean testDistance(Player player1, Player player2, double range) {
		
		// unlimited range (all worlds)
		if (range == -1)
			return true;
		
		// Same world only
		if (range == 0)
			return player1.getWorld().equals(player2.getWorld());
		
		// Range check (same world)
		if (player1.getWorld().equals(player2.getWorld()))
			return (player1.getLocation().distance(player2.getLocation()) < range);
		else
			return false;
	}

}
