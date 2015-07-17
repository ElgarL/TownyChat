package com.palmergames.bukkit.TownyChat.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.TownyChat.channels.channelFormats;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;


public class ChatSettings extends tag_formats {

	private static Double spam_time;
	private static boolean modify_chat;
	private static boolean per_world;
	private static boolean alone_message;
	private static String alone_message_string;
	private static boolean heroicDeathToIRC = true;
	private static String heroicDeathTags ="admin";

	private static Map<String, channelFormats> formatGroups = new HashMap<String, channelFormats>();

	/**
	 * @return the formatGroups
	 */
	public static Map<String, channelFormats> getFormatGroups() {
		return formatGroups;
	}

	/**
	 * @param formatGroups
	 *            the formatGroups to set
	 */
	public static void setFormatGroups(Map<String, channelFormats> formatGroups) {
		ChatSettings.formatGroups = formatGroups;
	}

	/**
	 * @return the formatGroup
	 */
	public static channelFormats getFormatGroup(String name) {
		return formatGroups.get(name.toLowerCase());
	}

	public static boolean hasFormatGroup(String groupName) {
		return formatGroups.containsKey(groupName.toLowerCase());
	}

	public static void addFormatGroup(channelFormats group) {
		if (hasFormatGroup(group.getName()))
			formatGroups.remove(group.getName());

		formatGroups.put(group.getName(), group);
	}

	public static void removeFormatGroup(String name) {
		if (hasFormatGroup(name.toLowerCase()))
			formatGroups.remove(name.toLowerCase());
	}
	
	public static channelFormats getRelevantFormatGroup (Player player) {
		
		if (isPer_world()) {
			String name = player.getWorld().getName();
			if (hasFormatGroup(name))
				return getFormatGroup(name);	
		}
		
		return getFormatGroup("channel_formats");

	}

	/**
	 * @return the spam_time
	 */
	public static Double getSpam_time() {
		return spam_time;
	}

	/**
	 * @param d
	 *            the spam_time to set
	 */
	public static void setSpam_time(Double d) {
		ChatSettings.spam_time = d;
	}

	/**
	 * @return the modify_chat
	 */
	public static boolean isModify_chat() {
		return modify_chat;
	}

	/**
	 * @param modify_chat
	 *            the modify_chat to set
	 */
	public static void setModify_chat(boolean modify_chat) {
		ChatSettings.modify_chat = modify_chat;
	}

	/**
	 * @return the alone_message
	 */
	public static boolean isUsingAloneMessage() {
		return alone_message;
	}
	
	/**
	 * @param  alone_message
	 */
	public static boolean setUsingAloneMessage(boolean alone_message) {
		return ChatSettings.alone_message = alone_message;
	}

	/**
	 * @param  alone_message_string
	 * @return 
	 */
	public static String setUsingAloneMessageString(String alone_message_string) {
		return ChatSettings.alone_message_string = alone_message_string;
	}
	
	/**
	 * @param  alone_message_string
	 * @return 
	 */
	public static String getUsingAloneMessageString() {
		return ChatSettings.alone_message_string;
	}

	/**
	 * @return the per_world
	 */
	public static boolean isPer_world() {
		return per_world;
	}

	/**
	 * @param per_world
	 *            the per_world to set
	 */
	public static void setPer_world(boolean per_world) {
		ChatSettings.per_world = per_world;
	}
	
	/**
	 * @return the HeroicDeathToIRC
	 */
	public static boolean isHeroicDeathToIRC() {
		return heroicDeathToIRC;
	}

	/**
	 * @param HeroicDeathToIRC
	 *            the HeroicDeathToIRC to set
	 */
	public static void setHeroicDeathToIRC(boolean HeroicDeathToIRC) {
		ChatSettings.heroicDeathToIRC = HeroicDeathToIRC;
	}
	
	/**
	 * @return the heroicDeathTags
	 */
	public static String getHeroicDeathTags() {
		return heroicDeathTags;
	}

	/**
	 * @param heroicDeathTags
	 *            the heroicDeathTags to set
	 */
	public static void setheroicDeathTags(String heroicDeathTags) {
		ChatSettings.heroicDeathTags = heroicDeathTags;
	}
	
	
	

	/**
	 * Adds customizable channel formats for each world.
	 * 
	 * @return true if new worlds we're found
	 */
	public static boolean populateWorldFormats() {

		boolean updated = false;

		for (TownyWorld world : TownyUniverse.getDataSource().getWorlds()) {
			if (!hasFormatGroup(world.getName())) {
				addFormatGroup(getFormatGroup("channel_formats").clone(world.getName()));
				updated = true;
			}
		}

		return updated;

	}

}
//////////////////////////////////////////////////

class tag_formats extends chat_colours{

	private static String WorldTag, TownTag, NationTag, BothTags;

	/**
	 * @return the World tag
	 */
	public static String getWorldTag() {
		return WorldTag;
	}

	/**
	 * @param tag the World tag to set
	 */
	public static void setWorldTag(String tag) {
		tag_formats.WorldTag = tag;
	}

	/**
	 * @return the TownTag
	 */
	public static String getTownTag() {
		return TownTag;
	}

	/**
	 * @param tag the TOWN tag to set
	 */
	public static void setTownTag(String tag) {
		tag_formats.TownTag = tag;
	}

	/**
	 * @return NationTag
	 */
	public static String getNationTag() {
		return NationTag;
	}

	/**
	 * @param tag the NATION tag to set
	 */
	public static void setNationTag(String tag) {
		tag_formats.NationTag = tag;
	}

	/**
	 * @return BothTags the nation and town tags
	 */
	public static String getBothTags() {
		return BothTags;
	}

	/**
	 * @param tag the Nation and Town tags to set
	 */
	public static void setBothTags(String tag) {
		tag_formats.BothTags = tag;
	}

}

class chat_colours {

	private static String KING, MAYOR, RESIDENT;

	/**
	 * @return KING the KING colour
	 */
	public static String getKingColour() {
		return KING;
	}

	/**
	 * @param colour the colour to set
	 */
	public static void setKingColour(String colour) {
		chat_colours.KING = colour;
	}

	/**
	 * @return MAYOR the MAYOR colour
	 */
	public static String getMayorColour() {
		return MAYOR;
	}

	/**
	 * @param colour the colour to set
	 */
	public static void setMayorColour(String colour) {
		chat_colours.MAYOR = colour;
	}

	/**
	 * @return RESIDENT the RESIDENT colour
	 */
	public static String getResidentColour() {
		return RESIDENT;
	}

	/**
	 * @param colour the RESIDENT colour tag to set
	 */
	public static void setResidentColour(String colour) {
		chat_colours.RESIDENT = colour;
	}
	
}

