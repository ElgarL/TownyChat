package com.palmergames.bukkit.TownyChat.config;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.StandardChannel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.util.FileMgmt;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



public class ConfigurationHandler {
	
	private Chat plugin;
	
	/** Constructor
	 * 
	 * @param plugin
	 */
	public ConfigurationHandler(Chat plugin) {
		super();
		this.plugin = plugin;
	}
	
	
	/**
	 * Load all Channels from the Channels.yml
	 * If it doesn't exist, create it from
	 * the resource in this jar.
	 * 
	 * @param filepath
	 * @param defaultRes
	 * @return true if the channels were loaded
	 */
	@SuppressWarnings("unchecked")
	public boolean loadChannels(String filepath, String defaultRes) {

		String filename = filepath + File.separator + defaultRes;

		Map<String, Object> file;
		try {
			file = FileMgmt.getFile(filename, defaultRes);
		} catch (Exception e) {
			Bukkit.getLogger().severe("[TownyChat] Failed to load Channels.yml!");
			Bukkit.getLogger().severe("[TownyChat] Please check that the file passes a YAML Parser test:");
			Bukkit.getLogger().severe("[TownyChat] Online YAML Parser: https://yaml-online-parser.appspot.com/");
			return false;
		}
		if (file == null || !file.containsKey("Channels")) 
			return false;

		// Parse the channels
		Map<String, Object> allChannelNodes = (Map<String, Object>) file.get("Channels");
		if (allChannelNodes == null)
			return false;
		
		// Load channels if the file is NOT empty
		for (String channelKey : allChannelNodes.keySet()) {
				
			Map<String, Object> thisChannelNode = (Map<String, Object>) allChannelNodes.get(channelKey);
			Channel channel = new StandardChannel(plugin, channelKey.toLowerCase());

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

				if (key.equalsIgnoreCase("hooked")) {
					if (element instanceof Boolean) {
						channel.setHooked((Boolean)element);
					} else if (element instanceof String) {
						channel.setHooked(Boolean.parseBoolean(element.toString()));
					} else if (element instanceof Integer) {
						channel.setHooked(Integer.parseInt(element.toString()) != 0);
					}
				}

				if (key.equalsIgnoreCase("autojoin")) {
					if (element instanceof Boolean) {
						channel.setAutoJoin((Boolean)element);
					} else if (element instanceof String) {
						channel.setAutoJoin(Boolean.parseBoolean(element.toString()));
					} else if (element instanceof Integer) {
						channel.setAutoJoin(Integer.parseInt(element.toString()) != 0);
					}
				}

				if (key.equalsIgnoreCase("default")) {
					boolean set = false;
					if (element instanceof Boolean) {
						set = (element != null && ((Boolean)element) == true);
					} else if (element instanceof String) {
						set = Boolean.parseBoolean((String)element);
					} else if (element instanceof Integer) {
						set = (element != null && ((Integer)element) != 0);
					}
					if (set) {
						plugin.getChannelsHandler().setDefaultChannel(channel);
						plugin.getLogger().info("Default Channel set to " + channel.getName());
					}
				}
				
				if (key.equalsIgnoreCase("spam_time")) {
					channel.setSpam_time(Double.valueOf(element.toString()));
				}

				if (key.equalsIgnoreCase("channeltag"))
					if (element instanceof String)
						channel.setChannelTag(element.toString());

				if (key.equalsIgnoreCase("messagecolour"))
					if (element instanceof String)
						channel.setMessageColour(element.toString());

				if (key.equalsIgnoreCase("permission"))
					if (element instanceof String)
						channel.setPermission(element.toString());
				
				if (key.equalsIgnoreCase("leavepermission"))
					if (element instanceof String)
						channel.setLeavePermission(element.toString());

				if (key.equalsIgnoreCase("range"))
					channel.setRange(Double.valueOf(element.toString()));

				if (key.equalsIgnoreCase("sound"))
					channel.setChannelSound(String.valueOf(element));
			}
			
			// If no leave permission is set, create a default permission name
			if (channel.getLeavePermission() == null) {
				channel.setLeavePermission("towny.chat.leave." + channel.getName().toLowerCase());
			}
			plugin.getChannelsHandler().addChannel(channel);
		}
		if (plugin.getChannelsHandler().getDefaultChannel() == null) {
			// If there is no default channel set it to the first one that was parsed (the top one in the config)
			// This is because not everyone knows that you need to add a default: true into the channels.yml to make it the default channel!
			plugin.getChannelsHandler().setDefaultChannel(plugin.getChannelsHandler().getAllChannels().entrySet().iterator().next().getValue());
		}
		return true;
		
	}

}
