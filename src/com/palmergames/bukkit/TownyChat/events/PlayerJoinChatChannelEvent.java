package com.palmergames.bukkit.TownyChat.events;

import com.palmergames.bukkit.TownyChat.channels.Channel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Glare
 * Date: 12/14/2020
 * Time: 4:56 PM
 */
public class PlayerJoinChatChannelEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Channel channel;

    public PlayerJoinChatChannelEvent(Player player, Channel channel) {
    	super(!Bukkit.getServer().isPrimaryThread());
        this.player = player;
        this.channel = channel;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public Channel getChannel() {
        return channel;
    }
}
