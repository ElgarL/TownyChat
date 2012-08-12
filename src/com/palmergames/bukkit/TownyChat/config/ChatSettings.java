package com.palmergames.bukkit.TownyChat.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.channels.channelFormats;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;

public class ChatSettings extends Tag_Formats
{

	private static Double spam_time;
	private static boolean modify_chat;
	private static boolean per_world;
	private static boolean heroicDeathToIRC = true;
	private static String heroicDeathTags = "admin";

	private static Map<String, channelFormats> formatGroups = new HashMap<String, channelFormats>();

	/**
	 * @return the formatGroups
	 */
	public static Map<String, channelFormats> getFormatGroups()
	{
		return formatGroups;
	}

	/**
	 * @param formatGroups
	 *            the formatGroups to set
	 */
	public static void setFormatGroups(Map<String, channelFormats> formatGroups)
	{
		ChatSettings.formatGroups = formatGroups;
	}

	/**
	 * @return the formatGroup
	 */
	public static channelFormats getFormatGroup(String name)
	{
		return formatGroups.get(name.toLowerCase());
	}

	public static boolean hasFormatGroup(String groupName)
	{
		return formatGroups.containsKey(groupName.toLowerCase());
	}

	public static void addFormatGroup(channelFormats group)
	{
		if (hasFormatGroup(group.getName()))
			formatGroups.remove(group.getName());

		formatGroups.put(group.getName(), group);
	}

	public static void removeFormatGroup(String name)
	{
		if (hasFormatGroup(name.toLowerCase()))
			formatGroups.remove(name.toLowerCase());
	}

	public static channelFormats getRelevantFormatGroup(Player player)
	{

		if (isPer_world())
		{
			String name = player.getWorld().getName();
			if (hasFormatGroup(name))
				return getFormatGroup(name);
		}

		return getFormatGroup("channel_formats");

	}

	/**
	 * @return the spam_time
	 */
	public static Double getSpam_time()
	{
		return spam_time;
	}

	/**
	 * @param d
	 *            the spam_time to set
	 */
	public static void setSpam_time(Double d)
	{
		ChatSettings.spam_time = d;
	}

	/**
	 * @return the modify_chat
	 */
	public static boolean isModify_chat()
	{
		return modify_chat;
	}

	/**
	 * @param modify_chat
	 *            the modify_chat to set
	 */
	public static void setModify_chat(boolean modify_chat)
	{
		ChatSettings.modify_chat = modify_chat;
	}

	/**
	 * @return the per_world
	 */
	public static boolean isPer_world()
	{
		return per_world;
	}

	/**
	 * @param per_world
	 *            the per_world to set
	 */
	public static void setPer_world(boolean per_world)
	{
		ChatSettings.per_world = per_world;
	}

	/**
	 * @return the HeroicDeathToIRC
	 */
	public static boolean isHeroicDeathToIRC()
	{
		return heroicDeathToIRC;
	}

	/**
	 * @param HeroicDeathToIRC
	 *            the HeroicDeathToIRC to set
	 */
	public static void setHeroicDeathToIRC(boolean HeroicDeathToIRC)
	{
		ChatSettings.heroicDeathToIRC = HeroicDeathToIRC;
	}

	/**
	 * @return the heroicDeathTags
	 */
	public static String getHeroicDeathTags()
	{
		return heroicDeathTags;
	}

	/**
	 * @param heroicDeathTags
	 *            the heroicDeathTags to set
	 */
	public static void setheroicDeathTags(String heroicDeathTags)
	{
		ChatSettings.heroicDeathTags = heroicDeathTags;
	}

	/**
	 * Adds customizable channel formats for each world.
	 * 
	 * @return true if new worlds we're found
	 */
	public static boolean populateWorldFormats()
	{

		boolean updated = false;

		for (TownyWorld world : TownyUniverse.getDataSource().getWorlds())
		{
			if (!hasFormatGroup(world.getName()))
			{
				addFormatGroup(getFormatGroup("channel_formats").clone(
						world.getName()));
				updated = true;
			}
		}

		return updated;

	}

}
