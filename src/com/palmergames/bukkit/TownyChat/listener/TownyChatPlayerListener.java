package com.palmergames.bukkit.TownyChat.listener;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.listener.LocalTownyChatEvent;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.Colors;
import com.palmergames.util.StringMgmt;

public class TownyChatPlayerListener implements Listener  {
	private Chat plugin;
	
	private WeakHashMap<Player, Long> SpamTime = new WeakHashMap<Player, Long>();
	private WeakHashMap<Player, String> directedChat = new WeakHashMap<Player, String>();

	public TownyChatPlayerListener(Chat instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
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

			
			/*
			 *  If there is no message toggle the chat mode.
			 */
			if (message.isEmpty()) {
				if (plugin.getTowny().hasPlayerMode(player, channel.getName()))
					plugin.getTowny().removePlayerMode(player);
				else
					plugin.getTowny().setPlayerMode(player, new String[]{channel.getName()}, true);

			} else {
				// Notify player he is muted
				if (channel.isMuted(player.getName())) {
					TownyMessaging.sendErrorMsg(player, "You are currently muted in " + channel.getName() + "!");
					event.setCancelled(true);
					return;
				}
				
				/*
				 * Flag this as directed chat and trigger a player chat event
				 */
				event.setMessage(message);
				
				directedChat.put(player, command);
				player.chat(message);
			}
			
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		String name = event.getPlayer().getName(); 
		for (Channel channel : plugin.getChannelsHandler().getAllChannels().values()) {
			channel.forgetPlayer(name);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		Player player = event.getPlayer();
		String playerName = player.getName();
		
		// Check if essentials has this player muted.
		if (!isMuted(player)) {

			if (isSpam(player)) {
				event.setCancelled(true);
				return;
			}

			/*
			 * If this was directed chat send it via the relevant channel
			 */
			if (directedChat.containsKey(player)) {
				Channel channel = plugin.getChannelsHandler().getChannel(player, directedChat.get(player));
				directedChat.remove(player);
				
				if (channel != null) {
					// Notify player he is muted
					if (channel.isMuted(player.getName())) {
						TownyMessaging.sendErrorMsg(player, "You are currently muted in " + Colors.White + channel.getName() + Colors.Rose + "!");
						event.setCancelled(true);
						return;
					}
					// If player sends a message to a channel it had left
					// tell the channel to add the player back
					if (channel.isAbsent(playerName)) {
						channel.forgetPlayer(playerName);
					}
					channel.chatProcess(event);
					return;
				}
			}
			
			/*
			 * Check the player for any channel modes.
			 */
			for (Channel channel : plugin.getChannelsHandler().getAllChannels().values()) {
				if (plugin.getTowny().hasPlayerMode(player, channel.getName())) {
					// Notify player he is muted
					if (channel.isMuted(player.getName())) {
						TownyMessaging.sendErrorMsg(player, "You are currently muted in " + Colors.White + channel.getName() + Colors.Rose + "!");
						event.setCancelled(true);
						return;
					}
					/*
					 *  Channel Chat mode set
					 *  Process the chat
					 */
					channel.chatProcess(event);
					return;
				}
			}
			
			// Find a global channel this player has permissions for.
			Channel channel = plugin.getChannelsHandler().getActiveChannel(player, channelTypes.GLOBAL);
					
			if (channel != null) {
				// Notify player he is muted
				if (channel.isMuted(player.getName())) {
					TownyMessaging.sendErrorMsg(player, "You are currently muted in " + Colors.White + channel.getName() + Colors.Rose + "!");
					event.setCancelled(true);
					return;
				}

				channel.chatProcess(event);
				return;
			}
			
		}

		/*
		 * We found no channels available so modify the chat (if enabled) and exit.
		 */
		if (ChatSettings.isModify_chat()) {
			try {
				event.setFormat(ChatSettings.getRelevantFormatGroup(player).getGLOBAL().replace("{channelTag}", "").replace("{msgcolour}", ""));
				Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

				LocalTownyChatEvent chatEvent = new LocalTownyChatEvent(event, resident);
				event.setFormat(TownyChatFormatter.getChatFormat(chatEvent));
			} catch (NotRegisteredException e) {
				// World or resident not registered with Towny
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Is this player Muted via Essentials?
	 * 
	 * @param player
	 * @return true if muted
	 */
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
		
		long timeNow = System.currentTimeMillis();
		long spam = timeNow;
		
		if (SpamTime.containsKey(player)) {
			spam = SpamTime.get(player);
			SpamTime.remove(player);
		} else {
			// No record found so ensure we don't trigger for spam
			spam -= ((ChatSettings.getSpam_time() + 1)*1000);
		}
		
		SpamTime.put(player, timeNow);
		
		if (timeNow - spam < (ChatSettings.getSpam_time()*1000)) {
			TownyMessaging.sendErrorMsg(player, "Unable to talk...You are spamming!");
			return true;
		}
		return false;
	}
}
