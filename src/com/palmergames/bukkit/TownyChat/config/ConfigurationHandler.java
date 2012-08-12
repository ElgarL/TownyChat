package com.palmergames.bukkit.TownyChat.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.StandardChannel;
import com.palmergames.bukkit.TownyChat.channels.channelFormats;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.util.FileMgmt;

public class ConfigurationHandler
{

	private Chat plugin;

	/**
	 * Constructor
	 * 
	 * @param plugin
	 */
	public ConfigurationHandler(Chat plugin)
	{
		super();
		this.plugin = plugin;
	}

	/**
	 * Load all Channels from the Channels.yml If it doesn't exist, create it
	 * from the resource in this jar.
	 * 
	 * @param filepath
	 * @param defaultRes
	 * @return true if the channels were loaded
	 */
	@SuppressWarnings("unchecked")
	public boolean loadChannels(String filepath, String defaultRes)
	{

		String filename = filepath + FileMgmt.fileSeparator() + defaultRes;

		Map<String, Object> file = FileMgmt.getFile(filename, defaultRes, null);
		if (file != null)
		{

			for (String rootNode : file.keySet())
			{

				if (rootNode.equalsIgnoreCase("Channels"))
				{
					// Parse the channels
					Map<String, Object> allChannelNodes = (Map<String, Object>) file
							.get(rootNode);

					// Load channels if the file is NOT empty
					if (allChannelNodes != null)
					{
						for (String channelKey : allChannelNodes.keySet())
						{
							if (channelKey.equalsIgnoreCase("spam_time"))
								ChatSettings
										.setSpam_time((Double) allChannelNodes
												.get(channelKey));

							Map<String, Object> thisChannelNode = (Map<String, Object>) allChannelNodes
									.get(channelKey);
							Channel channel = new StandardChannel(plugin,
									channelKey.toLowerCase());

							for (String key : thisChannelNode.keySet())
							{
								Object element = thisChannelNode.get(key);

								if (key.equalsIgnoreCase("commands"))
									if (element instanceof ArrayList)
										channel.setCommands((List<String>) element);
									else if (element instanceof String)
										channel.setCommands(Arrays
												.asList(element.toString()));

								if (key.equalsIgnoreCase("type"))
									if (element instanceof String)
										channel.setType(channelTypes
												.valueOf(element.toString()));

								if (key.equalsIgnoreCase("channeltag"))
									if (element instanceof String)
										channel.setChannelTag(element
												.toString());

								if (key.equalsIgnoreCase("messagecolour"))
									if (element instanceof String)
										channel.setMessageColour(element
												.toString());

								if (key.equalsIgnoreCase("permission"))
									if (element instanceof String)
										channel.setPermission(element
												.toString());

								if (key.equalsIgnoreCase("craftIRCTag"))
									if (element instanceof String)
										channel.setCraftIRCTag(element
												.toString());

								if (key.equalsIgnoreCase("range"))
									channel.setRange(Double.valueOf(element
											.toString()));
							}

							plugin.getChannelsHandler().addChannel(channel);

							// System.out.print("Channel: " + channel.getName()
							// + " : Type : " + channel.getType().name());
						}
						return true;

					}
				}
			}
		}
		return false;
	}

