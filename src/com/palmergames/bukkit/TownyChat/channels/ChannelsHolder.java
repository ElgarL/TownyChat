package com.palmergames.bukkit.TownyChat.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.towny.object.TownyUniverse;

/**
 * @author ElgarL
 *
 */
public class ChannelsHolder {
	
	private Chat plugin;
	
	/** Constructor
	 * 
	 * @param plugin
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
	
	public void addChannel(Channel chan) {
		if (isChannel(chan.getName()))
			channels.remove(chan);
			
		channels.put(chan.getName(), chan);
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
	
	
	/**
	 * Find a channel we are able to talk in, starting with the greatest range.
	 * 
	 * @param player
	 * @param type
	 * @return channel or null if none.
	 */
	public Channel getChannel(Player player, channelTypes type) {
		
		Channel local = null;
		Channel global = null;
		Channel world = null;
		
		for (Channel channel: channels.values()) {
			if (channel.getType().equals(type)) {
				if (!plugin.getTowny().isPermissions()
					|| (plugin.getTowny().isPermissions() && ((TownyUniverse.getPermissionSource().hasPermission(player, channel.getPermission()))
														|| (channel.getPermission().isEmpty())))) {
					if (channel.getRange() == -1) {
						global = channel;
					} else if (channel.getRange() == 0) {
						world = channel;
					} else
						local = channel;
				}
			}
		}
		
		if (global != null)
			return global;
		
		if (world != null)
			return world;
		
		if (local != null)
			return local;
		
		return null;
	}
	
	/**
	 * Fetch all channel permissions
	 * 
	 * @return Set of all permission nodes
	 */
	public Set<String> getAllPermissions() {
		
		Set<String> perms = new HashSet<String>();
		
		for (Channel channel: channels.values()) {
			if (!perms.contains(channel.getPermission())) {
				perms.add(channel.getPermission());
				
			}
		}
		return perms;
	}


}