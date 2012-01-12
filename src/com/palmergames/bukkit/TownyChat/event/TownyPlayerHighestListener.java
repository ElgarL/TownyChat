package com.palmergames.bukkit.TownyChat.event;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

import org.dynmap.DynmapAPI;
import com.earth2me.essentials.User;
import com.palmergames.bukkit.towny.NotRegisteredException;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyException;
import com.palmergames.bukkit.towny.TownyLogger;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.TownyChat.event.TownyChatEvent;
import com.palmergames.bukkit.TownyChat.CraftIRCHandler;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.util.StringMgmt;

public class TownyPlayerHighestListener extends PlayerListener  {
	private final Towny towny;
	private final CraftIRCHandler ircHander;
	private final DynmapAPI dynMap;
	
	private HashMap<Player, Long> SpamTime = new HashMap<Player, Long>();

	public TownyPlayerHighestListener(Towny instance, CraftIRCHandler irc, DynmapAPI dynMap) {
		this.towny = instance;
		this.ircHander = irc;
		this.dynMap = dynMap;
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();

		// Test if this player is registered with Towny.
		try {
			TownyUniverse.getDataSource().getResident(player.getName());
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
			

			//if not muted and has permission
			if (!isMuted(player)) {
				
				if (isSpam(player)) {
					event.setCancelled(true);
					return;
				}
				
				if (!towny.isPermissions() || (towny.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(player, TownySettings.getChatChannelPermission(command)))) {

					event.setMessage(message);
					
					// If no message toggle the chat mode.
					if (message.isEmpty()) {
						towny.setPlayerChatMode(player, command.replace("/", ""));
						
					} else {
						// Process the chat
						chatProcess(event, command, player);
						
					}
					event.setCancelled(true);
	
				} else {
					TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_command_disable"));
					event.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {

		Player player = event.getPlayer();
		
		// Check if essentials has this player muted.
		if (!isMuted(player)) {
			
			if (isSpam(player)) {
				event.setCancelled(true);
				return;
			}
			
			for (String channel : TownySettings.getChatChannels()) {
				if (towny.hasPlayerMode(player, channel.replace("/", ""))) {
					// Channel Chat
					// Process the chat
					chatProcess(event, channel, player);
					event.setCancelled(true);
					return;
				}
			}
			
			// No modes set so this must be open/global chat.
			parseGlobalChannelChatCommand(event, "/g", player);
			
		}
		event.setCancelled(true);

	}
	
	private void chatProcess(PlayerChatEvent event, String command, Player player) {
		
		 if (command.equalsIgnoreCase("/tc")) {
			// Town Chat
			parseTownChatCommand(event, "/tc", player);
				
		} else if (command.equalsIgnoreCase("/nc")) {
			// Nation chat
			parseNationChatCommand(event, "/nc", player);
				
		} else if (command.equalsIgnoreCase("/g")) {
			// Global chat
			parseGlobalChannelChatCommand(event, "/g", player);
				
		} else {
			
			// Deal with custom channel cases (mod, admin etc)
			parseDefaultChannelChatCommand(event, command, player);
		}
		
	}

	private void parseTownChatCommand(PlayerChatEvent event, String command, Player player) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Town town = resident.getTown();

			event.setFormat(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()).getChatTownChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			TownyLogger.log.info(ChatTools.stripColour(msg));
			
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
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Nation nation = resident.getTown().getNation();

			event.setFormat(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()).getChatNationChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			TownyLogger.log.info(ChatTools.stripColour(msg));

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
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Boolean bEssentials = towny.isEssentials();
			
			event.setFormat(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()).getChatDefaultChannelFormat().replace("{channelTag}", TownySettings.getChatChannelName(command)).replace("{msgcolour}", TownySettings.getChatChannelColour(command)));

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			TownyLogger.log.info(ChatTools.stripColour(msg));
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			for (Player test : TownyUniverse.getOnlinePlayers()) {
				if (!towny.isPermissions() || (towny.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, TownySettings.getChatChannelPermission(command)))) {
					
					if (bEssentials) {
						try {
							User targetUser = towny.getEssentials().getUser(test);
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
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			Boolean bEssentials = towny.isEssentials();

			if (TownySettings.isUsingModifyChat()) {
				event.setFormat(TownyUniverse.getDataSource().getWorld(player.getWorld().getName()).getChatGlobalChannelFormat());
			}

			TownyChatEvent chatEvent = new TownyChatEvent(event, resident);
			event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			String msg = chatEvent.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s", event.getMessage());
			
			TownyLogger.log.info(ChatTools.stripColour(msg));
			
			// Relay to IRC
			if (ircHander != null)
				ircHander.IRCSender(msg);
			
			if (dynMap != null)
				dynMap.postPlayerMessageToWeb(player, event.getMessage());
			
			for (Player test : TownyUniverse.getOnlinePlayers()) {
				if (!towny.isPermissions() || (towny.isPermissions() && TownyUniverse.getPermissionSource().hasPermission(test, TownySettings.getChatChannelPermission(command)))) {
					
					if (bEssentials) {
						try {
							User targetUser = towny.getEssentials().getUser(test);
							// Don't send this message if the user is ignored
							if (targetUser.isIgnoredPlayer(player.getName()))
								continue;
						} catch (TownyException e) {
							// Failed to fetch user so ignore.
						}
					}
					if (testDistance(player, test, TownySettings.getChatChannelRange(command)))
						TownyMessaging.sendMessage(test, msg);
				}
			}

			// TownyMessaging.sendNationMessage(nation, chatEvent.getFormat());
		} catch (NotRegisteredException x) {
			TownyMessaging.sendErrorMsg(player, x.getError());
		}
	}
	
	private boolean isMuted(Player player) {
		// Check if essentials has this player muted.
		if (towny.isEssentials()) {
			try {
				if (towny.getEssentials().getUser(player).isMuted()) {
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
			spam -= 3;
		}
		
		SpamTime.put(player, timeNow);
		
		if (timeNow - spam < 2) {
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
		
		// Range check
		return (player1.getLocation().distance(player2.getLocation()) < range);
	}
}
