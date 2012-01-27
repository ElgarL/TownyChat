package com.palmergames.bukkit.TownyChat.event;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
//import org.bukkit.event.player.PlayerListener;

import org.dynmap.DynmapAPI;
import com.earth2me.essentials.User;
import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.TownyException;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.util.StringMgmt;

public class TownyPlayerHighestListener implements Listener  {
	private final Chat plugin;
	private final CraftIRCHandler ircHander;
	private final DynmapAPI dynMap;
	
	private HashMap<Player, Long> SpamTime = new HashMap<Player, Long>();

	public TownyPlayerHighestListener(Chat instance, CraftIRCHandler irc, DynmapAPI dynMap) {
		this.plugin = instance;
		this.ircHander = irc;
		this.dynMap = dynMap;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();

		// Test if this player is registered with Towny.
		try {
			TownyUniverse.getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e) {
			return;
		}
		String split[] = event.getMessage().split("\\ ");
		String command = split[0].trim().toLowerCase().replace("/", "");
		String message = "";

		if (split.length > 1)
			message = StringMgmt.join(StringMgmt.remFirstArg(split), " ");

		Channel channel = plugin.getChannelsHandler().getChannel(player, command);
		if (channel != null) {
			//System.out.print("Channel Found");
			
			event.setMessage(message);

			// if not muted and has permission
			if (!isMuted(player)) {

				if (isSpam(player)) {
					event.setCancelled(true);
					return;
				}
				event.setMessage(message);

				// If no message toggle the chat mode.
				if (message.isEmpty()) {
					if (plugin.getTowny().hasPlayerMode(player, channel.getName()))
						plugin.getTowny().removePlayerMode(player);
					else
						plugin.getTowny().setPlayerMode(player, new String[]{channel.getName()}, true);

				} else {
					// Process the chat
					chatProcess(event, channel, player);

				}
				event.setCancelled(true);

			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(PlayerChatEvent event) {

		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();

		// Check if essentials has this player muted.
		if (!isMuted(player)) {

			if (isSpam(player)) {
				event.setCancelled(true);
				return;
			}

			for (Channel channel : plugin.getChannelsHandler().getAllChannels().values()) {
				if (plugin.getTowny().hasPlayerMode(player, channel.getName())) {
					// Channel Chat mode set
					// Process the chat
					chatProcess(event, channel, player);
					event.setCancelled(true);
					return;
				}
			}
			
			// Find a global channel this player has permissions for.
			Channel channel = plugin.getChannelsHandler().getChannel(player, channelTypes.GLOBAL);
					
			if (channel != null) {
				chatProcess(event, channel, player);
				event.setCancelled(true);
				return;
			}
			
		}

		if (ChatSettings.isModify_chat()) {
			try {
				event.setFormat(ChatSettings.getRelevantFormatGroup(player).getGLOBAL());
				Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

				TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
				event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			} catch (NotRegisteredException e) {
				// World or resident not registered with Towny
				e.printStackTrace();
			}
		}

	}


	
	private void chatProcess(PlayerChatEvent event, Channel chan, Player player) {
		
		
		channelTypes exec = channelTypes.valueOf(chan.getType().name());
		
		switch (exec) {
		
		case TOWN:
			parseTownChatCommand(event, chan, player);
			break;
		
		case NATION:
			parseNationChatCommand(event, chan, player);			
			break;
			
		case DEFAULT:
			parseDefaultChannelChatCommand(event, chan, player);
			
			break;
			
		case GLOBAL:
			parseGlobalChannelChatCommand(event, chan, player);
			
		}
		
	}

	private void parseTownChatCommand(PlayerChatEvent event, Channel chan, Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Town town = resident.getTown();

			event.setFormat(ChatSettings.getRelevantFormatGroup(player).getTOWN().replace("{channelTag}", chan.getChannelTag()).replace("{msgcolour}", chan.getMessageColour()));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour("[Town Msg] " + town.getName() + ": " + msg));
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			sendSpy(player, "[Town Msg] " + town.getName() + ": " + msg);

			// Send the town message
			int count = 0;
	        for (Player test : TownyUniverse.getOnlinePlayers(town))
	        	if ((testDistance(player, test, chan.getRange())) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
	        		count += 1;
	                test.sendMessage(msg);
	        	}
	        
	        if (count <= 1)
				player.sendMessage(TownySettings.parseSingleLineString("&cYou feel so lonely."));
			
			
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}

	private void parseNationChatCommand(PlayerChatEvent event, Channel chan, Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Nation nation = resident.getTown().getNation();

			event.setFormat(ChatSettings.getRelevantFormatGroup(player).getNATION().replace("{channelTag}", chan.getChannelTag()).replace("{msgcolour}", chan.getMessageColour()));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour("[Nation Msg] " + nation.getName() + ": " + msg));

			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			sendSpy(player, "[Nation Msg] " + nation.getName() + ": " + msg);
			
			// Send the town message
			int count = 0;
	        for (Player test : TownyUniverse.getOnlinePlayers(nation))
	        	if ((testDistance(player, test, chan.getRange())) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
	        		count += 1;
	                test.sendMessage(msg);
	        	}
	        
	        if (count <= 1)
	        	player.sendMessage(TownySettings.parseSingleLineString("&cYou feel so lonely."));
	        	
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getMessage());
		}
	}

	private void parseDefaultChannelChatCommand(PlayerChatEvent event, Channel chan, Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Boolean bEssentials = plugin.getTowny().isEssentials();
			
			event.setFormat(ChatSettings.getRelevantFormatGroup(player).getDEFAULT().replace("{channelTag}", chan.getChannelTag()).replace("{msgcolour}", chan.getMessageColour()));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour(msg));
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			sendSpy(player, msg);
			
			int count = 0;
			for (Player test : TownyUniverse.getOnlinePlayers()) {
				if (!plugin.getTowny().isPermissions() || (plugin.getTowny().isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, chan.getPermission()))) {
					
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
					if ((testDistance(player, test, chan.getRange())) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
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
	
	private void parseGlobalChannelChatCommand(PlayerChatEvent event, Channel chan, Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Boolean bEssentials = plugin.getTowny().isEssentials();

			if (ChatSettings.isModify_chat())
				event.setFormat(ChatSettings.getRelevantFormatGroup(player).getGLOBAL().replace("{channelTag}", chan.getChannelTag()).replace("{msgcolour}", chan.getMessageColour()));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			plugin.getLogger().info(ChatTools.stripColour(msg));
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			sendSpy(player, msg);
			
			if (dynMap != null)
				dynMap.postPlayerMessageToWeb(player, event.getMessage());
			
			int count = 0;
			for (Player test : TownyUniverse.getOnlinePlayers()) {
				if (!plugin.getTowny().isPermissions() || (plugin.getTowny().isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, chan.getPermission()))) {
					
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
					if (testDistance(player, test, chan.getRange()) && (!plugin.getTowny().hasPlayerMode(test, "spy"))) {
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
	private boolean isMuted(Player player) {
		// Check if essentials has this player muted.
		if (plugin.getTowny().isEssentials()) {
			try {
				if (plugin.getTowny().getEssentials().getUser(player).isMuted()) {
					TownyMessaging.sendErrorMsg(player, "Unable to talk...You are currently muted!");
					return true;
				}
			} catch (TownyException e) {
				// Get essentials failed
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Test if this player is spamming chat.
	 * One message every 2 seconds limit
	 * 
	 * @param player
	 * @return
	 */
	private boolean isSpam(Player player) {
		
		long timeNow = System.currentTimeMillis()/1000;
		long spam = timeNow;
		
		if (SpamTime.containsKey(player)) {
			spam = SpamTime.get(player);
			SpamTime.remove(player);
		} else {
			// No record found so ensure we don't trigger for spam
			spam -= (ChatSettings.getSpam_time() + 1);
		}
		
		SpamTime.put(player, timeNow);
		
		if (timeNow - spam < ChatSettings.getSpam_time()) {
			TownyMessaging.sendErrorMsg(player, "Unable to talk...You are spamming!");
			return true;
		}
		return false;
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
