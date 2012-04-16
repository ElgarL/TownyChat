package com.palmergames.bukkit.TownyChat.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class TownyChatEvent extends PlayerChatEvent {

    public TownyChatEvent(Player player, String message) {
        super(player, message);
    }
}
