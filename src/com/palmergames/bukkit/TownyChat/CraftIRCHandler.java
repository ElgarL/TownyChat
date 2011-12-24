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
    - source: 'minecraft'
      target: 'admin'
      formatting:
        chat: '%message%'
      
    - source: 'admin'     # These are endpoint tags
      target: 'minecraft'    #
      formatting:
        chat: '%foreground%[%red%%ircPrefix%%sender%%foreground%] %message%'
 
 */
public class CraftIRCHandler extends BasePoint implements CommandEndPoint  {

	Towny plugin;
	CraftIRC irc;

	public CraftIRCHandler(Towny plugin, CraftIRC irc, String tag) {
		this.plugin = plugin;
		this.irc = irc;
		// Register this as the tags endpoint
		irc.registerEndPoint(tag, this);
		// Second argument below is the command name
		//irc.registerCommand(tag, tag);
	}

	@Override
	public Type getType() {
		return Type.MINECRAFT;
	}
	
	@Override
	public void commandIn(RelayedCommand cmd) {
		// Do nothing here as we do not process commands from IRC.
	}

	/**
	 * @param message
	 */
	public void IRCSender(String message) {
		/**
		 * admin is the channel tag to send ALL messages to.
		 */
		RelayedMessage msg = irc.newMsgToTag(this, "admin", "chat");
		//msg.setField("command", "");
		msg.setField("message", message);
		msg.post(false);
	}

}