package com.palmergames.bukkit.TownyChat.config;

public class Chat_Colors
{

	private static String KING, MAYOR, RESIDENT;

	/**
	 * @return KING the KING colour
	 */
	public static String getKingColour()
	{
		return KING;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public static void setKingColour(String colour)
	{
		Chat_Colors.KING = colour;
	}

	/**
	 * @return MAYOR the MAYOR colour
	 */
	public static String getMayorColour()
	{
		return MAYOR;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public static void setMayorColour(String colour)
	{
		Chat_Colors.MAYOR = colour;
	}

	/**
	 * @return RESIDENT the RESIDENT colour
	 */
	public static String getResidentColour()
	{
		return RESIDENT;
	}

	/**
	 * @param colour
	 *            the RESIDENT colour tag to set
	 */
	public static void setResidentColour(String colour)
	{
		Chat_Colors.RESIDENT = colour;
	}

}
