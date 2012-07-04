package com.palmergames.bukkit.TownyChat;

import com.palmergames.bukkit.TownyChat.IRC.IRCMain;


public class PircHandler {

	/**
	 * @param message
	 */
	public void IRCSender(String message, String tags) {
		/**
		 * Relay this to every channel tag listed
		 */
		String[] tagArray = tags.split(",");
		
		for (String channelToSend : tagArray) {
			
			IRCMain.sendMesage(channelToSend, message);
			
		}
	}
}
