package com.palmergames.bukkit.TownyChat.listener;

//import org.bukkit.event.CustomEventListener;
//import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.squallseed31.heroicdeath.HeroicDeathEvent;
import com.palmergames.bukkit.TownyChat.IRCHandler;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;

/**
 * @author ElgarL
 * 
 */
//@SuppressWarnings("deprecation")
public class HeroicDeathForwarder implements Listener{	// extends CustomEventListener

	private IRCHandler ircHandler = null;

	public HeroicDeathForwarder(IRCHandler irc) {
		this.ircHandler = irc;
	}

	/**
	 * New style event handler
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onHeroicDeathEvent(HeroicDeathEvent event) {

		if (ircHandler != null)
			ircHandler.sendMessage(ChatSettings.getHeroicDeathTags(), event.getDeathCertificate().getMessage());

	}

	/**
	 * Old style Event handler
	 */
	/*
	@Override
	public void onCustomEvent(Event event) {

		if (!(event instanceof HeroicDeathEvent)) {
			return;
		} else {

			if (ircHandler != null) {
				HeroicDeathEvent heroicDeathEvent = (HeroicDeathEvent) event;
				ircHandler.IRCSender(heroicDeathEvent.getDeathCertificate().getMessage(), ChatSettings.getHeroicDeathTags());
			}
		}
	}
	*/
}
