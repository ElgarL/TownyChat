package com.palmergames.bukkit.TownyChat.config;

public class Tag_Formats
{

	private static String WorldTag, TownTag, NationTag, BothTags;

	/**
	 * @return the World tag
	 */
	public static String getWorldTag()
	{
		return WorldTag;
	}

	/**
	 * @param tag
	 *            the World tag to set
	 */
	public static void setWorldTag(String tag)
	{
		Tag_Formats.WorldTag = tag;
	}

	/**
	 * @return the TownTag
	 */
	public static String getTownTag()
	{
		return TownTag;
	}

	/**
	 * @param tag
	 *            the TOWN tag to set
	 */
	public static void setTownTag(String tag)
	{
		Tag_Formats.TownTag = tag;
	}

	/**
	 * @return NationTag
	 */
	public static String getNationTag()
	{
		return NationTag;
	}

	/**
	 * @param tag
	 *            the NATION tag to set
	 */
	public static void setNationTag(String tag)
	{
		Tag_Formats.NationTag = tag;
	}

	/**
	 * @return BothTags the nation and town tags
	 */
	public static String getBothTags()
	{
		return BothTags;
	}

	/**
	 * @param tag
	 *            the Nation and Town tags to set
	 */
	public static void setBothTags(String tag)
	{
		Tag_Formats.BothTags = tag;
	}

}
