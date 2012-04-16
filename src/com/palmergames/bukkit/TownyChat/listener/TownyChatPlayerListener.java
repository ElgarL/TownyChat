package com.palmergames.bukkit.TownyChat.listener;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
//import org.bukkit.event.player.PlayerListener;

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
import com.palmergames.util.StringMgmt;

public class TownyChatPlayerListener implements Listener  {
	private Chat plugin;
	
	private WeakHashMap<Player, Long> SpamTime = new WeakHashMap<Player, Long>();

	public TownyChatPlayerListener(Chat instance) {
		this.plugin = instance;
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
					channel.chatProcess(event);

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
					channel.chatProcess(event);
					event.setCancelled(true);
					return;
				}
			}
			
			// Find a global channel this player has permissions for.
			Channel channel = plugin.getChannelsHandler().getChannel(player, channelTypes.GLOBAL);
					
			if (channel != null) {
				channel.chatProcess(event);
				event.setCancelled(true);
				return;
			}
			
		}

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
