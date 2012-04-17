package com.palmergames.bukkit.TownyChat.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class TownyChatEvent extends PlayerChatEvent {
	
    public TownyChatEvent(Player player, String message, String format) {
        super(player, message);
        this.setFormat(format);
    }
    
    private boolean tcCanceled = false;
    
    public void setCanceledByTownyChat(boolean canceled) {
    	this.tcCanceled = canceled;
    	this.setCancelled(canceled);
    }

    public boolean isCanceledByTownyChat() {
    	return tcCanceled;
    }
    
}
