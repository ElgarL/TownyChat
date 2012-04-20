package com.palmergames.bukkit.TownyChat;

import com.ensifera.animosity.craftirc.BasePoint;
import com.ensifera.animosity.craftirc.CommandEndPoint;
import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.RelayedCommand;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.palmergames.bukkit.towny.Towny;

/**
 * @author ElgarL
 * 
 * Add a channel tag of 'admin' to the
 * receiving channel in the craftIRC config.
 * 
 * Disable Auto paths and add the following to the 'paths:' section
 */
/*
	Set...
 		auto-paths: false
 		
 	Under channels:
 	
 	create your channel...
 	
 	- name: '#YourChannel'
        password: ''
        
        #Identifies this channel's endpoint (for the paths: section).
        tag: 'admin'
 	
 	Under paths:
 		
    - source: 'admin'     # These are endpoint tags
      target: 'minecraft'    #
      formatting:
        chat: '%foreground%[%red%%ircPrefix%%sender%%foreground%] %message%'
        
 	
 
 */
public class CraftIRCHandler extends BasePoint implements CommandEndPoint   {

	Towny plugin;
	CraftIRC irc;

	public CraftIRCHandler( CraftIRC irc, String tag) {

		this.irc = irc;
		// Register this as the tags endpoint
		if (irc != null)
			irc.registerEndPoint(tag, this);
		// Second argument below is the command name
		//irc.registerCommand(tag, tag);
	}

	@Override
	public Type getType() {
		return Type.MINECRAFT;
	}

	@Override
	public void commandIn(RelayedCommand arg0) {

		// TODO Auto-generated method stub
		System.out.print("command");
	}

	/**
	 * @param message
	 */
	public void IRCSender(String message, String tags) {
		/**
		 * Relay this to every channel tag listed
		 */
		String[] tagArray = tags.split(",");
		
		for (String destinationTag : tagArray) {
			RelayedMessage msg = irc.newMsgToTag(this, destinationTag.trim(), "chat");
			//msg.setField("command", "");
			msg.setField("message", message);
			msg.post(false);
		}
	}


}