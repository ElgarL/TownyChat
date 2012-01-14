package com.palmergames.bukkit.TownyChat.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.util.FileMgmt;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * @author ElgarL
 *
 */
public class ChannelsHolder {
	
	private Chat plugin;
	
	/** Constructor
	 * 
	 * @param towny
	 */
	public ChannelsHolder(Chat plugin) {
		super();
		this.plugin = plugin;
	}

	
	// Container for all channels
	private Map<String,Channel> channels = new HashMap<String,Channel>();

	/**
	 * @return the channels
	 */
	public Map<String, Channel> getAllChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(HashMap<String, Channel> channels) {
		this.channels = channels;
	}
	
	public boolean isChannel(String channelName) {
		return channels.containsKey(channelName.toLowerCase());
	}
	
	public Channel getChannel(String channelName) {
		return channels.get(channelName.toLowerCase());
	}
	
	/**
	 * Get the relevant channel for this command
	 * if the player is permitted to use it.
	 * 
	 * @param player
	 * @param command
	 * @return channel or null if not permitted
	 */
	public Channel getChannel(Player player, String command) {
		
		for (Channel channel: channels.values()) {
			if (channel.getCommands().contains(command.toLowerCase())) {
				if (!plugin.getTowny().isPermissions()
					|| (plugin.getTowny().isPermissions() && ((TownyUniverse.getPermissionSource().hasPermission(player, channel.getPermission()))
														|| (channel.getPermission().isEmpty()))))
					return channel;
				
			}
		}
		return null;
	}
	
	public Channel getChannel(Player player, channelTypes type) {
		
		for (Channel channel: channels.values()) {
			if (channel.getType().equals(type)) {
				if (!plugin.getTowny().isPermissions()
					|| (plugin.getTowny().isPermissions() && ((TownyUniverse.getPermissionSource().hasPermission(player, channel.getPermission()))
														|| (channel.getPermission().isEmpty()))))
					return channel;
				
			}
		}
		return null;
	}
	
	/**
	 * Load all Channels form the Channels.yml
	 * If it doesn't exist, create it from
	 * the resource in this jar.
	 * 
	 * @param filepath
	 * @param defaultRes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean loadChannels(String filepath, String defaultRes) {

		String filename = filepath + FileMgmt.fileSeparator() + defaultRes;

		Map<String, Object> file = FileMgmt.getFile(filename, defaultRes);
		if (file != null) {

			// Parse the channels
			Map<String, Object> allChannelNodes = (Map<String, Object>) file.get("Channels");

			// Load channels if the file is NOT empty
			if (allChannelNodes != null) {
				for (String channelKey : allChannelNodes.keySet()) {
					if (channelKey.equalsIgnoreCase("spam_time"))
						plugin.setSpam_time((Long)allChannelNodes.get(channelKey));
						
						
					Map<String, Object> thisChannelNode = (Map<String, Object>) allChannelNodes.get(channelKey);
					Channel channel = new Channel(channelKey.toLowerCase());

					for (String key : thisChannelNode.keySet()) {
						Object element = thisChannelNode.get(key);

						if (key.equalsIgnoreCase("commands"))
							if (element instanceof ArrayList)
								channel.setCommands((List<String>) element);
							else if (element instanceof String)
								channel.setCommands(Arrays.asList(element.toString()));

						if (key.equalsIgnoreCase("type"))
							if (element instanceof String)
								channel.setType(channelTypes.valueOf(element.toString()));

						if (key.equalsIgnoreCase("channeltag"))
							if (element instanceof String)
								channel.setChannelTag(element.toString());

						if (key.equalsIgnoreCase("messagecolour"))
							if (element instanceof String)
								channel.setMessageColour(element.toString());

						if (key.equalsIgnoreCase("permission"))
							if (element instanceof String)
								channel.setPermission(element.toString());

						if (key.equalsIgnoreCase("range"))
							channel.setRange(Double.valueOf(element.toString()));
					}
					
					channels.put(channel.getName(), channel);
					
					//System.out.print("Channel: " + channel.getName() + " : Type : " + channel.getType().name());
				}
				return true;
				
			}
		}
		return false;
	}
	

	
}