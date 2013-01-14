package com.palmergames.bukkit.TownyChat.channels;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class Channel {
	
	private String name;
	private List<String> commands;
	private channelTypes type;
	private String channelTag, messageColour, permission, leavePermission, craftIRCTag;
	private double range;
	private boolean hooked=false;
	private boolean autojoin=true;
	protected ConcurrentMap<String, Integer> absentPlayers = null;  
	protected ConcurrentMap<String, Integer> mutedPlayers = null;
	
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
	 * @return the permission
	 */
	public String getCraftIRCTag() {
		if ((craftIRCTag == null) || (craftIRCTag.isEmpty()))
			return "admin";
			
		return craftIRCTag;
	}
	/**
	 * @param craftIRCTag the CraftIRC channel Tag to set
	 */
	public void setCraftIRCTag(String craftIRCTag) {
		this.craftIRCTag = craftIRCTag;
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
	
	/*
	 * Used to reset channel settings for a given player
	 */
	public void forgetPlayer(String name) {
		// If the channel is auto join, they will be added
		// If the channel is not auto join, they will marked as absent
		if (autojoin) {
			join(name);
		} else {
			leave(name);
		}
	}
	
	/*
	 * Mark a player as having left chat
	 */
	public boolean leave(String name) {
		if (absentPlayers == null) {
			absentPlayers = new ConcurrentHashMap<String, Integer> ();
		}
		Integer res = absentPlayers.put(name, 1);
		return (res == null || res == 0);
	}
	
	/*
	 * Mark a player has having joined the chat
	 */
	public boolean join(String name) {
		if (absentPlayers == null) return false;
		Integer res = absentPlayers.remove(name);
		return (res != null && res == 1);
	}
	
	/*
	 * Check if a player is present in a chat
	 */
	public boolean isPresent(String name) {
		if (absentPlayers == null) return true;
		if (absentPlayers.containsKey(name)) return false;
		return true;
	}

	/*
	 * Check if a player is not present in a chat
	 */
	public boolean isAbsent(String name) {
		return !isPresent(name);
	}

	/*
	 * Check if a player is muted in a channel
	 */
	public boolean isMuted(String name) {
		if (mutedPlayers == null) return false;
		if (!mutedPlayers.containsKey(name)) return false;
		return true;
	}

	/*
	 * Mute a player
	 */
	public boolean mute(String name) {
		if (mutedPlayers == null) {
			mutedPlayers = new ConcurrentHashMap<String, Integer> ();
		}
		Integer i = mutedPlayers.get(name);
		if (i != null) return false;
		mutedPlayers.put(name, 1);
		return true;
	}

	/*
	 * Unmute a player 
	 */
	public boolean unmute(String name) {
		if (mutedPlayers == null) return false;
		Integer i = mutedPlayers.get(name);
		if (i == null) return false;
		mutedPlayers.remove(name);
		return true;
	}
	
	/*
	 * Get name of permissions node to leave a the channel 
	 */
	public String getLeavePermission() {
		return leavePermission;
	}

	/*
	 * Set name of permissions node to leave a the channel 
	 */
	public void setLeavePermission(String permission) {
		leavePermission = permission;;
	}

	public boolean hasMuteList() {
		if (mutedPlayers == null || mutedPlayers.isEmpty()) return false;
		return true;
	}

	public Set<String> getMuteList() {
		return mutedPlayers.keySet();
	}
	
	public void setHooked(boolean hooked) {
		this.hooked = hooked;
	}

	public boolean isHooked() {
		return hooked;
	}
	
	public void setAutoJoin(boolean autojoin) {
		this.autojoin = autojoin;
	}
	
	public boolean isAutoJoin() {
		return autojoin;
	}
}