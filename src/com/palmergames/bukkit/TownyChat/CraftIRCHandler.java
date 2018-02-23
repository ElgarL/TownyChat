package com.palmergames.bukkit.TownyChat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

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

	  - source: 'minecraft'
	    target: 'admin'
	    formatting:
	      chat: '%message%'
	    attributes:
	        chat: false
	      
	  - source: 'admin'     # These are endpoint tags
	    target: 'minecraft'    #
	    formatting:
	      chat: '%foreground%[%red%%ircPrefix%%sender%%foreground%] %message%'

 */
public class CraftIRCHandler extends BasePoint implements CommandEndPoint   {

	Towny towny;
	Chat plugin;
	CraftIRC irc;

	public CraftIRCHandler(Chat plugin, CraftIRC irc, String tag) {

		this.irc = irc;
		this.plugin = plugin;
		// Register this as the tags endpoint
		if (irc != null)
			irc.registerEndPoint(tag, this);
		// Second argument below is the command name
		//irc.registerCommand(tag, tag);
		
		// register a Listen for any restarts of craftIRC
		plugin.getServer().getPluginManager().registerEvents(new BukkitEvents(), plugin);
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

	protected class BukkitEvents implements Listener {

		@EventHandler(priority = EventPriority.NORMAL)
		public void onPluginEnable(PluginEnableEvent event) {

			if ((irc != null) && (event.getPlugin().equals(irc)))
				plugin.reload();


		}

	}

}

