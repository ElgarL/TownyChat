package com.palmergames.bukkit.TownyChat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IRCUtil {
	
	private static final Pattern pattern = Pattern.compile("&{1}[0-9A-Fa-f]{1}");

	/**
	 * Convert a game message to a IRC message (Colours)
	 * 
	 * @param message
	 * 
	 * @return String, formated message
	 */
	public static String gameToIRCColours(String message) {

		if (message.contains("\\u00A7")) {
			message = message.replaceAll("\\u00A70", "00");
			message = message.replaceAll("\\u00A71", "02");
			message = message.replaceAll("\\u00A72", "03");
			message = message.replaceAll("\\u00A73", "10");
			message = message.replaceAll("\\u00A74", "04");
			message = message.replaceAll("\\u00A75", "06");
			message = message.replaceAll("\\u00A76", "07");
			message = message.replaceAll("\\u00A77", "15");
			message = message.replaceAll("\\u00A78", "14");
			message = message.replaceAll("\\u00A79", "12");
			message = message.replaceAll("\\u00A7a", "09");
			message = message.replaceAll("\\u00A7b", "11");
			message = message.replaceAll("\\u00A7c", "05");
			message = message.replaceAll("\\u00A7d", "13");
			message = message.replaceAll("\\u00A7e", "08");
			message = message.replaceAll("\\u00A7f", "01");
		}
		
		return message;
	}
	
	/**
	 * Convert an IRC message to game
	 * 
	 * @return String, formated message
	 * 
	 */
	public static String ircToIRCChat(String sender, String channel, String message, String rank, String format) {
		
		String formatedMessage = format.replace("{Sender}", sender).replace("{Channel}", channel).replace("{Ranked}", rank);
        Matcher match = pattern.matcher(formatedMessage);
        StringBuffer sb = new StringBuffer();
        
        while (match.find()) {
        	match.appendReplacement(sb, "\u00A7" + match.group().substring(1));
        }
        
        match.appendTail(sb);
        
        return sb.toString().replace("{Message}", message);
		
	}
}