	/**
	 * Load the ChatConfig.yml
	 * 
	 * If it doesn't exist, create it from the resource in this jar.
	 * 
	 * @param filepath
	 * @param defaultRes
	 * @return true if the config was loaded
	 */
	@SuppressWarnings(
	{
		"unchecked"
	})
	public boolean loadConfig(String filepath, String defaultRes)
	{

		String filename = filepath + FileMgmt.fileSeparator() + defaultRes;

		// Pass the plugin reference so it can load the defaults if needed.
		Map<String, Object> file = FileMgmt.getFile(filename, defaultRes,
				plugin);

		if (file != null)
		{

			for (String Key : file.keySet())
			{

				if (Key.equalsIgnoreCase("spam_time"))
					ChatSettings
							.setSpam_time(Double.parseDouble((file.get(Key))
									.toString()));

				if (Key.equalsIgnoreCase("HeroicDeathToIRC"))
				{
					Map<String, Object> subNodes = (Map<String, Object>) file
							.get(Key);

					for (String element : subNodes.keySet())
					{
						if (element.equalsIgnoreCase("enabled"))
							ChatSettings.setHeroicDeathToIRC(Boolean
									.valueOf(subNodes.get(element).toString()));

						if (element.equalsIgnoreCase("craftIRCTags"))
							ChatSettings.setheroicDeathTags(subNodes.get(
									element).toString());
					}
				}

				if (Key.equalsIgnoreCase("modify_chat"))
				{
					Map<String, Object> subNodes = (Map<String, Object>) file
							.get(Key);

					for (String element : subNodes.keySet())
					{
						if (element.equalsIgnoreCase("enable"))
							ChatSettings.setModify_chat(Boolean
									.valueOf(subNodes.get(element).toString()));

						if (element.equalsIgnoreCase("per_world"))
							ChatSettings.setPer_world(Boolean.valueOf(subNodes
									.get(element).toString()));
					}

				}

				if (Key.equalsIgnoreCase("colour"))
				{
					Map<String, Object> subNodes = (Map<String, Object>) file
							.get(Key);

					for (String element : subNodes.keySet())
					{
						if (element.equalsIgnoreCase("king"))
							Chat_Colors.setKingColour(subNodes.get(element)
									.toString());

						if (element.equalsIgnoreCase("mayor"))
							Chat_Colors.setMayorColour(subNodes.get(element)
									.toString());

						if (element.equalsIgnoreCase("resident"))
							Chat_Colors.setResidentColour(subNodes.get(element)
									.toString());
					}

				}

				if (Key.equalsIgnoreCase("tag_formats"))
				{
					Map<String, Object> subNodes = (Map<String, Object>) file
							.get(Key);

					for (String element : subNodes.keySet())
					{
						if (element.equalsIgnoreCase("world"))
							Tag_Formats.setWorldTag(subNodes.get(element)
									.toString());

						if (element.equalsIgnoreCase("town"))
							Tag_Formats.setTownTag(subNodes.get(element)
									.toString());

						if (element.equalsIgnoreCase("nation"))
							Tag_Formats.setNationTag(subNodes.get(element)
									.toString());

						if (element.equalsIgnoreCase("both"))
							Tag_Formats.setBothTags(subNodes.get(element)
									.toString());
					}

				}

				if (Key.equalsIgnoreCase("channel_formats"))
				{
					Map<String, Object> subNodes = (Map<String, Object>) file
							.get(Key);

					channelFormats group = new channelFormats(Key);

					for (String element : subNodes.keySet())
					{
						if (element.equalsIgnoreCase("global"))
							group.setGLOBAL(subNodes.get(element).toString());

						if (element.equalsIgnoreCase("town"))
							group.setTOWN(subNodes.get(element).toString());

						if (element.equalsIgnoreCase("nation"))
							group.setNATION(subNodes.get(element).toString());

						if (element.equalsIgnoreCase("default"))
							group.setDEFAULT(subNodes.get(element).toString());
					}
					ChatSettings.addFormatGroup(group);

				}

				if (Key.equalsIgnoreCase("worlds"))
				{
					Map<String, Object> allWorlds = (Map<String, Object>) file
							.get(Key);

					if (allWorlds != null)
					{
						for (String worlds : allWorlds.keySet())
						{
							Map<String, Object> world = (Map<String, Object>) allWorlds
									.get(worlds);

							channelFormats group = new channelFormats(worlds);

							for (String element : world.keySet())
							{
								if (element.equalsIgnoreCase("global"))
									group.setGLOBAL(world.get(element)
											.toString());

								if (element.equalsIgnoreCase("town"))
									group.setTOWN(world.get(element).toString());

								if (element.equalsIgnoreCase("nation"))
									group.setNATION(world.get(element)
											.toString());

								if (element.equalsIgnoreCase("default"))
									group.setDEFAULT(world.get(element)
											.toString());
							}
							ChatSettings.addFormatGroup(group);
						}
					}

				}

			}

			if (ChatSettings.populateWorldFormats())
				saveConfig(filename);

			return true;

		}

		return false;
	}

