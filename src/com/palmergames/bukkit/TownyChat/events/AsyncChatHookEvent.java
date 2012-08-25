package com.palmergames.bukkit.TownyChat.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.palmergames.bukkit.TownyChat.channels.Channel;

/*
 * Allows other plugins to hook into a chat message being accepted into any of the channels
 */
public class AsyncChatHookEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
	protected AsyncPlayerChatEvent event = null;
	protected boolean changed = false;
	protected Channel channel=null;

	public AsyncChatHookEvent(AsyncPlayerChatEvent event, Channel channel) {
		this.event = event;
		this.changed = false;
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}

	/*
	 * Returns true if the hooked event was changed
	 */
	public boolean isChanged() {
		return changed;
	}

	/*
	 * Informs TownyChat if the event was changed or not
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public AsyncPlayerChatEvent getAsyncPlayerChatEvent() {
		return event;
	}
	
	public String getFormat() {
		return event.getFormat();
	}
	
	public String getMessage() {
		return event.getMessage();
	}
	
	public Set<Player> getRecipients() {
		return event.getRecipients();
	}
	
	public boolean isCancelled() {
		return event.isCancelled();
	}
	
	public Player getPlayer() {
		return event.getPlayer();
	}
	
	public void setRecipients(Set<Player> recipients) {
		changed = true;
		event.getRecipients().clear();
		event.getRecipients().addAll(recipients);
	}
	
	public void setFormat(String format) {
		changed = true;
		event.setFormat(format);
	}
	
	public void setMessage(String message) {
		changed = true;
		event.setFormat(message);
	}
	
	public void setCancelled(boolean cancel) {
		changed = (cancel == true);
		event.setCancelled(cancel);
	}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}