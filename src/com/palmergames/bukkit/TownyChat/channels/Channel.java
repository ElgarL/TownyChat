package com.palmergames.bukkit.TownyChat.channels;

import java.util.List;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class Channel {
	
	private String name;
	private List<String> commands;
	private channelTypes type;
	private String channelTag, messageColour, permission, IRCChannel;
	private double range;
	private boolean relayIRCToGame = false;
	private String format;
	
	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public Channel(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the commands
	 */
	public List<String> getCommands() {
		return commands;
	}
	/**
	 * @param commands the commands to set
	 */
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}
	/**
	 * @return the type
	 */
	public channelTypes getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(channelTypes type) {
		this.type = type;
	}
	/**
	 * @return the channelTag
	 */
	public String getChannelTag() {
		return channelTag;
	}
	/**
	 * @param channelTag the channelTag to set
	 */
	public void setChannelTag(String channelTag) {
		this.channelTag = channelTag;
	}
	/**
	 * @return the messageColour
	 */
	public String getMessageColour() {
		return messageColour;
	}
	/**
	 * @param messageColour the messageColour to set
	 */
	public void setMessageColour(String messageColour) {
		this.messageColour = messageColour;
	}
	/**
	 * @return the permission
	 */
	public String getPermission() {
		return permission;
	}
	/**
	 * @param permission the permission to set
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}
	/**
	 * @return Channel list
	 */
	public String getIRCChannels() {
		if ((IRCChannel == null) || (IRCChannel.isEmpty()))
			return "";
			
		return IRCChannel;
	}
	/**
	 * @param IRCChannels the PIRC channel Tag to set
	 */
	public void setIRCChannels(String IRCChannel) {
		this.IRCChannel = IRCChannel;
	}
	/**
	 * @return the range
	 */
	public double getRange() {
		return range;
	}
	/**
	 * @param range the range to set
	 */
	public void setRange(double range) {
		this.range = range;
	}
	/**
	 * @param event the event to process
	 */
	public abstract void chatProcess(AsyncPlayerChatEvent event);
	
	/**
	 * Is the channel going to receive messages from IRC
	 * 
	 * @return
	 */
	public Boolean isRelayIRCToGame() { 
		return relayIRCToGame;
	}
	
	/**
	 * Set if we can send messages to this channel from IRC
	 * 
	 * @param relayIRCToGame
	 */
	public void setRelayIRCToGame(boolean relayIRCToGameCan) {
		this.relayIRCToGame = relayIRCToGameCan;
	}
	
	/**
	 * Send a message to game.
	 * @param sender
	 * @param msg
	 */
	public abstract void sendMessage(String msg);
	
	/**
	 * Get the format
	 * 
	 * @return Format of chat from IRC to game!
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Set format
	 * 
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
}