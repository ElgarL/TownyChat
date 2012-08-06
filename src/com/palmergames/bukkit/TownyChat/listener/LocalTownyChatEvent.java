package com.palmergames.bukkit.TownyChat.listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.palmergames.bukkit.towny.object.Resident;

public class LocalTownyChatEvent {
  private AsyncPlayerChatEvent event;
  private Resident resident;
  
  public LocalTownyChatEvent(AsyncPlayerChatEvent event, Resident resident) {
    this.event = event;
    this.resident = resident;
  }

  /**
   * Get the resident associated with the chat event's talking player.
   * @return resident associated with the chat event's talking player
   */
  public Resident getResident() {
    return resident;
  }
  
  /**
   * 
   * @return the PlayerChatEvent
   */
  public AsyncPlayerChatEvent getEvent() {
    return event;
  }
  
  
  /**
   * Convenience method for setting the chat event's format.
   * @param format
   */
  public void setFormat(String format) {
    event.setFormat(format);
  }
  
  /**
   * Convenience method for getting the chat event's format
   * @return the chat event's format
   */
  public String getFormat() {
    return event.getFormat();
  }
  
  /**
   * Convenience method for getting the chat event's message
   * @return the chat event's message
   */
  public String getMessage() {
    return event.getMessage();
  }
}