	private void saveConfig(String filepath)
	{

		File file = new File(filepath);

		if (file.exists() && file.isFile())
		{

			String newConfig;
			try
			{
				newConfig = FileMgmt.convertStreamToString("/ChatConfig.yml");
				newConfig = setConfigs(newConfig, false);

				FileMgmt.stringToFile(newConfig, filepath);

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		else
		{
			// Big error. No file found.
		}

	}

	/**
	 * Build the new Config file to save.
	 * 
	 * @param newConfig
	 * @param defaults
	 *            apply defaults or data from settings.
	 * @return the config in a string
	 */
	public String setConfigs(String newConfig, boolean defaults)
	{

		String config = newConfig;

		String global = "{channelTag} {worldname}{townytagoverride}{townycolor}{permprefix}{group} {townyprefix}{modplayername}{townypostfix}{permsuffix}&f:{msgcolour} {msg}";
		String town = "{channelTag} {townycolor}{permprefix}{townyprefix}{playername}{townypostfix}{permsuffix}&f:{msgcolour} {msg}";
		String nation = "{channelTag}{towntagoverride}{townycolor}{permprefix}{townyprefix}{playername}{townypostfix}{permsuffix}&f:{msgcolour} {msg}";
		String default_ = "{channelTag} {permprefix}{playername}{permsuffix}&f:{msgcolour} {msg}";

		String tag_world = "&f[&f%s&f] ";
		String tag_town = "&f[&3%s&f] ";
		String tag_nation = "&f[&e%s&f] ";
		String tag_both = "&f[&6%s&f|&3%s&f] ";

		String king = "&6";
		String mayor = "&b";
		String resident = "&f";

		config = config.replace(
				"[spam_time]",
				(defaults) ? "0.5" : Double.toString(ChatSettings
						.getSpam_time()));

		config = config.replace(
				"[hd_enable]",
				(defaults) ? "true" : Boolean.toString(ChatSettings
						.isHeroicDeathToIRC()));
		config = config.replace("[hd_tags]", (defaults) ? "admin"
				: ChatSettings.getHeroicDeathTags());

		config = config.replace("[globalformat]", (defaults) ? global
				: ChatSettings.getFormatGroup("channel_formats").getGLOBAL());
		config = config.replace("[townformat]", (defaults) ? town
				: ChatSettings.getFormatGroup("channel_formats").getTOWN());
		config = config.replace("[nationformat]", (defaults) ? nation
				: ChatSettings.getFormatGroup("channel_formats").getNATION());
		config = config.replace("[defaultformat]", (defaults) ? default_
				: ChatSettings.getFormatGroup("channel_formats").getDEFAULT());

		config = config.replace("[tag_world]", (defaults) ? tag_world
				: Tag_Formats.getWorldTag());
		config = config.replace("[tag_town]", (defaults) ? tag_town
				: Tag_Formats.getTownTag());
		config = config.replace("[tag_nation]", (defaults) ? tag_nation
				: Tag_Formats.getNationTag());
		config = config.replace("[tag_both]", (defaults) ? tag_both
				: Tag_Formats.getBothTags());

		config = config.replace("[colour_king]", (defaults) ? king
				: Chat_Colors.getKingColour());
		config = config.replace("[colour_mayor]", (defaults) ? mayor
				: Chat_Colors.getMayorColour());
		config = config.replace("[colour_resident]", (defaults) ? resident
				: Chat_Colors.getResidentColour());

		config = config.replace("[modify_enable]", (defaults) ? "true"
				: Boolean.toString(ChatSettings.isModify_chat()));
		config = config.replace("[modify_per_world]", (defaults) ? "false"
				: Boolean.toString(ChatSettings.isPer_world()));

		for (String key : ChatSettings.getFormatGroups().keySet())
		{
			if (!key.equalsIgnoreCase("channel_formats"))
			{
				channelFormats world = ChatSettings.getFormatGroup(key);

				config += "    '" + key + "':"
						+ System.getProperty("line.separator");

				config += "      global: '"
						+ ((defaults) ? global : world.getGLOBAL()) + "'"
						+ System.getProperty("line.separator");
				config += "      town: '"
						+ ((defaults) ? town : world.getTOWN()) + "'"
						+ System.getProperty("line.separator");
				config += "      nation: '"
						+ ((defaults) ? nation : world.getNATION()) + "'"
						+ System.getProperty("line.separator");
				config += "      default: '"
						+ ((defaults) ? default_ : world.getDEFAULT()) + "'"
						+ System.getProperty("line.separator");
			}

		}

		return config;
	}

}
